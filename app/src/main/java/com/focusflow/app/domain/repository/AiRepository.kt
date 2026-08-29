package com.focusflow.app.domain.repository

import com.focusflow.app.domain.model.TaskPriority

data class SuggestedSubtask(
    val title: String,
    val description: String = "",
    val estimatedMinutes: Int = 25,
    val priority: TaskPriority = TaskPriority.MEDIUM
)

data class SuggestedScheduleSession(
    val title: String,
    val startTime: String,
    val durationMinutes: Int,
    val category: String
)

interface AiRepository {
    suspend fun sendMessage(history: List<Pair<String, Boolean>>, prompt: String): Result<String>
    suspend fun breakdownGoalOrTask(goalOrTask: String): Result<List<SuggestedSubtask>>
    suspend fun generateDailySchedule(tasks: List<String>, availableHours: Int): Result<List<SuggestedScheduleSession>>
    suspend fun generateFocusTip(): Result<String>
}
