package com.raven.segundocerebro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    primaryContainer = TerracottaSoft,
    onPrimaryContainer = Ink,
    secondary = AreaColor,
    background = Paper,
    onBackground = Ink,
    surface = Card,
    onSurface = Ink,
    surfaceVariant = PaperDim,
    onSurfaceVariant = InkSoft,
    outline = Line,
    outlineVariant = Line
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE0997E),
    onPrimary = Color(0xFF2A1A12),
    background = Color(0xFF1A1714),
    onBackground = Color(0xFFEDE6DC),
    surface = Color(0xFF231F1B),
    onSurface = Color(0xFFEDE6DC),
    surfaceVariant = Color(0xFF2C2722),
    onSurfaceVariant = Color(0xFFB6ABA0),
    outline = Color(0xFF3A342E)
)

@Composable
fun SegundoCerebroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
