package com.focusflow.app.domain.repository

import com.focusflow.app.domain.model.Commitment
import com.focusflow.app.domain.model.CommitmentStatus
import kotlinx.coroutines.flow.Flow

interface CommitmentRepository {
    fun getAllCommitments(userId: String): Flow<List<Commitment>>
    fun getCommitmentById(commitmentId: String): Flow<Commitment?>
    fun getActiveCommitments(userId: String): Flow<List<Commitment>>
    fun getCommitmentForTask(taskId: String): Flow<Commitment?>
    suspend fun insertCommitment(commitment: Commitment)
    suspend fun updateCommitment(commitment: Commitment)
    suspend fun deleteCommitment(commitmentId: String)
    fun getCommitmentHistory(userId: String): Flow<List<Commitment>>
    fun getCommitmentsByStatus(userId: String, status: CommitmentStatus): Flow<List<Commitment>>
}
