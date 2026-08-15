package com.focusflow.app.domain.model

import java.util.UUID

data class FocusSession(
    val id: String = UUID.randomUUID().toString(),
    val taskId: String? = null,
    val commitmentId: String? = null,
    val startTime: Long,
    val endTime: Long? = null,
    val plannedDurationMinutes: Int,
    val actualDurationMinutes: Int? = null,
    val breakDurationMinutes: Int = 5,
    val sessionType: FocusSessionType = FocusSessionType.POMODORO_25_5,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val userId: String = "",
    val isSynced: Boolean = false
)

enum class FocusSessionType {
    POMODORO_25_5,
    POMODORO_50_10,
    CUSTOM
}
