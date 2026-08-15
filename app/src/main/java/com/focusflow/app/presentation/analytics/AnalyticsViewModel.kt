package com.focusflow.app.presentation.analytics

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    fun selectPeriod(period: AnalyticsPeriod) {
        _uiState.value = _uiState.value.copy(period = period)
    }
}

data class AnalyticsUiState(
    val period: AnalyticsPeriod = AnalyticsPeriod.WEEK,
    val totalFocusTime: Int = 1200,
    val tasksCompleted: Int = 45,
    val completionRate: Float = 0.85f,
    val currentStreak: Int = 7,
    val commitmentScore: Int = 92
)

enum class AnalyticsPeriod { WEEK, MONTH, YEAR }
