package com.focusflow.app.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val navigationTarget by viewModel.navigationTarget.collectAsState()
    val density = LocalDensity.current

    // 3D Spatial Animatable Values
    val rotX = remember { Animatable(30f) }
    val rotY = remember { Animatable(-35f) }
    val rotZ = remember { Animatable(-15f) }
    val emblemScale = remember { Animatable(0.4f) }
    val emblemAlpha = remember { Animatable(0f) }
    val arcProgress = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textTranslationY = remember { Animatable(25f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: 3D Emblem Fly-in & Spatial Unfold
        launch {
            emblemAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        }
        launch {
            emblemScale.animateTo(1f, spring(dampingRatio = 0.65f, stiffness = 220f))
        }
        launch {
            rotX.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 180f))
        }
        launch {
            rotY.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 180f))
        }
        launch {
            rotZ.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = 200f))
        }

        // Step 2: Active Arc Sweep
        launch {
            delay(400)
            arcProgress.animateTo(1f, tween(1400, easing = FastOutSlowInEasing))
        }

        // Step 3: Brand Typography Entrance
        launch {
            delay(700)
            textAlpha.animateTo(1f, tween(600))
            textTranslationY.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = 300f))
        }

        // Step 4: Tagline Reveal
        launch {
            delay(1200)
            taglineAlpha.animateTo(1f, tween(800))
        }
    }

    LaunchedEffect(navigationTarget) {
        when (navigationTarget) {
            NavigationTarget.Onboarding -> onNavigateToOnboarding()
            NavigationTarget.Login -> onNavigateToLogin()
            NavigationTarget.Home -> onNavigateToHome()
            null -> Unit
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    val accentGreen = Color(0xFF52B788)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient radial depth aura
        Canvas(modifier = Modifier.size(340.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.12f * emblemAlpha.value),
                        accentGreen.copy(alpha = 0.05f * emblemAlpha.value),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width / 2f
                ),
                radius = size.width / 2f,
                center = center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 3D Perspective Assembly Emblem
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .graphicsLayer {
                        rotationX = rotX.value
                        rotationY = rotY.value
                        rotationZ = rotZ.value
                        scaleX = emblemScale.value
                        scaleY = emblemScale.value
                        alpha = emblemAlpha.value
                        cameraDistance = 16f * density.density
                    },
                contentAlignment = Alignment.Center
            ) {
                // Outer Precision 3D Arc Gauge
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Static Track
                    drawArc(
                        color = trackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Sweeping Progress Arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                primaryColor,
                                accentGreen,
                                primaryColor
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * arcProgress.value,
                        useCenter = false,
                        style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Inner Elevated 3D Floating Disc
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = primaryColor.copy(alpha = 0.35f),
                            ambientColor = Color.Black.copy(alpha = 0.1f)
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    primaryColor,
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Focus Flow",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha.value
                    translationY = textTranslationY.value
                }
            ) {
                Text(
                    text = "FocusFlow",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Plan • Focus • Progress",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = taglineAlpha.value),
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}
