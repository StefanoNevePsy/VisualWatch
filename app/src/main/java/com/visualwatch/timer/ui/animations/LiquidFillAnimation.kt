package com.visualwatch.timer.ui.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import com.visualwatch.timer.ui.theme.TimerColors
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun LiquidFillAnimation(
    progress: Float,
    isInFinalSector: Boolean,
    finalSectorRatio: Float,
    modifier: Modifier = Modifier
) {
    val mainColor = TimerColors.progressColor(progress, isInFinalSector)
    val darkColor = TimerColors.progressColorDark(progress, isInFinalSector)
    val markerColor = TimerColors.finalSectorMarkerColor(progress, isInFinalSector)

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )
    val wavePhase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase2"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.9f

        val circlePath = Path().apply {
            addOval(Rect(center, radius))
        }

        // Circle border
        drawCircle(
            color = Color(0xFF333333),
            radius = radius,
            center = center,
            style = Stroke(width = size.minDimension * 0.02f)
        )

        clipPath(circlePath, clipOp = ClipOp.Intersect) {
            val fillHeight = size.height * progress
            val waterTop = size.height - fillHeight

            // Primary wave
            val waveAmplitude = size.minDimension * 0.025f
            val wavePath = Path().apply {
                moveTo(0f, waterTop)
                var x = 0f
                while (x <= size.width) {
                    val wave1 = waveAmplitude *
                        sin((x / size.width * 4 * Math.PI + wavePhase).toDouble()).toFloat()
                    val wave2 = waveAmplitude * 0.5f *
                        sin((x / size.width * 6 * Math.PI + wavePhase2).toDouble()).toFloat()
                    val y = waterTop + wave1 + wave2
                    lineTo(x, y)
                    x += 2f
                }
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            // Liquid fill
            drawPath(path = wavePath, color = mainColor.copy(alpha = 0.7f))

            // Darker bottom gradient
            val bottomPath = Path().apply {
                val gradientTop = waterTop + fillHeight * 0.4f
                moveTo(0f, gradientTop)
                lineTo(size.width, gradientTop)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path = bottomPath, color = darkColor.copy(alpha = 0.4f))

            // Final sector: dashed line with dynamic color
            if (finalSectorRatio > 0f) {
                val finalLineY = size.height - (size.height * finalSectorRatio)
                val dy = finalLineY - center.y
                val halfChord = if (dy * dy < radius * radius) {
                    sqrt(radius * radius - dy * dy)
                } else radius
                val lineStart = center.x - halfChord
                val lineEnd = center.x + halfChord

                // Dashed line
                drawLine(
                    color = markerColor.copy(alpha = 0.75f),
                    start = Offset(lineStart, finalLineY),
                    end = Offset(lineEnd, finalLineY),
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f), 0f)
                )
                // Edge dots
                drawCircle(
                    color = markerColor.copy(alpha = 0.8f),
                    radius = 3.5f,
                    center = Offset(lineStart + 4f, finalLineY)
                )
                drawCircle(
                    color = markerColor.copy(alpha = 0.8f),
                    radius = 3.5f,
                    center = Offset(lineEnd - 4f, finalLineY)
                )
            }
        }
    }
}
