package com.focusflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val dueDate: Long?,
    val estimatedDurationMinutes: Int?,
    val isCompleted: Boolean,
    val completedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    val reminderTime: Long?,
    val goalId: String?,
    val userId: String,
    val isSynced: Boolean
)
