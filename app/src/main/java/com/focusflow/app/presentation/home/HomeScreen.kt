package com.focusflow.app.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.model.TaskCategory
import com.focusflow.app.presentation.components.StatCard
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
        contentPadding = PaddingValues(bottom = 100.dp)
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
            Hero3DFocusCard(
                focusMinutes = uiState.focusMinutesToday,
                completedTasks = uiState.completedCount,
                onStartFocus = onNavigateToFocus
            )
        }
        item {
            StatsRow3D(
                focusMinutes = uiState.focusMinutesToday,
                completedTasks = uiState.completedCount,
                currentStreak = uiState.currentStreak,
                onFocusClick = onNavigateToFocus,
                onTasksClick = onNavigateToTasks
            )
        }
        item {
            AiStudyTipCard3D(
                recommendation = uiState.aiRecommendation,
                onClick = onNavigateToAiChat
            )
        }
        item {
            QuickActionsSection3D(
                onNavigateToTasks = onNavigateToTasks,
                onNavigateToCreateTask = onNavigateToCreateTask,
                onNavigateToPlanner = onNavigateToPlanner,
                onNavigateToAiChat = onNavigateToAiChat,
                onNavigateToFocus = onNavigateToFocus
            )
        }
        item {
            UpcomingSessionsSection3D(
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
                    .size(48.dp)
                    .shadow(4.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Text(
                    text = "Hi, $userName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .pressSpring3D(pressScale = 0.92f, onClick = onSettingsClick)
                .glass3D(shape = CircleShape, elevation = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun Hero3DFocusCard(
    focusMinutes: Int,
    completedTasks: Int,
    onStartFocus: () -> Unit
) {
    val heroShape = FocusFlowCorners.Card

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .ambientGlow(MaterialTheme.colorScheme.primary, radius = 140.dp, alpha = 0.2f)
            .tilt3D(maxTiltDegrees = 7f, scaleOnTouch = 1.02f, shape = heroShape)
            .glass3D(shape = heroShape, elevation = 8.dp)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "DAILY FOCUS FLOW",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Ready to achieve your goals?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$focusMinutes mins focused • $completedTasks tasks completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3D Start Session Button
                Box(
                    modifier = Modifier
                        .pressSpring3D(pressScale = 0.94f, onClick = onStartFocus)
                        .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Start Focus",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // 3D Isometric Compass/Timer Orb Icon
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .shadow(8.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
    }
}

@Composable
fun StatsRow3D(
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
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Focus Time",
            value = timeString,
            icon = Icons.Outlined.AccessTime,
            iconColor = MaterialTheme.colorScheme.primary,
            iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = onFocusClick
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Tasks Done",
            value = "$animatedTasks",
            icon = Icons.Outlined.CheckCircle,
            iconColor = MaterialTheme.colorScheme.secondary,
            iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            onClick = onTasksClick
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Streak",
            value = "$animatedStreak d",
            icon = Icons.Outlined.LocalFireDepartment,
            iconColor = MaterialTheme.colorScheme.tertiary,
            iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onClick = {}
        )
    }
}

@Composable
fun AiStudyTipCard3D(
    recommendation: String,
    onClick: () -> Unit
) {
    val cardShape = FocusFlowCorners.Card

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .tilt3D(maxTiltDegrees = 6f, scaleOnTouch = 1.02f, shape = cardShape)
            .pressSpring3D(pressScale = 0.98f, onClick = onClick)
            .glass3D(shape = cardShape, elevation = 4.dp)
            .padding(18.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .shadow(3.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = "AI Tip",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "AI Flow Recommendation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = recommendation.takeIf { it.isNotBlank() }
                    ?: "You are most productive during morning hours. Plan your deep focus sessions before noon for maximum retention!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
fun QuickActionsSection3D(
    onNavigateToTasks: () -> Unit,
    onNavigateToCreateTask: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToAiChat: () -> Unit,
    onNavigateToFocus: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAction3DCard(
                modifier = Modifier.weight(1f),
                title = "AI Mentor",
                subtitle = "Ask & plan",
                icon = Icons.Outlined.ChatBubbleOutline,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onNavigateToAiChat
            )
            QuickAction3DCard(
                modifier = Modifier.weight(1f),
                title = "Focus Room",
                subtitle = "Timer & lock",
                icon = Icons.Outlined.Timer,
                accentColor = MaterialTheme.colorScheme.tertiary,
                onClick = onNavigateToFocus
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAction3DCard(
                modifier = Modifier.weight(1f),
                title = "Schedule",
                subtitle = "Daily planner",
                icon = Icons.Outlined.DateRange,
                accentColor = MaterialTheme.colorScheme.secondary,
                onClick = onNavigateToPlanner
            )
            QuickAction3DCard(
                modifier = Modifier.weight(1f),
                title = "New Task",
                subtitle = "Add & track",
                icon = Icons.Outlined.AddCircleOutline,
                accentColor = MaterialTheme.colorScheme.primary,
                onClick = onNavigateToCreateTask
            )
        }
    }
}

@Composable
fun QuickAction3DCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    val cardShape = FocusFlowCorners.Card

    Box(
        modifier = modifier
            .height(82.dp)
            .tilt3D(maxTiltDegrees = 7f, scaleOnTouch = 1.03f, shape = cardShape)
            .pressSpring3D(pressScale = 0.96f, onClick = onClick)
            .glass3D(shape = cardShape, elevation = 3.dp)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(3.dp, CircleShape, spotColor = accentColor.copy(alpha = 0.35f))
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                accentColor.copy(alpha = 0.2f),
                                accentColor.copy(alpha = 0.08f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun UpcomingSessionsSection3D(
    tasks: List<Task>,
    onViewAllClick: () -> Unit = {},
    onTaskClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
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
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View all",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .glass3D(shape = FocusFlowCorners.Card, elevation = 2.dp)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No upcoming sessions. Relax or schedule a new one!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    SessionCard3D(
                        task = task,
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SessionCard3D(
    task: Task,
    onClick: () -> Unit = {}
) {
    val categoryColor = when (task.category) {
        TaskCategory.WORK -> CategoryWork
        TaskCategory.STUDY -> CategoryStudy
        TaskCategory.PERSONAL -> CategoryPersonal
        TaskCategory.HEALTH -> CategoryHealth
        TaskCategory.CREATIVE -> CategoryCreative
        else -> CategoryOther
    }

    val cardShape = FocusFlowCorners.Card

    Box(
        modifier = Modifier
            .width(210.dp)
            .tilt3D(maxTiltDegrees = 7f, scaleOnTouch = 1.03f, shape = cardShape)
            .pressSpring3D(pressScale = 0.96f, onClick = onClick)
            .glass3D(shape = cardShape, elevation = 4.dp)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .shadow(3.dp, CircleShape, spotColor = categoryColor.copy(alpha = 0.6f))
                        .clip(CircleShape)
                        .background(categoryColor)
                )
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${task.estimatedDurationMinutes ?: 30} mins",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
