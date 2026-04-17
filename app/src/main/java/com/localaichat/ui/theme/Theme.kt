package com.localaichat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = InkBlue,
    onPrimary = Sand,
    secondary = Coral,
    tertiary = Seafoam,
    background = Sand,
    surface = ColorWhite,
    surfaceVariant = ColorMist,
)

private val DarkColors = darkColorScheme(
    primary = Seafoam,
    secondary = Coral,
    tertiary = Sand,
    background = InkBlue,
    surface = ColorDeep,
    surfaceVariant = ColorDeepVariant,
)

@Composable
fun LocalAIChatTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content,
    )
}
