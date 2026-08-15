package com.focusflow.app.presentation.goals

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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
) : ViewModel() {

    private val _goals = MutableStateFlow<List<GoalUiModel>>(
        listOf(
            GoalUiModel(
                id = "1",
                title = "Learn Jetpack Compose",
                descriptionPreview = "Master the modern Android UI toolkit.",
                targetDate = "Dec 31, 2026",
                linkedTasksCount = 5,
                progress = 0.4f,
                linkedTasks = listOf(
                    TaskStub("t1", "Read documentation", true),
                    TaskStub("t2", "Build sample app", false)
                )
            ),
            GoalUiModel(
                id = "2",
                title = "Fitness Challenge",
                descriptionPreview = "Run 100km this month.",
                targetDate = "Aug 31, 2026",
                linkedTasksCount = 10,
                progress = 0.8f,
                linkedTasks = listOf(
                    TaskStub("t3", "Morning run", true)
                )
            )
        )
    )
    val goals: StateFlow<List<GoalUiModel>> = _goals.asStateFlow()

    fun getGoal(id: String): Flow<GoalUiModel?> {
        return _goals.map { list -> list.find { it.id == id } }
    }

    fun createGoal(title: String, description: String, targetDate: String, linkedTaskIds: List<String>) {
        val newGoal = GoalUiModel(
            id = UUID.randomUUID().toString(),
            title = title,
            descriptionPreview = description,
            targetDate = targetDate,
            linkedTasksCount = linkedTaskIds.size,
            progress = 0f
        )
        _goals.value = _goals.value + newGoal
    }

    fun deleteGoal(id: String) {
        _goals.value = _goals.value.filterNot { it.id == id }
    }
}
