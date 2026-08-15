package com.focusflow.app.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.*
import com.focusflow.app.domain.repository.GoalRepository
import com.focusflow.app.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val selectedCategory: TaskCategory = TaskCategory.OTHER,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Long? = null,
    val estimatedDurationMinutes: Int = 30,
    val subtasks: List<Subtask> = emptyList(),
    val commitmentEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val availableGoals: List<Goal> = emptyList()
)

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun updateDescription(desc: String) = _uiState.update { it.copy(description = desc) }
    fun updateCategory(cat: TaskCategory) = _uiState.update { it.copy(selectedCategory = cat) }
    fun updatePriority(priority: TaskPriority) = _uiState.update { it.copy(priority = priority) }
    fun updateDueDate(date: Long?) = _uiState.update { it.copy(dueDate = date) }
    fun updateDuration(duration: Int) = _uiState.update { it.copy(estimatedDurationMinutes = duration) }
    fun toggleCommitment(enabled: Boolean) = _uiState.update { it.copy(commitmentEnabled = enabled) }

    fun addSubtask(title: String) {
        if (title.isBlank()) return
        val newSubtask = Subtask(id = UUID.randomUUID().toString(), title = title, isCompleted = false)
        _uiState.update { it.copy(subtasks = it.subtasks + newSubtask) }
    }

    fun removeSubtask(id: String) {
        _uiState.update { state -> 
            state.copy(subtasks = state.subtasks.filter { it.id != id }) 
        }
    }

    fun saveTask() {
        if (_uiState.value.title.isBlank()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val task = Task(
                id = UUID.randomUUID().toString(),
                title = _uiState.value.title,
                description = _uiState.value.description,
                priority = _uiState.value.priority,
                category = _uiState.value.selectedCategory,
                dueDate = _uiState.value.dueDate,
                subtasks = _uiState.value.subtasks,
                estimatedDurationMinutes = _uiState.value.estimatedDurationMinutes
            )
            taskRepository.insertTask(task)
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
