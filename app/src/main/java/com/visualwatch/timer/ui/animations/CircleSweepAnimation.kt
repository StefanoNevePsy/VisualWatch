package com.visualwatch.timer.ui.animations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.visualwatch.timer.ui.theme.TimerColors

@Composable
fun CircleSweepAnimation(
    progress: Float,
    isInFinalSector: Boolean,
    finalSectorRatio: Float,
    modifier: Modifier = Modifier
) {
    val mainColor = TimerColors.progressColor(progress, isInFinalSector)
    val darkColor = TimerColors.progressColorDark(progress, isInFinalSector)

    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = size.minDimension * 0.08f
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)
        val topLeft = Offset(center.x - radius, center.y - radius)
        val arcSize = Size(radius * 2, radius * 2)

        // Background track
        drawArc(
            color = Color(0xFF333333),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Final sector indicator (thin outer ring)
        if (finalSectorRatio > 0f) {
            val finalSectorSweep = 360f * finalSectorRatio
            drawArc(
                color = TimerColors.FinalSectorDark.copy(alpha = 0.4f),
                startAngle = -90f + 360f * (1f - finalSectorRatio),
                sweepAngle = finalSectorSweep,
                useCenter = false,
                topLeft = Offset(
                    center.x - radius - strokeWidth * 0.4f,
                    center.y - radius - strokeWidth * 0.4f
                ),
                size = Size(
                    (radius + strokeWidth * 0.4f) * 2,
                    (radius + strokeWidth * 0.4f) * 2
                ),
                style = Stroke(width = strokeWidth * 0.3f)
            )
        }

        // Main progress arc
        val sweepAngle = 360f * progress
        drawArc(
            color = mainColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Inner glow
        drawArc(
            color = darkColor.copy(alpha = 0.3f),
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(
                center.x - radius + strokeWidth * 0.3f,
                center.y - radius + strokeWidth * 0.3f
            ),
            size = Size(
                (radius - strokeWidth * 0.3f) * 2,
                (radius - strokeWidth * 0.3f) * 2
            ),
            style = Stroke(width = strokeWidth * 0.4f)
        )
    }
}
