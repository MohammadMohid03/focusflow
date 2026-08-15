package com.focusflow.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.focusflow.app.domain.model.TaskPriority

@Composable
fun PriorityIndicator(
    priority: TaskPriority,
    modifier: Modifier = Modifier
) {
    val color = when (priority) {
        TaskPriority.URGENT -> Color(0xFFD32F2F) // Red
        TaskPriority.HIGH -> Color(0xFFF57C00) // Orange
        TaskPriority.MEDIUM -> Color(0xFF1976D2) // Blue
        TaskPriority.LOW -> Color(0xFF757575) // Gray
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}
