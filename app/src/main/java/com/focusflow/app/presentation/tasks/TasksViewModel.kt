package com.focusflow.app.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.model.TaskFilter
import com.focusflow.app.domain.model.TaskPriority
import com.focusflow.app.domain.model.TaskSortOrder
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val sortOrder: TaskSortOrder = TaskSortOrder.DUE_DATE,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        val userId = authRepository.getCurrentUser()?.id ?: ""
        viewModelScope.launch {
            taskRepository.getAllTasks(userId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error loading tasks") }
                }
                .collect { allTasks ->
                    val state = _uiState.value
                    val filteredAndSorted = applyFiltersAndSort(
                        allTasks, state.selectedFilter, state.sortOrder, state.searchQuery
                    )
                    _uiState.update { it.copy(tasks = filteredAndSorted, isLoading = false) }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        refreshTasks()
    }

    fun onFilterSelect(filter: TaskFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        refreshTasks()
    }

    fun onSortOrderSelect(sortOrder: TaskSortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
        refreshTasks()
    }

    fun toggleTaskCompletion(task: Task, isCompleted: Boolean) {
        viewModelScope.launch {
            if (isCompleted) {
                taskRepository.completeTask(task.id)
            } else {
                taskRepository.restoreTask(task.id)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task.id)
        }
    }

    private fun refreshTasks() {
        val userId = authRepository.getCurrentUser()?.id ?: ""
        viewModelScope.launch {
            val allTasks = taskRepository.getAllTasks(userId).first()
            val state = _uiState.value
            val filteredAndSorted = applyFiltersAndSort(allTasks, state.selectedFilter, state.sortOrder, state.searchQuery)
            _uiState.update { it.copy(tasks = filteredAndSorted) }
        }
    }

    private fun applyFiltersAndSort(
        tasks: List<Task>,
        filter: TaskFilter,
        sortOrder: TaskSortOrder,
        query: String
    ): List<Task> {
        var result = tasks
        if (query.isNotBlank()) {
            result = result.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
        }
        result = when (filter) {
            TaskFilter.ALL -> result
            TaskFilter.TODAY -> result.filter { !it.isCompleted }
            TaskFilter.UPCOMING -> result.filter { !it.isCompleted && it.dueDate != null }
            TaskFilter.COMPLETED -> result.filter { it.isCompleted }
            TaskFilter.HIGH_PRIORITY -> result.filter { it.priority == TaskPriority.HIGH || it.priority == TaskPriority.URGENT }
        }
        result = when (sortOrder) {
            TaskSortOrder.DUE_DATE -> result.sortedBy { it.dueDate ?: Long.MAX_VALUE }
            TaskSortOrder.PRIORITY -> result.sortedByDescending { it.priority.ordinal }
            TaskSortOrder.CREATED_DATE -> result.sortedByDescending { it.createdAt }
            TaskSortOrder.TITLE -> result.sortedBy { it.title }
        }
        return result
    }
}
