package com.focusflow.app.domain.repository

import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.model.TaskFilter
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(userId: String): Flow<List<Task>>
    fun getTaskById(taskId: String): Flow<Task?>
    fun getTasksByFilter(userId: String, filter: TaskFilter): Flow<List<Task>>
    fun searchTasks(userId: String, query: String): Flow<List<Task>>
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)
    suspend fun completeTask(taskId: String)
    suspend fun restoreTask(taskId: String)
    fun getTasksForGoal(goalId: String): Flow<List<Task>>
    fun getTasksDueToday(userId: String): Flow<List<Task>>
    fun getCompletedTasksCount(userId: String): Flow<Int>
}
