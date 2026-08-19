package com.focusflow.app.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.Goal
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TaskStub(val id: String, val title: String, val isCompleted: Boolean)

data class GoalUiModel(
    val id: String,
    val title: String,
    val descriptionPreview: String,
    val targetDate: String,
    val linkedTasksCount: Int,
    val progress: Float,
    val linkedTasks: List<TaskStub> = emptyList()
)

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _goals = MutableStateFlow<List<GoalUiModel>>(emptyList())
    val goals: StateFlow<List<GoalUiModel>> = _goals.asStateFlow()

    private val userId: String
        get() = authRepository.getCurrentUser()?.id ?: ""

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            goalRepository.getAllGoals(userId)
                .catch { /* Handle error */ }
                .collect { domainGoals ->
                    _goals.value = domainGoals.map { goal ->
                        GoalUiModel(
                            id = goal.id,
                            title = goal.title,
                            descriptionPreview = goal.description,
                            targetDate = goal.targetDate?.toString() ?: "",
                            linkedTasksCount = goal.linkedTaskIds.size,
                            progress = goal.progress,
                            linkedTasks = emptyList()
                        )
                    }
                }
        }
    }

    fun getGoal(id: String): Flow<GoalUiModel?> {
        return goalRepository.getGoalById(id).map { goal ->
            goal?.let {
                GoalUiModel(
                    id = it.id,
                    title = it.title,
                    descriptionPreview = it.description,
                    targetDate = it.targetDate?.toString() ?: "",
                    linkedTasksCount = it.linkedTaskIds.size,
                    progress = it.progress,
                    linkedTasks = emptyList()
                )
            }
        }
    }

    fun createGoal(title: String, description: String, targetDate: String, linkedTaskIds: List<String>) {
        viewModelScope.launch {
            try {
                val newGoal = Goal(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    userId = userId,
                    linkedTaskIds = linkedTaskIds
                )
                goalRepository.insertGoal(newGoal)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteGoal(id: String) {
        viewModelScope.launch {
            try {
                goalRepository.deleteGoal(id)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
