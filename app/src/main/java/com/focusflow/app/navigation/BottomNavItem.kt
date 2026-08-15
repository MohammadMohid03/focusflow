package com.focusflow.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
) {
    Home(Screen.Home.route, Icons.Outlined.Home, Icons.Filled.Home, "Home"),
    Planner(Screen.Planner.route, Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth, "Planner"),
    AiChat(Screen.AiChat.route, Icons.Outlined.AutoAwesome, Icons.Filled.AutoAwesome, "AI Chat"),
    Profile(Screen.Profile.route, Icons.Outlined.Person, Icons.Filled.Person, "Profile")
}
