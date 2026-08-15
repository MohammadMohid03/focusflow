package com.focusflow.app.domain.repository

import com.focusflow.app.domain.model.Habit
import com.focusflow.app.domain.model.HabitCompletion
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllHabits(userId: String): Flow<List<Habit>>
    fun getHabitById(habitId: String): Flow<Habit?>
    suspend fun insertHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habitId: String)
    suspend fun completeHabit(habitId: String, date: Long)
    fun getHabitCompletions(habitId: String): Flow<List<HabitCompletion>>
    fun getHabitsForToday(userId: String): Flow<List<Habit>>
}
