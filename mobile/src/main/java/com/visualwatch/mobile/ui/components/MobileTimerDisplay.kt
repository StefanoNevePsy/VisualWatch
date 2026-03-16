package com.visualwatch.mobile.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visualwatch.shared.animations.CircleSweepAnimation
import com.visualwatch.shared.animations.LiquidFillAnimation
import com.visualwatch.shared.animations.PieSliceAnimation
import com.visualwatch.shared.animations.RadialFadeAnimation
import com.visualwatch.shared.data.AnimationType
import com.visualwatch.shared.data.TimerState
import com.visualwatch.shared.theme.TimerColors
import com.visualwatch.shared.util.formatTime

@Composable
fun MobileTimerDisplay(
    state: TimerState,
    isDimmed: Boolean = false,
    modifier: Modifier = Modifier
) {
    val finalSectorRatio = if (state.totalSeconds > 0) {
        state.finalSectorSeconds.toFloat() / state.totalSeconds
    } else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // Animation
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

        // Time text overlay
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = formatTime(state.remainingSeconds),
                fontSize = if (isDimmed) 40.sp else 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDimmed) TimerColors.TextSecondary
                    else TimerColors.TextPrimary,
                textAlign = TextAlign.Center
            )

            if (state.isInFinalSector && !state.isFinished) {
                Text(
                    text = "\u2B24 Settore finale",
                    fontSize = 16.sp,
                    color = TimerColors.FinalSector,
                    textAlign = TextAlign.Center
                )
            }

            if (state.isPaused) {
                Text(
                    text = "PAUSA",
                    fontSize = 16.sp,
                    color = TimerColors.Yellow,
                    textAlign = TextAlign.Center
                )
            } else if (state.isFinished) {
                Text(
                    text = "TEMPO SCADUTO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimerColors.Red,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
