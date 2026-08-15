package com.focusflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.focusflow.app.data.local.entity.CommitmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommitmentDao {
    @Query("SELECT * FROM commitments WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllCommitments(userId: String): Flow<List<CommitmentEntity>>

    @Query("SELECT * FROM commitments WHERE id = :id")
    fun getById(id: String): Flow<CommitmentEntity?>

    @Query("SELECT * FROM commitments WHERE userId = :userId AND status IN ('ACTIVE', 'WARNING') ORDER BY deadline ASC")
    fun getActiveCommitments(userId: String): Flow<List<CommitmentEntity>>

    @Query("SELECT * FROM commitments WHERE taskId = :taskId ORDER BY createdAt DESC")
    fun getCommitmentForTask(taskId: String): Flow<List<CommitmentEntity>>

    @Query("SELECT * FROM commitments WHERE userId = :userId AND status = :status ORDER BY deadline DESC")
    fun getCommitmentsByStatus(userId: String, status: String): Flow<List<CommitmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commitment: CommitmentEntity)

    @Update
    suspend fun update(commitment: CommitmentEntity)

    @Query("DELETE FROM commitments WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM commitments WHERE userId = :userId AND status IN ('COMPLETED', 'MISSED', 'CANCELLED', 'RESTORED') ORDER BY deadline DESC")
    fun getCommitmentHistory(userId: String): Flow<List<CommitmentEntity>>

    @Query("SELECT COUNT(*) FROM commitments WHERE userId = :userId AND status = 'COMPLETED'")
    fun getCompletedCommitmentsCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM commitments WHERE userId = :userId AND status = 'MISSED'")
    fun getMissedCommitmentsCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM commitments WHERE userId = :userId AND status = 'CANCELLED'")
    fun getCancelledCommitmentsCount(userId: String): Flow<Int>

    @Query("SELECT * FROM commitments WHERE isSynced = 0")
    suspend fun getUnsyncedCommitments(): List<CommitmentEntity>

    @Query("UPDATE commitments SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
