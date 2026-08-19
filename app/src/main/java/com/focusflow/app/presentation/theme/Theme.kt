package com.focusflow.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.focusflow.app.domain.model.ThemeMode

val LocalSpacing = staticCompositionLocalOf { Spacing }

// 1. Warm Sage (Default)
val LightSageColorScheme = lightColorScheme(
    primary = Sage50, // #5E8C61 (Soft sage green)
    onPrimary = Color.White,
    primaryContainer = Sage95, // #E7F0E5 (Accent light)
    onPrimaryContainer = Sage20,
    secondary = Sand50,
    onSecondary = Color.White,
    secondaryContainer = Sand90, // #F3EDE3
    onSecondaryContainer = Sand20,
    tertiary = Amber50,
    onTertiary = Color.White,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber20,
    error = Red50,
    errorContainer = Red90,
    onError = Color.White,
    onErrorContainer = Red20,
    background = Neutral98, // #F7F7F5
    onBackground = Neutral10,
    surface = Neutral100, // #FFFFFF
    onSurface = Neutral10,
    surfaceVariant = Neutral95, // #F1F2EF
    onSurfaceVariant = Neutral40,
    outline = Neutral90, // #E6E6E2
    outlineVariant = NeutralVariant90
)

// 2. Minimalist Off-White & Charcoal
val LightOffWhiteColorScheme = lightColorScheme(
    primary = OffWhitePrimary, // #333D35
    onPrimary = Color.White,
    primaryContainer = OffWhitePrimaryContainer, // #EBE7DD
    onPrimaryContainer = OffWhiteText,
    secondary = Sand50,
    onSecondary = Color.White,
    secondaryContainer = OffWhiteSurfaceVar, // #F3F1EC
    onSecondaryContainer = OffWhiteText,
    tertiary = Amber50,
    onTertiary = Color.White,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber20,
    error = Red50,
    errorContainer = Red90,
    onError = Color.White,
    onErrorContainer = Red20,
    background = OffWhiteBg, // #FAF9F6
    onBackground = OffWhiteText,
    surface = OffWhiteSurface, // #FFFFFF
    onSurface = OffWhiteText,
    surfaceVariant = OffWhiteSurfaceVar, // #F3F1EC
    onSurfaceVariant = OffWhiteSecondaryText,
    outline = OffWhiteBorder, // #E9E5DD
    outlineVariant = NeutralVariant90
)

// 3. Clean Slate & Navy
val LightSlateColorScheme = lightColorScheme(
    primary = SlatePrimary, // #3E5C76
    onPrimary = Color.White,
    primaryContainer = SlatePrimaryContainer, // #E2EAF0
    onPrimaryContainer = SlateText,
    secondary = Sand50,
    onSecondary = Color.White,
    secondaryContainer = SlateSurfaceVar,
    onSecondaryContainer = SlateText,
    tertiary = Amber50,
    onTertiary = Color.White,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber20,
    error = Red50,
    errorContainer = Red90,
    onError = Color.White,
    onErrorContainer = Red20,
    background = SlateBg, // #F6F8FA
    onBackground = SlateText,
    surface = SlateSurface, // #FFFFFF
    onSurface = SlateText,
    surfaceVariant = SlateSurfaceVar, // #EDF1F5
    onSurfaceVariant = SlateSecondaryText,
    outline = SlateBorder, // #E1E5EA
    outlineVariant = NeutralVariant90
)

// 4. Dark Theme
val DarkColorScheme = darkColorScheme(
    primary = Sage70,
    onPrimary = Sage10,
    primaryContainer = Sage30,
    onPrimaryContainer = Sage90,
    secondary = Sand70,
    onSecondary = Sand10,
    secondaryContainer = Sand30,
    onSecondaryContainer = Sand90,
    tertiary = Amber70,
    onTertiary = Amber10,
    tertiaryContainer = Amber30,
    onTertiaryContainer = Amber90,
    error = Red70,
    errorContainer = Red20,
    onError = Red10,
    onErrorContainer = Red90,
    background = Neutral10,
    onBackground = Neutral95,
    surface = Neutral20,
    onSurface = Neutral95,
    surfaceVariant = Neutral30,
    onSurfaceVariant = Neutral70,
    outline = Neutral40,
    outlineVariant = Neutral30
)

@Composable
fun FocusFlowTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.DARK -> true
        else -> false
    }
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        themeMode == ThemeMode.LIGHT_OFFWHITE -> LightOffWhiteColorScheme
        themeMode == ThemeMode.LIGHT_SLATE -> LightSlateColorScheme
        else -> LightSageColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(LocalSpacing provides Spacing) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FocusFlowTypography,
            shapes = FocusFlowShapes,
            content = content
        )
    }
}
