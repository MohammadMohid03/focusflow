package com.focusflow.app.domain.model

import java.util.UUID

data class PlannerSession(
    val id: String = UUID.randomUUID().toString(),
    val goal: String,
    val deadline: Long,
    val availableHoursPerDay: Float,
    val skillLevel: SkillLevel = SkillLevel.INTERMEDIATE,
    val preferredStudyTime: PreferredTime = PreferredTime.MORNING,
    val existingCommitments: String = "",
    val generatedPlan: List<PlannerDay> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val userId: String = "",
    val isSynced: Boolean = false
)

data class PlannerDay(
    val date: Long,
    val sessions: List<PlannerTask>
)

data class PlannerTask(
    val title: String,
    val description: String = "",
    val durationMinutes: Int,
    val type: PlannerTaskType = PlannerTaskType.STUDY,
    val priority: Int = 0,
    val isCompleted: Boolean = false
)

enum class SkillLevel { BEGINNER, INTERMEDIATE, ADVANCED }
enum class PreferredTime { MORNING, AFTERNOON, EVENING, NIGHT }
enum class PlannerTaskType { STUDY, BREAK, REVISION, PROJECT, EXERCISE }
