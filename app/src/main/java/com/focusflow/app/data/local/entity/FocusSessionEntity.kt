package com.focusflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey val id: String,
    val taskId: String?,
    val commitmentId: String?,
    val startTime: Long,
    val endTime: Long?,
    val plannedDurationMinutes: Int,
    val actualDurationMinutes: Int?,
    val breakDurationMinutes: Int,
    val sessionType: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val userId: String,
    val isSynced: Boolean
)
