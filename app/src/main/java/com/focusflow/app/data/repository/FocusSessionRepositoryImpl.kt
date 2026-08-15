package com.focusflow.app.data.repository

import com.focusflow.app.data.local.dao.FocusSessionDao
import com.focusflow.app.data.local.mapper.toDomain
import com.focusflow.app.data.local.mapper.toEntity
import com.focusflow.app.di.IoDispatcher
import com.focusflow.app.domain.model.FocusSession
import com.focusflow.app.domain.repository.FocusSessionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FocusSessionRepositoryImpl @Inject constructor(
    private val focusSessionDao: FocusSessionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : FocusSessionRepository {

    override fun getAllSessions(userId: String): Flow<List<FocusSession>> {
        return focusSessionDao.getAllSessions(userId).map { list -> list.map { it.toDomain() } }
    }

    override fun getSessionById(sessionId: String): Flow<FocusSession?> {
        return focusSessionDao.getById(sessionId).map { it?.toDomain() }
    }

    override suspend fun insertSession(session: FocusSession) = withContext(ioDispatcher) {
        focusSessionDao.insert(session.toEntity())
    }

    override suspend fun updateSession(session: FocusSession) = withContext(ioDispatcher) {
        focusSessionDao.update(session.toEntity())
    }

    override suspend fun deleteSession(sessionId: String) = withContext(ioDispatcher) {
        focusSessionDao.delete(sessionId)
    }

    override fun getSessionsForTask(taskId: String): Flow<List<FocusSession>> {
        return focusSessionDao.getSessionsForTask(taskId).map { list -> list.map { it.toDomain() } }
    }

    override fun getTotalFocusMinutes(userId: String): Flow<Long> {
        return focusSessionDao.getTotalFocusMinutes(userId).map { (it ?: 0).toLong() }
    }

    override fun getSessionsInRange(userId: String, startTime: Long, endTime: Long): Flow<List<FocusSession>> {
        return focusSessionDao.getSessionsInRange(userId, startTime, endTime).map { list -> list.map { it.toDomain() } }
    }
}
