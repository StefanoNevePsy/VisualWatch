package com.visualwatch.timer.ui.screens

import android.app.RemoteInput
import android.content.Intent
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.InlineSliderDefaults
import androidx.wear.compose.material.Text
import androidx.wear.input.RemoteInputIntentHelper
import com.visualwatch.shared.data.AnimationType
import com.visualwatch.shared.data.TimerPreset
import com.visualwatch.shared.theme.TimerColors
import com.visualwatch.shared.util.formatTime
import com.visualwatch.shared.util.parseTimeInput

@Composable
fun EditPresetScreen(
    preset: TimerPreset? = null,
    onSave: (TimerPreset) -> Unit,
    onDelete: ((String) -> Unit)? = null,
    onBack: () -> Unit
) {
    val isNew = preset == null
    var name by remember { mutableStateOf(preset?.name ?: "Nuovo preset") }
    var totalSeconds by remember { mutableLongStateOf(preset?.totalSeconds ?: 3000L) }
    var finalSectorSeconds by remember { mutableLongStateOf(preset?.finalSectorSeconds ?: 600L) }
    var selectedAnimation by remember {
        mutableStateOf(preset?.animationType ?: AnimationType.CIRCLE_SWEEP)
    }
    val listState = rememberScalingLazyListState()

    val durationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            val results = RemoteInput.getResultsFromIntent(data)
            val text = results?.getCharSequence("time_input")?.toString() ?: return@let
            parseTimeInput(text)?.let { seconds ->
                totalSeconds = seconds
                name = name.replace(Regex("\\d+min"), "${totalSeconds / 60}min")
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

    val nameLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.let { data ->
            val results = RemoteInput.getResultsFromIntent(data)
            val text = results?.getCharSequence("name_input")?.toString()
            if (!text.isNullOrBlank()) {
                name = text
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
                text = if (isNew) "Nuovo preset" else "Modifica",
                fontSize = 15.sp,
                color = TimerColors.TextPrimary,
                textAlign = TextAlign.Center
            )
        }

        item {
            TappableTimeLabel(
                label = name,
                seconds = -1,
                color = TimerColors.Green,
                onClick = {
                    val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                    val remoteInput = RemoteInput.Builder("name_input")
                        .setLabel("Nome preset")
                        .build()
                    remoteInput.extras.putInt(
                        "android.remoteinput.editChoicesBeforeSending",
                        EditorInfo.IME_ACTION_DONE
                    )
                    RemoteInputIntentHelper.putRemoteInputsExtra(intent, listOf(remoteInput))
                    nameLauncher.launch(intent)
                }
            )
        }

        item {
            val nameOptions = listOf("Seduta", "Sessione", "Pausa", "Attività")
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                nameOptions.forEach { n ->
                    Button(
                        onClick = { name = "$n ${totalSeconds / 60}min" },
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

        item {
            TappableTimeLabel(
                label = "Durata",
                seconds = totalSeconds,
                color = TimerColors.TextSecondary,
                onClick = {
                    launchTimeInputForEdit(durationLauncher, "Durata (min o MM:SS)")
                }
            )
        }
        item {
            val sliderMinutes = (totalSeconds / 60).toInt().coerceIn(1, 120)
            InlineSlider(
                value = sliderMinutes,
                onValueChange = { newValue ->
                    totalSeconds = newValue * 60L
                    name = name.replace(Regex("\\d+min"), "${newValue}min")
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
                    launchTimeInputForEdit(finalSectorLauncher, "Settore finale (min o MM:SS)")
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
            AnimationSelector(
                selected = selectedAnimation,
                onSelected = { selectedAnimation = it }
            )
        }

        item {
            Button(
                onClick = {
                    onSave(
                        TimerPreset(
                            id = preset?.id ?: java.util.UUID.randomUUID().toString(),
                            name = name,
                            totalSeconds = totalSeconds,
                            finalSectorSeconds = finalSectorSeconds,
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

private fun launchTimeInputForEdit(
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>,
    label: String
) {
    val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
    val remoteInput = RemoteInput.Builder("time_input")
        .setLabel(label)
        .build()
    remoteInput.extras.putInt(
        "android.remoteinput.editChoicesBeforeSending",
        EditorInfo.IME_ACTION_DONE
    )
    RemoteInputIntentHelper.putRemoteInputsExtra(intent, listOf(remoteInput))
    launcher.launch(intent)
}
