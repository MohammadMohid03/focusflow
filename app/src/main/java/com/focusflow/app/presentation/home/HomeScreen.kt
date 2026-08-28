package com.focusflow.app.presentation.home

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.model.TaskCategory
import com.focusflow.app.presentation.components.AIStudyTipCard
import com.focusflow.app.presentation.components.QuickActionCard
import com.focusflow.app.presentation.components.StatCard
import com.focusflow.app.presentation.components.UpcomingSessionCard
import com.focusflow.app.presentation.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToTasks: () -> Unit = {},
    onNavigateToCreateTask: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {},
    onNavigateToAiChat: () -> Unit = {},
    onNavigateToFocus: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToTaskDetail: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            ProfileHeaderArea(
                userName = uiState.userName,
                greeting = uiState.greeting,
                onProfileClick = onNavigateToProfile,
                onSettingsClick = onNavigateToSettings
            )
        }
        item {
            HeroFocusCard(
                focusMinutes = uiState.focusMinutesToday,
                completedTasks = uiState.completedCount,
                onStartFocus = onNavigateToFocus
            )
        }
        item {
            StatsRow(
                focusMinutes = uiState.focusMinutesToday,
                completedTasks = uiState.completedCount,
                currentStreak = uiState.currentStreak,
                onFocusClick = onNavigateToFocus,
                onTasksClick = onNavigateToTasks
            )
        }
        item {
            AIStudyTipCard(
                tipText = uiState.aiRecommendation.takeIf { it.isNotBlank() }
                    ?: "You are most productive during morning hours. Plan your deep focus sessions before noon for maximum retention!",
                onClick = onNavigateToAiChat
            )
        }
        item {
            QuickActionsSection(
                onNavigateToTasks = onNavigateToTasks,
                onNavigateToCreateTask = onNavigateToCreateTask,
                onNavigateToPlanner = onNavigateToPlanner,
                onNavigateToAiChat = onNavigateToAiChat,
                onNavigateToFocus = onNavigateToFocus
            )
        }
        item {
            UpcomingSessionsSection(
                tasks = uiState.todayTasks,
                onViewAllClick = onNavigateToTasks,
                onTaskClick = onNavigateToTaskDetail
            )
        }
    }
}

@Composable
fun ProfileHeaderArea(
    userName: String,
    greeting: String,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f, fill = false)
                .pressSpring3D(pressScale = 0.96f, onClick = onProfileClick)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Hi, $userName",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Surface(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(38.dp)
                .pressSpring3D(pressScale = 0.92f),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun HeroFocusCard(
    focusMinutes: Int,
    completedTasks: Int,
    onStartFocus: () -> Unit
) {
    val cardShape = FocusFlowCorners.Card

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .pressSpring3D(pressScale = 0.985f),
        shape = cardShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "TODAY'S TARGET",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        letterSpacing = 0.5.sp,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ready to Focus Today?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$focusMinutes mins recorded • $completedTasks tasks done",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    onClick = onStartFocus,
                    modifier = Modifier.pressSpring3D(pressScale = 0.95f),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Start Focus",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
fun StatsRow(
    focusMinutes: Int,
    completedTasks: Int,
    currentStreak: Int,
    onFocusClick: () -> Unit = {},
    onTasksClick: () -> Unit = {}
) {
    val animatedMinutes by animateIntAsState(targetValue = focusMinutes, label = "focusMin")
    val animatedTasks by animateIntAsState(targetValue = completedTasks, label = "tasks")
    val animatedStreak by animateIntAsState(targetValue = currentStreak, label = "streak")

    val hours = animatedMinutes / 60
    val minutes = animatedMinutes % 60
    val timeString = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Focus Time",
            value = timeString,
            icon = Icons.Outlined.AccessTime,
            onClick = onFocusClick
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Tasks Done",
            value = "$animatedTasks",
            icon = Icons.Outlined.CheckCircle,
            onClick = onTasksClick
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Streak",
            value = "$animatedStreak d",
            icon = Icons.Outlined.LocalFireDepartment,
            onClick = {}
        )
    }
}

@Composable
fun QuickActionsSection(
    onNavigateToTasks: () -> Unit,
    onNavigateToCreateTask: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToAiChat: () -> Unit,
    onNavigateToFocus: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionCard(
                icon = Icons.Outlined.ChatBubbleOutline,
                label = "AI Mentor",
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                iconColor = MaterialTheme.colorScheme.primary,
                onClick = onNavigateToAiChat,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Outlined.Timer,
                label = "Focus Room",
                iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                iconColor = MaterialTheme.colorScheme.secondary,
                onClick = onNavigateToFocus,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Outlined.DateRange,
                label = "Schedule",
                iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                iconColor = MaterialTheme.colorScheme.primary,
                onClick = onNavigateToPlanner,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Outlined.AddCircleOutline,
                label = "New Task",
                iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                iconColor = MaterialTheme.colorScheme.secondary,
                onClick = onNavigateToCreateTask,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun UpcomingSessionsSection(
    tasks: List<Task>,
    onViewAllClick: () -> Unit = {},
    onTaskClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Upcoming Sessions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View all",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (tasks.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = FocusFlowCorners.Card,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Box(
                    modifier = Modifier.padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No upcoming sessions. Relax or schedule a new one!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tasks.take(3).forEach { task ->
                    val categoryColor = when (task.category) {
                        TaskCategory.WORK -> CategoryWork
                        TaskCategory.STUDY -> CategoryStudy
                        TaskCategory.PERSONAL -> CategoryPersonal
                        TaskCategory.HEALTH -> CategoryHealth
                        TaskCategory.CREATIVE -> CategoryCreative
                        else -> CategoryOther
                    }
                    UpcomingSessionCard(
                        subject = task.title,
                        time = "${task.estimatedDurationMinutes ?: 30} mins",
                        categoryColor = categoryColor,
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
    }
}
