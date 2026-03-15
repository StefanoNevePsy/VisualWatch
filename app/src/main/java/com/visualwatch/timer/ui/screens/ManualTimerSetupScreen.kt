package com.visualwatch.timer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.InlineSliderDefaults
import androidx.wear.compose.material.Text
import com.visualwatch.timer.data.AnimationType
import com.visualwatch.timer.ui.theme.TimerColors

@Composable
fun ManualTimerSetupScreen(
    onStartTimer: (totalMinutes: Int, finalSectorMinutes: Int, animationType: AnimationType) -> Unit,
    onBack: () -> Unit
) {
    var minutes by remember { mutableIntStateOf(50) }
    var finalSectorMinutes by remember { mutableIntStateOf(10) }
    var selectedAnimation by remember { mutableStateOf(AnimationType.CIRCLE_SWEEP) }
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Timer manuale",
                fontSize = 15.sp,
                color = TimerColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Minutes selector
        item {
            Text(
                text = "Durata: ${minutes} min",
                fontSize = 13.sp,
                color = TimerColors.TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        item {
            InlineSlider(
                value = minutes,
                onValueChange = {
                    minutes = it
                    if (finalSectorMinutes > minutes) {
                        finalSectorMinutes = minutes / 4
                    }
                },
                valueRange = 1..120,
                steps = 118,
                segmented = false,
                modifier = Modifier.fillMaxWidth(0.85f),
                colors = InlineSliderDefaults.colors(
                    selectedBarColor = TimerColors.Green
                )
            )
        }

        // Final sector selector
        item {
            Text(
                text = "Settore finale: ${finalSectorMinutes} min",
                fontSize = 13.sp,
                color = TimerColors.FinalSector,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        item {
            InlineSlider(
                value = finalSectorMinutes,
                onValueChange = { finalSectorMinutes = it },
                valueRange = 0..minutes.coerceAtLeast(1),
                steps = (minutes.coerceAtLeast(1) - 1).coerceAtLeast(0),
                segmented = false,
                modifier = Modifier.fillMaxWidth(0.85f),
                colors = InlineSliderDefaults.colors(
                    selectedBarColor = TimerColors.FinalSector
                )
            )
        }

        // Animation type
        item {
            Text(
                text = "Animazione",
                fontSize = 13.sp,
                color = TimerColors.TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        item {
            AnimationSelector(
                selected = selectedAnimation,
                onSelected = { selectedAnimation = it }
            )
        }

        // Start button
        item {
            Button(
                onClick = { onStartTimer(minutes, finalSectorMinutes, selectedAnimation) },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = TimerColors.Green
                )
            ) {
                Text(
                    text = "AVVIA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimerColors.TextPrimary
                )
            }
        }
    }
}

@Composable
fun AnimationSelector(
    selected: AnimationType,
    onSelected: (AnimationType) -> Unit
) {
    val animations = listOf(
        AnimationType.CIRCLE_SWEEP to "Cerchio",
        AnimationType.LIQUID_FILL to "Liquido",
        AnimationType.RADIAL_FADE to "Radiale",
        AnimationType.PIE_SLICE to "Torta"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth(0.95f)
    ) {
        animations.forEach { (type, label) ->
            val isSelected = selected == type
            Button(
                onClick = { onSelected(type) },
                modifier = Modifier.size(width = 48.dp, height = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isSelected) TimerColors.Green
                        else TimerColors.SurfaceLight
                )
            ) {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    color = TimerColors.TextPrimary
                )
            }
        }
    }
}
