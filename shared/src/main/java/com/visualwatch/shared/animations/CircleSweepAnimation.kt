package com.visualwatch.shared.animations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.visualwatch.shared.theme.TimerColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircleSweepAnimation(
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
        val strokeWidth = size.minDimension * 0.08f
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)
        val topLeft = Offset(center.x - radius, center.y - radius)
        val arcSize = Size(radius * 2, radius * 2)

        drawArc(
            color = Color(0xFF333333),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        if (finalSectorRatio > 0f) {
            val finalSectorSweep = 360f * finalSectorRatio
            val finalStartAngle = -90f + 360f * (1f - finalSectorRatio)
            drawArc(
                color = zoneColor.copy(alpha = 0.3f),
                startAngle = finalStartAngle,
                sweepAngle = finalSectorSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
        }

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

        if (finalSectorRatio > 0f) {
            val finalAngleDeg = -90f + 360f * (1f - finalSectorRatio)
            val finalAngleRad = Math.toRadians(finalAngleDeg.toDouble())
            val cosA = cos(finalAngleRad).toFloat()
            val sinA = sin(finalAngleRad).toFloat()
            val innerR = radius - strokeWidth * 0.7f
            val outerR = radius + strokeWidth * 0.7f
            drawLine(
                color = markerColor.copy(alpha = 0.85f),
                start = Offset(center.x + innerR * cosA, center.y + innerR * sinA),
                end = Offset(center.x + outerR * cosA, center.y + outerR * sinA),
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )
        }
    }
}
