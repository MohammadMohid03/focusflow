package com.focusflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val frequency: String,
    val customDays: String,
    val reminderTime: Long?,
    val goalTarget: Int,
    val icon: String,
    val color: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalCompletions: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val userId: String,
    val isSynced: Boolean
)
