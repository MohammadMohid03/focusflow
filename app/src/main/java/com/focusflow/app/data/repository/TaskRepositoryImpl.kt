package com.focusflow.app.data.repository

import com.focusflow.app.data.local.dao.CommitmentDao
import com.focusflow.app.data.local.dao.SubtaskDao
import com.focusflow.app.data.local.dao.TaskDao
import com.focusflow.app.data.local.mapper.toDomain
import com.focusflow.app.data.local.mapper.toEntity
import com.focusflow.app.di.IoDispatcher
import com.focusflow.app.domain.model.CommitmentStatus
import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.model.TaskFilter
import com.focusflow.app.domain.repository.TaskRepository
import com.focusflow.app.service.AppRestrictionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val subtaskDao: SubtaskDao,
    private val commitmentDao: CommitmentDao,
    private val appRestrictionManager: AppRestrictionManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TaskRepository {

    override fun getAllTasks(userId: String): Flow<List<Task>> {
        return taskDao.getAllTasks(userId).map { entities ->
            entities.map { taskEntity ->
                taskEntity.toDomain(emptyList())
            }
        }
    }

    override fun getTaskById(taskId: String): Flow<Task?> {
        return combine(
            taskDao.getTaskById(taskId),
            subtaskDao.getSubtasksForTask(taskId)
        ) { taskEntity, subtasks ->
            taskEntity?.toDomain(subtasks)
        }
    }

    override fun getTasksByFilter(userId: String, filter: TaskFilter): Flow<List<Task>> {
        return when (filter) {
            TaskFilter.ALL -> getAllTasks(userId)
            TaskFilter.TODAY -> getTasksDueToday(userId)
            TaskFilter.UPCOMING -> taskDao.getUpcomingTasks(userId, System.currentTimeMillis()).map { list -> list.map { it.toDomain(emptyList()) } }
            TaskFilter.COMPLETED -> taskDao.getCompletedTasks(userId).map { list -> list.map { it.toDomain(emptyList()) } }
            TaskFilter.HIGH_PRIORITY -> taskDao.getHighPriorityTasks(userId).map { list -> list.map { it.toDomain(emptyList()) } }
            TaskFilter.OVERDUE -> getAllTasks(userId).map { list -> list.filter { it.isOverdue } }
        }
    }

    override fun searchTasks(userId: String, query: String): Flow<List<Task>> {
        return taskDao.searchTasks(userId, query).map { list -> list.map { it.toDomain(emptyList()) } }
    }

    override suspend fun insertTask(task: Task) = withContext(ioDispatcher) {
        taskDao.insertTask(task.toEntity())
        task.subtasks.forEach { subtask ->
            subtaskDao.insertSubtask(subtask.toEntity(task.id))
        }
    }

    override suspend fun updateTask(task: Task) = withContext(ioDispatcher) {
        taskDao.updateTask(task.toEntity())
        subtaskDao.deleteSubtasksForTask(task.id)
        task.subtasks.forEach { subtask ->
            subtaskDao.insertSubtask(subtask.toEntity(task.id))
        }
        if (task.isCompleted) {
            completeTaskCommitments(task.id)
        }
    }

    override suspend fun deleteTask(taskId: String) = withContext(ioDispatcher) {
        taskDao.deleteTask(taskId)
        cancelTaskCommitments(taskId)
    }

    override suspend fun completeTask(taskId: String) = withContext(ioDispatcher) {
        val now = System.currentTimeMillis()
        taskDao.completeTask(taskId, now, now)
        completeTaskCommitments(taskId)
    }

    private suspend fun completeTaskCommitments(taskId: String) {
        val now = System.currentTimeMillis()
        val commitments = commitmentDao.getCommitmentsForTaskSync(taskId).map { it.toDomain() }
        commitments.forEach { commitment ->
            if (commitment.status != CommitmentStatus.COMPLETED && commitment.status != CommitmentStatus.CANCELLED) {
                val updated = commitment.copy(
                    status = CommitmentStatus.COMPLETED,
                    completedAt = now
                )
                commitmentDao.update(updated.toEntity())
            }
        }

        val remainingActive = commitmentDao.getAllActiveCommitmentsSync().map { it.toDomain() }
        val remainingActiveApps = remainingActive.flatMap { it.selectedAppPackages }.toSet()
        val allUnlocked = commitments.flatMap { it.selectedAppPackages }.filter { !remainingActiveApps.contains(it) }
        appRestrictionManager.disableRestriction(allUnlocked)
    }

    private suspend fun cancelTaskCommitments(taskId: String) {
        val now = System.currentTimeMillis()
        val commitments = commitmentDao.getCommitmentsForTaskSync(taskId).map { it.toDomain() }
        commitments.forEach { commitment ->
            if (commitment.status != CommitmentStatus.COMPLETED && commitment.status != CommitmentStatus.CANCELLED) {
                val updated = commitment.copy(
                    status = CommitmentStatus.CANCELLED,
                    cancelledAt = now
                )
                commitmentDao.update(updated.toEntity())
            }
        }

        val remainingActive = commitmentDao.getAllActiveCommitmentsSync().map { it.toDomain() }
        val remainingActiveApps = remainingActive.flatMap { it.selectedAppPackages }.toSet()
        val allUnlocked = commitments.flatMap { it.selectedAppPackages }.filter { !remainingActiveApps.contains(it) }
        appRestrictionManager.disableRestriction(allUnlocked)
    }

    override suspend fun restoreTask(taskId: String) = withContext(ioDispatcher) {
        taskDao.restoreTask(taskId, System.currentTimeMillis())
    }

    override fun getTasksForGoal(goalId: String): Flow<List<Task>> {
        return taskDao.getTasksForGoal(goalId).map { list -> list.map { it.toDomain(emptyList()) } }
    }

    override fun getTasksDueToday(userId: String): Flow<List<Task>> {
        val zoneId = java.time.ZoneId.systemDefault()
        val today = java.time.LocalDate.now(zoneId)
        val startOfDay = today.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = today.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
        return taskDao.getTasksDueToday(userId, startOfDay, endOfDay).map { list -> list.map { it.toDomain(emptyList()) } }
    }

    override fun getCompletedTasksCount(userId: String): Flow<Int> {
        return taskDao.getCompletedTasksCount(userId)
    }
}
