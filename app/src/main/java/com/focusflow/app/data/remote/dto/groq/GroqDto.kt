package com.focusflow.app.data.remote.dto.groq

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroqChatRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<GroqMessageDto>,
    val temperature: Double = 0.6,
    @SerialName("max_tokens") val maxTokens: Int = 1024,
    @SerialName("response_format") val responseFormat: GroqResponseFormat? = null
)

@Serializable
data class GroqMessageDto(
    val role: String,
    val content: String
)

@Serializable
data class GroqResponseFormat(
    val type: String = "json_object"
)

@Serializable
data class GroqChatResponse(
    val id: String? = null,
    val choices: List<GroqChoiceDto> = emptyList(),
    val usage: GroqUsageDto? = null
)

@Serializable
data class GroqChoiceDto(
    val index: Int = 0,
    val message: GroqMessageDto,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class GroqUsageDto(
    @SerialName("prompt_tokens") val promptTokens: Int = 0,
    @SerialName("completion_tokens") val completionTokens: Int = 0,
    @SerialName("total_tokens") val totalTokens: Int = 0
)

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
