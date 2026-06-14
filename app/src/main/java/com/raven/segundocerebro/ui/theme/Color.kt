package com.raven.segundocerebro.ui.theme

import androidx.compose.ui.graphics.Color
import com.raven.segundocerebro.data.ParaType

// Paleta "papel cálido"
val Paper = Color(0xFFFAF6F0)
val PaperDim = Color(0xFFF1EBE1)
val Card = Color(0xFFFFFDF9)
val Ink = Color(0xFF2A2622)
val InkSoft = Color(0xFF6B635A)
val Line = Color(0xFFE6DFD3)
val Terracotta = Color(0xFFC2674A)
val TerracottaSoft = Color(0xFFEAD4CB)

// Acentos por categoría P.A.R.A.
val ProjectColor = Color(0xFFC2674A)
val AreaColor = Color(0xFF5E8B7E)
val ResourceColor = Color(0xFF4A6FA5)
val ArchiveColor = Color(0xFF8A8178)
val InboxColor = Color(0xFFB08968)

fun ParaType.accent(): Color = when (this) {
    ParaType.INBOX -> InboxColor
    ParaType.PROJECT -> ProjectColor
    ParaType.AREA -> AreaColor
    ParaType.RESOURCE -> ResourceColor
    ParaType.ARCHIVE -> ArchiveColor
}
