package com.focusflow.app.domain.repository

import com.focusflow.app.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    fun getAllSessions(userId: String): Flow<List<FocusSession>>
    fun getSessionById(sessionId: String): Flow<FocusSession?>
    suspend fun insertSession(session: FocusSession)
    suspend fun updateSession(session: FocusSession)
    suspend fun deleteSession(sessionId: String)
    fun getSessionsForTask(taskId: String): Flow<List<FocusSession>>
    fun getTotalFocusMinutes(userId: String): Flow<Long>
    fun getSessionsInRange(userId: String, startTime: Long, endTime: Long): Flow<List<FocusSession>>
}
