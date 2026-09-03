package com.focusflow.app.data.local.mapper

import com.focusflow.app.data.local.entity.*
import com.focusflow.app.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T : Enum<T>> safeValueOf(name: String, default: T): T {
    return try { enumValueOf<T>(name) } catch (e: Exception) { default }
}

// Assuming standard domain models exist in com.focusflow.app.domain.model

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    category = category.name,
    priority = priority.name,
    dueDate = dueDate,
    estimatedDurationMinutes = estimatedDurationMinutes,
    isCompleted = isCompleted,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
    reminderTime = reminderTime,
    goalId = goalId,
    userId = userId,
    isSynced = isSynced
)

fun TaskEntity.toDomain(subtasks: List<SubtaskEntity>): Task = Task(
    id = id,
    title = title,
    description = description,
    category = safeValueOf(category, TaskCategory.STUDY),
    priority = safeValueOf(priority, TaskPriority.MEDIUM),
    dueDate = dueDate,
    estimatedDurationMinutes = estimatedDurationMinutes,
    isCompleted = isCompleted,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
    reminderTime = reminderTime,
    goalId = goalId,
    userId = userId,
    isSynced = isSynced,
    subtasks = subtasks.map { it.toDomain() }
)

fun Subtask.toEntity(taskId: String): SubtaskEntity = SubtaskEntity(
    id = id,
    taskId = taskId,
    title = title,
    isCompleted = isCompleted,
    completedAt = completedAt
)

fun SubtaskEntity.toDomain(): Subtask = Subtask(
    id = id,
    title = title,
    isCompleted = isCompleted,
    completedAt = completedAt
)

fun Goal.toEntity(): GoalEntity = GoalEntity(
    id = id,
    title = title,
    description = description,
    targetDate = targetDate,
    progress = progress,
    isCompleted = isCompleted,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
    userId = userId,
    isSynced = isSynced
)

fun GoalEntity.toDomain(): Goal = Goal(
    id = id,
    title = title,
    description = description,
    targetDate = targetDate,
    progress = progress,
    isCompleted = isCompleted,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
    userId = userId,
    isSynced = isSynced
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = id,
    name = name,
    description = description,
    frequency = frequency.name,
    customDays = Json.encodeToString(customDays),
    reminderTime = reminderTime,
    goalTarget = goalTarget,
    icon = icon,
    color = color,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    totalCompletions = totalCompletions,
    createdAt = createdAt,
    updatedAt = updatedAt,
    userId = userId,
    isSynced = isSynced
)

fun HabitEntity.toDomain(): Habit = Habit(
    id = id,
    name = name,
    description = description,
    frequency = safeValueOf(frequency, HabitFrequency.DAILY),
    customDays = try { Json.decodeFromString<List<Int>>(customDays) } catch (e: Exception) { emptyList() },
    reminderTime = reminderTime,
    goalTarget = goalTarget,
    icon = icon,
    color = color,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    totalCompletions = totalCompletions,
    createdAt = createdAt,
    updatedAt = updatedAt,
    userId = userId,
    isSynced = isSynced
)

fun HabitCompletion.toEntity(): HabitCompletionEntity = HabitCompletionEntity(
    id = id,
    habitId = habitId,
    completedAt = completedAt,
    date = date
)

fun HabitCompletionEntity.toDomain(): HabitCompletion = HabitCompletion(
    id = id,
    habitId = habitId,
    completedAt = completedAt,
    date = date
)

fun FocusSession.toEntity(): FocusSessionEntity = FocusSessionEntity(
    id = id,
    taskId = taskId,
    commitmentId = commitmentId,
    startTime = startTime,
    endTime = endTime,
    plannedDurationMinutes = plannedDurationMinutes,
    actualDurationMinutes = actualDurationMinutes,
    breakDurationMinutes = breakDurationMinutes,
    sessionType = sessionType.name,
    isCompleted = isCompleted,
    createdAt = createdAt,
    userId = userId,
    isSynced = isSynced
)

