package com.focusflow.app.presentation.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Clean, production-grade physical spring specifications
 */
object FramerSprings {
    val Snappy = spring<Float>(dampingRatio = 0.78f, stiffness = 600f)
    val Bouncy = spring<Float>(dampingRatio = 0.72f, stiffness = 450f)
    val Gentle = spring<Float>(dampingRatio = 0.88f, stiffness = 280f)
    val Spatial3D = spring<Float>(dampingRatio = 0.8f, stiffness = 500f)

    val SnappyOffset = spring<Offset>(dampingRatio = 0.78f, stiffness = 600f)
    val BouncyOffset = spring<Offset>(dampingRatio = 0.72f, stiffness = 450f)
}

/**
 * Subtle 2.5D physical compression and elevation shift on press.
 * Gives a responsive, tactile feel without game-like distortion.
 */
fun Modifier.pressSpring3D(
    pressScale: Float = 0.98f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val scale = remember { Animatable(1f) }

    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .pointerInput(onClick) {
            detectTapGestures(
                onPress = {
                    coroutineScope {
                        launch { scale.animateTo(pressScale, FramerSprings.Snappy) }
                    }
                    val released = tryAwaitRelease()
                    coroutineScope {
                        launch { scale.animateTo(1f, FramerSprings.Bouncy) }
                    }
                    if (released && onClick != null) {
                        onClick()
                    }
                }
            )
        }
}

/**
 * Micro 2.5D Depth response on interactive cards.
 */
fun Modifier.tilt3D(
    maxTiltDegrees: Float = 2f,
    scaleOnTouch: Float = 1.01f,
    elevationBoost: Dp = 2.dp,
    shape: Shape = FocusFlowCorners.Card
): Modifier = composed {
    val scale = remember { Animatable(1f) }

    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    coroutineScope {
                        launch { scale.animateTo(scaleOnTouch, FramerSprings.Snappy) }
                    }
                    tryAwaitRelease()
                    coroutineScope {
                        launch { scale.animateTo(1f, FramerSprings.Bouncy) }
                    }
                }
            )
        }
}

/**
 * Premium 2.5D Card Surface:
 * Clean, solid surface with subtle 1dp border and soft directional shadow.
 */
fun Modifier.glass3D(
    shape: Shape = FocusFlowCorners.Card,
    backgroundColor: Color? = null,
    borderAlpha: Float = 0.5f,
    elevation: Dp = 1.5.dp
): Modifier = composed {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bg = backgroundColor ?: if (isDark) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isDark) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
    }

    this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.04f),
            spotColor = Color.Black.copy(alpha = 0.08f)
        )
        .background(bg, shape)
        .border(BorderStroke(1.dp, borderColor), shape)
        .clip(shape)
}

/**
 * Subtle radiant background tint for hero sections.
 */
fun Modifier.ambientGlow(
    color: Color,
    radius: Dp = 100.dp,
    alpha: Float = 0.08f
): Modifier = composed {
    this
}
