package com.example.sortret.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    palette: BackgroundPalette = backgroundPalettes.first(),
    speed: Float = 1f
) {
    val time = rememberVsyncTime(speed = speed.coerceIn(0.35f, 1.8f))

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val maxDim = maxOf(w, h)
        val tau = (PI * 2.0).toFloat()
        val baseShift = wave(time, 1, 0.12f)

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    lerp(darken(palette.gradientA[0], 0.54f), darken(palette.gradientB[0], 0.54f), baseShift),
                    lerp(darken(palette.gradientA[1], 0.54f), darken(palette.gradientB[1], 0.54f), wave(time, 1, 0.42f)),
                    lerp(darken(palette.gradientA[2], 0.54f), darken(palette.gradientB[2], 0.54f), wave(time, 1, 0.72f))
                ),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )
        )

        softFlares.forEachIndexed { index, flare ->
            val colors = palette.blobColors[(index * 2 + 1) % palette.blobColors.size]
            val xPhase = tau * (time * flare.xCycles + flare.phase)
            val yPhase = tau * (time * flare.yCycles + flare.phase * 0.79f)
            val x = w * flare.centerX + w * flare.travelX * cos(xPhase)
            val y = h * flare.centerY + h * flare.travelY * sin(yPhase)
            val radius = maxDim * flare.radius * (1f + 0.08f * sin(tau * (time + flare.phase)))
            val color = darken(lerp(colors.first, colors.second, wave(time, 1, flare.phase)), 0.58f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = flare.alpha * palette.softGlowStrength * 0.76f),
                        color.copy(alpha = flare.alpha * palette.softGlowStrength * 0.24f),
                        Color.Transparent
                    ),
                    center = Offset(x, y),
                    radius = radius
                ),
                radius = radius,
                center = Offset(x, y),
                blendMode = BlendMode.Screen
            )
        }

        backgroundForms.forEachIndexed { index, form ->
            val colors = palette.blobColors[(index + 2) % palette.blobColors.size]
            val xPhase = tau * (time * form.xCycles + form.phase)
            val yPhase = tau * (time * form.yCycles + form.phase * 0.61f)
            val scalePhase = tau * (time + form.phase * 1.17f)
            val x = w * form.centerX + w * form.travelX * cos(xPhase)
            val y = h * form.centerY + h * form.travelY * sin(yPhase)
            val widthPx = maxDim * form.width
            val heightPx = maxDim * form.height
            val scale = 1f + form.scaleAmplitude * sin(scalePhase)
            val rotation = form.rotation + 18f * sin(tau * (time + form.phase))

            withTransform({
                translate(x, y)
                rotate(rotation)
                scale(scale, 1f + form.scaleAmplitude * 0.7f * cos(scalePhase))
            }) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            darken(colors.first, 0.50f).copy(alpha = form.alpha * 0.78f),
                            darken(colors.second, 0.56f).copy(alpha = form.alpha * 0.44f),
                            Color.Transparent
                        ),
                        start = Offset(-widthPx * 0.5f, -heightPx * 0.5f),
                        end = Offset(widthPx * 0.5f, heightPx * 0.5f)
                    ),
                    topLeft = Offset(-widthPx * 0.5f, -heightPx * 0.5f),
                    size = Size(widthPx, heightPx),
                    cornerRadius = CornerRadius(widthPx * form.radius, heightPx * form.radius),
                    blendMode = BlendMode.Screen
                )
            }
        }

        backgroundBlobs.forEachIndexed { index, blob ->
            val colors = palette.blobColors[index % palette.blobColors.size]
            val xPhase = tau * (time * blob.xCycles + blob.phase)
            val yPhase = tau * (time * blob.yCycles + blob.phase * 0.73f)
            val scalePhase = tau * (time * blob.scaleCycles + blob.phase * 1.31f)
            val colorPhase = wave(time, blob.colorCycles, blob.phase)

            val x = w * blob.centerX + w * blob.travelX * cos(xPhase)
            val y = h * blob.centerY + h * blob.travelY * sin(yPhase)
            val scaleX = blob.scaleBase + blob.scaleAmplitude * sin(scalePhase)
            val scaleY = blob.scaleBase + blob.scaleAmplitude * cos(scalePhase)
            val rotation = 360f * (time * blob.rotationCycles + blob.phase)
            val color = darken(lerp(colors.first, colors.second, colorPhase), 0.62f)

            withTransform({
                translate(x, y)
                rotate(rotation)
                scale(scaleX, scaleY)
            }) {
                drawOval(
                    color = color.copy(alpha = blob.alpha * palette.blobStrength * 0.70f),
                    topLeft = Offset(-maxDim * blob.radius, -maxDim * blob.radius),
                    size = Size(maxDim * blob.radius * 2f, maxDim * blob.radius * 2f),
                    blendMode = if (index % 3 == 0) BlendMode.Screen else BlendMode.Plus
                )
            }
        }
    }
}

