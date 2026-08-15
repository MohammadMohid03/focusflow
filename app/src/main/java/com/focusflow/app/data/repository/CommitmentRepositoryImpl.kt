package com.focusflow.app.data.repository

import com.focusflow.app.data.local.dao.CommitmentDao
import com.focusflow.app.data.local.mapper.toDomain
import com.focusflow.app.data.local.mapper.toEntity
import com.focusflow.app.di.IoDispatcher
import com.focusflow.app.domain.model.Commitment
import com.focusflow.app.domain.model.CommitmentStatus
import com.focusflow.app.domain.repository.CommitmentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommitmentRepositoryImpl @Inject constructor(
    private val commitmentDao: CommitmentDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CommitmentRepository {

    override fun getAllCommitments(userId: String): Flow<List<Commitment>> {
        return commitmentDao.getAllCommitments(userId).map { list -> list.map { it.toDomain() } }
    }

    override fun getCommitmentById(commitmentId: String): Flow<Commitment?> {
        return commitmentDao.getById(commitmentId).map { it?.toDomain() }
    }

    override fun getActiveCommitments(userId: String): Flow<List<Commitment>> {
        return commitmentDao.getActiveCommitments(userId).map { list -> list.map { it.toDomain() } }
    }

    override fun getCommitmentForTask(taskId: String): Flow<Commitment?> {
        return commitmentDao.getCommitmentForTask(taskId).map { list -> list.firstOrNull()?.toDomain() }
    }

    override suspend fun insertCommitment(commitment: Commitment) = withContext(ioDispatcher) {
        commitmentDao.insert(commitment.toEntity())
    }

    override suspend fun updateCommitment(commitment: Commitment) = withContext(ioDispatcher) {
        commitmentDao.update(commitment.toEntity())
    }

    override suspend fun deleteCommitment(commitmentId: String) = withContext(ioDispatcher) {
        commitmentDao.delete(commitmentId)
    }

    override fun getCommitmentHistory(userId: String): Flow<List<Commitment>> {
        return commitmentDao.getCommitmentHistory(userId).map { list -> list.map { it.toDomain() } }
    }

    override fun getCommitmentsByStatus(userId: String, status: CommitmentStatus): Flow<List<Commitment>> {
        return commitmentDao.getCommitmentsByStatus(userId, status.name).map { list -> list.map { it.toDomain() } }
    }
}
