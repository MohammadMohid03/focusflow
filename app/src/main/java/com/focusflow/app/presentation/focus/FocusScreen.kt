package com.focusflow.app.presentation.focus

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.domain.model.FocusSessionType
import com.focusflow.app.presentation.components.CompactTopBar
import com.focusflow.app.presentation.theme.FocusFlowCorners

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
            CompactTopBar(title = "Focus Mode")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Row in Surface
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = FocusFlowCorners.Card,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Today's Focus", "${uiState.totalFocusToday}m")
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

            // Session Types
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SessionTypeCard(
                    title = "Pomodoro",
                    subtitle = "25 / 5 min",
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
                    isSelected = selectedType == FocusSessionType.CUSTOM,
                    onClick = {
                        selectedType = FocusSessionType.CUSTOM
                        viewModel.selectSessionType(FocusSessionType.CUSTOM)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.startSession()
                    onNavigateToSession()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = FocusFlowCorners.Button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Start Focus Session",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SessionTypeCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.aspectRatio(1f),
        shape = FocusFlowCorners.CardSmall,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
