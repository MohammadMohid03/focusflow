package com.focusflow.app.domain.model

import java.util.UUID

data class Goal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val targetDate: Long? = null,
    val progress: Float = 0f,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val userId: String = "",
    val linkedTaskIds: List<String> = emptyList(),
    val isSynced: Boolean = false
)
