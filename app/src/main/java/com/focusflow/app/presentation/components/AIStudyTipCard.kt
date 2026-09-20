package com.focusflow.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusflow.app.presentation.theme.ForestAccent
import com.focusflow.app.presentation.theme.ForestContainer
import com.focusflow.app.presentation.theme.ForestPrimary
import com.focusflow.app.presentation.theme.WarmSandContainer
import com.focusflow.app.presentation.theme.pressSpring3D

@Composable
fun AIStudyTipCard(
    tipText: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)
    val primaryColor = MaterialTheme.colorScheme.primary
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f

    val cardBackgroundBrush = if (isLight) {
        Brush.linearGradient(
            colors = listOf(
                ForestContainer.copy(alpha = 0.75f),
                WarmSandContainer.copy(alpha = 0.55f),
                ForestContainer.copy(alpha = 0.40f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.surface
            )
        )
    }

    val cardBorderBrush = Brush.horizontalGradient(
        colors = listOf(
            ForestAccent.copy(alpha = 0.45f),
            primaryColor.copy(alpha = 0.30f),
            ForestAccent.copy(alpha = 0.15f)
        )
    )

    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(
                elevation = 2.dp,
                shape = cardShape,
                ambientColor = primaryColor.copy(alpha = 0.15f),
                spotColor = primaryColor.copy(alpha = 0.20f)
            )
            .pressSpring3D(pressScale = if (onClick != null) 0.982f else 1f),
        shape = cardShape,
        color = Color.Transparent,
        border = BorderStroke(1.2.dp, cardBorderBrush)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBackgroundBrush)
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Glowing Icon Aura Box
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(primaryColor.copy(alpha = 0.14f))
                                .border(
                                    width = 1.dp,
                                    color = primaryColor.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Tip",
                                tint = primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "AI INSIGHT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                color = primaryColor.copy(alpha = 0.75f)
                            )
                            Text(
                                text = "Study Recommendation",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Interactive Action Pill if clickable
                    if (onClick != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = primaryColor.copy(alpha = 0.10f),
                            border = BorderStroke(0.8.dp, primaryColor.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Ask AI",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = primaryColor
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                // Tip Body
                val cleanTip = remember(tipText) {
                    tipText
                        .replace(Regex("""\*\*Tip:?\*\*:\s*""", RegexOption.IGNORE_CASE), "")
                        .replace(Regex("""^\*?\*?Tip:?\*?\*?:?\s*""", RegexOption.IGNORE_CASE), "")
                        .replace("**", "")
                        .replace("__", "")
                        .replace(Regex("""^["']|["']$"""), "")
                        .trim()
                }

                Text(
                    text = cleanTip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
