package com.focusflow.app.presentation.focus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FocusSessionScreen(
    viewModel: FocusViewModel = hiltViewModel(),
    onSessionEnd: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (!uiState.isRunning) {
            viewModel.startSession()
        }
    }

    LaunchedEffect(uiState.timeRemaining, uiState.isRunning) {
        if (uiState.timeRemaining <= 0 && !uiState.isRunning) {
            onSessionEnd()
        }
    }

    val progress by animateFloatAsState(
        targetValue = uiState.timeRemaining.toFloat() / (25 * 60).toFloat(),
        label = "Progress"
    )

    val trackColor = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.1f)
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseSurface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (uiState.isBreak) "Break Phase" else "Work Phase",
            color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.7f),
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Round ${uiState.currentRound}/4",
            color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.5f),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(300.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = trackColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            val minutes = uiState.timeRemaining / 60
            val seconds = uiState.timeRemaining % 60
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FloatingActionButton(
                onClick = { 
                    if (uiState.isPaused) viewModel.resumeSession() 
                    else viewModel.pauseSession() 
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = if (uiState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = "Pause/Resume"
                )
            }
            
            FloatingActionButton(
                onClick = {
                    viewModel.endSession()
                    onSessionEnd()
                },
                containerColor = MaterialTheme.colorScheme.errorContainer
            ) {
                Icon(Icons.Default.Stop, contentDescription = "End Session")
            }
        }
    }
}
