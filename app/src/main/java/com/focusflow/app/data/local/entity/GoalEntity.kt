package com.focusflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val targetDate: Long?,
    val progress: Float,
    val isCompleted: Boolean,
    val completedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    val userId: String,
    val isSynced: Boolean
)
