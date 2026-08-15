package com.focusflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commitments")
data class CommitmentEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val deadline: Long,
    val estimatedDurationMinutes: Int,
    val status: String,
    val commitmentType: String,
    val consequenceType: String,
    val selectedAppPackages: String,
    val unlockCondition: String,
    val createdAt: Long,
    val activatedAt: Long?,
    val warningAt: Long?,
    val missedAt: Long?,
    val completedAt: Long?,
    val cancelledAt: Long?,
    val restoredAt: Long?,
    val recoveryMinutesRequired: Int?,
    val recoveryMinutesCompleted: Int,
    val cancellationReason: String?,
    val userId: String,
    val isSynced: Boolean
)
