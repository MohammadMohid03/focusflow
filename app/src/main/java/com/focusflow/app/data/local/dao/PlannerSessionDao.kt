package com.focusflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.focusflow.app.data.local.entity.PlannerSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerSessionDao {
    @Query("SELECT * FROM planner_sessions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllSessions(userId: String): Flow<List<PlannerSessionEntity>>

    @Query("SELECT * FROM planner_sessions WHERE id = :id")
    fun getById(id: String): Flow<PlannerSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: PlannerSessionEntity)

    @Update
    suspend fun update(session: PlannerSessionEntity)

    @Query("DELETE FROM planner_sessions WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM planner_sessions WHERE isSynced = 0")
    suspend fun getUnsyncedSessions(): List<PlannerSessionEntity>

    @Query("UPDATE planner_sessions SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
