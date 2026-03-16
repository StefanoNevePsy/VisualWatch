package com.visualwatch.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visualwatch.mobile.ui.components.MobileTimerDisplay
import com.visualwatch.shared.data.TimerState
import com.visualwatch.shared.theme.TimerColors
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MobileActiveTimerScreen(
    timerStateFlow: StateFlow<TimerState>,
    isDimmed: Boolean,
    onPauseResume: () -> Unit,
    onStop: () -> Unit,
    onToggleDim: () -> Unit
) {
    val timerState by timerStateFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TimerColors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Timer animation
        MobileTimerDisplay(
            state = timerState,
            isDimmed = isDimmed,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(24.dp))

        // Controls (hidden when dimmed — tap anywhere to un-dim)
        if (!isDimmed && !timerState.isFinished) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                // Pause/Resume
                Button(
                    onClick = onPauseResume,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (timerState.isPaused) TimerColors.Green
                            else TimerColors.Yellow
                    )
                ) {
                    Text(
                        text = if (timerState.isPaused) "RIPRENDI" else "PAUSA",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimerColors.Background
                    )
                }

                // Stop
                Button(
                    onClick = onStop,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TimerColors.Red
                    )
                ) {
                    Text(
                        text = "STOP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TimerColors.TextPrimary
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Dim button
            Text(
                text = "\uD83D\uDD05 Tieni acceso (dim)",
                fontSize = 13.sp,
                color = TimerColors.TextSecondary,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onToggleDim)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        } else if (isDimmed) {
            // Tap anywhere to un-dim — small hint
            Text(
                text = "Tocca per i controlli",
                fontSize = 12.sp,
                color = TimerColors.TextSecondary.copy(alpha = 0.5f),
                modifier = Modifier
                    .clickable(onClick = onToggleDim)
                    .padding(16.dp)
            )
        } else if (timerState.isFinished) {
            Button(
                onClick = onStop,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TimerColors.Green
                )
            ) {
                Text(
                    text = "OK",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TimerColors.TextPrimary
                )
            }
        }
    }
}
