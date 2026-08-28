package com.focusflow.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val FocusFlowShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

// Intentional, architectural shape tokens
object FocusFlowCorners {
    val Card = RoundedCornerShape(14.dp)
    val CardSmall = RoundedCornerShape(10.dp)
    val CardLarge = RoundedCornerShape(18.dp)

    val Button = RoundedCornerShape(12.dp)
    val ButtonSmall = RoundedCornerShape(8.dp)
    val ButtonLarge = RoundedCornerShape(14.dp)

    val Chip = RoundedCornerShape(8.dp)
    val Input = RoundedCornerShape(10.dp)
    val BottomSheet = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    val Dialog = RoundedCornerShape(16.dp)

    val FloatingBottomNav = RoundedCornerShape(16.dp)
    val FloatingActionButton = RoundedCornerShape(14.dp)
    val ProgressBar = RoundedCornerShape(6.dp)
    val Avatar = RoundedCornerShape(50)
    val Full = RoundedCornerShape(50)

    val StatCard = RoundedCornerShape(14.dp)
    val ChatBubble = RoundedCornerShape(14.dp)
    val ChatBubbleCorner = RoundedCornerShape(4.dp)
    val TimelineIndicator = RoundedCornerShape(50)
    val CalendarPill = RoundedCornerShape(10.dp)
}

// Consistent Spacing System (8pt grid)
object Spacing {
    val xxxs = 2.dp
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 40.dp
    val huge = 48.dp
    val massive = 56.dp
}

// Subtle, layered 2.5D elevation tokens
object FocusFlowElevation {
    val none = 0.dp
    val flat = 0.dp
    val xs = 1.dp
    val sm = 2.dp
    val md = 4.dp
    val lg = 6.dp
    val xl = 8.dp
    val card = 1.dp
    val floatingNav = 4.dp
}
