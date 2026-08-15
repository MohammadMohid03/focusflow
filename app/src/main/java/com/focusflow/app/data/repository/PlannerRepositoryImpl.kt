package com.focusflow.app.data.repository

import com.focusflow.app.data.local.dao.PlannerSessionDao
import com.focusflow.app.data.local.mapper.toDomain
import com.focusflow.app.data.local.mapper.toEntity
import com.focusflow.app.di.IoDispatcher
import com.focusflow.app.domain.model.PlannerSession
import com.focusflow.app.domain.repository.PlannerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlannerRepositoryImpl @Inject constructor(
    private val plannerSessionDao: PlannerSessionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PlannerRepository {

    override fun getAllSessions(userId: String): Flow<List<PlannerSession>> {
        return plannerSessionDao.getAllSessions(userId).map { list -> list.map { it.toDomain() } }
    }

    override fun getSessionById(sessionId: String): Flow<PlannerSession?> {
        return plannerSessionDao.getById(sessionId).map { it?.toDomain() }
    }

    override suspend fun insertSession(session: PlannerSession) = withContext(ioDispatcher) {
        plannerSessionDao.insert(session.toEntity())
    }

    override suspend fun updateSession(session: PlannerSession) = withContext(ioDispatcher) {
        plannerSessionDao.update(session.toEntity())
    }

    override suspend fun deleteSession(sessionId: String) = withContext(ioDispatcher) {
        plannerSessionDao.delete(sessionId)
    }
}
