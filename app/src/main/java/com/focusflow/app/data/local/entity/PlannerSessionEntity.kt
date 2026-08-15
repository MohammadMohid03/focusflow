package com.focusflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planner_sessions")
data class PlannerSessionEntity(
    @PrimaryKey val id: String,
    val goal: String,
    val deadline: Long,
    val availableHoursPerDay: Float,
    val skillLevel: String,
    val preferredStudyTime: String,
    val existingCommitments: String,
    val generatedPlan: String,
    val createdAt: Long,
    val userId: String,
    val isSynced: Boolean
)
