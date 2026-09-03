package com.focusflow.app.service

import android.content.Context
import android.util.Log
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
        private const val TAG = "CommitmentWorker"
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
                        // Unlock apps since commitment is completed
                        unlockAppsIfNoActiveCommitments(commitment.selectedAppPackages)
                    } else {
                        // Task not completed - MISSED
                        val updatedCommitment = commitment.copy(
                            status = CommitmentStatus.MISSED,
                            missedAt = System.currentTimeMillis()
                        )
                        commitmentRepository.updateCommitment(updatedCommitment)

                        // On missed deadline, keep restrictions active but update status.
                        // Don't try to start a new foreground service from background
                        // (would throw ForegroundServiceStartNotAllowedException on Android 12+).
                        // The existing AppBlockerService remains running if it was already started.

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
            Log.e(TAG, "CommitmentDeadlineWorker failed", e)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    /**
     * After completing a commitment, check if there are any remaining active commitments.
     * If not, stop the blocker service and unlock all apps.
     */
    private suspend fun unlockAppsIfNoActiveCommitments(appsToCheck: List<String>) {
        try {
            appRestrictionManager.disableRestriction(appsToCheck)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unlock apps after commitment completion", e)
        }
    }

    private fun calculateRecoveryMinutes(estimatedDuration: Int): Int {
        return maxOf(15, estimatedDuration / 4)
    }
}
