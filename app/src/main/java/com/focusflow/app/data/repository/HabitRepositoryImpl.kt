package com.focusflow.app.data.repository

import com.focusflow.app.data.local.dao.HabitCompletionDao
import com.focusflow.app.data.local.dao.HabitDao
import com.focusflow.app.data.local.entity.HabitCompletionEntity
import com.focusflow.app.data.local.mapper.toDomain
import com.focusflow.app.data.local.mapper.toEntity
import com.focusflow.app.di.IoDispatcher
import androidx.room.withTransaction
import com.focusflow.app.data.local.FocusFlowDatabase
import com.focusflow.app.domain.model.*
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.flow.combine
import com.focusflow.app.domain.model.Habit
import com.focusflow.app.domain.model.HabitCompletion
import com.focusflow.app.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val habitCompletionDao: HabitCompletionDao,
    private val database: FocusFlowDatabase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : HabitRepository {

    private fun dateOf(timestamp: Long): LocalDate = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

    override fun getAllHabits(userId: String): Flow<List<Habit>> = combine(
        habitDao.getAllHabits(userId), habitCompletionDao.getCompletionsForUser(userId), observeToday()
    ) { habits, completions, today ->
        val byHabit = completions.groupBy { it.habitId }
        habits.map { it.toDomain().withProgress(byHabit[it.id].orEmpty().map { dateOf(it.completedAt) }, today) }
    }

    override fun getHabitById(habitId: String): Flow<Habit?> = combine(
        habitDao.getById(habitId), habitCompletionDao.getCompletionsForHabit(habitId), observeToday()
    ) { habit, completions, today ->
        habit?.toDomain()?.withProgress(completions.map { dateOf(it.completedAt) }, today)
    }

    override suspend fun insertHabit(habit: Habit) = withContext(ioDispatcher) {
        habitDao.insert(habit.toEntity())
    }

    override suspend fun updateHabit(habit: Habit) = withContext(ioDispatcher) {
        habitDao.update(habit.toEntity())
    }

    override suspend fun deleteHabit(habitId: String) = withContext(ioDispatcher) {
        habitDao.delete(habitId)
    }

    override suspend fun completeHabit(habitId: String, date: Long) = withContext(ioDispatcher) {
        database.withTransaction {
            val habit = habitDao.getByIdSync(habitId)?.toDomain() ?: return@withTransaction
            val day = dateOf(date)
            if (!habit.isScheduledOn(day)) return@withTransaction
            val existing = habitCompletionDao.getCompletionsForHabitSync(habitId)
            val dates = existing.map { dateOf(it.completedAt) }
            val alreadyCompleted = if (habit.frequency == HabitFrequency.WEEKLY) {
                val week = day.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                dates.any { it >= week && it < week.plusWeeks(1) }
            } else day in dates
            if (alreadyCompleted) return@withTransaction
            habitCompletionDao.insertCompletion(HabitCompletionEntity(
                id = UUID.randomUUID().toString(), habitId = habitId, completedAt = date,
                date = day.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            ))
            habitDao.update(habit.withProgress(dates + day, day).copy(updatedAt = System.currentTimeMillis()).toEntity())
        }
    }

    override fun getCompletionsForUser(userId: String): Flow<List<HabitCompletion>> =
        habitCompletionDao.getCompletionsForUser(userId).map { rows -> rows.map { it.toDomain() } }

    override fun getHabitCompletions(habitId: String): Flow<List<HabitCompletion>> {
        return habitCompletionDao.getCompletionsForHabit(habitId).map { list -> list.map { it.toDomain() } }
    }

    override fun getHabitsForToday(userId: String): Flow<List<Habit>> {
        return combine(getAllHabits(userId), observeToday()) { habits, today -> habits.filter { it.isScheduledOn(today) } }
    }
}
