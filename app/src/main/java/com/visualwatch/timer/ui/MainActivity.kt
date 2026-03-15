package com.visualwatch.timer.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.ambient.AmbientLifecycleObserver
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.visualwatch.timer.data.AnimationType
import com.visualwatch.timer.data.TimerPreset
import com.visualwatch.timer.data.TimerState
import com.visualwatch.timer.service.TimerService
import com.visualwatch.timer.ui.screens.ActiveTimerScreen
import com.visualwatch.timer.ui.screens.EditPresetScreen
import com.visualwatch.timer.ui.screens.ManualTimerSetupScreen
import com.visualwatch.timer.ui.screens.PresetListScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {

    private var timerService: TimerService? = null
    private var serviceBound = false
    private var isAmbient = mutableStateOf(false)
    private var isTimerActive = mutableStateOf(false)

    // Fallback flow when service not yet bound
    private val fallbackTimerState = MutableStateFlow(TimerState())

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val service = (binder as TimerService.TimerBinder).getService()
            timerService = service
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            serviceBound = false
        }
    }

    private val ambientCallback = object : AmbientLifecycleObserver.AmbientLifecycleCallback {
        override fun onEnterAmbient(ambientDetails: AmbientLifecycleObserver.AmbientDetails) {
            isAmbient.value = true
            // In ambient mode, remove keep-screen-on to allow low-power display
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        override fun onExitAmbient() {
            isAmbient.value = false
            // Restore keep-screen-on when interactive and timer is active
            if (isTimerActive.value) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        override fun onUpdateAmbient() {
            // Called periodically (~1/min) in ambient mode — UI recomposes via state
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambient mode support — keeps activity alive when screen dims
        val ambientObserver = AmbientLifecycleObserver(this, ambientCallback)
        lifecycle.addObserver(ambientObserver)

        // Bind to timer service
        val serviceIntent = Intent(this, TimerService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            VisualWatchApp()
        }
    }

    @Composable
    private fun VisualWatchApp() {
        val navController = rememberSwipeDismissableNavController()
        val viewModel: TimerViewModel = viewModel()
        var editingPreset by remember { mutableStateOf<TimerPreset?>(null) }
        var isNewPreset by remember { mutableStateOf(false) }

        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "presets"
        ) {
            // Preset list (home)
            composable("presets") {
                PresetListScreen(
                    presetsFlow = viewModel.presets,
                    onPresetSelected = { preset ->
                        startTimerFromPreset(preset)
                        navController.navigate("timer")
                    },
                    onManualSetup = {
                        navController.navigate("manual_setup")
                    },
                    onEditPreset = { preset ->
                        editingPreset = preset
                        isNewPreset = false
                        navController.navigate("edit_preset")
                    },
                    onAddPreset = {
                        editingPreset = null
                        isNewPreset = true
                        navController.navigate("edit_preset")
                    }
                )
            }

            // Manual timer setup
            composable("manual_setup") {
                ManualTimerSetupScreen(
                    onStartTimer = { totalSec, finalSec, animation ->
                        startTimerDirect(totalSec, finalSec, animation)
                        navController.navigate("timer") {
                            popUpTo("presets")
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // Edit/create preset
            composable("edit_preset") {
                EditPresetScreen(
                    preset = editingPreset,
                    onSave = { preset ->
                        viewModel.savePreset(preset, isNewPreset)
                        navController.popBackStack()
                    },
                    onDelete = if (!isNewPreset) { { id ->
                        viewModel.deletePreset(id)
                        navController.popBackStack()
                    } } else null,
                    onBack = { navController.popBackStack() }
                )
            }

            // Active timer
            composable("timer") {
                val timerStateFlow: StateFlow<TimerState> =
                    timerService?.timerState ?: fallbackTimerState

                ActiveTimerScreen(
                    timerStateFlow = timerStateFlow,
                    isAmbient = isAmbient.value,
                    onTap = {
                        // Tap to pause/resume
                        timerService?.let { service ->
                            val state = service.timerState.value
                            when {
                                state.isFinished -> {
                                    service.stopTimer()
                                    setTimerActive(false)
                                    navController.popBackStack()
                                }
                                state.isPaused -> service.resumeTimer()
                                state.isRunning -> service.pauseTimer()
                            }
                        }
                    },
                    onLongPress = {
                        // Long press to stop and go back
                        timerService?.stopTimer()
                        setTimerActive(false)
                        navController.popBackStack()
                    }
                )
            }
        }
    }

    private fun setTimerActive(active: Boolean) {
        isTimerActive.value = active
        if (active && !isAmbient.value) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else if (!active) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun startTimerFromPreset(preset: TimerPreset) {
        startTimerService()
        timerService?.startTimer(
            totalSec = preset.totalSeconds,
            finalSectorSec = preset.finalSectorSeconds,
            animation = preset.animationType
        )
        setTimerActive(true)
    }

    private fun startTimerDirect(totalSeconds: Long, finalSectorSeconds: Long, animation: AnimationType) {
        startTimerService()
        timerService?.startTimer(
            totalSec = totalSeconds,
            finalSectorSec = finalSectorSeconds,
            animation = animation
        )
        setTimerActive(true)
    }

    private fun startTimerService() {
        val intent = Intent(this, TimerService::class.java)
        startForegroundService(intent)
        if (!serviceBound) {
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        if (serviceBound) {
            unbindService(serviceConnection)
            serviceBound = false
        }
        super.onDestroy()
    }
}
