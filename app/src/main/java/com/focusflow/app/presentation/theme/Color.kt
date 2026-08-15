package com.focusflow.app.presentation.theme

import androidx.compose.ui.graphics.Color

// Primary palette - Soft pastel lavender
val Lavender10 = Color(0xFF1F103A)
val Lavender20 = Color(0xFF331D5A)
val Lavender30 = Color(0xFF4C3080)
val Lavender40 = Color(0xFF6A47A5)
val Lavender50 = Color(0xFF8B6CC1) // Primary
val Lavender60 = Color(0xFFA58BD2)
val Lavender70 = Color(0xFFBCA6E1)
val Lavender80 = Color(0xFFD4C4EF)
val Lavender90 = Color(0xFFEBE1F8)
val Lavender95 = Color(0xFFF8F5FF)
val Lavender99 = Color(0xFFFCFBFF)

// Secondary palette - Pink/Lilac
val Lilac10 = Color(0xFF3A1832)
val Lilac20 = Color(0xFF5A2A4F)
val Lilac30 = Color(0xFF804272)
val Lilac40 = Color(0xFFA56095)
val Lilac50 = Color(0xFFD4A5CC) // Secondary
val Lilac60 = Color(0xFFDFB9D8)
val Lilac70 = Color(0xFFE8CBE3)
val Lilac80 = Color(0xFFF1DEEE)
val Lilac90 = Color(0xFFF9EEF7)
val Lilac95 = Color(0xFFFCF6FB)

// Tertiary palette - Warm amber/orange
val Amber10 = Color(0xFF3A1C00)
val Amber20 = Color(0xFF5C2E00)
val Amber30 = Color(0xFF804200)
val Amber40 = Color(0xFFA65800)
val Amber50 = Color(0xFFCC7000)
val Amber60 = Color(0xFFE89030)
val Amber70 = Color(0xFFF5AD5A)
val Amber80 = Color(0xFFFFCC8A)
val Amber90 = Color(0xFFFFE4C0)
val Amber95 = Color(0xFFFFF2E0)

// Error palette
val Red10 = Color(0xFF410002)
val Red20 = Color(0xFF690005)
val Red30 = Color(0xFF93000A)
val Red40 = Color(0xFFBA1A1A)
val Red50 = Color(0xFFDE3730)
val Red60 = Color(0xFFFF5449)
val Red70 = Color(0xFFFF897D)
val Red80 = Color(0xFFFFB4AB)
val Red90 = Color(0xFFFFDAD6)
val Red95 = Color(0xFFFFEDEA)

// Neutral palette
val Neutral0 = Color(0xFF000000)
val Neutral10 = Color(0xFF1A1A2E) // Near-black for primary text
val Neutral20 = Color(0xFF2D2D3F)
val Neutral30 = Color(0xFF414152)
val Neutral40 = Color(0xFF6B7280) // Muted gray for secondary text
val Neutral50 = Color(0xFF77777A)
val Neutral60 = Color(0xFF919094)
val Neutral70 = Color(0xFFABABAF)
val Neutral80 = Color(0xFFC7C6CA)
val Neutral90 = Color(0xFFE5E7EB)
val Neutral95 = Color(0xFFF3F4F6)
val Neutral98 = Color(0xFFF8F5FF) // Near-white lavender background
val Neutral99 = Color(0xFFFCFBFF)
val Neutral100 = Color(0xFFFFFFFF)

// Neutral variant
val NeutralVariant10 = Color(0xFF1C1A22)
val NeutralVariant20 = Color(0xFF312F37)
val NeutralVariant30 = Color(0xFF48454E)
val NeutralVariant40 = Color(0xFF605D67)
val NeutralVariant50 = Color(0xFF797680)
val NeutralVariant60 = Color(0xFF938F9A)
val NeutralVariant70 = Color(0xFFAEAAB5)
val NeutralVariant80 = Color(0xFFCAC5D0)
val NeutralVariant90 = Color(0xFFE6E1EC)
val NeutralVariant95 = Color(0xFFF5EFFA)

// Semantic colors
val SuccessGreen = Color(0xFF2ECC71)
val SuccessGreenDark = Color(0xFF27AE60)
val WarningOrange = Color(0xFFF39C12)
val InfoBlue = Color(0xFF3498DB)

// Focus mode colors
val FocusDeep = Color(0xFF1A1A2E)
val FocusAccent = Color(0xFF6C63FF)
val FocusProgress = Color(0xFF00E676)

// Commitment Lock colors
val CommitmentActive = Color(0xFF7B52C9)
val CommitmentWarning = Color(0xFFF39C12)
val CommitmentMissed = Color(0xFFE74C3C)
val CommitmentRecovery = Color(0xFF3498DB)
val CommitmentRestored = Color(0xFF2ECC71)

// Priority colors
val PriorityUrgent = Color(0xFFE74C3C)
val PriorityHigh = Color(0xFFF39C12)
val PriorityMedium = Color(0xFF3498DB)
val PriorityLow = Color(0xFF95A5A6)

// Category colors
val CategoryWork = Color(0xFF6C63FF)
val CategoryStudy = Color(0xFF00BCD4)
val CategoryPersonal = Color(0xFFFF6B6B)
val CategoryHealth = Color(0xFF2ECC71)
val CategoryCreative = Color(0xFFFFAB40)
val CategoryOther = Color(0xFF9E9E9E)

object FocusFlowDesign {
    val StatCardPurple = Color(0xFFF0EBF8)
    val StatCardPink = Color(0xFFF8EBF4)
    val StatCardBlue = Color(0xFFEBF2F8)
    
    val AiCardGradientStart = Lavender50
    val AiCardGradientEnd = Lilac50
    
    val ChatBubbleUser = Lavender50
    val ChatBubbleUserText = Color.White
    val ChatBubbleAi = Color(0xFFF0EBF8)
    val ChatBubbleAiText = Neutral10
    
    val TimelineIndicator = Lilac50
    val TimelineLine = Neutral90
    
    val BottomNavBackground = Color.White
    val BottomNavSelected = Lavender50
    val BottomNavUnselected = Neutral40
}
