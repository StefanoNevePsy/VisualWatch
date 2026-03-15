package com.visualwatch.timer.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.visualwatch.timer.data.AnimationType
import com.visualwatch.timer.data.TimerState
import com.visualwatch.timer.ui.animations.CircleSweepAnimation
import com.visualwatch.timer.ui.animations.LiquidFillAnimation
import com.visualwatch.timer.ui.animations.PieSliceAnimation
import com.visualwatch.timer.ui.animations.RadialFadeAnimation
import com.visualwatch.timer.ui.theme.TimerColors

@Composable
fun TimerDisplay(
    state: TimerState,
    isAmbient: Boolean = false,
    modifier: Modifier = Modifier
) {
    val finalSectorRatio = if (state.totalSeconds > 0) {
        state.finalSectorSeconds.toFloat() / state.totalSeconds
    } else 0f

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Animation background (skip complex rendering in ambient mode)
        if (!isAmbient) {
            when (state.animationType) {
                AnimationType.CIRCLE_SWEEP -> CircleSweepAnimation(
                    progress = state.progress,
                    isInFinalSector = state.isInFinalSector,
                    finalSectorRatio = finalSectorRatio
                )
                AnimationType.LIQUID_FILL -> LiquidFillAnimation(
                    progress = state.progress,
                    isInFinalSector = state.isInFinalSector,
                    finalSectorRatio = finalSectorRatio
                )
                AnimationType.RADIAL_FADE -> RadialFadeAnimation(
                    progress = state.progress,
                    isInFinalSector = state.isInFinalSector,
                    finalSectorRatio = finalSectorRatio
                )
                AnimationType.PIE_SLICE -> PieSliceAnimation(
                    progress = state.progress,
                    isInFinalSector = state.isInFinalSector,
                    finalSectorRatio = finalSectorRatio
                )
            }
        } else {
            // Simplified ambient display
            AmbientTimerDisplay(state)
        }

        // Time text overlay
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            // Main time
            Text(
                text = formatTime(state.remainingSeconds),
                fontSize = if (isAmbient) 28.sp else 32.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAmbient) TimerColors.TextSecondary
                    else TimerColors.TextPrimary,
                textAlign = TextAlign.Center
            )

            // Final sector indicator
            if (state.isInFinalSector && !state.isFinished) {
                Text(
                    text = "⬤ ${formatTime(state.remainingSeconds)}",
                    fontSize = 14.sp,
                    color = TimerColors.FinalSector,
                    textAlign = TextAlign.Center
                )
            }

            // Status
            if (state.isPaused) {
                Text(
                    text = "PAUSA",
                    fontSize = 12.sp,
                    color = TimerColors.Yellow,
                    textAlign = TextAlign.Center
                )
            } else if (state.isFinished) {
                Text(
                    text = "TEMPO SCADUTO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimerColors.Red,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AmbientTimerDisplay(state: TimerState) {
    // Minimal display for AOD — just outline arc to save power
    val finalSectorRatio = if (state.totalSeconds > 0) {
        state.finalSectorSeconds.toFloat() / state.totalSeconds
    } else 0f

    CircleSweepAnimation(
        progress = state.progress,
        isInFinalSector = state.isInFinalSector,
        finalSectorRatio = finalSectorRatio
    )
}

fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
    else String.format("%02d:%02d", m, s)
}
