package com.focusflow.app.data.repository

import com.focusflow.app.data.local.dao.GoalDao
import com.focusflow.app.data.local.mapper.toDomain
import com.focusflow.app.data.local.mapper.toEntity
import com.focusflow.app.di.IoDispatcher
import com.focusflow.app.domain.model.Goal
import com.focusflow.app.domain.repository.GoalRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GoalRepository {

    override fun getAllGoals(userId: String): Flow<List<Goal>> {
        return goalDao.getAllGoals(userId).map { list -> list.map { it.toDomain() } }
    }

    override fun getGoalById(goalId: String): Flow<Goal?> {
        return goalDao.getById(goalId).map { it?.toDomain() }
    }

    override suspend fun insertGoal(goal: Goal) = withContext(ioDispatcher) {
        goalDao.insert(goal.toEntity())
    }

    override suspend fun updateGoal(goal: Goal) = withContext(ioDispatcher) {
        goalDao.update(goal.toEntity())
    }

    override suspend fun deleteGoal(goalId: String) = withContext(ioDispatcher) {
        goalDao.delete(goalId)
    }

    override suspend fun completeGoal(goalId: String) = withContext(ioDispatcher) {
        val now = System.currentTimeMillis()
        goalDao.complete(goalId, now, now)
    }
}
