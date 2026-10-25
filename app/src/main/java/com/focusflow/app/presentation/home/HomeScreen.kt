package com.focusflow.app.presentation.home

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusflow.app.R
import com.focusflow.app.domain.model.Task
import com.focusflow.app.presentation.components.CompactTopBar
import com.focusflow.app.presentation.components.LocalFloatingNavigationPadding
import com.focusflow.app.presentation.theme.FocusFlowCorners
import com.focusflow.app.presentation.theme.FocusFlowTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToTasks: () -> Unit = {}, onNavigateToCreateTask: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {}, onNavigateToAiChat: () -> Unit = {},
    onNavigateToFocus: () -> Unit = {}, onNavigateToProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}, onNavigateToTaskDetail: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(state, onNavigateToTasks, onNavigateToCreateTask, onNavigateToPlanner,
        onNavigateToAiChat, onNavigateToFocus, onNavigateToProfile, onNavigateToSettings, onNavigateToTaskDetail)
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onTasks: () -> Unit = {}, onCreateTask: () -> Unit = {}, onPlanner: () -> Unit = {},
    onAiChat: () -> Unit = {}, onFocus: () -> Unit = {}, onProfile: () -> Unit = {},
    onSettings: () -> Unit = {}, onTask: (String) -> Unit = {}, modifier: Modifier = Modifier
) {
    val navigationPadding = LocalFloatingNavigationPadding.current
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CompactTopBar(title = stringResource(R.string.app_name), actions = {
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, stringResource(R.string.ui_settings), Modifier.size(21.dp))
                }
                FilledTonalIconButton(onClick = onProfile, modifier = Modifier.size(48.dp)) {
                    Text(state.userName.firstOrNull()?.uppercase() ?: "U", fontWeight = FontWeight.SemiBold)
                }
            })
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 640.dp).fillMaxSize().testTag("home_scroll"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = navigationPadding + 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item(key = "greeting", contentType = "greeting") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.home_greeting, state.userName), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    Text(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())),
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item(key = "focus", contentType = "focus") { FocusSummary(state.focusMinutesToday, onFocus) }
            item(key = "progress", contentType = "progress") {
                Surface(shape = FocusFlowCorners.Card, color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f).clickable(onClick = onTasks)) {
                            Text("${state.completedCount}/${state.totalCount}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            Text(stringResource(R.string.home_tasks_done), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        VerticalDivider(Modifier.height(36.dp).padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline)
                        Column(Modifier.weight(1f)) {
                            Text(state.currentStreak.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            Text(stringResource(R.string.home_streak), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            item(key = "actions", contentType = "actions") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onCreateTask, Modifier.weight(1f).heightIn(min = 48.dp), shape = FocusFlowCorners.Button) {
                        Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp)); Text(stringResource(R.string.home_create_task))
                    }
                    FilledTonalButton(onAiChat, Modifier.weight(1f).heightIn(min = 48.dp), shape = FocusFlowCorners.Button) {
                        Icon(Icons.Default.AutoAwesome, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp)); Text(stringResource(R.string.home_ask_ai))
                    }
                }
            }
            item(key = "plan_header", contentType = "header") {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.home_your_plan), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    TextButton(onTasks) { Text(stringResource(R.string.home_view_all)) }
                    IconButton(onPlanner) { Icon(Icons.Default.CalendarMonth, stringResource(R.string.ui_planner), Modifier.size(20.dp)) }
                }
            }
            if (state.todayTasks.isEmpty()) {
                item(key = "empty", contentType = "empty") {
                    Column(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(stringResource(R.string.home_empty_title), style = MaterialTheme.typography.titleSmall)
                        Text(stringResource(R.string.home_empty_description), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onCreateTask, contentPadding = PaddingValues(0.dp)) { Text(stringResource(R.string.home_create_task)) }
                    }
                }
            } else {
                items(state.todayTasks, key = { it.id }, contentType = { "task" }) { task ->
                    TodayTaskRow(task, { onTask(task.id) })
                }
            }
            if (state.aiRecommendation.isNotBlank()) {
                item(key = "tip", contentType = "tip") {
                    Surface(onClick = onAiChat, shape = FocusFlowCorners.Card, color = MaterialTheme.colorScheme.surfaceVariant) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(stringResource(R.string.home_focus_tip), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                            Text(state.aiRecommendation, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            state.error?.let { error -> item(key = "error") { Text(error, color = MaterialTheme.colorScheme.error) } }
        }
        }
    }
}

@Composable
private fun FocusSummary(minutes: Int, onFocus: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxWidth(), shape = FocusFlowCorners.CardLarge,
        color = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.home_focus_time), style = MaterialTheme.typography.bodyMedium)
            Text(if (minutes < 60) stringResource(R.string.home_minutes, minutes)
                else stringResource(R.string.home_hours_minutes, minutes / 60, minutes % 60),
                style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.SemiBold)
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f))
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).clip(FocusFlowCorners.Button).clickable(onClick = onFocus),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayArrow, null, Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.home_start_focus), Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun TodayTaskRow(task: Task, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(onClick = onClick, modifier = modifier.fillMaxWidth(), shape = FocusFlowCorners.Card,
        color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
        Row(Modifier.padding(14.dp).heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                null, Modifier.size(22.dp), tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(task.title, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                task.estimatedDurationMinutes?.let {
                    Text(stringResource(R.string.home_minutes, it), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Default.ChevronRight, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(name = "Home light", showBackground = true, widthDp = 393, heightDp = 852)
@Preview(name = "Home dark", showBackground = true, widthDp = 393, heightDp = 852, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomePreview() {
    FocusFlowTheme { HomeContent(HomeUiState(userName = "Alex", focusMinutesToday = 45, totalCount = 4,
        completedCount = 2, todayTasks = listOf(Task(title = "Review lecture notes", estimatedDurationMinutes = 25)))) }
}
