package com.example.sortret.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.softPressClick(
    enabled: Boolean = true,
    highlightColor: Color = Color.White,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = 520f
        ),
        label = "softPressScale"
    )

    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
        .drawWithContent {
            drawContent()
            if (enabled && pressed) {
                drawRect(highlightColor.copy(alpha = 0.07f))
            }
        }
        .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}
