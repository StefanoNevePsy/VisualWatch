package com.visualwatch.timer.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.visualwatch.shared.data.TimerState
import com.visualwatch.timer.ui.components.TimerDisplay
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActiveTimerScreen(
    timerStateFlow: StateFlow<TimerState>,
    isAmbient: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    val timerState by timerStateFlow.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = onTap,
                onLongClick = onLongPress
            )
    ) {
        TimerDisplay(
            state = timerState,
            isAmbient = isAmbient
        )
    }
}
