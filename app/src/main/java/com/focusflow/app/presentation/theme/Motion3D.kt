package com.focusflow.app.presentation.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * High-performance Framer-motion inspired spring curves and 3D physics specifications
 */
object FramerSprings {
    val Snappy = spring<Float>(dampingRatio = 0.7f, stiffness = 500f)
    val Bouncy = spring<Float>(dampingRatio = 0.55f, stiffness = 380f)
    val Gentle = spring<Float>(dampingRatio = 0.85f, stiffness = 220f)
    val Spatial3D = spring<Float>(dampingRatio = 0.6f, stiffness = 400f)

    val SnappyOffset = spring<Offset>(dampingRatio = 0.7f, stiffness = 500f)
    val BouncyOffset = spring<Offset>(dampingRatio = 0.55f, stiffness = 380f)
}

/**
 * Interactive 3D Card Tilt on touch/drag.
 * Maps touch position relative to the center of the component to 3D rotationX & rotationY.
 */
fun Modifier.tilt3D(
    maxTiltDegrees: Float = 9f,
    scaleOnTouch: Float = 1.025f,
    elevationBoost: Dp = 8.dp,
    shape: Shape = FocusFlowCorners.Card
): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val rotX = remember { Animatable(0f) }
    val rotY = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val elevation = remember { Animatable(0f) }

    val density = LocalDensity.current

    this
        .onSizeChanged { size = it }
        .graphicsLayer {
            rotationX = rotX.value
            rotationY = rotY.value
            scaleX = scale.value
            scaleY = scale.value
            cameraDistance = 16f * density.density
            shadowElevation = elevation.value
            this.shape = shape
            clip = false
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { offset ->
                    if (size.width > 0 && size.height > 0) {
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val normalizedX = ((offset.x - centerX) / centerX).coerceIn(-1f, 1f)
                        val normalizedY = ((offset.y - centerY) / centerY).coerceIn(-1f, 1f)

                        coroutineScope {
                            launch { rotY.animateTo(normalizedX * maxTiltDegrees, FramerSprings.Spatial3D) }
                            launch { rotX.animateTo(-normalizedY * maxTiltDegrees, FramerSprings.Spatial3D) }
                            launch { scale.animateTo(scaleOnTouch, FramerSprings.Spatial3D) }
                            launch { elevation.animateTo(with(density) { elevationBoost.toPx() }, FramerSprings.Spatial3D) }
                        }

                        tryAwaitRelease()

                        coroutineScope {
                            launch { rotX.animateTo(0f, FramerSprings.Bouncy) }
                            launch { rotY.animateTo(0f, FramerSprings.Bouncy) }
                            launch { scale.animateTo(1f, FramerSprings.Bouncy) }
                            launch { elevation.animateTo(0f, FramerSprings.Bouncy) }
                        }
                    }
                }
            )
        }
}

/**
 * Tactile 3D Button and Surface Press Spring with depth depression and bounce
 */
fun Modifier.pressSpring3D(
    pressScale: Float = 0.955f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val depth = remember { Animatable(0f) }

    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
            translationY = depth.value
        }
        .pointerInput(onClick) {
            detectTapGestures(
                onPress = {
                    coroutineScope {
                        launch { scale.animateTo(pressScale, FramerSprings.Snappy) }
                        launch { depth.animateTo(4f, FramerSprings.Snappy) }
                    }
                    val released = tryAwaitRelease()
                    coroutineScope {
                        launch { scale.animateTo(1f, FramerSprings.Bouncy) }
                        launch { depth.animateTo(0f, FramerSprings.Bouncy) }
                    }
                    if (released && onClick != null) {
                        onClick()
                    }
                }
            )
        }
}

/**
 * 3D Glassmorphic Surface with specular highlight border and depth shadow
 */
fun Modifier.glass3D(
    shape: Shape = FocusFlowCorners.Card,
    backgroundColor: Color? = null,
    borderAlpha: Float = 0.45f,
    elevation: Dp = 4.dp
): Modifier = composed {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bg = backgroundColor ?: if (isDark) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
    } else {
        Color.White.copy(alpha = 0.88f)
    }

    val borderBrush = Brush.linearGradient(
        colors = if (isDark) {
            listOf(
                Color.White.copy(alpha = borderAlpha),
                Color.White.copy(alpha = borderAlpha * 0.2f),
                Color.Transparent,
                MaterialTheme.colorScheme.primary.copy(alpha = borderAlpha * 0.3f)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.9f),
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                Color.White.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        },
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = if (isDark) Color.Black.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            spotColor = if (isDark) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.06f)
        )
        .background(bg, shape)
        .border(BorderStroke(1.dp, borderBrush), shape)
        .clip(shape)
}

/**
 * Ambient Glowing Aura behind hero cards or timers
 */
fun Modifier.ambientGlow(
    color: Color,
    radius: Dp = 120.dp,
    alpha: Float = 0.35f
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "ambientGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    this.drawBehind {
        val center = Offset(size.width / 2f, size.height / 2f)
        val glowRadius = radius.toPx() * pulseScale
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = alpha),
                    color.copy(alpha = alpha * 0.4f),
                    Color.Transparent
                ),
                center = center,
                radius = glowRadius
            ),
            radius = glowRadius,
            center = center
        )
    }
}
