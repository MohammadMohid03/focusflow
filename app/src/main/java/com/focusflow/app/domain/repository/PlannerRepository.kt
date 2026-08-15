package com.focusflow.app.domain.repository

import com.focusflow.app.domain.model.PlannerSession
import kotlinx.coroutines.flow.Flow

interface PlannerRepository {
    fun getAllSessions(userId: String): Flow<List<PlannerSession>>
    fun getSessionById(sessionId: String): Flow<PlannerSession?>
    suspend fun insertSession(session: PlannerSession)
    suspend fun updateSession(session: PlannerSession)
    suspend fun deleteSession(sessionId: String)
}
