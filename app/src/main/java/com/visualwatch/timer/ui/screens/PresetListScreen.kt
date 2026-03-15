package com.visualwatch.timer.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import com.visualwatch.timer.data.TimerPreset
import com.visualwatch.timer.ui.components.formatTime
import com.visualwatch.timer.ui.theme.TimerColors
import kotlinx.coroutines.flow.Flow

@Composable
fun PresetListScreen(
    presetsFlow: Flow<List<TimerPreset>>,
    onPresetSelected: (TimerPreset) -> Unit,
    onManualSetup: () -> Unit,
    onEditPreset: (TimerPreset) -> Unit,
    onAddPreset: () -> Unit
) {
    val presets by presetsFlow.collectAsState(initial = emptyList())
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        item {
            Text(
                text = "VisualWatch",
                fontSize = 16.sp,
                color = TimerColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Manual timer button
        item {
            Button(
                onClick = onManualSetup,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 2.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = TimerColors.Green
                )
            ) {
                Text(
                    text = "Timer manuale",
                    fontSize = 14.sp,
                    color = TimerColors.TextPrimary
                )
            }
        }

        // Preset items
        items(presets, key = { it.id }) { preset ->
            PresetItem(
                preset = preset,
                onClick = { onPresetSelected(preset) },
                onLongClick = { onEditPreset(preset) }
            )
        }

        // Add preset button
        item {
            Button(
                onClick = onAddPreset,
                modifier = Modifier.size(40.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = TimerColors.SurfaceLight
                )
            ) {
                Text("+", fontSize = 20.sp, color = TimerColors.TextPrimary)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PresetItem(
    preset: TimerPreset,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(TimerColors.Surface)
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = preset.name,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = TimerColors.TextPrimary
        )
        val info = buildString {
            append(formatTime(preset.totalSeconds))
            if (preset.finalSectorSeconds > 0) {
                append("  \u2022  fin: ${formatTime(preset.finalSectorSeconds)}")
            }
        }
        Text(
            text = info,
            fontSize = 11.sp,
            color = TimerColors.TextSecondary
        )
    }
}
