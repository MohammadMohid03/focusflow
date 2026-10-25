package com.focusflow.app.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusflow.app.navigation.BottomNavItem
import com.focusflow.app.presentation.theme.FocusFlowTheme

@Immutable
data class BottomNavItemData(val route: String, val icon: ImageVector, val selectedIcon: ImageVector, val label: String)

@Composable
fun FocusFlowBottomNav(
    items: List<BottomNavItemData>, currentRoute: String?, onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier, compact: Boolean = false
) {
    val width by animateDpAsState(if (compact) 216.dp else 288.dp, tween(220), label = "dockWidth")
    val height by animateDpAsState(if (compact) 52.dp else 60.dp, tween(220), label = "dockHeight")
    Surface(
        modifier = modifier.widthIn(max = width).width(width).height(height).testTag("floating_navigation")
            .semantics { stateDescription = if (compact) "Compact navigation" else "Expanded navigation" },
        shape = CircleShape, color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)),
        shadowElevation = 6.dp, tonalElevation = 0.dp
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 2.dp).selectableGroup(),
            verticalAlignment = Alignment.CenterVertically) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                val tint by animateColorAsState(
                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    tween(160), label = "dockTint")
                val background by animateColorAsState(
                    if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    tween(160), label = "dockSelection")
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().clip(CircleShape).background(background)
                        .selectable(selected = selected, role = Role.Tab, onClick = { onNavigate(item.route) })
                        .semantics(mergeDescendants = true) { contentDescription = item.label }
                        .testTag("nav_${item.route}"),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                ) {
                    Icon(if (selected) item.selectedIcon else item.icon, null, Modifier.size(20.dp), tint)
                    AnimatedVisibility(visible = !compact,
                        enter = fadeIn(tween(120)) + expandVertically(tween(180)),
                        exit = fadeOut(tween(90)) + shrinkVertically(tween(180))) {
                        Text(item.label, Modifier.padding(top = 1.dp).clearAndSetSemantics { },
                            color = tint, fontSize = 10.sp, lineHeight = 14.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Preview(name = "Expanded dock", showBackground = true)
@Preview(name = "Dark dock", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NavigationPreview() {
    FocusFlowTheme {
        Box(Modifier.padding(24.dp)) {
            FocusFlowBottomNav(BottomNavItem.entries.map { BottomNavItemData(it.route, it.icon, it.selectedIcon, it.label) }, "home", {})
        }
    }
}

@Preview(name = "Compact dock", showBackground = true)
@Composable
private fun CompactNavigationPreview() {
    FocusFlowTheme {
        Box(Modifier.padding(24.dp)) {
            FocusFlowBottomNav(BottomNavItem.entries.map { BottomNavItemData(it.route, it.icon, it.selectedIcon, it.label) }, "planner", {}, compact = true)
        }
    }
}
