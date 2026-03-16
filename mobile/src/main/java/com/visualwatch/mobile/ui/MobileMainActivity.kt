package com.visualwatch.mobile.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.visualwatch.mobile.service.MobileTimerService
import com.visualwatch.mobile.ui.screens.MobileActiveTimerScreen
import com.visualwatch.mobile.ui.screens.MobileEditPresetScreen
import com.visualwatch.mobile.ui.screens.MobilePresetListScreen
import com.visualwatch.mobile.ui.screens.MobileSetupScreen
import com.visualwatch.shared.data.AnimationType
import com.visualwatch.shared.data.TimerPreset
import com.visualwatch.shared.data.TimerState
import com.visualwatch.shared.theme.TimerColors
import com.visualwatch.shared.viewmodel.TimerViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MobileMainActivity : ComponentActivity() {

    private var timerService: MobileTimerService? = null
    private var serviceBound = false
    private var isTimerActive = mutableStateOf(false)
    private var isDimmed = mutableStateOf(false)

    private val fallbackTimerState = MutableStateFlow(TimerState())

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val service = (binder as MobileTimerService.TimerBinder).getService()
            timerService = service
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            serviceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceIntent = Intent(this, MobileTimerService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            VisualWatchMobileApp()
        }
    }

    @Composable
    private fun VisualWatchMobileApp() {
        val navController = rememberNavController()
        val viewModel: TimerViewModel = viewModel()
        var editingPreset by remember { mutableStateOf<TimerPreset?>(null) }
        var isNewPreset by remember { mutableStateOf(false) }

        // Wrap in a clickable box when dimmed to catch taps for un-dimming
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TimerColors.Background)
        ) {
            NavHost(
                navController = navController,
                startDestination = "presets"
            ) {
                composable("presets") {
                    MobilePresetListScreen(
                        presetsFlow = viewModel.presets,
                        onPresetSelected = { preset ->
                            startTimerFromPreset(preset)
                            navController.navigate("timer") {
                                popUpTo("presets")
                            }
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

                composable("manual_setup") {
                    MobileSetupScreen(
                        onStartTimer = { totalSec, finalSec, animation ->
                            startTimerDirect(totalSec, finalSec, animation)
                            navController.navigate("timer") {
                                popUpTo("presets")
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("edit_preset") {
                    MobileEditPresetScreen(
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

                composable("timer") {
                    val timerStateFlow: StateFlow<TimerState> =
                        timerService?.timerState ?: fallbackTimerState

                    // Full-screen clickable area when dimmed
                    if (isDimmed.value) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    setDimMode(false)
                                }
                        ) {
                            MobileActiveTimerScreen(
                                timerStateFlow = timerStateFlow,
                                isDimmed = true,
                                onPauseResume = {},
                                onStop = {},
                                onToggleDim = { setDimMode(false) }
                            )
                        }
                    } else {
                        MobileActiveTimerScreen(
                            timerStateFlow = timerStateFlow,
                            isDimmed = false,
                            onPauseResume = {
                                timerService?.let { service ->
                                    val state = service.timerState.value
                                    when {
                                        state.isPaused -> service.resumeTimer()
                                        state.isRunning -> service.pauseTimer()
                                    }
                                }
                            },
                            onStop = {
                                timerService?.stopTimer()
                                setTimerActive(false)
                                navController.popBackStack()
                            },
                            onToggleDim = { setDimMode(true) }
                        )
                    }
                }
            }
        }
    }

    private fun setDimMode(dim: Boolean) {
        isDimmed.value = dim
        val lp = window.attributes
        if (dim) {
            // Keep screen on with very low brightness
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            lp.screenBrightness = 0.01f // Minimum brightness
            window.attributes = lp
        } else {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            window.attributes = lp
            if (isTimerActive.value) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    private fun setTimerActive(active: Boolean) {
        isTimerActive.value = active
        if (active) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            isDimmed.value = false
            val lp = window.attributes
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            window.attributes = lp
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
        val intent = Intent(this, MobileTimerService::class.java)
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
