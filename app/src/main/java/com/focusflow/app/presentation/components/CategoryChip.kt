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
        "work" -> Pair(Color(0xFFE3F2FD), Color(0xFF1976D2))
        "study" -> Pair(Color(0xFFF3E5F5), Color(0xFF7B1FA2))
        "personal" -> Pair(Color(0xFFE8F5E9), Color(0xFF388E3C))
        "health" -> Pair(Color(0xFFFFEBEE), Color(0xFFD32F2F))
        "creative" -> Pair(Color(0xFFFFF3E0), Color(0xFFF57C00))
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
