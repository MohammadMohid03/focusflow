package com.focusflow.app.presentation.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// FocusFlow Premium Human-Crafted Palette
// Deep Botanical Pine / Forest Sage with warm neutrals
// ==========================================

// Primary Palette - Refined Botanical Forest Pine
val ForestPrimary = Color(0xFF1B3D2F)         // Deep, authoritative botanical pine
val ForestPrimaryHover = Color(0xFF244F3D)
val ForestAccent = Color(0xFF2D6A4F)          // Medium accent green
val ForestContainer = Color(0xFFE8F2EC)       // Soft tinted container
val ForestOnContainer = Color(0xFF112D22)

// Secondary Palette - Warm Oat / Sandstone
val WarmSand = Color(0xFF5E503F)
val WarmSandContainer = Color(0xFFF3EFE9)
val WarmSandOnContainer = Color(0xFF33291F)

// Tertiary / Warning - Refined Warm Amber & Bronze
val BronzeAmber = Color(0xFFC07018)
val BronzeContainer = Color(0xFFFDF3E6)
val BronzeOnContainer = Color(0xFF422100)

// Semantic Accent Colors
val SemanticSuccess = Color(0xFF2D6A4F)
val SemanticSuccessContainer = Color(0xFFE8F5EE)
val SemanticWarning = Color(0xFFC07018)
val SemanticWarningContainer = Color(0xFFFDF3E6)
val SemanticError = Color(0xFFB91C1C)
val SemanticErrorContainer = Color(0xFFFEF2F2)
val SemanticInfo = Color(0xFF2563EB)
val SemanticInfoContainer = Color(0xFFEFF6FF)

// Neutrals - Warm, crisp, editorial
val CanvasBackground = Color(0xFFF8F9FA)      // Clean warm off-white canvas
val SurfacePure = Color(0xFFFFFFFF)            // Elevated card surface
val SurfaceSubtle = Color(0xFFF1F3F5)          // Secondary surface / pills / chips
val BorderSubtle = Color(0xFFE5E7EB)           // Crisp 1dp borders
val BorderMedium = Color(0xFFD1D5DB)

// Typography Text Shades (High readability, WCAG AAA compliant)
val TextPrimary = Color(0xFF111827)            // Obsidian black
val TextSecondary = Color(0xFF4B5563)          // Refined slate
val TextTertiary = Color(0xFF9CA3AF)           // Muted supporting text
val TextOnPrimary = Color(0xFFFFFFFF)

// Dark Theme Palette - Deep Obsidian & Emerald Glow
val DarkCanvas = Color(0xFF0F1412)
val DarkSurface = Color(0xFF161D1A)
val DarkSurfaceSubtle = Color(0xFF1F2925)
val DarkBorder = Color(0xFF2B3A34)
val DarkPrimary = Color(0xFF52B788)
val DarkPrimaryContainer = Color(0xFF1E3A2F)
val DarkTextPrimary = Color(0xFFF3F4F6)
val DarkTextSecondary = Color(0xFF9CA3AF)

// Category Colors - Harmonious, earthy tones
val CategoryWork = Color(0xFF1B3D2F)
val CategoryStudy = Color(0xFF1E40AF)
val CategoryPersonal = Color(0xFFB45309)
val CategoryHealth = Color(0xFF15803D)
val CategoryCreative = Color(0xFF7E22CE)
val CategoryOther = Color(0xFF475569)

// Priority Colors
val PriorityUrgent = Color(0xFFB91C1C)
val PriorityHigh = Color(0xFFC07018)
val PriorityMedium = Color(0xFF2D6A4F)
val PriorityLow = Color(0xFF6B7280)

// Design system tokens for backwards compatibility
object FocusFlowDesign {
    val StatCardGreen = ForestContainer
    val StatCardSand = WarmSandContainer
    val StatCardWarm = SurfaceSubtle

    val AiCardGradientStart = ForestContainer
    val AiCardGradientEnd = WarmSandContainer

    val ChatBubbleUser = ForestPrimary
    val ChatBubbleUserText = Color.White
    val ChatBubbleAi = SurfaceSubtle
    val ChatBubbleAiText = TextPrimary

    val TimelineIndicator = ForestAccent
    val TimelineLine = BorderSubtle

    val FloatingNavBackground = SurfacePure
    val FloatingNavBorder = BorderSubtle
    val FloatingNavSelectedBg = ForestContainer
    val FloatingNavSelectedTint = ForestPrimary
    val FloatingNavUnselected = TextSecondary
}
