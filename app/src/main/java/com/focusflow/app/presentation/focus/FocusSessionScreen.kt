package com.focusflow.app.presentation.focus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.presentation.theme.pressSpring3D

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
        targetValue = if (uiState.totalDuration > 0) {
            uiState.timeRemaining.toFloat() / uiState.totalDuration.toFloat()
        } else {
            0f
        },
        animationSpec = tween(400),
        label = "Progress"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight().padding(vertical = 32.dp)
        ) {
            // Header Status Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isRunning && !uiState.isPaused) primaryColor else MaterialTheme.colorScheme.error)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isBreak) "Break Interval • Round ${uiState.currentRound}/4" else "Focus Session • Round ${uiState.currentRound}/4",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Minimalist Architectural Timer Gauge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(280.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    // Track
                    drawArc(
                        color = trackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Active Progress
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = (360f * progress).coerceIn(0f, 360f),
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                val minutes = uiState.timeRemaining / 60
                val seconds = uiState.timeRemaining % 60
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (uiState.isPaused) "PAUSED" else "IN FOCUS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Tactile Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pause / Resume Button
                Surface(
                    onClick = {
                        if (uiState.isPaused) viewModel.resumeSession()
                        else viewModel.pauseSession()
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .pressSpring3D(pressScale = 0.92f),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (uiState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause/Resume",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // Stop Button
                Surface(
                    onClick = {
                        viewModel.endSession()
                        onSessionEnd()
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .pressSpring3D(pressScale = 0.92f),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "End Session",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
