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
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.domain.model.Task
import com.focusflow.app.domain.model.TaskCategory
import com.focusflow.app.presentation.theme.CategoryCreative
import com.focusflow.app.presentation.theme.CategoryHealth
import com.focusflow.app.presentation.theme.CategoryOther
import com.focusflow.app.presentation.theme.CategoryPersonal
import com.focusflow.app.presentation.theme.CategoryStudy
import com.focusflow.app.presentation.theme.CategoryWork
import kotlinx.coroutines.delay

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
        contentPadding = PaddingValues(bottom = 80.dp)
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
            HeroTitle()
        }
        item {
            StatsRow(
                focusMinutes = uiState.focusMinutesToday,
                completedTasks = uiState.completedCount,
                currentStreak = uiState.currentStreak
            )
        }
        item {
            AiStudyTipCard(
                recommendation = uiState.aiRecommendation
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
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f, fill = false)
                .clickable(onClick = onProfileClick)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "U",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Hi, $userName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        
        Surface(
            onClick = onSettingsClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun HeroTitle() {
    Text(
        text = "Ready to study\nToday?",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
fun StatsRow(focusMinutes: Int, completedTasks: Int, currentStreak: Int) {
    val animatedMinutes by animateIntAsState(targetValue = focusMinutes, label = "focusMin")
    val animatedTasks by animateIntAsState(targetValue = completedTasks, label = "tasks")
    val animatedStreak by animateIntAsState(targetValue = currentStreak, label = "streak")

    val hours = animatedMinutes / 60
    val minutes = animatedMinutes % 60
    val timeString = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Study Time",
            value = timeString,
            icon = Icons.Outlined.AccessTime,
            iconBgColor = MaterialTheme.colorScheme.primaryContainer,
            bgColor = MaterialTheme.colorScheme.surface,
            iconTint = MaterialTheme.colorScheme.primary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Completed",
            value = "$animatedTasks Task",
            icon = Icons.Outlined.CheckCircle,
            iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
            bgColor = MaterialTheme.colorScheme.surface,
            iconTint = MaterialTheme.colorScheme.secondary
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Current Streak",
            value = "$animatedStreak Days",
            icon = Icons.Outlined.LocalFireDepartment,
            iconBgColor = MaterialTheme.colorScheme.tertiaryContainer,
            bgColor = MaterialTheme.colorScheme.surface,
            iconTint = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    bgColor: Color,
    iconTint: Color
) {
    Card(
        modifier = modifier
            .shadow(4.dp, MaterialTheme.shapes.large, spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AiStudyTipCard(recommendation: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .shadow(6.dp, MaterialTheme.shapes.large, spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                ),
                shape = MaterialTheme.shapes.large
            )
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = "AI Tip",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "AI Study Tip",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = recommendation.takeIf { it.isNotBlank() }
                    ?: "You're most productive from 10 AM to 12 PM. Schedule difficult topics then for better retention.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2f
            )
        }
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
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "AI Chat",
                icon = Icons.Outlined.ChatBubbleOutline,
                iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                iconTint = MaterialTheme.colorScheme.primary,
                onClick = onNavigateToAiChat
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Focus Timer",
                icon = Icons.Outlined.Timer,
                iconBgColor = MaterialTheme.colorScheme.tertiaryContainer,
                iconTint = MaterialTheme.colorScheme.tertiary,
                onClick = onNavigateToFocus
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Study Plan",
                icon = Icons.Outlined.DateRange,
                iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                iconTint = MaterialTheme.colorScheme.secondary,
                onClick = onNavigateToPlanner
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Tasks",
                icon = Icons.Outlined.Checklist,
                iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                iconTint = MaterialTheme.colorScheme.primary,
                onClick = onNavigateToTasks
            )
        }
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(2.dp, MaterialTheme.shapes.large, spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
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
            .padding(vertical = 16.dp)
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
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View all",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No upcoming sessions. Relax or add a new one!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    SessionCard(
                        task = task,
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SessionCard(
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

    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick)
            .shadow(2.dp, MaterialTheme.shapes.large, spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(categoryColor, CircleShape)
                )
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
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
