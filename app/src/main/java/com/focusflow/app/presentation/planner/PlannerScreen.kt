package com.focusflow.app.presentation.planner

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ScheduleItem(
    val id: String,
    val title: String,
    val subject: String,
    val time: String,
    val isCompleted: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedDate by remember { mutableStateOf(LocalDate.of(2026, 4, 15)) } // Dummy April 2026 date
    val weekDays = (0..6).map { selectedDate.minusDays(selectedDate.dayOfWeek.value.toLong() - 1).plusDays(it.toLong()) }
    
    val scheduleItems = remember {
        mutableStateListOf(
            ScheduleItem("1", "Calculus Assignment", "Mathematics", "10:00 AM · 60 min", false),
            ScheduleItem("2", "Read Chapter 5", "Physics", "2:00 PM · 45 min", false),
            ScheduleItem("3", "Practice Problems", "Chemistry", "4:00 PM · 30 min", true)
        )
    }

    val backgroundColor = Color(0xFFF6F4FB)
    val purplePrimary = Color(0xFF7B52F4)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.padding(start = 8.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(28.dp))
                    }
                },
                actions = {
                    IconButton(
                        onClick = { /* Toggle theme */ },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(Color.White, CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Theme", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Heading
            Text(
                text = "Study\nPlanner !",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 36.sp
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            // Calendar Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Previous month */ }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
                }
                Text(
                    text = "April 2026",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                IconButton(onClick = { /* Next month */ }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
                }
            }

            // Week Selector
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(weekDays) { date ->
                    val isSelected = date == selectedDate
                    val dayName = date.format(DateTimeFormatter.ofPattern("EEE")).take(3)
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isSelected) purplePrimary else Color.Transparent)
                            .clickable { selectedDate = date }
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = dayName,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = date.dayOfMonth.toString(),
                            color = if (isSelected) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Summary Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pending Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8DFFE))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("2", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = purplePrimary)
                        Text("Tasks", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("Pending", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                // Total Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E6)) // light pink pastel
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("135", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE04F69))
                        Text("Time", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("Total min", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Today's Schedule Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Schedule",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { /* Add task */ },
                    modifier = Modifier
                        .background(Color.White, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeline
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                scheduleItems.forEachIndexed { index, item ->
                    val isLast = index == scheduleItems.size - 1
                    TimelineItem(
                        item = item,
                        isLast = isLast,
                        onToggle = { 
                            val updatedItem = item.copy(isCompleted = !item.isCompleted)
                            scheduleItems[index] = updatedItem
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TimelineItem(
    item: ScheduleItem,
    isLast: Boolean,
    onToggle: () -> Unit
) {
    val purplePrimary = Color(0xFF7B52F4)
    val lightGray = Color(0xFFE0E0E0)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Timeline graphic
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 28.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (item.isCompleted) lightGray else purplePrimary)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .padding(top = 4.dp, bottom = 4.dp)
                        .background(lightGray)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (item.isCompleted) purplePrimary else Color.Transparent)
                        .drawBehind {
                            if (!item.isCompleted) {
                                drawCircle(
                                    color = Color.LightGray,
                                    radius = size.minDimension / 2,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx())
                                )
                            }
                        }
                        .clickable(onClick = onToggle),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (item.isCompleted) Color.Gray else Color.Black,
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF3F0FA),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = item.subject,
                                fontSize = 12.sp,
                                color = purplePrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = item.time,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
