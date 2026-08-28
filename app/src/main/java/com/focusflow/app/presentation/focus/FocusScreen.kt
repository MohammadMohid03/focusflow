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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.domain.model.FocusSessionType
import com.focusflow.app.presentation.components.CompactTopBar
import com.focusflow.app.presentation.components.PrimaryButton
import com.focusflow.app.presentation.theme.FocusFlowCorners
import com.focusflow.app.presentation.theme.pressSpring3D

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = FocusFlowCorners.Card,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Daily Focus", "${uiState.totalFocusToday}m")
                    StatItem("Sessions", "${uiState.recentSessions.size}")
                    StatItem("Streak", "3 Days")
                }
            }

            Text(
                text = "Select Focus Mode",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Session Type Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SessionTypeCard(
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
                SessionTypeCard(
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
                SessionTypeCard(
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
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Backwards compatibility alias
@Composable
fun StatItem3D(label: String, value: String) = StatItem(label, value)

@Composable
fun SessionTypeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = FocusFlowCorners.Card

    Surface(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(0.95f)
            .pressSpring3D(pressScale = 0.96f),
        shape = cardShape,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        ),
        shadowElevation = if (isSelected) 1.5.dp else 0.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
