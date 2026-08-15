package com.focusflow.app.presentation.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Calendar", fontWeight = FontWeight.Bold) })
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Day") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Week") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Month") })
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> DayView()
                1 -> WeekView()
                2 -> MonthView()
            }
        }
    }
}

@Composable
fun DayView() {
    Text("Day View Timeline", style = MaterialTheme.typography.titleMedium)
    // Timeline drawing placeholder
}

@Composable
fun WeekView() {
    Text("Week View Layout", style = MaterialTheme.typography.titleMedium)
    // 7 column layout placeholder
}

@Composable
fun MonthView() {
    Text("Month View Grid", style = MaterialTheme.typography.titleMedium)
    // Month grid placeholder
}
