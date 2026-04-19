package com.localaichat.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkScheme = darkColorScheme(
    primary              = NeonCyan,
    onPrimary            = VoidBlack,
    primaryContainer     = NeonCyanGhost,
    onPrimaryContainer   = NeonCyan,
    secondary            = ElectricViolet,
    onSecondary          = VoidBlack,
    secondaryContainer   = ElectricVioletGhost,
    onSecondaryContainer = ElectricViolet,
    tertiary             = Amber,
    onTertiary           = VoidBlack,
    tertiaryContainer    = AmberGhost,
    onTertiaryContainer  = Amber,
    error                = ErrorRed,
    onError              = VoidBlack,
    errorContainer       = Color(0x33FF6B7A),
    onErrorContainer     = ErrorRed,
    background           = VoidBlack,
    onBackground         = PureWhite,
    surface              = DeepCharcoal,
    onSurface            = PureWhite,
    surfaceVariant       = MidCharcoal,
    onSurfaceVariant     = DimWhite,
    outline              = MutedGray,
    outlineVariant       = SubtleGray,
    inverseSurface       = PureWhite,
    inverseOnSurface     = VoidBlack,
    inversePrimary       = NeonCyanDim,
    surfaceTint          = NeonCyan,
)

// Keep the legacy light scheme reference for compilation compatibility
private val LightColors = darkColorScheme(
    primary = NeonCyan,
    onPrimary = VoidBlack,
    secondary = ElectricViolet,
    tertiary = Amber,
    background = VoidBlack,
    surface = DeepCharcoal,
    surfaceVariant = MidCharcoal,
)

@Composable
fun LocalAIChatTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = VoidBlack.toArgb()
            window.navigationBarColor = VoidBlack.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
