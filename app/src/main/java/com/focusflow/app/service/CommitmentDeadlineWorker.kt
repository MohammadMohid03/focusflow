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
        const val KEY_MINUTES_REMAINING = "minutes_remaining"
        const val TYPE_WARNING = "warning"
        const val TYPE_DEADLINE = "deadline"
    }

    override suspend fun doWork(): Result {
        val commitmentId = inputData.getString(KEY_COMMITMENT_ID) ?: return Result.failure()
        val notificationType = inputData.getString(KEY_NOTIFICATION_TYPE) ?: TYPE_DEADLINE
        val minutesRemaining = inputData.getInt(KEY_MINUTES_REMAINING, 5)

        return try {
            val commitment = commitmentRepository.getCommitmentById(commitmentId).first()
                ?: return Result.failure()

            // Only process if commitment is still active/warning
            if (commitment.status != CommitmentStatus.ACTIVE &&
                commitment.status != CommitmentStatus.WARNING) {
                return Result.success()
            }

            when (notificationType) {
                TYPE_WARNING -> {
                    val task = taskRepository.getTaskById(commitment.taskId).first()
                    notificationHelper.showCommitmentWarning(
                        commitmentId = commitmentId,
                        taskName = task?.title ?: "Focus Session",
                        minutesRemaining = minutesRemaining
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
        return maxOf(15, estimatedDuration / 4)
    }
}
