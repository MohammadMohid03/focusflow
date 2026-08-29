package com.focusflow.app.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.PlannerDay
import com.focusflow.app.domain.model.PlannerSession
import com.focusflow.app.domain.model.PlannerTask
import com.focusflow.app.domain.model.PlannerTaskType
import com.focusflow.app.domain.model.Subtask
import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.model.TaskCategory
import com.focusflow.app.domain.model.TaskPriority
import com.focusflow.app.domain.repository.AiRepository
import com.focusflow.app.domain.repository.PlannerRepository
import com.focusflow.app.domain.repository.SuggestedScheduleSession
import com.focusflow.app.domain.repository.SuggestedSubtask
import com.focusflow.app.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AiUiState(
    val isGenerating: Boolean = false,
    val breakdownPrompt: String = "",
    val suggestedSubtasks: List<SuggestedSubtask> = emptyList(),
    val isTaskSaved: Boolean = false,
    
    val plannerHours: Int = 3,
    val suggestedSchedule: List<SuggestedScheduleSession> = emptyList(),
    val isScheduleSaved: Boolean = false,
    
    val error: String? = null
)

@HiltViewModel
class AiViewModel @Inject constructor(
    private val aiRepository: AiRepository,
    private val taskRepository: TaskRepository,
    private val plannerRepository: PlannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    fun onBreakdownPromptChanged(prompt: String) {
        _uiState.update { it.copy(breakdownPrompt = prompt, isTaskSaved = false) }
    }

    fun onPlannerHoursChanged(hours: Int) {
        _uiState.update { it.copy(plannerHours = hours, isScheduleSaved = false) }
    }

    fun generateTaskBreakdown() {
        val prompt = _uiState.value.breakdownPrompt.trim()
        if (prompt.isBlank()) return

        _uiState.update { it.copy(isGenerating = true, error = null, isTaskSaved = false) }
        viewModelScope.launch {
            val result = aiRepository.breakdownGoalOrTask(prompt)
            result.onSuccess { subtasks ->
                _uiState.update { it.copy(isGenerating = false, suggestedSubtasks = subtasks) }
            }.onFailure { err ->
                _uiState.update { it.copy(isGenerating = false, error = err.message) }
            }
        }
    }

    fun saveGeneratedTasksAsParentTask(parentTitle: String) {
        val subtasks = _uiState.value.suggestedSubtasks
        if (subtasks.isEmpty()) return

        viewModelScope.launch {
            val taskId = UUID.randomUUID().toString()
            val domainSubtasks = subtasks.map { s ->
                Subtask(
                    id = UUID.randomUUID().toString(),
                    title = s.title,
                    isCompleted = false
                )
            }
            val totalMinutes = subtasks.sumOf { it.estimatedMinutes }
            val highestPriority = subtasks.maxByOrNull { it.priority.ordinal }?.priority ?: TaskPriority.MEDIUM

            val task = Task(
                id = taskId,
                title = parentTitle.ifBlank { _uiState.value.breakdownPrompt },
                description = "AI-generated action plan (${subtasks.size} subtasks)",
                category = TaskCategory.STUDY,
                priority = highestPriority,
                estimatedDurationMinutes = totalMinutes,
                subtasks = domainSubtasks
            )

            taskRepository.insertTask(task)
            _uiState.update { it.copy(isTaskSaved = true) }
        }
    }

    fun generateDailySchedule() {
        _uiState.update { it.copy(isGenerating = true, error = null, isScheduleSaved = false) }
        viewModelScope.launch {
            val existingTasks = try {
                taskRepository.getAllTasks("").first().filter { !it.isCompleted }.map { it.title }
            } catch (e: Exception) {
                emptyList()
            }

            val result = aiRepository.generateDailySchedule(existingTasks, _uiState.value.plannerHours)
            result.onSuccess { schedule ->
                _uiState.update { it.copy(isGenerating = false, suggestedSchedule = schedule) }
            }.onFailure { err ->
                _uiState.update { it.copy(isGenerating = false, error = err.message) }
            }
        }
    }

    fun saveScheduleToPlanner() {
        val schedule = _uiState.value.suggestedSchedule
        if (schedule.isEmpty()) return

        viewModelScope.launch {
            val plannerTasks = schedule.map { s ->
                PlannerTask(
                    title = s.title,
                    description = "${s.category} block starting at ${s.startTime}",
                    durationMinutes = s.durationMinutes,
                    type = when (s.category.uppercase()) {
                        "REVIEW", "REVISION" -> PlannerTaskType.REVISION
                        "BREAK" -> PlannerTaskType.BREAK
                        "PROJECT" -> PlannerTaskType.PROJECT
                        else -> PlannerTaskType.STUDY
                    },
                    isCompleted = false
                )
            }
            val plannerDay = PlannerDay(
                date = System.currentTimeMillis(),
                sessions = plannerTasks
            )
            val plannerSession = PlannerSession(
                id = UUID.randomUUID().toString(),
                goal = "AI Daily Schedule (${_uiState.value.plannerHours} hours)",
                deadline = System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
                availableHoursPerDay = _uiState.value.plannerHours.toFloat(),
                generatedPlan = listOf(plannerDay)
            )
            plannerRepository.insertSession(plannerSession)
            _uiState.update { it.copy(isScheduleSaved = true) }
        }
    }
}
