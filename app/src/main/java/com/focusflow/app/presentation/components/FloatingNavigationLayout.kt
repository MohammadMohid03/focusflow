package com.focusflow.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/** Extra scrollable space for the final item, rather than an opaque bottom-bar slot. */
val LocalFloatingNavigationPadding = staticCompositionLocalOf { 0.dp }

@Composable
fun FloatingNavigationLayout(
    items: List<BottomNavItemData>, currentRoute: String?, onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier, showNavigation: Boolean = true, content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val keyboardVisible = WindowInsets.ime.getBottom(density) > 0
    val showDock = showNavigation && !keyboardVisible
    val canObserveScroll by rememberUpdatedState(showDock)
    var compact by rememberSaveable(currentRoute) { mutableStateOf(false) }
    val controller = remember(currentRoute, density) {
        NavigationScrollController(with(density) { 32.dp.toPx() }, with(density) { 16.dp.toPx() })
    }
    val connection = remember(controller) {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (canObserveScroll && source == NestedScrollSource.UserInput) compact = controller.onScroll(consumed.y)
                return Offset.Zero
            }
        }
    }
    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).nestedScroll(connection)) {
        CompositionLocalProvider(LocalFloatingNavigationPadding provides if (showDock) 84.dp else 0.dp) {
            Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) { content() }
        }
        AnimatedVisibility(visible = showDock, modifier = Modifier.align(Alignment.BottomCenter), enter = fadeIn(), exit = fadeOut()) {
            // This container paints nothing. Content stays visible on every side of the dock.
            Box(Modifier.navigationBarsPadding().padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                FocusFlowBottomNav(items = items, currentRoute = items.firstOrNull { it.route == currentRoute }?.route ?: items.firstOrNull()?.route, compact = compact,
                    onNavigate = { controller.reset(); compact = false; onNavigate(it) })
            }
        }
    }
}
