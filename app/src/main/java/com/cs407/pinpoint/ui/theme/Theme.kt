package com.cs407.pinpoint.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PinPointPrimary,
    onPrimary = Color.Black,
    primaryContainer = PinPointGreenAccent,
    secondary = PinPointRed,
    background = BackgroundLight,
    surface = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = PinPointPrimary,
    onPrimary = Color.Black,
    primaryContainer = PinPointBackground,
    secondary = PinPointRed,
    background = BackgroundDark,
    surface = Color(0xFF2A2A2A),
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun PinPointTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}