package com.focusflow.app.data.repository

import com.google.firebase.functions.FirebaseFunctions
import com.focusflow.app.data.remote.dto.groq.DailyScheduleResponse
import com.focusflow.app.data.remote.dto.groq.SubtaskBreakdownResponse
import com.focusflow.app.domain.model.TaskPriority
import com.focusflow.app.domain.repository.AiRepository
import com.focusflow.app.domain.repository.SuggestedScheduleSession
import com.focusflow.app.domain.repository.SuggestedSubtask
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val functions: FirebaseFunctions,
    private val json: Json
) : AiRepository {

    override suspend fun sendMessage(history: List<Pair<String, Boolean>>, prompt: String): Result<String> =
        resultOf {
            request("chat", mapOf(
                "prompt" to prompt,
                "history" to history.takeLast(8).map { (content, isUser) ->
                    mapOf("role" to if (isUser) "user" else "assistant", "content" to content)
                }
            ))
        }

    override suspend fun breakdownGoalOrTask(goalOrTask: String): Result<List<SuggestedSubtask>> =
        resultOf {
            val raw = request("breakdown", mapOf("goal" to goalOrTask))
            json.decodeFromString<SubtaskBreakdownResponse>(raw).tasks.take(6).map { dto ->
                SuggestedSubtask(
                    title = dto.title.take(120),
                    description = dto.description.take(500),
                    estimatedMinutes = dto.estimatedMinutes.coerceIn(10, 120),
                    priority = runCatching { TaskPriority.valueOf(dto.priority.uppercase()) }
                        .getOrDefault(TaskPriority.MEDIUM)
                )
            }.ifEmpty { error("AI returned no subtasks. Please try again.") }
        }

    override suspend fun generateDailySchedule(
        tasks: List<String>,
        availableHours: Int
    ): Result<List<SuggestedScheduleSession>> = resultOf {
        val raw = request("schedule", mapOf(
            "tasks" to tasks.take(30).map { it.take(300) },
            "availableHours" to availableHours.coerceIn(1, 24)
        ))
        json.decodeFromString<DailyScheduleResponse>(raw).schedule.take(24).map { dto ->
            SuggestedScheduleSession(
                title = dto.title.take(120),
                startTime = dto.startTime.take(20),
                durationMinutes = dto.durationMinutes.coerceIn(5, 240),
                category = dto.category.take(40)
            )
        }.ifEmpty { error("AI returned no schedule. Please try again.") }
    }

    override suspend fun generateFocusTip(): Result<String> = resultOf {
        request("tip", emptyMap()).take(500)
            .replace("**", "")
            .replace("__", "")
            .trim()
            .ifBlank { error("AI returned an empty focus tip") }
    }

    private suspend fun request(operation: String, input: Map<String, Any?>): String {
        val result = functions.getHttpsCallable("generateAi")
            .call(input + ("operation" to operation))
            .await()
        val data = result.getData() as? Map<*, *> ?: error("Invalid AI service response")
        return data["content"] as? String ?: error("Missing AI response")
    }

    private suspend inline fun <T> resultOf(block: () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
