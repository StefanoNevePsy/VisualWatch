package com.visualwatch.shared.animations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import com.visualwatch.shared.theme.TimerColors

@Composable
fun RadialFadeAnimation(
    progress: Float,
    isInFinalSector: Boolean,
    finalSectorRatio: Float,
    modifier: Modifier = Modifier
) {
    val mainColor = TimerColors.progressColor(progress, isInFinalSector)
    val darkColor = TimerColors.progressColorDark(progress, isInFinalSector)
    val markerColor = TimerColors.finalSectorMarkerColor(progress, isInFinalSector)
    val zoneColor = TimerColors.finalSectorZoneColor(progress, isInFinalSector)

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = size.minDimension / 2 * 0.9f
        val currentRadius = maxRadius * progress

        drawCircle(
            color = Color(0xFF1A1A1A),
            radius = maxRadius,
            center = center
        )

        if (finalSectorRatio > 0f) {
            val finalRadius = maxRadius * finalSectorRatio
            drawCircle(
                color = zoneColor.copy(alpha = 0.12f),
                radius = finalRadius,
                center = center
            )
            drawCircle(
                color = markerColor.copy(alpha = 0.5f),
                radius = finalRadius,
                center = center,
                style = Stroke(
                    width = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                )
            )
        }

        if (currentRadius > 0f) {
            drawCircle(
                color = darkColor.copy(alpha = 0.3f),
                radius = currentRadius + 4f,
                center = center
            )
            drawCircle(
                color = mainColor.copy(alpha = 0.6f),
                radius = currentRadius,
                center = center
            )
            drawCircle(
                color = mainColor.copy(alpha = 0.3f),
                radius = currentRadius * 0.6f,
                center = center
            )
        }

        drawCircle(
            color = Color(0xFF444444),
            radius = maxRadius,
            center = center,
            style = Stroke(width = 2f)
        )
    }
}
