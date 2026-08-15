package com.focusflow.app.domain.repository

import com.focusflow.app.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getAllGoals(userId: String): Flow<List<Goal>>
    fun getGoalById(goalId: String): Flow<Goal?>
    suspend fun insertGoal(goal: Goal)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goalId: String)
    suspend fun completeGoal(goalId: String)
}
