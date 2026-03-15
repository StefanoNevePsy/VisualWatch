package com.visualwatch.timer.ui.animations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import com.visualwatch.timer.ui.theme.TimerColors

@Composable
fun RadialFadeAnimation(
    progress: Float,
    isInFinalSector: Boolean,
    finalSectorRatio: Float,
    modifier: Modifier = Modifier
) {
    val mainColor = TimerColors.progressColor(progress, isInFinalSector)
    val darkColor = TimerColors.progressColorDark(progress, isInFinalSector)

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = size.minDimension / 2 * 0.9f
        val currentRadius = maxRadius * progress

        // Background circle
        drawCircle(
            color = Color(0xFF1A1A1A),
            radius = maxRadius,
            center = center
        )

        // Final sector ring: subtle dashed circle
        if (finalSectorRatio > 0f) {
            val finalRadius = maxRadius * finalSectorRatio
            drawCircle(
                color = TimerColors.FinalSectorDark.copy(alpha = 0.15f),
                radius = finalRadius,
                center = center
            )
            drawCircle(
                color = TimerColors.FinalSector.copy(alpha = 0.35f),
                radius = finalRadius,
                center = center,
                style = Stroke(
                    width = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                )
            )
        }

        // Filled radial area (shrinks as time passes)
        if (currentRadius > 0f) {
            // Outer glow
            drawCircle(
                color = darkColor.copy(alpha = 0.3f),
                radius = currentRadius + 4f,
                center = center
            )
            // Main fill
            drawCircle(
                color = mainColor.copy(alpha = 0.6f),
                radius = currentRadius,
                center = center
            )
            // Inner bright core
            drawCircle(
                color = mainColor.copy(alpha = 0.3f),
                radius = currentRadius * 0.6f,
                center = center
            )
        }

        // Outer border
        drawCircle(
            color = Color(0xFF444444),
            radius = maxRadius,
            center = center,
            style = Stroke(width = 2f)
        )
    }
}
