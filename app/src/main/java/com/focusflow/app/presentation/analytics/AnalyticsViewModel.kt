package com.focusflow.app.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.domain.model.*
import com.focusflow.app.domain.repository.*
import com.focusflow.app.domain.usecase.commitment.CalculateCommitmentScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val habitRepository: HabitRepository,
    private val authRepository: AuthRepository,
    private val commitmentRepository: CommitmentRepository
) : ViewModel() {
    private val _period = MutableStateFlow(AnalyticsPeriod.WEEK)
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.observeAuthState().flatMapLatest { user ->
                val id = user?.id ?: ""
                val records = combine(
                    focusSessionRepository.getAllSessions(id), taskRepository.getAllTasks(id),
                    habitRepository.getAllHabits(id), commitmentRepository.getAllCommitments(id)
                ) { sessions, tasks, habits, commitments -> AnalyticsRecords(sessions, tasks, habits, commitments) }
                combine(records, _period, observeToday()) { data, period, today ->
                    buildAnalytics(data, period, today)
                }
            }.catch { error -> _uiState.update { it.copy(error = error.message) } }
                .collect { _uiState.value = it }
        }
    }

    fun selectPeriod(period: AnalyticsPeriod) { _period.value = period }
}

internal data class AnalyticsRecords(
    val sessions: List<FocusSession>, val tasks: List<Task>, val habits: List<Habit>, val commitments: List<Commitment>
)

data class AnalyticsUiState(
    val period: AnalyticsPeriod = AnalyticsPeriod.WEEK,
    val totalFocusTime: Int = 0,
    val tasksCompleted: Int = 0,
    val completionRate: Float = 0f,
    val currentStreak: Int = 0,
    val commitmentScore: Int = 0,
    val chartLabels: List<String> = emptyList(),
    val chartMinutes: List<Int> = emptyList(),
    val insight: String = "Complete a focus session to see your most productive hours.",
    val error: String? = null
)

enum class AnalyticsPeriod { WEEK, MONTH, YEAR }

internal fun buildAnalytics(
    data: AnalyticsRecords, period: AnalyticsPeriod, today: LocalDate, zone: ZoneId = ZoneId.systemDefault()
): AnalyticsUiState {
    val start = when (period) {
        AnalyticsPeriod.WEEK -> today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        AnalyticsPeriod.MONTH -> today.withDayOfMonth(1)
        AnalyticsPeriod.YEAR -> today.withDayOfYear(1)
    }
    val lower = start.atStartOfDay(zone).toInstant().toEpochMilli()
    val upper = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
    fun inRange(timestamp: Long?) = timestamp != null && timestamp >= lower && timestamp < upper
    val sessions = data.sessions.filter { inRange(it.startTime) }
    val completed = data.tasks.count { it.isCompleted && inRange(it.completedAt) }
    val cohort = data.tasks.count { inRange(it.createdAt) || (it.isCompleted && inRange(it.completedAt)) }
    val labels = when (period) {
        AnalyticsPeriod.WEEK -> (0L..6L).map { start.plusDays(it).format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault())) }
        AnalyticsPeriod.MONTH -> (1..today.lengthOfMonth() step 7).map { "$it–${minOf(it + 6, today.lengthOfMonth())}" }
        AnalyticsPeriod.YEAR -> (1..12).map { Month.of(it).getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()) }
    }
    val minutes = MutableList(labels.size) { 0 }
    sessions.forEach { session ->
        val date = Instant.ofEpochMilli(session.startTime).atZone(zone).toLocalDate()
        val index = when (period) {
            AnalyticsPeriod.WEEK -> date.dayOfWeek.value - 1
            AnalyticsPeriod.MONTH -> (date.dayOfMonth - 1) / 7
            AnalyticsPeriod.YEAR -> date.monthValue - 1
        }
        minutes[index] += session.actualDurationMinutes ?: 0
    }
    val byHour = sessions.filter { (it.actualDurationMinutes ?: 0) > 0 }.groupBy {
        Instant.ofEpochMilli(it.startTime).atZone(zone).hour
    }.mapValues { (_, values) -> values.sumOf { it.actualDurationMinutes ?: 0 } }
    val best = byHour.maxByOrNull { it.value }
    val insight = if (best == null) "Complete a focus session to see your most productive hours."
    else "You logged the most focus time starting between %02d:00 and %02d:00 this %s (%d minutes).".format(
        best.key, (best.key + 1) % 24, period.name.lowercase(), best.value
    )
    val commitments = data.commitments.filter {
        inRange(it.completedAt ?: it.restoredAt ?: it.cancelledAt ?: it.missedAt ?: it.createdAt)
    }
    return AnalyticsUiState(
        period = period, totalFocusTime = sessions.sumOf { it.actualDurationMinutes ?: 0 },
        tasksCompleted = completed, completionRate = if (cohort == 0) 0f else completed.toFloat() / cohort,
        currentStreak = data.habits.maxOfOrNull { it.currentStreak } ?: 0,
        commitmentScore = CalculateCommitmentScoreUseCase()(commitments).score,
        chartLabels = labels, chartMinutes = minutes, insight = insight
    )
}
