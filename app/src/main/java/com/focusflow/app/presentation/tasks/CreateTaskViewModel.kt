package com.focusflow.app.presentation.tasks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.focusflow.app.domain.model.*
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.CommitmentRepository
import com.focusflow.app.domain.repository.GoalRepository
import com.focusflow.app.domain.repository.TaskRepository
import com.focusflow.app.service.AppRestrictionManager
import com.focusflow.app.service.CommitmentDeadlineWorker
import com.focusflow.app.service.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val selectedCategory: TaskCategory = TaskCategory.STUDY,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTime: LocalTime = LocalTime.now(),
    val estimatedDurationMinutes: Int = 10,
    val customDurationInput: String = "10",
    val subtasks: List<Subtask> = emptyList(),
    val commitmentEnabled: Boolean = false,
    val availableApps: List<RestrictableApp> = emptyList(),
    val selectedAppPackages: Set<String> = emptySet(),
    val appSearchQuery: String = "",
    val hasUsagePermission: Boolean = true,
    val hasOverlayPermission: Boolean = true,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val availableGoals: List<Goal> = emptyList()
)

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val commitmentRepository: CommitmentRepository,
    private val appRestrictionManager: AppRestrictionManager,
    private val notificationHelper: NotificationHelper,
    private val goalRepository: GoalRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    init {
        loadAvailableApps()
        checkPermissions()
    }

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title, error = null) }
    fun updateDescription(desc: String) = _uiState.update { it.copy(description = desc) }
    fun updateCategory(cat: TaskCategory) = _uiState.update { it.copy(selectedCategory = cat) }
    fun updatePriority(priority: TaskPriority) = _uiState.update { it.copy(priority = priority) }

    fun selectDate(date: LocalDate) {
        val today = LocalDate.now()
        val validDate = if (date.isBefore(today)) today else date
        _uiState.update { it.copy(selectedDate = validDate) }
    }

    fun selectTime(time: LocalTime) {
        _uiState.update { it.copy(selectedTime = time) }
    }

    fun updateDuration(duration: Int) {
        _uiState.update { 
            it.copy(
                estimatedDurationMinutes = duration, 
                customDurationInput = duration.toString(),
                error = null
            ) 
        }
    }

    fun updateCustomDurationInput(input: String) {
        val filtered = input.filter { it.isDigit() }
        val duration = filtered.toIntOrNull() ?: 0
        _uiState.update {
            it.copy(
                customDurationInput = filtered,
                estimatedDurationMinutes = if (duration > 0) duration else it.estimatedDurationMinutes,
                error = null
            )
        }
    }

    fun toggleCommitment(enabled: Boolean) {
        _uiState.update { it.copy(commitmentEnabled = enabled) }
        if (enabled) {
            checkPermissions()
            loadAvailableApps()
        }
    }

    fun loadAvailableApps() {
        viewModelScope.launch {
            try {
                val apps = appRestrictionManager.getAvailableApps()
                _uiState.update { it.copy(availableApps = apps) }
            } catch (e: Exception) {
                // Fallback
            }
        }
    }

    fun checkPermissions() {
        viewModelScope.launch {
            val hasUsage = appRestrictionManager.hasUsagePermission()
            val hasOverlay = appRestrictionManager.hasOverlayPermission()
            _uiState.update { 
                it.copy(
                    hasUsagePermission = hasUsage,
                    hasOverlayPermission = hasOverlay
                ) 
            }
        }
    }

    fun requestUsagePermission() {
        viewModelScope.launch {
            appRestrictionManager.requestPermissionSetup()
        }
    }

    fun requestOverlayPermission() {
        viewModelScope.launch {
            appRestrictionManager.requestOverlayPermission()
        }
    }

    fun toggleAppSelection(packageName: String) {
        _uiState.update { state ->
            val set = state.selectedAppPackages.toMutableSet()
            if (set.contains(packageName)) set.remove(packageName) else set.add(packageName)
            state.copy(selectedAppPackages = set)
        }
    }

    fun updateAppSearchQuery(query: String) {
        _uiState.update { it.copy(appSearchQuery = query) }
    }

    fun saveTask() {
        val state = _uiState.value
        val title = state.title.trim()
        if (title.isBlank()) {
            _uiState.update { it.copy(error = "Task title is required.") }
            return
        }

        val duration = state.customDurationInput.toIntOrNull() ?: state.estimatedDurationMinutes
        if (duration <= 0) {
            _uiState.update { it.copy(error = "Duration must be greater than 0.") }
            return
        }

        if (state.isLoading) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val userId = authRepository.getCurrentUser()?.id ?: ""
                val startDateTime = state.selectedDate.atTime(state.selectedTime)
                val dueDateTime = startDateTime.plusMinutes(duration.toLong())
                val epochDue = dueDateTime
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                val taskId = UUID.randomUUID().toString()
                val task = Task(
                    id = taskId,
                    title = title,
                    description = state.description.trim(),
                    priority = state.priority,
                    category = state.selectedCategory,
                    dueDate = epochDue,
                    subtasks = state.subtasks,
                    estimatedDurationMinutes = duration,
                    userId = userId
                )
                taskRepository.insertTask(task)

                // If Commitment Lock was enabled, create and activate the Commitment
                if (state.commitmentEnabled) {
                    val now = System.currentTimeMillis()
                    val commitment = Commitment(
                        id = UUID.randomUUID().toString(),
                        taskId = taskId,
                        deadline = epochDue,
                        estimatedDurationMinutes = duration,
                        status = CommitmentStatus.ACTIVE,
                        consequenceType = ConsequenceType.COMMITMENT_LOCK,
                        selectedAppPackages = state.selectedAppPackages.toList(),
                        createdAt = now,
                        activatedAt = now,
                        userId = userId
                    )
                    commitmentRepository.insertCommitment(commitment)

                    // Enable app restrictions
                    if (commitment.selectedAppPackages.isNotEmpty()) {
                        appRestrictionManager.enableRestriction(
                            apps = commitment.selectedAppPackages,
                            reason = "Commitment Active: $title"
                        )
                    }

                    // Show activation notification with exact DUE / DEADLINE time
                    val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a")
                    notificationHelper.showCommitmentActivated(
                        taskName = title,
                        deadline = dueDateTime.format(timeFormatter)
                    )

                    // Schedule deadline and warning worker
                    scheduleCommitmentWork(commitment)
                }

                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to save task. Please try again.") }
            }
        }
    }

    private fun scheduleCommitmentWork(commitment: Commitment) {
        val now = System.currentTimeMillis()
        val totalDelayMillis = maxOf(0L, commitment.deadline - now)

        val warningRemainingMillis = when {
            totalDelayMillis >= 35 * 60 * 1000L -> 10 * 60 * 1000L
            totalDelayMillis >= 12 * 60 * 1000L -> 5 * 60 * 1000L
            totalDelayMillis >= 3 * 60 * 1000L -> totalDelayMillis / 2
            else -> 0L
        }

        if (warningRemainingMillis > 0L) {
            val warningDelay = maxOf(0L, totalDelayMillis - warningRemainingMillis)
            val minsRemaining = maxOf(1, (warningRemainingMillis / 60000L).toInt())
            val warningRequest = OneTimeWorkRequestBuilder<CommitmentDeadlineWorker>()
                .setInitialDelay(warningDelay, TimeUnit.MILLISECONDS)
                .setInputData(
                    Data.Builder()
                        .putString(CommitmentDeadlineWorker.KEY_COMMITMENT_ID, commitment.id)
                        .putString(CommitmentDeadlineWorker.KEY_NOTIFICATION_TYPE, CommitmentDeadlineWorker.TYPE_WARNING)
                        .putInt(CommitmentDeadlineWorker.KEY_MINUTES_REMAINING, minsRemaining)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueue(warningRequest)
        }

        val deadlineRequest = OneTimeWorkRequestBuilder<CommitmentDeadlineWorker>()
            .setInitialDelay(totalDelayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                Data.Builder()
                    .putString(CommitmentDeadlineWorker.KEY_COMMITMENT_ID, commitment.id)
                    .putString(CommitmentDeadlineWorker.KEY_NOTIFICATION_TYPE, CommitmentDeadlineWorker.TYPE_DEADLINE)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(deadlineRequest)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
