package com.focusflow.app.presentation.planner

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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class PlannerScheduleSlot(
    val id: String,
    val taskId: String,
    val title: String,
    val category: TaskCategory,
    val priority: TaskPriority,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val isCompleted: Boolean,
    val isOverdue: Boolean,
    val hasConflict: Boolean = false
)

data class PlannerUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val slots: List<PlannerScheduleSlot> = emptyList(),
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val totalPlannedMinutes: Int = 0,
    val completedMinutes: Int = 0,
    val progressPercentage: Int = 0,
    val availableApps: List<RestrictableApp> = emptyList(),
    val selectedAppPackages: Set<String> = emptySet(),
    val hasUsagePermission: Boolean = true,
    val hasConflict: Boolean = false,
    val conflictMessage: String? = null,
    val isGeneratingPlan: Boolean = false,
    val isLoading: Boolean = false
)

private data class PlannerFilterParams(
    val userId: String,
    val date: LocalDate,
    val isGenerating: Boolean,
    val selectedApps: Set<String>,
    val apps: List<RestrictableApp>
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val commitmentRepository: CommitmentRepository,
    private val appRestrictionManager: AppRestrictionManager,
    private val notificationHelper: NotificationHelper,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    private val _selectedAppPackages = MutableStateFlow<Set<String>>(emptySet())
    private val _availableApps = MutableStateFlow<List<RestrictableApp>>(emptyList())
    private val _hasUsagePermission = MutableStateFlow(true)

    init {
        loadAvailableApps()
        checkPermission()
    }

    val uiState: StateFlow<PlannerUiState> = combine(
        authRepository.observeAuthState(),
        _selectedDate,
        _isGenerating,
        _selectedAppPackages,
        _availableApps
    ) { user, date, isGenerating, selectedApps, apps ->
        val userId = user?.id ?: ""
        PlannerFilterParams(userId, date, isGenerating, selectedApps, apps)
    }.flatMapLatest { params ->
        combine(
            taskRepository.getAllTasks(params.userId),
            _hasUsagePermission
        ) { allTasks, hasPerm ->
            val baseState = buildPlannerState(allTasks, params.date, params.isGenerating)
            baseState.copy(
                availableApps = params.apps,
                selectedAppPackages = params.selectedApps,
                hasUsagePermission = hasPerm
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlannerUiState()
    )

    fun selectDate(date: LocalDate) {
        val today = LocalDate.now()
        _selectedDate.value = if (date.isBefore(today)) today else date
    }

    fun nextWeek() {
        _selectedDate.value = _selectedDate.value.plusWeeks(1)
    }

    fun previousWeek() {
        val target = _selectedDate.value.minusWeeks(1)
        val today = LocalDate.now()
        _selectedDate.value = if (target.isBefore(today)) today else target
    }

    fun loadAvailableApps() {
        viewModelScope.launch {
            try {
                val apps = appRestrictionManager.getAvailableApps()
                _availableApps.value = apps
            } catch (e: Exception) {
                // Fallback
            }
        }
    }

    fun checkPermission() {
        viewModelScope.launch {
            _hasUsagePermission.value = appRestrictionManager.hasRequiredPermission()
        }
    }

    fun requestUsagePermission() {
        viewModelScope.launch {
            appRestrictionManager.requestPermissionSetup()
        }
    }

    fun toggleAppSelection(packageName: String) {
        val set = _selectedAppPackages.value.toMutableSet()
        if (set.contains(packageName)) set.remove(packageName) else set.add(packageName)
        _selectedAppPackages.value = set
    }

    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            if (isCompleted) {
                taskRepository.completeTask(taskId)
            } else {
                taskRepository.restoreTask(taskId)
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }

    fun generateStudyPlan() {
        viewModelScope.launch {
            _isGenerating.value = true
            kotlinx.coroutines.delay(300)
            _isGenerating.value = false
        }
    }

    fun createTaskForDate(
        title: String,
        description: String = "",
        category: TaskCategory = TaskCategory.STUDY,
        priority: TaskPriority = TaskPriority.MEDIUM,
        durationMinutes: Int = 45,
        startTime: LocalTime? = null,
        enableCommitmentLock: Boolean = false
    ) {
        if (title.isBlank()) return
        val date = _selectedDate.value
        val actualStartTime = startTime ?: if (date == LocalDate.now()) LocalTime.now() else LocalTime.of(9, 0)
        val dueDateTime = date.atTime(actualStartTime).plusMinutes(durationMinutes.toLong())
        val dueEpoch = dueDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val userId = authRepository.getCurrentUser()?.id ?: ""
        val taskId = UUID.randomUUID().toString()

        val task = Task(
            id = taskId,
            title = title.trim(),
            description = description.trim(),
            category = category,
            priority = priority,
            dueDate = dueEpoch,
            estimatedDurationMinutes = durationMinutes,
            userId = userId
        )

        viewModelScope.launch {
            taskRepository.insertTask(task)

            if (enableCommitmentLock) {
                val now = System.currentTimeMillis()
                val commitment = Commitment(
                    id = UUID.randomUUID().toString(),
                    taskId = taskId,
                    deadline = dueEpoch,
                    estimatedDurationMinutes = durationMinutes,
                    status = CommitmentStatus.ACTIVE,
                    consequenceType = ConsequenceType.COMMITMENT_LOCK,
                    selectedAppPackages = _selectedAppPackages.value.toList(),
                    createdAt = now,
                    activatedAt = now,
                    userId = userId
                )
                commitmentRepository.insertCommitment(commitment)

                if (commitment.selectedAppPackages.isNotEmpty()) {
                    appRestrictionManager.enableRestriction(
                        apps = commitment.selectedAppPackages,
                        reason = "Commitment Active: $title"
                    )
                }

                val timeFmt = DateTimeFormatter.ofPattern("hh:mm a")
                notificationHelper.showCommitmentActivated(
                    taskName = title,
                    deadline = dueDateTime.format(timeFmt)
                )

                scheduleCommitmentWork(commitment)
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

    private fun buildPlannerState(
        allTasks: List<Task>,
        date: LocalDate,
        isGenerating: Boolean
    ): PlannerUiState {
        val zone = ZoneId.systemDefault()
        val startOfDay = date.atStartOfDay(zone).toInstant().toEpochMilli()
        val endOfDay = date.atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli()
        val isToday = date == LocalDate.now()

        val relevantTasks = allTasks.filter { task ->
            if (task.dueDate != null) {
                task.dueDate in startOfDay..endOfDay
            } else if (task.completedAt != null) {
                task.completedAt in startOfDay..endOfDay
            } else {
                isToday
            }
        }

        val sortedTasks = relevantTasks.sortedWith(
            compareBy<Task> { it.priority.ordinal }
                .thenBy { it.dueDate ?: Long.MAX_VALUE }
        )

        var currentSlotTime = if (isToday) {
            val now = LocalTime.now()
            if (now.isBefore(LocalTime.of(9, 0))) LocalTime.of(9, 0) else now
        } else {
            LocalTime.of(9, 0)
        }

        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

        val slots = sortedTasks.mapIndexed { index, task ->
            val duration = task.estimatedDurationMinutes ?: 45
            
            val startTime = if (task.dueDate != null) {
                Instant.ofEpochMilli(task.dueDate).atZone(zone).toLocalTime()
            } else {
                currentSlotTime
            }

            val endTime = startTime.plusMinutes(duration.toLong())
            currentSlotTime = endTime.plusMinutes(10)

            PlannerScheduleSlot(
                id = "slot_${task.id}",
                taskId = task.id,
                title = task.title,
                category = task.category,
                priority = task.priority,
                startTime = startTime.format(timeFormatter),
                endTime = endTime.format(timeFormatter),
                durationMinutes = duration,
                isCompleted = task.isCompleted,
                isOverdue = task.isOverdue,
                hasConflict = false
            )
        }

        val completed = slots.count { it.isCompleted }
        val pending = slots.size - completed
        val totalPlannedMins = slots.sumOf { it.durationMinutes }
        val completedMins = slots.filter { it.isCompleted }.sumOf { it.durationMinutes }
        val progress = if (slots.isNotEmpty()) (completed * 100) / slots.size else 0

        return PlannerUiState(
            selectedDate = date,
            slots = slots,
            pendingCount = pending,
            completedCount = completed,
            totalCount = slots.size,
            totalPlannedMinutes = totalPlannedMins,
            completedMinutes = completedMins,
            progressPercentage = progress,
            hasConflict = false,
            isGeneratingPlan = isGenerating,
            isLoading = false
        )
    }
}
