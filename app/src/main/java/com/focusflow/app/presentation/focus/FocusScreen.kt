package com.focusflow.app.presentation.focus

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.domain.model.FocusSessionType
import com.focusflow.app.presentation.components.CompactTopBar
import com.focusflow.app.presentation.components.PrimaryButton
import com.focusflow.app.presentation.theme.*

@Composable
fun FocusScreen(
    viewModel: FocusViewModel = hiltViewModel(),
    onNavigateToSession: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedType by remember { mutableStateOf(FocusSessionType.POMODORO_25_5) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CompactTopBar(title = "Focus Studio")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 3D Glassmorphic Stats Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .tilt3D(maxTiltDegrees = 6f, scaleOnTouch = 1.02f, shape = FocusFlowCorners.Card)
                    .glass3D(shape = FocusFlowCorners.Card, elevation = 6.dp)
                    .padding(vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem3D("Daily Focus", "${uiState.totalFocusToday}m")
                    StatItem3D("Sessions", "${uiState.recentSessions.size}")
                    StatItem3D("Streak", "3 Days")
                }
            }

            Text(
                text = "Select Focus Mode",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 3D Session Type Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SessionTypeCard3D(
                    title = "Pomodoro",
                    subtitle = "25 / 5 min",
                    icon = Icons.Outlined.Bolt,
                    isSelected = selectedType == FocusSessionType.POMODORO_25_5,
                    onClick = {
                        selectedType = FocusSessionType.POMODORO_25_5
                        viewModel.selectSessionType(FocusSessionType.POMODORO_25_5)
                    },
                    modifier = Modifier.weight(1f)
                )
                SessionTypeCard3D(
                    title = "Deep Work",
                    subtitle = "50 / 10 min",
                    icon = Icons.Outlined.Psychology,
                    isSelected = selectedType == FocusSessionType.POMODORO_50_10,
                    onClick = {
                        selectedType = FocusSessionType.POMODORO_50_10
                        viewModel.selectSessionType(FocusSessionType.POMODORO_50_10)
                    },
                    modifier = Modifier.weight(1f)
                )
                SessionTypeCard3D(
                    title = "Custom",
                    subtitle = "Flexible",
                    icon = Icons.Outlined.Tune,
                    isSelected = selectedType == FocusSessionType.CUSTOM,
                    onClick = {
                        selectedType = FocusSessionType.CUSTOM
                        viewModel.selectSessionType(FocusSessionType.CUSTOM)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "Enter Focus Room",
                onClick = {
                    viewModel.startSession()
                    onNavigateToSession()
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatItem3D(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = (-0.5).sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun SessionTypeCard3D(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = FocusFlowCorners.CardSmall

    Box(
        modifier = modifier
            .aspectRatio(0.92f)
            .tilt3D(maxTiltDegrees = 8f, scaleOnTouch = 1.04f, shape = cardShape)
            .pressSpring3D(pressScale = 0.94f, onClick = onClick)
            .glass3D(
                shape = cardShape,
                backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else null,
                elevation = if (isSelected) 6.dp else 2.dp
            )
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
