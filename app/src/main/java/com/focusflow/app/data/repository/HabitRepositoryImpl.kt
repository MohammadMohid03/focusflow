package com.focusflow.app.data.repository

import com.focusflow.app.data.local.dao.HabitCompletionDao
import com.focusflow.app.data.local.dao.HabitDao
import com.focusflow.app.data.local.entity.HabitCompletionEntity
import com.focusflow.app.data.local.mapper.toDomain
import com.focusflow.app.data.local.mapper.toEntity
import com.focusflow.app.di.IoDispatcher
import com.focusflow.app.domain.model.Habit
import com.focusflow.app.domain.model.HabitCompletion
import com.focusflow.app.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val habitCompletionDao: HabitCompletionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : HabitRepository {

    override fun getAllHabits(userId: String): Flow<List<Habit>> {
        return habitDao.getAllHabits(userId).map { list -> list.map { it.toDomain() } }
    }

    override fun getHabitById(habitId: String): Flow<Habit?> {
        return habitDao.getById(habitId).map { it?.toDomain() }
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
        val completion = HabitCompletionEntity(
            id = UUID.randomUUID().toString(),
            habitId = habitId,
            completedAt = System.currentTimeMillis(),
            date = date
        )
        habitCompletionDao.insertCompletion(completion)
    }

    override fun getHabitCompletions(habitId: String): Flow<List<HabitCompletion>> {
        return habitCompletionDao.getCompletionsForHabit(habitId).map { list -> list.map { it.toDomain() } }
    }

    override fun getHabitsForToday(userId: String): Flow<List<Habit>> {
        return habitDao.getHabitsForToday(userId).map { list -> list.map { it.toDomain() } }
    }
}
