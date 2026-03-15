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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PieSliceAnimation(
    progress: Float,
    isInFinalSector: Boolean,
    finalSectorRatio: Float,
    modifier: Modifier = Modifier
) {
    val mainColor = TimerColors.progressColor(progress, isInFinalSector)
    val darkColor = TimerColors.progressColorDark(progress, isInFinalSector)

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.85f
        val topLeft = Offset(center.x - radius, center.y - radius)
        val arcSize = Size(radius * 2, radius * 2)

        // Background
        drawCircle(
            color = Color(0xFF1A1A1A),
            radius = radius,
            center = center
        )

        // Final sector pie slice (background hint)
        if (finalSectorRatio > 0f) {
            val finalSweep = 360f * finalSectorRatio
            drawArc(
                color = TimerColors.FinalSectorDark.copy(alpha = 0.25f),
                startAngle = -90f + 360f * (1f - finalSectorRatio),
                sweepAngle = finalSweep,
                useCenter = true,
                topLeft = topLeft,
                size = arcSize
            )
        }

        // Main pie fill
        val sweepAngle = 360f * progress
        drawArc(
            color = mainColor.copy(alpha = 0.65f),
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = topLeft,
            size = arcSize
        )

        // Final sector dividing line (from center to edge)
        if (finalSectorRatio > 0f) {
            val finalAngleDeg = -90f + 360f * (1f - finalSectorRatio)
            val finalAngleRad = Math.toRadians(finalAngleDeg.toDouble())
            val edgeX = center.x + radius * cos(finalAngleRad).toFloat()
            val edgeY = center.y + radius * sin(finalAngleRad).toFloat()
            drawLine(
                color = TimerColors.FinalSector,
                start = center,
                end = Offset(edgeX, edgeY),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
            // Dot at the edge
            drawCircle(
                color = TimerColors.FinalSector,
                radius = 5f,
                center = Offset(edgeX, edgeY)
            )
        }

        // Darker inner overlay for depth
        val innerRadius = radius * 0.3f
        drawCircle(
            color = darkColor.copy(alpha = 0.2f),
            radius = innerRadius,
            center = center
        )

        // Border
        drawCircle(
            color = Color(0xFF444444),
            radius = radius,
            center = center,
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )

        // Progress edge line
        if (progress > 0f && progress < 1f) {
            val angle = Math.toRadians((-90.0 + sweepAngle))
            val edgeX = center.x + radius * cos(angle).toFloat()
            val edgeY = center.y + radius * sin(angle).toFloat()
            drawLine(
                color = Color.White.copy(alpha = 0.6f),
                start = center,
                end = Offset(edgeX, edgeY),
                strokeWidth = 2f
            )
        }
    }
}
