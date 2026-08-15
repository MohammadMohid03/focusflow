package com.focusflow.app.domain.model

data class ProductivityStats(
    val totalFocusMinutes: Long = 0,
    val totalTasksCompleted: Int = 0,
    val totalTasksCreated: Int = 0,
    val completionRate: Float = 0f,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val averageDailyFocusMinutes: Float = 0f,
    val mostProductiveHour: Int? = null,
    val commitmentScore: CommitmentScore = CommitmentScore()
)
