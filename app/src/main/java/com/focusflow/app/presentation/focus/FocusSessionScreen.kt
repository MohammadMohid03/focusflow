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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.presentation.theme.*

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
        animationSpec = tween(500),
        label = "Progress"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight().padding(vertical = 32.dp)
        ) {
            // Header Info Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                shadowElevation = 2.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isRunning && !uiState.isPaused) primaryColor else MaterialTheme.colorScheme.error)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isBreak) "Break Interval • Round ${uiState.currentRound}/4" else "Deep Focus • Round ${uiState.currentRound}/4",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 3D Breathing Timer Gauge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(310.dp)
                    .ambientGlow(primaryColor, radius = 150.dp, alpha = 0.35f)
            ) {
                // Background Layered Rings
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // Outer Track
                    drawArc(
                        color = trackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Progress Arc with Gradient
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                primaryColor.copy(alpha = 0.7f),
                                primaryColor,
                                primaryColor.copy(alpha = 0.9f)
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = (360f * progress).coerceIn(0f, 360f),
                        useCenter = false,
                        style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Inner Floating Glass Disc
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .glass3D(shape = CircleShape, elevation = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val minutes = uiState.timeRemaining / 60
                    val seconds = uiState.timeRemaining % 60
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-1).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (uiState.isPaused) "PAUSED" else "FLOWING",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            // 3D Tactile Play/Pause & Stop Action Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pause / Resume Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .pressSpring3D(pressScale = 0.9f) {
                            if (uiState.isPaused) viewModel.resumeSession()
                            else viewModel.pauseSession()
                        }
                        .shadow(8.dp, CircleShape, spotColor = primaryColor.copy(alpha = 0.45f))
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    primaryColor,
                                    primaryColor.copy(alpha = 0.85f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (uiState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause/Resume",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Stop Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .pressSpring3D(pressScale = 0.9f) {
                            viewModel.endSession()
                            onSessionEnd()
                        }
                        .shadow(6.dp, CircleShape, spotColor = MaterialTheme.colorScheme.error.copy(alpha = 0.35f))
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.errorContainer,
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "End Session",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
