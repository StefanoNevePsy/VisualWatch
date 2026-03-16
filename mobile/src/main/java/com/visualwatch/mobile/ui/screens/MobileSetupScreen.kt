package com.visualwatch.mobile.ui.screens

import androidx.compose.foundation.clickable
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
import com.visualwatch.shared.theme.TimerColors
import com.visualwatch.shared.util.formatTime
import com.visualwatch.shared.util.parseTimeInput

@Composable
fun MobileSetupScreen(
    onStartTimer: (totalSeconds: Long, finalSectorSeconds: Long, animationType: AnimationType) -> Unit,
    onBack: () -> Unit
) {
    var totalSeconds by remember { mutableLongStateOf(50 * 60L) }
    var finalSectorSeconds by remember { mutableLongStateOf(10 * 60L) }
    var selectedAnimation by remember { mutableStateOf(AnimationType.CIRCLE_SWEEP) }
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
            text = "Timer manuale",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TimerColors.TextPrimary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Duration section
        Text(
            text = "Durata: ${formatTime(totalSeconds)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = TimerColors.TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = durationInput,
            onValueChange = { durationInput = it },
            label = { Text("Minuti o MM:SS", color = TimerColors.TextSecondary) },
            placeholder = { Text("es. 50, 4:25, 1:30:00", color = TimerColors.TextSecondary.copy(alpha = 0.5f)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                parseTimeInput(durationInput)?.let {
                    totalSeconds = it
                    if (finalSectorSeconds > totalSeconds) {
                        finalSectorSeconds = totalSeconds / 4
                    }
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
                if (finalSectorSeconds > totalSeconds) {
                    finalSectorSeconds = totalSeconds / 4
                }
            },
            valueRange = 1f..120f,
            steps = 118,
            colors = SliderDefaults.colors(
                thumbColor = TimerColors.Green,
                activeTrackColor = TimerColors.Green
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Final sector section
        Text(
            text = "Settore finale: ${formatTime(finalSectorSeconds)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = TimerColors.FinalSector,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = finalInput,
            onValueChange = { finalInput = it },
            label = { Text("Minuti o MM:SS", color = TimerColors.TextSecondary) },
            placeholder = { Text("es. 10, 5:30", color = TimerColors.TextSecondary.copy(alpha = 0.5f)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                parseTimeInput(finalInput)?.let {
                    finalSectorSeconds = it.coerceAtMost(totalSeconds)
                }
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
            colors = SliderDefaults.colors(
                thumbColor = TimerColors.FinalSector,
                activeTrackColor = TimerColors.FinalSector
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Animation selector
        Text(
            text = "Animazione",
            fontSize = 16.sp,
            color = TimerColors.TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val animations = listOf(
                AnimationType.CIRCLE_SWEEP to "Cerchio",
                AnimationType.LIQUID_FILL to "Liquido",
                AnimationType.RADIAL_FADE to "Radiale",
                AnimationType.PIE_SLICE to "Torta"
            )
            animations.forEach { (type, label) ->
                FilterChip(
                    selected = selectedAnimation == type,
                    onClick = { selectedAnimation = type },
                    label = { Text(label, fontSize = 12.sp) },
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

        Spacer(Modifier.height(32.dp))

        // Start button
        Button(
            onClick = { onStartTimer(totalSeconds, finalSectorSeconds, selectedAnimation) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TimerColors.Green
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "AVVIA",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TimerColors.TextPrimary
            )
        }
    }
}
