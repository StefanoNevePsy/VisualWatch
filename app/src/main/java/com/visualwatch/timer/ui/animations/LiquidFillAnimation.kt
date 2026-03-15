package com.visualwatch.timer.ui.animations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import com.visualwatch.timer.ui.theme.TimerColors
import kotlin.math.sin

@Composable
fun LiquidFillAnimation(
    progress: Float,
    isInFinalSector: Boolean,
    finalSectorRatio: Float,
    modifier: Modifier = Modifier
) {
    val mainColor = TimerColors.progressColor(progress, isInFinalSector)
    val darkColor = TimerColors.progressColorDark(progress, isInFinalSector)
    val wavePhase by remember { mutableFloatStateOf(0f) }

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.9f

        // Clip to circle
        val circlePath = Path().apply {
            addOval(Rect(center, radius))
        }

        // Circle border
        drawCircle(
            color = Color(0xFF333333),
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = size.minDimension * 0.02f
            )
        )

        clipPath(circlePath, clipOp = ClipOp.Intersect) {
            // Fill level: progress 1.0 = full, 0.0 = empty
            val fillHeight = size.height * progress
            val waterTop = size.height - fillHeight

            // Wave effect on the surface
            val wavePath = Path().apply {
                moveTo(0f, waterTop)
                val waveAmplitude = size.minDimension * 0.02f
                val waveLength = size.width / 2
                var x = 0f
                while (x <= size.width) {
                    val y = waterTop + waveAmplitude *
                        sin((x / waveLength * 2 * Math.PI + wavePhase).toFloat())
                    lineTo(x, y)
                    x += 2f
                }
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            // Liquid fill
            drawPath(
                path = wavePath,
                color = mainColor.copy(alpha = 0.7f)
            )

            // Darker bottom gradient effect
            val bottomPath = Path().apply {
                val gradientTop = waterTop + fillHeight * 0.4f
                moveTo(0f, gradientTop)
                lineTo(size.width, gradientTop)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                path = bottomPath,
                color = darkColor.copy(alpha = 0.4f)
            )

            // Final sector line
            if (finalSectorRatio > 0f) {
                val finalLineY = size.height - (size.height * finalSectorRatio)
                drawLine(
                    color = TimerColors.FinalSector.copy(alpha = 0.8f),
                    start = Offset(center.x - radius, finalLineY),
                    end = Offset(center.x + radius, finalLineY),
                    strokeWidth = 3f
                )
            }
        }
    }
}
