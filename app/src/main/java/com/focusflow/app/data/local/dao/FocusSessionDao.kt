package com.focusflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.focusflow.app.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions WHERE userId = :userId ORDER BY startTime DESC")
    fun getAllSessions(userId: String): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE id = :id")
    fun getById(id: String): Flow<FocusSessionEntity?>

    @Query("SELECT * FROM focus_sessions WHERE taskId = :taskId ORDER BY startTime DESC")
    fun getSessionsForTask(taskId: String): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId AND startTime >= :start AND startTime <= :end ORDER BY startTime ASC")
    fun getSessionsInRange(userId: String, start: Long, end: Long): Flow<List<FocusSessionEntity>>

    @Query("SELECT SUM(actualDurationMinutes) FROM focus_sessions WHERE userId = :userId AND isCompleted = 1")
    fun getTotalFocusMinutes(userId: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: FocusSessionEntity)

    @Update
    suspend fun update(session: FocusSessionEntity)

    @Query("DELETE FROM focus_sessions WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM focus_sessions WHERE isSynced = 0")
    suspend fun getUnsyncedSessions(): List<FocusSessionEntity>

    @Query("UPDATE focus_sessions SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
