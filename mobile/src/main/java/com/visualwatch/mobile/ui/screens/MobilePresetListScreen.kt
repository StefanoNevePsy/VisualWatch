package com.visualwatch.mobile.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visualwatch.shared.data.TimerPreset
import com.visualwatch.shared.theme.TimerColors
import com.visualwatch.shared.util.formatTime
import kotlinx.coroutines.flow.Flow

@Composable
fun MobilePresetListScreen(
    presetsFlow: Flow<List<TimerPreset>>,
    onPresetSelected: (TimerPreset) -> Unit,
    onManualSetup: () -> Unit,
    onEditPreset: (TimerPreset) -> Unit,
    onAddPreset: () -> Unit
) {
    val presets by presetsFlow.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = TimerColors.Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPreset,
                containerColor = TimerColors.SurfaceLight,
                contentColor = TimerColors.TextPrimary,
                shape = CircleShape
            ) {
                Text("+", fontSize = 24.sp)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "VisualWatch",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimerColors.TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                Button(
                    onClick = onManualSetup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TimerColors.Green
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Timer manuale",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TimerColors.TextPrimary
                    )
                }
            }

            items(presets, key = { it.id }) { preset ->
                MobilePresetItem(
                    preset = preset,
                    onClick = { onPresetSelected(preset) },
                    onLongClick = { onEditPreset(preset) }
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MobilePresetItem(
    preset: TimerPreset,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(TimerColors.Surface)
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                }
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = preset.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
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
                fontSize = 13.sp,
                color = TimerColors.TextSecondary
            )
        }

        // Play icon
        Text(
            text = "\u25B6",
            fontSize = 20.sp,
            color = TimerColors.Green,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
