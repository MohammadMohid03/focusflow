package com.focusflow.app.data.remote.dto.groq

import kotlinx.serialization.Serializable

// Subtask JSON format
@Serializable
data class SubtaskBreakdownResponse(
    val tasks: List<SuggestedSubtaskDto> = emptyList()
)

@Serializable
data class SuggestedSubtaskDto(
    val title: String,
    val description: String = "",
    val estimatedMinutes: Int = 25,
    val priority: String = "MEDIUM"
)

// Daily Schedule JSON format
@Serializable
data class DailyScheduleResponse(
    val schedule: List<SuggestedScheduleSessionDto> = emptyList(),
    val summary: String = ""
)

@Serializable
data class SuggestedScheduleSessionDto(
    val title: String,
    val startTime: String, // e.g. "09:00 AM"
    val durationMinutes: Int,
    val category: String = "STUDY"
)
