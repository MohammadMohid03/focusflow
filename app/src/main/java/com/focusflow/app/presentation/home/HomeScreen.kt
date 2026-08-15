package com.focusflow.app.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToTasks: () -> Unit,
    onNavigateToCreateTask: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        visible = true
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFFC084FC))
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F5FB)), // soft lavender background
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500), initialOffsetY = { 50 })
            ) {
                ProfileHeaderArea(
                    userName = uiState.userName,
                    greeting = uiState.greeting
                )
            }
        }
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 100)) + slideInVertically(tween(500, delayMillis = 100), initialOffsetY = { 50 })
            ) {
                HeroTitle()
            }
        }
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 200)) + slideInVertically(tween(500, delayMillis = 200), initialOffsetY = { 50 })
            ) {
                StatsRow(
                    focusMinutes = uiState.focusMinutesToday,
                    completedTasks = uiState.completedCount,
                    currentStreak = uiState.currentStreak
                )
            }
        }
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 300)) + slideInVertically(tween(500, delayMillis = 300), initialOffsetY = { 50 })
            ) {
                AiStudyTipCard(
                    recommendation = uiState.aiRecommendation
                )
            }
        }
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 400)) + slideInVertically(tween(500, delayMillis = 400), initialOffsetY = { 50 })
            ) {
                QuickActionsSection(
                    onNavigateToTasks = onNavigateToTasks,
                    onNavigateToCreateTask = onNavigateToCreateTask
                )
            }
        }
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 500)) + slideInVertically(tween(500, delayMillis = 500), initialOffsetY = { 50 })
            ) {
                UpcomingSessionsSection(tasks = uiState.todayTasks)
            }
        }
    }
}

@Composable
fun ProfileHeaderArea(userName: String, greeting: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFC084FC)), // purple
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Hi, $userName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        
        // Moon/theme toggle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable { /* Toggle theme */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.DarkMode,
                contentDescription = "Dark Mode",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun HeroTitle() {
    Text(
        text = "Ready to study\nToday?",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF1E1E2C),
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
            iconBgColor = Color(0xFFE8D5FF),
            bgColor = Color(0xFFF3E8FF),
            iconTint = Color(0xFF9333EA)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Completed",
            value = "$animatedTasks Task",
            icon = Icons.Outlined.CheckCircle,
            iconBgColor = Color(0xFFFFD5F0),
            bgColor = Color(0xFFFCE7F3),
            iconTint = Color(0xFFDB2777)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Current Streak",
            value = "$animatedStreak Days",
            icon = Icons.Outlined.LocalFireDepartment,
            iconBgColor = Color(0xFFBAE6FD),
            bgColor = Color(0xFFE0F2FE),
            iconTint = Color(0xFF0284C7)
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
    Column(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1A000000))
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(12.dp),
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
            color = Color(0xFF1E1E2C)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
fun AiStudyTipCard(recommendation: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = Color(0x1A9333EA))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFE8D5FF), Color(0xFFFFD5F0))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = "AI Tip",
                        tint = Color(0xFF9333EA),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "AI Study Tip",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E2C)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = recommendation.takeIf { it.isNotBlank() }
                    ?: "You're most productive from 10 AM to 12 PM. Schedule difficult topics then for better retention.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF475569),
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2f
            )
        }
    }
}

@Composable
fun QuickActionsSection(onNavigateToTasks: () -> Unit, onNavigateToCreateTask: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E2C),
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
                iconBgColor = Color(0xFFF3E8FF),
                iconTint = Color(0xFF9333EA),
                onClick = {}
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Focus Timer",
                icon = Icons.Outlined.Timer,
                iconBgColor = Color(0xFFDCFCE7),
                iconTint = Color(0xFF16A34A),
                onClick = {}
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
                icon = Icons.Outlined.MenuBook,
                iconBgColor = Color(0xFFFCE7F3),
                iconTint = Color(0xFFDB2777),
                onClick = {}
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Tasks",
                icon = Icons.Outlined.Checklist,
                iconBgColor = Color(0xFFE0F2FE),
                iconTint = Color(0xFF0284C7),
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
    Row(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0D000000))
            .background(Color.White, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
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
            color = Color(0xFF1E1E2C)
        )
    }
}

@Composable
fun UpcomingSessionsSection(tasks: List<Task>) {
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
                color = Color(0xFF1E1E2C)
            )
            TextButton(onClick = { /* View all */ }) {
                Text(
                    text = "View all",
                    color = Color(0xFF9333EA),
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
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No upcoming sessions. Relax or add a new one!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tasks) { task ->
                    SessionCard(task = task)
                }
            }
        }
    }
}

@Composable
fun SessionCard(task: Task) {
    val categoryColor = when (task.category) {
        TaskCategory.WORK -> Color(0xFF3B82F6) // Blue
        TaskCategory.STUDY -> Color(0xFF8B5CF6) // Purple
        TaskCategory.PERSONAL -> Color(0xFF10B981) // Green
        TaskCategory.HEALTH -> Color(0xFFEF4444) // Red
        TaskCategory.CREATIVE -> Color(0xFFF59E0B) // Orange
        else -> Color(0xFF6B7280) // Gray
    }

    Column(
        modifier = Modifier
            .width(200.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0D000000))
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp)
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
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1E2C),
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${task.estimatedDurationMinutes ?: 30} mins",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
