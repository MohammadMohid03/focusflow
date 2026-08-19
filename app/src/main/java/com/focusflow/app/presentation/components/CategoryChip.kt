package com.focusflow.app.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusflow.app.domain.model.TaskCategory
import com.focusflow.app.presentation.theme.WarningOrange

@Composable
fun CategoryChip(
    category: TaskCategory,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = getCategoryColors(category.name)
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun getCategoryColors(categoryName: String): Pair<Color, Color> {
    return when (categoryName.lowercase()) {
        "work" -> Pair(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary)
        "study" -> Pair(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.secondary)
        "personal" -> Pair(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.tertiary)
        "health" -> Pair(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error)
        "creative" -> Pair(WarningOrange.copy(alpha = 0.2f), WarningOrange)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
