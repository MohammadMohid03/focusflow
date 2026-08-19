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
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    private val _sort = MutableStateFlow(TaskSortOrder.DUE_DATE)
    private val _query = MutableStateFlow("")

    val uiState: StateFlow<TasksUiState> = combine(
        authRepository.observeAuthState(),
        _filter,
        _sort,
        _query
    ) { _, filter, sort, query ->
        val userId = authRepository.getCurrentUser()?.id ?: ""
        Triple(userId, filter, Pair(sort, query))
    }.flatMapLatest { (userId, filter, sortAndQuery) ->
        val (sort, query) = sortAndQuery
        taskRepository.getAllTasks(userId).map { allTasks ->
            val filteredAndSorted = applyFiltersAndSort(allTasks, filter, sort, query)
            TasksUiState(
                tasks = filteredAndSorted,
                selectedFilter = filter,
                sortOrder = sort,
                searchQuery = query,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksUiState(isLoading = false)
    )

    fun onSearchQueryChange(query: String) {
        _query.value = query
    }

    fun onFilterSelect(filter: TaskFilter) {
        _filter.value = filter
    }

    fun onSortOrderSelect(sortOrder: TaskSortOrder) {
        _sort.value = sortOrder
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

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
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
            result = result.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.description.contains(query, ignoreCase = true) ||
                it.category.name.contains(query, ignoreCase = true)
            }
        }
        val now = System.currentTimeMillis()
        result = when (filter) {
            TaskFilter.ALL -> result
            TaskFilter.TODAY -> result.filter { 
                !it.isCompleted && (it.dueDate == null || it.dueDate <= now + 86400000)
            }
            TaskFilter.UPCOMING -> result.filter { !it.isCompleted && it.dueDate != null && it.dueDate > now }
            TaskFilter.COMPLETED -> result.filter { it.isCompleted }
            TaskFilter.HIGH_PRIORITY -> result.filter { it.priority == TaskPriority.HIGH || it.priority == TaskPriority.URGENT }
            TaskFilter.OVERDUE -> result.filter { it.isOverdue }
        }
        result = when (sortOrder) {
            TaskSortOrder.DUE_DATE -> result.sortedWith(
                compareBy<Task> { it.isCompleted }
                    .thenBy { it.dueDate ?: Long.MAX_VALUE }
                    .thenByDescending { it.priority.ordinal }
            )
            TaskSortOrder.PRIORITY -> result.sortedWith(
                compareBy<Task> { it.isCompleted }
                    .thenByDescending { it.priority.ordinal }
                    .thenBy { it.dueDate ?: Long.MAX_VALUE }
            )
            TaskSortOrder.CREATED_DATE -> result.sortedByDescending { it.createdAt }
            TaskSortOrder.TITLE -> result.sortedBy { it.title.lowercase() }
        }
        return result
    }
}
