package com.visualwatch.shared.theme

import androidx.compose.ui.graphics.Color

object TimerColors {
    val Green = Color(0xFF4CAF50)
    val GreenDark = Color(0xFF2E7D32)
    val Yellow = Color(0xFFFFC107)
    val YellowDark = Color(0xFFF57F17)
    val Orange = Color(0xFFFF9800)
    val Red = Color(0xFFF44336)
    val RedDark = Color(0xFFC62828)
    val FinalSector = Color(0xFFE91E63)
    val FinalSectorDark = Color(0xFF880E4F)
    val Background = Color(0xFF000000)
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFB0BEC5)
    val Surface = Color(0xFF1A1A1A)
    val SurfaceLight = Color(0xFF2A2A2A)

    fun progressColor(progress: Float, isInFinalSector: Boolean): Color {
        if (isInFinalSector) return FinalSector
        return when {
            progress > 0.5f -> Green
            progress > 0.25f -> Yellow
            progress > 0.1f -> Orange
            else -> Red
        }
    }

    fun phaseColor(phase: com.visualwatch.shared.data.TimerPhase): Color = when (phase) {
        com.visualwatch.shared.data.TimerPhase.NORMAL -> Green
        com.visualwatch.shared.data.TimerPhase.WARNING -> Yellow
        com.visualwatch.shared.data.TimerPhase.CRITICAL -> Red
        com.visualwatch.shared.data.TimerPhase.FINAL_SECTOR -> FinalSector
        com.visualwatch.shared.data.TimerPhase.FINISHED -> RedDark
    }

    fun progressColorDark(progress: Float, isInFinalSector: Boolean): Color {
        if (isInFinalSector) return FinalSectorDark
        return when {
            progress > 0.5f -> GreenDark
            progress > 0.25f -> YellowDark
            else -> RedDark
        }
    }

    fun finalSectorMarkerColor(progress: Float, isInFinalSector: Boolean): Color {
        if (isInFinalSector) return Color(0xFFFF80AB)
        return when {
            progress > 0.5f -> Color(0xFF81C784)
            progress > 0.25f -> Color(0xFFFFD54F)
            progress > 0.1f -> Color(0xFFFFB74D)
            else -> Color(0xFFEF9A9A)
        }
    }

    fun finalSectorZoneColor(progress: Float, isInFinalSector: Boolean): Color {
        if (isInFinalSector) return FinalSectorDark
        return when {
            progress > 0.5f -> GreenDark
            progress > 0.25f -> YellowDark
            else -> RedDark
        }
    }
}
