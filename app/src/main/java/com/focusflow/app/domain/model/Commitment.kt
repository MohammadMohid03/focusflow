package com.focusflow.app.domain.model

import java.util.UUID

data class Commitment(
    val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val deadline: Long,
    val estimatedDurationMinutes: Int,
    val status: CommitmentStatus = CommitmentStatus.DRAFT,
    val commitmentType: CommitmentType = CommitmentType.COMPLETE_BEFORE_DEADLINE,
    val consequenceType: ConsequenceType = ConsequenceType.COMMITMENT_LOCK,
    val selectedAppPackages: List<String> = emptyList(),
    val unlockCondition: UnlockCondition = UnlockCondition.TASK_COMPLETED,
    val createdAt: Long = System.currentTimeMillis(),
    val activatedAt: Long? = null,
    val warningAt: Long? = null,
    val missedAt: Long? = null,
    val completedAt: Long? = null,
    val cancelledAt: Long? = null,
    val restoredAt: Long? = null,
    val recoveryMinutesRequired: Int? = null,
    val recoveryMinutesCompleted: Int = 0,
    val cancellationReason: String? = null,
    val userId: String = "",
    val isSynced: Boolean = false
)

enum class CommitmentStatus {
    DRAFT, ACTIVE, WARNING, COMPLETED, MISSED, RESTRICTED, RECOVERY, RESTORED, CANCELLED, EXPIRED
}

enum class CommitmentType {
    COMPLETE_BEFORE_DEADLINE,
    COMPLETE_SUBTASKS,
    MINIMUM_FOCUS_DURATION
}

enum class ConsequenceType {
    NONE,
    COMMITMENT_LOCK
}

enum class UnlockCondition {
    TASK_COMPLETED,
    FOCUS_SESSION_COMPLETED,
    RECOVERY_PLAN_COMPLETED,
    USER_CANCELS
}
