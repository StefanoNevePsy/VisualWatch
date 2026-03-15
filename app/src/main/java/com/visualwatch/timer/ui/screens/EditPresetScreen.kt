package com.visualwatch.timer.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.visualwatch.timer.data.TimerPreset
import com.visualwatch.timer.ui.theme.TimerColors

@Composable
fun EditPresetScreen(
    preset: TimerPreset? = null,
    onSave: (TimerPreset) -> Unit,
    onDelete: ((String) -> Unit)? = null,
    onBack: () -> Unit
) {
    val isNew = preset == null
    var name by remember { mutableStateOf(preset?.name ?: "Nuovo preset") }
    var minutes by remember { mutableIntStateOf(((preset?.totalSeconds ?: 3000L) / 60).toInt()) }
    var finalSectorMinutes by remember {
        mutableIntStateOf(((preset?.finalSectorSeconds ?: 600L) / 60).toInt())
    }
    var selectedAnimation by remember {
        mutableStateOf(preset?.animationType ?: AnimationType.CIRCLE_SWEEP)
    }
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = if (isNew) "Nuovo preset" else "Modifica",
                fontSize = 15.sp,
                color = TimerColors.TextPrimary,
                textAlign = TextAlign.Center
            )
        }

        // Name display (simplified for watch - shows current name)
        item {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TimerColors.Green,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Quick name presets
        item {
            val nameOptions = listOf("Seduta", "Sessione", "Pausa", "Attività")
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                nameOptions.forEach { n ->
                    Button(
                        onClick = { name = "$n ${minutes}min" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = TimerColors.SurfaceLight
                        )
                    ) {
                        Text(text = n, fontSize = 8.sp, color = TimerColors.TextPrimary)
                    }
                }
            }
        }

        // Duration
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
                    name = name.replace(Regex("\\d+min"), "${minutes}min")
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

        // Final sector
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

        // Animation
        item {
            AnimationSelector(
                selected = selectedAnimation,
                onSelected = { selectedAnimation = it }
            )
        }

        // Save button
        item {
            Button(
                onClick = {
                    onSave(
                        TimerPreset(
                            id = preset?.id ?: java.util.UUID.randomUUID().toString(),
                            name = name,
                            totalSeconds = minutes * 60L,
                            finalSectorSeconds = finalSectorMinutes * 60L,
                            animationType = selectedAnimation
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = TimerColors.Green
                )
            ) {
                Text(
                    text = "SALVA",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimerColors.TextPrimary
                )
            }
        }

        // Delete button (only for existing presets)
        if (!isNew && onDelete != null) {
            item {
                Button(
                    onClick = { onDelete(preset!!.id) },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(top = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = TimerColors.Red
                    )
                ) {
                    Text(
                        text = "ELIMINA",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimerColors.TextPrimary
                    )
                }
            }
        }
    }
}
