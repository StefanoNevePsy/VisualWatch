package com.visualwatch.timer.data

import java.util.UUID

/**
 * A timer preset with total duration and optional "final sector" warning zone.
 */
data class TimerPreset(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val totalSeconds: Long,
    val finalSectorSeconds: Long = 0L,
    val animationType: AnimationType = AnimationType.CIRCLE_SWEEP
)

enum class AnimationType {
    CIRCLE_SWEEP,
    LIQUID_FILL,
    RADIAL_FADE,
    PIE_SLICE
}
