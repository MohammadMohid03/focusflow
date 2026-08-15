package com.focusflow.app.domain.model

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val category: TaskCategory = TaskCategory.OTHER,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Long? = null,
    val estimatedDurationMinutes: Int? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val reminderTime: Long? = null,
    val goalId: String? = null,
    val subtasks: List<Subtask> = emptyList(),
    val userId: String = "",
    val isSynced: Boolean = false
)

data class Subtask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)

enum class TaskCategory { WORK, STUDY, PERSONAL, HEALTH, CREATIVE, OTHER }
enum class TaskPriority { URGENT, HIGH, MEDIUM, LOW }
enum class TaskFilter { ALL, TODAY, UPCOMING, COMPLETED, HIGH_PRIORITY }
enum class TaskSortOrder { DUE_DATE, PRIORITY, CREATED_DATE, TITLE }