fun FocusSessionEntity.toDomain(): FocusSession = FocusSession(
    id = id,
    taskId = taskId,
    commitmentId = commitmentId,
    startTime = startTime,
    endTime = endTime,
    plannedDurationMinutes = plannedDurationMinutes,
    actualDurationMinutes = actualDurationMinutes,
    breakDurationMinutes = breakDurationMinutes,
    sessionType = safeValueOf(sessionType, FocusSessionType.POMODORO_25_5),
    isCompleted = isCompleted,
    createdAt = createdAt,
    userId = userId,
    isSynced = isSynced
)

fun Commitment.toEntity(): CommitmentEntity = CommitmentEntity(
    id = id,
    taskId = taskId,
    deadline = deadline,
    estimatedDurationMinutes = estimatedDurationMinutes,
    status = status.name,
    commitmentType = commitmentType.name,
    consequenceType = consequenceType.name,
    selectedAppPackages = Json.encodeToString(selectedAppPackages),
    unlockCondition = unlockCondition.name,
    createdAt = createdAt,
    activatedAt = activatedAt,
    warningAt = warningAt,
    missedAt = missedAt,
    completedAt = completedAt,
    cancelledAt = cancelledAt,
    restoredAt = restoredAt,
    recoveryMinutesRequired = recoveryMinutesRequired,
    recoveryMinutesCompleted = recoveryMinutesCompleted,
    cancellationReason = cancellationReason,
    userId = userId,
    isSynced = isSynced
)

fun CommitmentEntity.toDomain(): Commitment = Commitment(
    id = id,
    taskId = taskId,
    deadline = deadline,
    estimatedDurationMinutes = estimatedDurationMinutes,
    status = safeValueOf(status, CommitmentStatus.ACTIVE),
    commitmentType = safeValueOf(commitmentType, CommitmentType.COMPLETE_BEFORE_DEADLINE),
    consequenceType = safeValueOf(consequenceType, ConsequenceType.COMMITMENT_LOCK),
    selectedAppPackages = try { Json.decodeFromString<List<String>>(selectedAppPackages) } catch (e: Exception) { emptyList() },
    unlockCondition = safeValueOf(unlockCondition, UnlockCondition.TASK_COMPLETED),
    createdAt = createdAt,
    activatedAt = activatedAt,
    warningAt = warningAt,
    missedAt = missedAt,
    completedAt = completedAt,
    cancelledAt = cancelledAt,
    restoredAt = restoredAt,
    recoveryMinutesRequired = recoveryMinutesRequired,
    recoveryMinutesCompleted = recoveryMinutesCompleted,
    cancellationReason = cancellationReason,
    userId = userId,
    isSynced = isSynced
)

fun PlannerSession.toEntity(): PlannerSessionEntity = PlannerSessionEntity(
    id = id,
    goal = goal,
    deadline = deadline,
    availableHoursPerDay = availableHoursPerDay,
    skillLevel = skillLevel.name,
    preferredStudyTime = preferredStudyTime.name,
    existingCommitments = existingCommitments,
    generatedPlan = Json.encodeToString(generatedPlan),
    createdAt = createdAt,
    userId = userId,
    isSynced = isSynced
)

fun PlannerSessionEntity.toDomain(): PlannerSession = PlannerSession(
    id = id,
    goal = goal,
    deadline = deadline,
    availableHoursPerDay = availableHoursPerDay,
    skillLevel = safeValueOf(skillLevel, SkillLevel.INTERMEDIATE),
    preferredStudyTime = safeValueOf(preferredStudyTime, PreferredTime.MORNING),
    existingCommitments = existingCommitments,
    generatedPlan = try { Json.decodeFromString<List<PlannerDay>>(generatedPlan) } catch (e: Exception) { emptyList() },
    createdAt = createdAt,
    userId = userId,
    isSynced = isSynced
)
