package com.focusflow.app.domain.model

import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val customDays: List<Int> = emptyList(), // DayOfWeek ordinals
    val reminderTime: Long? = null,
    val goalTarget: Int = 1,
    val icon: String = "check_circle",
    val color: String = "#6C63FF",
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCompletions: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val userId: String = "",
    val isSynced: Boolean = false
)

data class HabitCompletion(
    val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val completedAt: Long = System.currentTimeMillis(),
    val date: Long // date only (start of day millis)
)

enum class HabitFrequency { DAILY, WEEKLY, CUSTOM }
