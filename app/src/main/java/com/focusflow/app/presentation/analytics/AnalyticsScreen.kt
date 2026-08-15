package com.focusflow.app.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Analytics", fontWeight = FontWeight.Bold) })
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Week") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Month") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Year") })
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Overview Cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OverviewCard(title = "Focus Time", value = "${uiState.totalFocusTime / 60}h", modifier = Modifier.weight(1f))
                OverviewCard(title = "Tasks", value = "${uiState.tasksCompleted}", modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OverviewCard(title = "Completion", value = "${(uiState.completionRate * 100).toInt()}%", modifier = Modifier.weight(1f))
                OverviewCard(title = "Streak", value = "${uiState.currentStreak} 🔥", modifier = Modifier.weight(1f))
            }

            // Charts Section
            Text("Productivity Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            PlaceholderChart()

            // Commitment Analytics
            Text("Commitment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            CommitmentScoreCard(score = uiState.commitmentScore)

            // AI Insights
            Text("AI Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("You focus best on Tuesdays between 9 AM and 11 AM.")
                }
            }
        }
    }
}

@Composable
fun OverviewCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun PlaceholderChart() {
    val primaryColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height
            val step = width / 6

            for (i in 0..6) {
                val barHeight = (Math.random() * height * 0.8).toFloat() + 20f
                drawLine(
                    color = primaryColor,
                    start = androidx.compose.ui.geometry.Offset(i * step, height),
                    end = androidx.compose.ui.geometry.Offset(i * step, height - barHeight),
                    strokeWidth = 32f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun CommitmentScoreCard(score: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Commitment Score", style = MaterialTheme.typography.titleMedium)
                Text("Top 10% of users", style = MaterialTheme.typography.bodySmall)
            }
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                val color = MaterialTheme.colorScheme.tertiary
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.Gray.copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12f)
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360f * (score / 100f),
                        useCenter = false,
                        style = Stroke(width = 12f, cap = StrokeCap.Round)
                    )
                }
                Text("$score", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}
