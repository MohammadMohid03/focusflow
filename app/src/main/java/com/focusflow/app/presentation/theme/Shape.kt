package com.focusflow.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val FocusFlowShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

// Custom shape tokens for specific components
object FocusFlowCorners {
    val Card = RoundedCornerShape(20.dp)
    val CardLarge = RoundedCornerShape(24.dp)
    val Button = RoundedCornerShape(14.dp)
    val ButtonLarge = RoundedCornerShape(20.dp)
    val Chip = RoundedCornerShape(24.dp)
    val Input = RoundedCornerShape(16.dp)
    val BottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val Dialog = RoundedCornerShape(28.dp)
    val BottomNav = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val FloatingActionButton = RoundedCornerShape(20.dp)
    val ProgressBar = RoundedCornerShape(10.dp)
    val Avatar = RoundedCornerShape(50)
    val Full = RoundedCornerShape(50)
    
    // Newly added shapes for the premium design
    val StatCard = RoundedCornerShape(22.dp)
    val ChatBubble = RoundedCornerShape(20.dp)
    val ChatBubbleCorner = RoundedCornerShape(4.dp)
    val TimelineIndicator = RoundedCornerShape(50)
    val CalendarPill = RoundedCornerShape(50)
}

// Spacing tokens
object Spacing {
    val xxxs = 2.dp
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 40.dp
    val xxxl = 48.dp
    val huge = 56.dp
    val massive = 64.dp
}

// Elevation tokens
object FocusFlowElevation {
    val none = 0.dp
    val xs = 1.dp
    val sm = 2.dp
    val md = 4.dp
    val lg = 6.dp
    val xl = 8.dp
    val xxl = 12.dp
    val card = 2.dp
    val cardHover = 6.dp
}
