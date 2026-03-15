package com.visualwatch.timer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.visualwatch.timer.R
import com.visualwatch.timer.data.AnimationType
import com.visualwatch.timer.data.TimerState
import com.visualwatch.timer.ui.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerService : Service() {

    private val binder = TimerBinder()
    private var countDownTimer: CountDownTimer? = null

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var totalSeconds: Long = 0L
    private var finalSectorSeconds: Long = 0L
    private var animationType: AnimationType = AnimationType.CIRCLE_SWEEP
    private var remainingOnPause: Long = 0L

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopTimer()
                stopSelf()
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
        }
        return START_STICKY
    }

    fun startTimer(
        totalSec: Long,
        finalSectorSec: Long = 0L,
        animation: AnimationType = AnimationType.CIRCLE_SWEEP
    ) {
        this.totalSeconds = totalSec
        this.finalSectorSeconds = finalSectorSec
        this.animationType = animation

        startForeground(NOTIFICATION_ID, createNotification(totalSec))
        startCountDown(totalSec)
    }

    private fun startCountDown(seconds: Long) {
        countDownTimer?.cancel()

        _timerState.value = TimerState(
            totalSeconds = totalSeconds,
            remainingSeconds = seconds,
            finalSectorSeconds = finalSectorSeconds,
            isRunning = true,
            isPaused = false,
            isFinished = false,
            animationType = animationType
        )

        countDownTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining = (millisUntilFinished + 500) / 1000
                _timerState.value = _timerState.value.copy(
                    remainingSeconds = remaining
                )
                updateNotification(remaining)
            }

            override fun onFinish() {
                _timerState.value = _timerState.value.copy(
                    remainingSeconds = 0,
                    isRunning = false,
                    isFinished = true
                )
                vibrateFinished()
                updateNotificationFinished()
            }
        }.start()
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        remainingOnPause = _timerState.value.remainingSeconds
        _timerState.value = _timerState.value.copy(
            isRunning = false,
            isPaused = true
        )
    }

    fun resumeTimer() {
        if (_timerState.value.isPaused) {
            _timerState.value = _timerState.value.copy(
                isRunning = true,
                isPaused = false
            )
            startCountDown(remainingOnPause)
        }
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        _timerState.value = TimerState()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun resetTimer() {
        countDownTimer?.cancel()
        startCountDown(totalSeconds)
    }

    private fun vibrateFinished() {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= 31) {
            val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 500, 200, 500, 200, 800)
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.timer_notification_channel),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            setShowBadge(false)
            enableVibration(false)
        }
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    private fun createNotification(remainingSeconds: Long): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(formatTime(remainingSeconds))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .build()
    }

    private fun updateNotification(remainingSeconds: Long) {
        val notification = createNotification(remainingSeconds)
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID, notification)
    }

    private fun updateNotificationFinished() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.timer_finished))
            .setOngoing(false)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID, notification)
    }

    private fun formatTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) String.format("%d:%02d:%02d", h, m, s)
        else String.format("%02d:%02d", m, s)
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "visual_watch_timer"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP = "com.visualwatch.timer.STOP"
        const val ACTION_PAUSE = "com.visualwatch.timer.PAUSE"
        const val ACTION_RESUME = "com.visualwatch.timer.RESUME"
    }
}
