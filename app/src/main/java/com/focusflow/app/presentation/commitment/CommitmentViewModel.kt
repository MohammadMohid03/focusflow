package com.focusflow.app.presentation.commitment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.focusflow.app.domain.model.*
import com.focusflow.app.domain.repository.AuthRepository
import com.focusflow.app.domain.repository.CommitmentRepository
import com.focusflow.app.domain.repository.TaskRepository
import com.focusflow.app.service.AppRestrictionManager
import com.focusflow.app.service.CommitmentDeadlineWorker
import com.focusflow.app.service.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class CommitmentUiState(
    val isLoading: Boolean = false,
    val activeCommitments: List<Commitment> = emptyList(),
    val allCommitments: List<Commitment> = emptyList(),
    val availableApps: List<RestrictableApp> = emptyList(),
    val selectedAppPackages: Set<String> = emptySet(),
    val searchQuery: String = "",
    val hasUsagePermission: Boolean = true,
    val draftTaskId: String = "",
    val draftTaskTitle: String = "Complete Task",
    val draftDeadline: Long = System.currentTimeMillis() + 7200000,
    val draftDurationMinutes: Int = 45,
    val error: String? = null
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommitmentViewModel @Inject constructor(
    private val commitmentRepository: CommitmentRepository,
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository,
    private val appRestrictionManager: AppRestrictionManager,
    private val notificationHelper: NotificationHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommitmentUiState())
    val uiState: StateFlow<CommitmentUiState> = _uiState.asStateFlow()

    private val userId: String
        get() = authRepository.getCurrentUser()?.id ?: ""

    init {
        loadCommitments()
        checkPermission()
        loadAvailableApps()
    }

    private fun loadCommitments() {
        viewModelScope.launch {
            authRepository.observeAuthState().flatMapLatest { user ->
                val uId = user?.id ?: ""
                combine(
                    commitmentRepository.getActiveCommitments(uId),
                    commitmentRepository.getAllCommitments(uId)
                ) { active, all ->
                    Pair(active, all)
                }
            }.collect { (active, all) ->
                _uiState.update { it.copy(activeCommitments = active, allCommitments = all, isLoading = false) }
            }
        }
    }

    fun checkPermission() {
        viewModelScope.launch {
            val hasPerm = appRestrictionManager.hasRequiredPermission()
            _uiState.update { it.copy(hasUsagePermission = hasPerm) }
        }
    }

    fun requestUsagePermission() {
        viewModelScope.launch {
            appRestrictionManager.requestPermissionSetup()
        }
    }

    fun loadAvailableApps() {
        viewModelScope.launch {
            try {
                val apps = appRestrictionManager.getAvailableApps()
                _uiState.update { it.copy(availableApps = apps) }
            } catch (e: Exception) {
                // Fallback to empty list
            }
        }
    }

    fun initDraft(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(draftTaskId = taskId) }
            val task = taskRepository.getTaskById(taskId).firstOrNull()
            if (task != null) {
                val deadline = task.dueDate ?: (System.currentTimeMillis() + 7200000)
                val duration = task.estimatedDurationMinutes ?: 45
                _uiState.update { 
                    it.copy(
                        draftTaskId = task.id,
                        draftTaskTitle = task.title,
                        draftDeadline = deadline,
                        draftDurationMinutes = duration
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleAppSelection(packageName: String) {
        _uiState.update { state ->
            val updated = state.selectedAppPackages.toMutableSet()
            if (updated.contains(packageName)) {
                updated.remove(packageName)
            } else {
                updated.add(packageName)
            }
            state.copy(selectedAppPackages = updated)
        }
    }

    fun setSelectedApps(apps: Set<String>) {
        _uiState.update { it.copy(selectedAppPackages = apps) }
    }

    fun updateDraftDeadline(deadline: Long) {
        _uiState.update { it.copy(draftDeadline = deadline) }
    }

    fun updateDraftDuration(duration: Int) {
        _uiState.update { it.copy(draftDurationMinutes = duration) }
    }

    fun activateCommitment(onSuccess: () -> Unit) {
        val state = _uiState.value
        val now = System.currentTimeMillis()
        val commitment = Commitment(
            id = UUID.randomUUID().toString(),
            taskId = state.draftTaskId.ifBlank { UUID.randomUUID().toString() },
            deadline = state.draftDeadline,
            estimatedDurationMinutes = state.draftDurationMinutes,
            status = CommitmentStatus.ACTIVE,
            consequenceType = ConsequenceType.COMMITMENT_LOCK,
            selectedAppPackages = state.selectedAppPackages.toList(),
            createdAt = now,
            activatedAt = now,
            userId = userId
        )

        viewModelScope.launch {
            try {
                // 1. Save commitment in Room
                commitmentRepository.insertCommitment(commitment)

                // 2. Enable app restrictions if apps selected
                if (commitment.selectedAppPackages.isNotEmpty()) {
                    appRestrictionManager.enableRestriction(
                        apps = commitment.selectedAppPackages,
                        reason = "Commitment Active: ${state.draftTaskTitle}"
                    )
                }

                // 3. Show instant confirmation notification
                val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a")
                val deadlineTimeStr = java.time.Instant.ofEpochMilli(commitment.deadline)
                    .atZone(java.time.ZoneId.systemDefault())
                    .format(timeFormatter)

                notificationHelper.showCommitmentActivated(
                    taskName = state.draftTaskTitle,
                    deadline = deadlineTimeStr
                )

                // 4. Schedule WorkManager warning and deadline workers
                scheduleCommitmentWork(commitment)

                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to activate commitment") }
            }
        }
    }

    private fun scheduleCommitmentWork(commitment: Commitment) {
        val now = System.currentTimeMillis()
        val totalDelayMillis = maxOf(0L, commitment.deadline - now)

        // 1. Schedule warning notification
        val warningRemainingMillis = when {
            totalDelayMillis >= 35 * 60 * 1000L -> 10 * 60 * 1000L // 10 mins before deadline
            totalDelayMillis >= 12 * 60 * 1000L -> 5 * 60 * 1000L  // 5 mins before deadline
            totalDelayMillis >= 3 * 60 * 1000L -> totalDelayMillis / 2 // halfway point
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

        // 2. Schedule deadline worker
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

    fun cancelCommitment(commitmentId: String) {
        viewModelScope.launch {
            try {
                val commitment = commitmentRepository.getCommitmentById(commitmentId).firstOrNull()
                commitmentRepository.deleteCommitment(commitmentId)
                if (commitment != null && commitment.selectedAppPackages.isNotEmpty()) {
                    appRestrictionManager.disableRestriction(commitment.selectedAppPackages)
                } else {
                    appRestrictionManager.disableRestriction(emptyList())
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
