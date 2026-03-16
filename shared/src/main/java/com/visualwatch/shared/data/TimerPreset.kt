package com.visualwatch.shared.data

import java.util.UUID

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
