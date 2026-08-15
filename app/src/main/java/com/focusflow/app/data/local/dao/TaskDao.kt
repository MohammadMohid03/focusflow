package com.focusflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.focusflow.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllTasks(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: String): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND category = :category ORDER BY createdAt DESC")
    fun getTasksByCategory(userId: String, category: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 0 ORDER BY dueDate ASC")
    fun getIncompleteTasks(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND dueDate >= :startOfDay AND dueDate <= :endOfDay ORDER BY dueDate ASC")
    fun getTasksDueToday(userId: String, startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND dueDate > :now ORDER BY dueDate ASC")
    fun getUpcomingTasks(userId: String, now: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 0 AND (priority = 'URGENT' OR priority = 'HIGH') ORDER BY priority ASC, dueDate ASC")
    fun getHighPriorityTasks(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')")
    fun searchTasks(userId: String, query: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE goalId = :goalId")
    fun getTasksForGoal(goalId: String): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 1")
    fun getCompletedTasksCount(userId: String): Flow<Int>

    @Query("SELECT * FROM tasks WHERE isSynced = 0")
    suspend fun getUnsyncedTasks(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("UPDATE tasks SET isCompleted = 1, completedAt = :completedAt, updatedAt = :updatedAt, isSynced = 0 WHERE id = :taskId")
    suspend fun completeTask(taskId: String, completedAt: Long, updatedAt: Long)

    @Query("UPDATE tasks SET isCompleted = 0, completedAt = NULL, updatedAt = :updatedAt, isSynced = 0 WHERE id = :taskId")
    suspend fun restoreTask(taskId: String, updatedAt: Long)

    @Query("UPDATE tasks SET isSynced = 1 WHERE id = :taskId")
    suspend fun markSynced(taskId: String)
}
