package com.focusflow.app.data.repository

import com.focusflow.app.BuildConfig
import com.focusflow.app.data.remote.api.GroqApiService
import com.focusflow.app.data.remote.dto.groq.*
import com.focusflow.app.domain.model.TaskPriority
import com.focusflow.app.domain.repository.AiRepository
import com.focusflow.app.domain.repository.SuggestedScheduleSession
import com.focusflow.app.domain.repository.SuggestedSubtask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val groqApiService: GroqApiService,
    private val json: Json
) : AiRepository {

    private val apiKey: String
        get() = BuildConfig.GROQ_API_KEY.trim()

    override suspend fun sendMessage(
        history: List<Pair<String, Boolean>>,
        prompt: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(getFallbackChatResponse(prompt))
        }

        try {
            val systemMessage = GroqMessageDto(
                role = "system",
                content = "You are FocusFlow AI, an elite study partner, productivity coach, and academic assistant. You provide concise, actionable, clear, and motivational advice. Use bullet points, short paragraphs, and practical steps."
            )

            val messages = mutableListOf(systemMessage)
            // Add up to last 8 messages for context
            history.takeLast(8).forEach { (content, isUser) ->
                messages.add(
                    GroqMessageDto(
                        role = if (isUser) "user" else "assistant",
                        content = content
                    )
                )
            }
            messages.add(GroqMessageDto(role = "user", content = prompt))

            val request = GroqChatRequest(
                model = "llama-3.3-70b-versatile",
                messages = messages,
                temperature = 0.7,
                maxTokens = 1024
            )

            val response = groqApiService.getChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            if (response.isSuccessful) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content
                if (!content.isNullOrBlank()) {
                    Result.success(content)
                } else {
                    Result.success(getFallbackChatResponse(prompt))
                }
            } else {
                Result.success(getFallbackChatResponse(prompt))
            }
        } catch (e: Exception) {
            Result.success(getFallbackChatResponse(prompt))
        }
    }

    override suspend fun breakdownGoalOrTask(
        goalOrTask: String
    ): Result<List<SuggestedSubtask>> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(getFallbackBreakdown(goalOrTask))
        }

        try {
            val systemPrompt = """
                You are a task deconstruction specialist. Break down the user's project/goal into 4 to 6 clear, actionable, and logical subtasks.
                Return ONLY valid JSON in this exact structure:
                {
                  "tasks": [
                    {
                      "title": "Subtask title",
                      "description": "Short explanation",
                      "estimatedMinutes": 25,
                      "priority": "HIGH"
                    }
                  ]
                }
                Priority must be one of: URGENT, HIGH, MEDIUM, LOW.
            """.trimIndent()

            val request = GroqChatRequest(
                model = "llama-3.3-70b-versatile",
                messages = listOf(
                    GroqMessageDto(role = "system", content = systemPrompt),
                    GroqMessageDto(role = "user", content = "Goal: $goalOrTask")
                ),
                temperature = 0.4,
                maxTokens = 1024,
                responseFormat = GroqResponseFormat("json_object")
            )

            val response = groqApiService.getChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            if (response.isSuccessful) {
                val rawJson = response.body()?.choices?.firstOrNull()?.message?.content
                if (!rawJson.isNullOrBlank()) {
                    val parsed = json.decodeFromString<SubtaskBreakdownResponse>(rawJson)
                    val subtasks = parsed.tasks.map { dto ->
                        SuggestedSubtask(
                            title = dto.title,
                            description = dto.description,
                            estimatedMinutes = dto.estimatedMinutes.coerceIn(10, 120),
                            priority = try {
                                TaskPriority.valueOf(dto.priority.uppercase())
                            } catch (e: Exception) {
                                TaskPriority.MEDIUM
                            }
                        )
                    }
                    if (subtasks.isNotEmpty()) {
                        return@withContext Result.success(subtasks)
                    }
                }
            }
            Result.success(getFallbackBreakdown(goalOrTask))
        } catch (e: Exception) {
            Result.success(getFallbackBreakdown(goalOrTask))
        }
    }

    override suspend fun generateDailySchedule(
        tasks: List<String>,
        availableHours: Int
    ): Result<List<SuggestedScheduleSession>> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(getFallbackSchedule(tasks, availableHours))
        }

        try {
            val taskListStr = if (tasks.isEmpty()) "General study and deep work" else tasks.joinToString(", ")
            val systemPrompt = """
                You are an expert time-block scheduler. Create an optimized Pomodoro-based daily study schedule for $availableHours available hours.
                Return ONLY valid JSON matching this schema:
                {
                  "schedule": [
                    {
                      "title": "Topic or Task",
                      "startTime": "09:00 AM",
                      "durationMinutes": 45,
                      "category": "STUDY"
                    }
                  ],
                  "summary": "Short overview"
                }
            """.trimIndent()

            val request = GroqChatRequest(
                model = "llama-3.3-70b-versatile",
                messages = listOf(
                    GroqMessageDto(role = "system", content = systemPrompt),
                    GroqMessageDto(role = "user", content = "Tasks to schedule: $taskListStr. Total hours available: $availableHours")
                ),
                temperature = 0.5,
                maxTokens = 1024,
                responseFormat = GroqResponseFormat("json_object")
            )

            val response = groqApiService.getChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            if (response.isSuccessful) {
                val rawJson = response.body()?.choices?.firstOrNull()?.message?.content
                if (!rawJson.isNullOrBlank()) {
                    val parsed = json.decodeFromString<DailyScheduleResponse>(rawJson)
                    val sessions = parsed.schedule.map { dto ->
                        SuggestedScheduleSession(
                            title = dto.title,
                            startTime = dto.startTime,
                            durationMinutes = dto.durationMinutes,
                            category = dto.category
                        )
                    }
                    if (sessions.isNotEmpty()) {
                        return@withContext Result.success(sessions)
                    }
                }
            }
            Result.success(getFallbackSchedule(tasks, availableHours))
        } catch (e: Exception) {
            Result.success(getFallbackSchedule(tasks, availableHours))
        }
    }

    override suspend fun generateFocusTip(): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(getRandomFallbackTip())
        }

        try {
            val systemPrompt = "You are a cognitive science & focus coach. Provide a single, powerful 1-2 sentence evidence-based study or focus tip. Be concise, direct, and practical."
            val request = GroqChatRequest(
                model = "llama-3.3-70b-versatile",
                messages = listOf(
                    GroqMessageDto(role = "system", content = systemPrompt),
                    GroqMessageDto(role = "user", content = "Give me today's actionable focus tip.")
                ),
                temperature = 0.8,
                maxTokens = 120
            )

            val response = groqApiService.getChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            if (response.isSuccessful) {
                val tip = response.body()?.choices?.firstOrNull()?.message?.content?.trim()
                if (!tip.isNullOrBlank()) {
                    return@withContext Result.success(tip.removePrefix("\"").removeSuffix("\""))
                }
            }
            Result.success(getRandomFallbackTip())
        } catch (e: Exception) {
            Result.success(getRandomFallbackTip())
        }
    }

    private fun getFallbackChatResponse(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("calculus") || lower.contains("derivative") || lower.contains("math") ->
                "For Calculus: 1. Identify the core rule (Power, Product, Chain). 2. Differentiate step-by-step. 3. Check critical points where f'(x)=0."
            lower.contains("tip") || lower.contains("exam") || lower.contains("memoriz") ->
                "Top Active Recall Tip: Teach the concept out loud in plain terms (Feynman Technique), then do a 25-minute closed-book practice session."
            lower.contains("plan") || lower.contains("schedule") ->
                "Recommended 2-Hour Plan: Block 1 (45m hardest topic) -> 10m walk/water -> Block 2 (40m active problems) -> 25m review & recap."
            else ->
                "Let's tackle this systematically! Break this into 2-3 focused milestones. What specific outcome would you like to achieve in this study session?"
        }
    }

    private fun getFallbackBreakdown(goal: String): List<SuggestedSubtask> {
        return listOf(
            SuggestedSubtask("1. Core Concept Review & Outline", "Understand key formulas and fundamental ideas", 30, TaskPriority.HIGH),
            SuggestedSubtask("2. Deep Practice & Problem Solving", "Solve 5-8 textbook problems without checking solutions", 45, TaskPriority.URGENT),
            SuggestedSubtask("3. Active Recall & Self-Quiz", "Test yourself on definitions and tricky edge cases", 25, TaskPriority.MEDIUM),
            SuggestedSubtask("4. Summary & Mistake Analysis", "Document errors and create 1-page quick revision sheet", 20, TaskPriority.LOW)
        )
    }

    private fun getFallbackSchedule(tasks: List<String>, hours: Int): List<SuggestedScheduleSession> {
        return listOf(
            SuggestedScheduleSession("Deep Focus Block 1", "09:00 AM", 45, "STUDY"),
            SuggestedScheduleSession("Active Practice Session", "10:00 AM", 45, "STUDY"),
            SuggestedScheduleSession("Review & Retention Check", "11:00 AM", 30, "REVIEW")
        )
    }

    private fun getRandomFallbackTip(): String {
        val tips = listOf(
            "Studying in 45-minute blocks followed by 10-minute breaks improves memory consolidation by up to 30%.",
            "Active recall beats passive re-reading every time: test yourself immediately after reading each chapter.",
            "Eliminating phone notifications during your first 25 minutes of deep work prevents attention residue."
        )
        return tips.random()
    }
}
