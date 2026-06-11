package com.example.sortret.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.shapes.RoundedRectangle

@Composable
fun GlassCard(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    shape: Shape = sortretGlassShape(40f),
    blurRadius: Float = 0.01f,
    baseColor: Color = Color.White,
    baseAlpha: Float = 0f,
    refractionHeight: Float = 20f,
    refractionOffset: Float = 70f,
    dispersion: Float = 0.5f,
    contrast: Float = 0f,
    whitePoint: Float = 0f,
    chromaMultiplier: Float = 1f,
    content: @Composable BoxScope.() -> Unit
) {
    val safeBlurRadius = blurRadius.coerceAtLeast(0f)
    val safeRefractionHeight = refractionHeight.coerceAtLeast(0f)
    val safeRefractionOffset = refractionOffset.coerceAtLeast(0f)

    Box(
        modifier = modifier
            .clip(shape)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    colorControls(
                        brightness = whitePoint.coerceIn(-0.35f, 0.35f),
                        contrast = (1f + contrast).coerceIn(0.2f, 2.4f),
                        saturation = chromaMultiplier.coerceIn(0f, 2.4f)
                    )
                    vibrancy()
                    blur(safeBlurRadius.dp.toPx())
                    lens(
                        refractionHeight = safeRefractionHeight.dp.toPx(),
                        refractionAmount = safeRefractionOffset.dp.toPx(),
                        depthEffect = true,
                        chromaticAberration = dispersion > 0.01f
                    )
                },
                highlight = null,
                shadow = null,
                innerShadow = null,
                onDrawSurface = {
                    if (baseAlpha > 0f) {
                        drawRect(baseColor.copy(alpha = baseAlpha.coerceIn(0f, 1f)))
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

internal fun sortretGlassShape(radiusDp: Float): Shape {
    return RoundedRectangle(radiusDp.coerceAtLeast(0f).dp)
}
