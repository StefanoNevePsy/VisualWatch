package com.visualwatch.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visualwatch.shared.data.AnimationType
import com.visualwatch.shared.data.TimerPreset
import com.visualwatch.shared.theme.TimerColors
import com.visualwatch.shared.util.formatTime
import com.visualwatch.shared.util.parseTimeInput

@Composable
fun MobileEditPresetScreen(
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
    var durationInput by remember { mutableStateOf("") }
    var finalInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isNew) "Nuovo preset" else "Modifica preset",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TimerColors.TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Name field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome", color = TimerColors.TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TimerColors.TextPrimary,
                unfocusedTextColor = TimerColors.TextPrimary,
                focusedBorderColor = TimerColors.Green,
                unfocusedBorderColor = TimerColors.SurfaceLight,
                cursorColor = TimerColors.Green
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Quick name presets
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            listOf("Seduta", "Sessione", "Pausa", "Attività").forEach { n ->
                FilterChip(
                    selected = false,
                    onClick = { name = "$n ${totalSeconds / 60}min" },
                    label = { Text(n, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = TimerColors.SurfaceLight,
                        labelColor = TimerColors.TextSecondary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Duration
        Text(
            text = "Durata: ${formatTime(totalSeconds)}",
            fontSize = 16.sp,
            color = TimerColors.TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = durationInput,
            onValueChange = { durationInput = it },
            label = { Text("Minuti o MM:SS", color = TimerColors.TextSecondary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                parseTimeInput(durationInput)?.let {
                    totalSeconds = it
                    name = name.replace(Regex("\\d+min"), "${totalSeconds / 60}min")
                    if (finalSectorSeconds > totalSeconds) finalSectorSeconds = totalSeconds / 4
                }
                durationInput = ""
                focusManager.clearFocus()
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TimerColors.TextPrimary,
                unfocusedTextColor = TimerColors.TextPrimary,
                focusedBorderColor = TimerColors.Green,
                unfocusedBorderColor = TimerColors.SurfaceLight,
                cursorColor = TimerColors.Green
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Slider(
            value = (totalSeconds / 60f).coerceIn(1f, 120f),
            onValueChange = {
                totalSeconds = it.toLong() * 60
                name = name.replace(Regex("\\d+min"), "${it.toLong()}min")
                if (finalSectorSeconds > totalSeconds) finalSectorSeconds = totalSeconds / 4
            },
            valueRange = 1f..120f,
            steps = 118,
            colors = SliderDefaults.colors(thumbColor = TimerColors.Green, activeTrackColor = TimerColors.Green),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Final sector
        Text(
            text = "Settore finale: ${formatTime(finalSectorSeconds)}",
            fontSize = 16.sp,
            color = TimerColors.FinalSector,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = finalInput,
            onValueChange = { finalInput = it },
            label = { Text("Minuti o MM:SS", color = TimerColors.TextSecondary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                parseTimeInput(finalInput)?.let { finalSectorSeconds = it.coerceAtMost(totalSeconds) }
                finalInput = ""
                focusManager.clearFocus()
            }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TimerColors.TextPrimary,
                unfocusedTextColor = TimerColors.TextPrimary,
                focusedBorderColor = TimerColors.FinalSector,
                unfocusedBorderColor = TimerColors.SurfaceLight,
                cursorColor = TimerColors.FinalSector
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        val maxFinalMin = (totalSeconds / 60f).coerceAtLeast(1f)
        Slider(
            value = (finalSectorSeconds / 60f).coerceIn(0f, maxFinalMin),
            onValueChange = { finalSectorSeconds = it.toLong() * 60 },
            valueRange = 0f..maxFinalMin,
            steps = maxFinalMin.toInt().coerceAtLeast(1) - 1,
            colors = SliderDefaults.colors(thumbColor = TimerColors.FinalSector, activeTrackColor = TimerColors.FinalSector),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // Animation
        Text("Animazione", fontSize = 14.sp, color = TimerColors.TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf(
                AnimationType.CIRCLE_SWEEP to "Cerchio",
                AnimationType.LIQUID_FILL to "Liquido",
                AnimationType.RADIAL_FADE to "Radiale",
                AnimationType.PIE_SLICE to "Torta"
            ).forEach { (type, label) ->
                FilterChip(
                    selected = selectedAnimation == type,
                    onClick = { selectedAnimation = type },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TimerColors.Green,
                        selectedLabelColor = TimerColors.TextPrimary,
                        containerColor = TimerColors.SurfaceLight,
                        labelColor = TimerColors.TextSecondary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                onSave(TimerPreset(
                    id = preset?.id ?: java.util.UUID.randomUUID().toString(),
                    name = name,
                    totalSeconds = totalSeconds,
                    finalSectorSeconds = finalSectorSeconds,
                    animationType = selectedAnimation
                ))
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TimerColors.Green),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("SALVA", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TimerColors.TextPrimary)
        }

        if (!isNew && onDelete != null) {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onDelete(preset!!.id) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TimerColors.Red),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("ELIMINA", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TimerColors.TextPrimary)
            }
        }
    }
}
