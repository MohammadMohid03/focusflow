package com.focusflow.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val FocusFlowShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// Custom shape tokens for specific components
object FocusFlowCorners {
    val Card = RoundedCornerShape(20.dp) // Major cards: 18-22dp
    val CardSmall = RoundedCornerShape(16.dp)
    val CardLarge = RoundedCornerShape(22.dp)
    val Button = RoundedCornerShape(14.dp) // Small cards/buttons: 12-16dp
    val ButtonSmall = RoundedCornerShape(12.dp)
    val ButtonLarge = RoundedCornerShape(16.dp)
    val Chip = RoundedCornerShape(12.dp)
    val Input = RoundedCornerShape(14.dp)
    val BottomSheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val Dialog = RoundedCornerShape(24.dp)
    val FloatingBottomNav = RoundedCornerShape(24.dp) // Modern floating pill
    val FloatingActionButton = RoundedCornerShape(16.dp)
    val ProgressBar = RoundedCornerShape(8.dp)
    val Avatar = RoundedCornerShape(50)
    val Full = RoundedCornerShape(50)
    
    val StatCard = RoundedCornerShape(20.dp)
    val ChatBubble = RoundedCornerShape(18.dp)
    val ChatBubbleCorner = RoundedCornerShape(4.dp)
    val TimelineIndicator = RoundedCornerShape(50)
    val CalendarPill = RoundedCornerShape(14.dp)
}

// Spacing tokens
object Spacing {
    val xxxs = 2.dp
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 28.dp
    val xxl = 36.dp
    val xxxl = 44.dp
    val huge = 52.dp
    val massive = 60.dp
}

// Elevation tokens - Subtle, modern shadows
object FocusFlowElevation {
    val none = 0.dp
    val xs = 1.dp
    val sm = 2.dp
    val md = 4.dp
    val lg = 6.dp
    val xl = 8.dp
    val xxl = 12.dp
    val card = 2.dp
    val floatingNav = 6.dp
}
