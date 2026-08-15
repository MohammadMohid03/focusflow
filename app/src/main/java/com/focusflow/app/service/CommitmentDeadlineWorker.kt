package com.focusflow.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.focusflow.app.domain.model.CommitmentStatus
import com.focusflow.app.domain.repository.CommitmentRepository
import com.focusflow.app.domain.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * WorkManager worker that evaluates commitment deadlines.
 * Scheduled when a commitment becomes active and runs at the deadline time.
 * Checks if the linked task is completed and transitions the commitment
 * to COMPLETED or MISSED accordingly.
 */
@HiltWorker
class CommitmentDeadlineWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val commitmentRepository: CommitmentRepository,
    private val taskRepository: TaskRepository,
    private val notificationHelper: NotificationHelper,
    private val appRestrictionManager: AppRestrictionManager
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_COMMITMENT_ID = "commitment_id"
        const val KEY_NOTIFICATION_TYPE = "notification_type"
        const val TYPE_WARNING_30 = "warning_30"
        const val TYPE_WARNING_10 = "warning_10"
        const val TYPE_DEADLINE = "deadline"
    }

    override suspend fun doWork(): Result {
        val commitmentId = inputData.getString(KEY_COMMITMENT_ID) ?: return Result.failure()
        val notificationType = inputData.getString(KEY_NOTIFICATION_TYPE) ?: TYPE_DEADLINE

        return try {
            val commitment = commitmentRepository.getCommitmentById(commitmentId).first()
                ?: return Result.failure()

            // Only process if commitment is still active/warning
            if (commitment.status != CommitmentStatus.ACTIVE &&
                commitment.status != CommitmentStatus.WARNING) {
                return Result.success()
            }

            when (notificationType) {
                TYPE_WARNING_30 -> {
                    // 30-minute warning
                    val task = taskRepository.getTaskById(commitment.taskId).first()
                    notificationHelper.showCommitmentWarning(
                        commitmentId = commitmentId,
                        taskName = task?.title ?: "Your task",
                        minutesRemaining = 30
                    )
                    // Update status to WARNING
                    commitmentRepository.updateCommitment(
                        commitment.copy(
                            status = CommitmentStatus.WARNING,
                            warningAt = System.currentTimeMillis()
                        )
                    )
                    Result.success()
                }

                TYPE_WARNING_10 -> {
                    val task = taskRepository.getTaskById(commitment.taskId).first()
                    notificationHelper.showCommitmentWarning(
                        commitmentId = commitmentId,
                        taskName = task?.title ?: "Your task",
                        minutesRemaining = 10
                    )
                    Result.success()
                }

                TYPE_DEADLINE -> {
                    // Deadline reached - evaluate task completion
                    val task = taskRepository.getTaskById(commitment.taskId).first()

                    if (task?.isCompleted == true) {
                        // Task completed before deadline - SUCCESS
                        commitmentRepository.updateCommitment(
                            commitment.copy(
                                status = CommitmentStatus.COMPLETED,
                                completedAt = System.currentTimeMillis()
                            )
                        )
                        notificationHelper.showCommitmentCompleted(
                            commitmentId = commitmentId,
                            taskName = task.title
                        )
                    } else {
                        // Task not completed - MISSED
                        val updatedCommitment = commitment.copy(
                            status = CommitmentStatus.MISSED,
                            missedAt = System.currentTimeMillis()
                        )
                        commitmentRepository.updateCommitment(updatedCommitment)

                        // Activate consequence if applicable
                        if (commitment.selectedAppPackages.isNotEmpty()) {
                            val result = appRestrictionManager.enableRestriction(
                                apps = commitment.selectedAppPackages,
                                reason = "Commitment missed: ${task?.title ?: "task"}"
                            )
                            if (result.success) {
                                commitmentRepository.updateCommitment(
                                    updatedCommitment.copy(
                                        status = CommitmentStatus.RESTRICTED,
                                        recoveryMinutesRequired = calculateRecoveryMinutes(commitment.estimatedDurationMinutes)
                                    )
                                )
                            }
                        }

                        notificationHelper.showCommitmentMissed(
                            commitmentId = commitmentId,
                            taskName = task?.title ?: "Your task"
                        )
                    }
                    Result.success()
                }

                else -> Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun calculateRecoveryMinutes(estimatedDuration: Int): Int {
        // Recovery requires 25% of estimated duration, minimum 15 minutes
        return maxOf(15, estimatedDuration / 4)
    }
}
