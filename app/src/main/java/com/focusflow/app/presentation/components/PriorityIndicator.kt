package com.focusflow.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.focusflow.app.domain.model.TaskPriority
import com.focusflow.app.presentation.theme.WarningOrange

@Composable
fun PriorityIndicator(
    priority: TaskPriority,
    modifier: Modifier = Modifier
) {
    val color = when (priority) {
        TaskPriority.URGENT -> MaterialTheme.colorScheme.error
        TaskPriority.HIGH -> WarningOrange
        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        TaskPriority.LOW -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
    )
}
