package com.visualwatch.shared.data

data class TimerState(
    val totalSeconds: Long = 0L,
    val remainingSeconds: Long = 0L,
    val finalSectorSeconds: Long = 0L,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val animationType: AnimationType = AnimationType.CIRCLE_SWEEP
) {
    val progress: Float
        get() = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f

    val isInFinalSector: Boolean
        get() = finalSectorSeconds > 0 && remainingSeconds <= finalSectorSeconds && remainingSeconds > 0

    val finalSectorProgress: Float
        get() = if (finalSectorSeconds > 0) {
            (remainingSeconds.toFloat() / finalSectorSeconds).coerceIn(0f, 1f)
        } else 0f

    val phase: TimerPhase
        get() = when {
            isFinished -> TimerPhase.FINISHED
            isInFinalSector -> TimerPhase.FINAL_SECTOR
            progress > 0.5f -> TimerPhase.NORMAL
            progress > 0.25f -> TimerPhase.WARNING
            else -> TimerPhase.CRITICAL
        }
}

enum class TimerPhase {
    NORMAL,
    WARNING,
    CRITICAL,
    FINAL_SECTOR,
    FINISHED
}