@Composable
private fun rememberVsyncTime(speed: Float): Float {
    var time by remember { mutableFloatStateOf(0f) }
    val currentSpeed by rememberUpdatedState(speed)

    LaunchedEffect(Unit) {
        var lastFrameNanos = 0L

        while (true) {
            withFrameNanos { frameNanos ->
                if (lastFrameNanos != 0L) {
                    val deltaSeconds = ((frameNanos - lastFrameNanos) / 1_000_000_000f)
                        .coerceAtMost(0.05f)
                    time = (time + deltaSeconds * currentSpeed / BASE_CYCLE_SECONDS) % 1f
                }
                lastFrameNanos = frameNanos
            }
        }
    }

    return time
}

private fun wave(time: Float, cycles: Int, phase: Float): Float {
    return ((sin((time * cycles + phase) * PI.toFloat() * 2f) + 1f) * 0.5f)
        .coerceIn(0f, 1f)
}

private fun darken(color: Color, amount: Float): Color {
    return lerp(Color.Black, color, amount.coerceIn(0f, 1f))
}

private data class BlobData(
    val alpha: Float,
    val centerX: Float,
    val centerY: Float,
    val travelX: Float,
    val travelY: Float,
    val radius: Float,
    val scaleBase: Float,
    val scaleAmplitude: Float,
    val phase: Float,
    val xCycles: Int,
    val yCycles: Int,
    val scaleCycles: Int,
    val colorCycles: Int,
    val rotationCycles: Int
)

private data class BackgroundForm(
    val alpha: Float,
    val centerX: Float,
    val centerY: Float,
    val width: Float,
    val height: Float,
    val radius: Float,
    val travelX: Float,
    val travelY: Float,
    val phase: Float,
    val rotation: Float,
    val scaleAmplitude: Float,
    val xCycles: Int,
    val yCycles: Int
)

private data class SoftFlare(
    val alpha: Float,
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
    val travelX: Float,
    val travelY: Float,
    val phase: Float,
    val xCycles: Int,
    val yCycles: Int
)

private val backgroundBlobs = listOf(
    BlobData(0.48f, 0.18f, 0.58f, 0.20f, 0.24f, 0.46f, 1.05f, 0.22f, 0.03f, 1, 2, 2, 2, 1),
    BlobData(0.42f, 0.62f, 0.28f, 0.28f, 0.20f, 0.54f, 1.12f, 0.20f, 0.17f, 2, 1, 3, 1, 1),
    BlobData(0.32f, 0.80f, 0.62f, 0.18f, 0.26f, 0.38f, 1.00f, 0.26f, 0.31f, 1, 2, 2, 2, 2),
    BlobData(0.26f, 0.48f, 0.42f, 0.24f, 0.18f, 0.42f, 1.10f, 0.18f, 0.49f, 2, 1, 1, 1, 1),
    BlobData(0.34f, 0.32f, 0.82f, 0.30f, 0.12f, 0.52f, 1.08f, 0.24f, 0.66f, 1, 1, 2, 2, 1),
    BlobData(0.22f, 0.74f, 0.18f, 0.16f, 0.22f, 0.32f, 0.95f, 0.18f, 0.82f, 2, 2, 1, 1, 2)
)

private val backgroundForms = listOf(
    BackgroundForm(0.16f, 0.16f, 0.18f, 0.72f, 0.34f, 0.36f, 0.12f, 0.08f, 0.04f, -22f, 0.10f, 1, 2),
    BackgroundForm(0.13f, 0.82f, 0.28f, 0.66f, 0.42f, 0.42f, 0.10f, 0.12f, 0.23f, 28f, 0.08f, 2, 1),
    BackgroundForm(0.12f, 0.34f, 0.72f, 0.92f, 0.30f, 0.32f, 0.16f, 0.10f, 0.46f, 34f, 0.09f, 1, 1),
    BackgroundForm(0.11f, 0.76f, 0.82f, 0.58f, 0.48f, 0.46f, 0.12f, 0.08f, 0.71f, -38f, 0.11f, 2, 2)
)

private val softFlares = listOf(
    SoftFlare(0.18f, 0.18f, 0.18f, 0.68f, 0.16f, 0.10f, 0.06f, 1, 2),
    SoftFlare(0.14f, 0.76f, 0.22f, 0.56f, 0.12f, 0.15f, 0.29f, 2, 1),
    SoftFlare(0.16f, 0.46f, 0.58f, 0.76f, 0.18f, 0.12f, 0.47f, 1, 1),
    SoftFlare(0.12f, 0.86f, 0.82f, 0.64f, 0.14f, 0.10f, 0.73f, 2, 2),
    SoftFlare(0.10f, 0.20f, 0.92f, 0.54f, 0.15f, 0.08f, 0.91f, 1, 2)
)

private const val BASE_CYCLE_SECONDS = 90f
