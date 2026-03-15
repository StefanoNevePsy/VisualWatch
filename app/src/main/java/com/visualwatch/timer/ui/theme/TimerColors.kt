package com.visualwatch.timer.ui.theme

import androidx.compose.ui.graphics.Color
import com.visualwatch.timer.data.TimerPhase

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

    fun phaseColor(phase: TimerPhase): Color = when (phase) {
        TimerPhase.NORMAL -> Green
        TimerPhase.WARNING -> Yellow
        TimerPhase.CRITICAL -> Red
        TimerPhase.FINAL_SECTOR -> FinalSector
        TimerPhase.FINISHED -> RedDark
    }

    fun progressColorDark(progress: Float, isInFinalSector: Boolean): Color {
        if (isInFinalSector) return FinalSectorDark
        return when {
            progress > 0.5f -> GreenDark
            progress > 0.25f -> YellowDark
            else -> RedDark
        }
    }

    // Lighter/brighter shade of the current progress color for the final sector marker
    fun finalSectorMarkerColor(progress: Float, isInFinalSector: Boolean): Color {
        if (isInFinalSector) return Color(0xFFFF80AB) // light pink
        return when {
            progress > 0.5f -> Color(0xFF81C784)  // light green
            progress > 0.25f -> Color(0xFFFFD54F) // light yellow
            progress > 0.1f -> Color(0xFFFFB74D)  // light orange
            else -> Color(0xFFEF9A9A)             // light red
        }
    }

    // Darker tint for the final sector background zone
    fun finalSectorZoneColor(progress: Float, isInFinalSector: Boolean): Color {
        if (isInFinalSector) return FinalSectorDark
        return when {
            progress > 0.5f -> GreenDark
            progress > 0.25f -> YellowDark
            else -> RedDark
        }
    }
}
