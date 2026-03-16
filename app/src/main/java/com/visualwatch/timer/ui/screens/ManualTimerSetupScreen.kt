package com.visualwatch.timer.ui.screens

import android.app.RemoteInput
import android.content.Intent
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.InlineSliderDefaults
import androidx.wear.compose.material.Text
import androidx.wear.input.RemoteInputIntentHelper
import com.visualwatch.shared.data.AnimationType
import com.visualwatch.shared.theme.TimerColors
import com.visualwatch.shared.util.formatTime
import com.visualwatch.shared.util.parseTimeInput

@Composable
fun ManualTimerSetupScreen(
    onStartTimer: (totalSeconds: Long, finalSectorSeconds: Long, animationType: AnimationType) -> Unit,
    onBack: () -> Unit
) {
    var totalSeconds by remember { mutableLongStateOf(50 * 60L) }
    var finalSectorSeconds by remember { mutableLongStateOf(10 * 60L) }
    var selectedAnimation by remember { mutableStateOf(AnimationType.CIRCLE_SWEEP) }
    val listState = rememberScalingLazyListState()

    val durationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            val results = RemoteInput.getResultsFromIntent(data)
            val text = results?.getCharSequence("time_input")?.toString() ?: return@let
            parseTimeInput(text)?.let { seconds ->
                totalSeconds = seconds
                if (finalSectorSeconds > totalSeconds) {
                    finalSectorSeconds = totalSeconds / 4
                }
            }
        }
    }

    val finalSectorLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            val results = RemoteInput.getResultsFromIntent(data)
            val text = results?.getCharSequence("time_input")?.toString() ?: return@let
            parseTimeInput(text)?.let { seconds ->
                finalSectorSeconds = seconds.coerceAtMost(totalSeconds)
            }
        }
    }

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

        item {
            TappableTimeLabel(
                label = "Durata",
                seconds = totalSeconds,
                color = TimerColors.TextSecondary,
                onClick = {
                    launchTimeInput(durationLauncher, "Durata (min o MM:SS)")
                }
            )
        }
        item {
            val sliderMinutes = (totalSeconds / 60).toInt().coerceIn(1, 120)
            InlineSlider(
                value = sliderMinutes,
                onValueChange = { newValue ->
                    totalSeconds = newValue * 60L
                    if (finalSectorSeconds > totalSeconds) {
                        finalSectorSeconds = totalSeconds / 4
                    }
                },
                valueProgression = 1..120,
                decreaseIcon = { Icon(InlineSliderDefaults.Decrease, "Diminuisci") },
                increaseIcon = { Icon(InlineSliderDefaults.Increase, "Aumenta") },
                segmented = false,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }

        item {
            TappableTimeLabel(
                label = "Settore finale",
                seconds = finalSectorSeconds,
                color = TimerColors.FinalSector,
                onClick = {
                    launchTimeInput(finalSectorLauncher, "Settore finale (min o MM:SS)")
                }
            )
        }
        item {
            val maxFinalMin = (totalSeconds / 60).toInt().coerceAtLeast(1)
            val sliderFinalMin = (finalSectorSeconds / 60).toInt().coerceIn(0, maxFinalMin)
            InlineSlider(
                value = sliderFinalMin,
                onValueChange = { newValue -> finalSectorSeconds = newValue * 60L },
                valueProgression = 0..maxFinalMin,
                decreaseIcon = { Icon(InlineSliderDefaults.Decrease, "Diminuisci") },
                increaseIcon = { Icon(InlineSliderDefaults.Increase, "Aumenta") },
                segmented = false,
                modifier = Modifier.fillMaxWidth(0.85f)
            )
        }

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

        item {
            Button(
                onClick = { onStartTimer(totalSeconds, finalSectorSeconds, selectedAnimation) },
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
fun TappableTimeLabel(
    label: String,
    seconds: Long,
    color: Color,
    onClick: () -> Unit
) {
    val displayText = if (seconds < 0) label else "$label: ${formatTime(seconds)}"
    Text(
        text = displayText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(top = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

private fun launchTimeInput(
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>,
    label: String
) {
    val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
    val remoteInput = RemoteInput.Builder("time_input")
        .setLabel(label)
        .build()
    remoteInput.extras.putInt("android.remoteinput.editChoicesBeforeSending",
        EditorInfo.IME_ACTION_DONE)
    RemoteInputIntentHelper.putRemoteInputsExtra(intent, listOf(remoteInput))
    launcher.launch(intent)
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
