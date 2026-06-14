package com.raven.segundocerebro.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Serif para titulares (aire editorial), sans del sistema para el cuerpo.
private val Serif = FontFamily.Serif

val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = Serif, fontWeight = FontWeight.Normal,
        fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Serif, fontWeight = FontWeight.Medium,
        fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = (-0.3).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Serif, fontWeight = FontWeight.Medium,
        fontSize = 20.sp, lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.2.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.8.sp
    )
)
