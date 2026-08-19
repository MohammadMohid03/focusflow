package com.focusflow.app.presentation.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.focusflow.app.presentation.theme.FocusFlowCorners
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onNavigateToPreferences: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage("Smart Planning", "Organize your study goals, schedules, and daily milestones.", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer),
        OnboardingPage("AI Assistant", "Get personalized recommendations, problem-solving, and study tips.", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondaryContainer),
        OnboardingPage("Deep Focus", "Achieve more with customizable Pomodoro sessions and Zen Mode.", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiaryContainer),
        OnboardingPage("Track Progress", "Visualize your daily streaks, completion rates, and focus analytics.", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
    )

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { position ->
            val page = pages[position]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(listOf(page.colorStart.copy(alpha = 0.3f), page.colorEnd))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${position + 1}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    viewModel.completeOnboarding()
                    onNavigateToPreferences()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(4) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    val width by animateFloatAsState(targetValue = if (isSelected) 24f else 8f, label = "indicator")
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .height(8.dp)
                            .width(width.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < 3) {
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        viewModel.completeOnboarding()
                        onNavigateToPreferences()
                    }
                },
                modifier = Modifier.weight(1f),
                shape = FocusFlowCorners.Button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (pagerState.currentPage == 3) "Get Started" else "Next", fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class OnboardingPage(val title: String, val description: String, val colorStart: Color, val colorEnd: Color)
