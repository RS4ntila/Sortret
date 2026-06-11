package com.example.sortret.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.example.sortret.logic.rememberTrackerState

@Composable
internal fun BackdropDialogSurface(
    title: String,
    subtitle: String? = null,
    backdrop: Backdrop,
    accentColor: Color,
    secondaryAccent: Color,
    onDismiss: () -> Unit,
    shape: androidx.compose.ui.graphics.Shape? = null,
    backgroundColor: Color? = null,
    borderBrush: Brush? = null,
    showCloseButton: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val state = rememberTrackerState()
    val cornerRadiusDp = state.cornerRadius
    val finalShape = shape ?: sortretGlassShape(cornerRadiusDp)

    val maxDialogHeight = (LocalConfiguration.current.screenHeightDp * 0.78f).dp
    val entrance = remember { Animatable(0f) }
    val scrimInteraction = remember { MutableInteractionSource() }

    BackHandler(onBack = onDismiss)

    LaunchedEffect(Unit) {
        entrance.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 150,
                easing = FastOutSlowInEasing
            )
        )
    }

    SortretNoOverscroll {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(40f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.18f * entrance.value))
                    .clickable(
                        interactionSource = scrimInteraction,
                        indication = null,
                        onClick = onDismiss
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .imePadding()
                    .heightIn(max = maxDialogHeight)
                    .graphicsLayer {
                        val progress = entrance.value
                        alpha = 0.90f + progress * 0.10f
                        scaleX = 0.965f + progress * 0.035f
                        scaleY = 0.965f + progress * 0.035f
                        clip = true
                        this.shape = finalShape
                    }
                    .clip(finalShape)
                    .then(
                        if (backgroundColor != null) {
                            Modifier.background(backgroundColor)
                        } else {
                            Modifier.drawBackdrop(
                                backdrop = backdrop,
                                shape = { finalShape },
                                effects = {
                                    colorControls(
                                        brightness = -0.10f,
                                        saturation = 1.25f
                                    )
                                    vibrancy()
                                    blur(8.dp.toPx())
                                    lens(
                                        refractionHeight = 14.dp.toPx(),
                                        refractionAmount = 42.dp.toPx(),
                                        depthEffect = true,
                                        chromaticAberration = true
                                    )
                                },
                                highlight = null,
                                shadow = null,
                                innerShadow = null,
                                onDrawSurface = {
                                    drawRoundRect(
                                        color = Color(0xFF070A14).copy(alpha = 0.35f),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusDp.dp.toPx(), cornerRadiusDp.dp.toPx())
                                    )
                                }
                            )
                        }
                    )
                    .then(
                        if (borderBrush != null) {
                            Modifier.border(
                                width = 1.dp,
                                brush = borderBrush,
                                shape = finalShape
                            )
                        } else {
                            Modifier
                        }
                    )
            ) {
                if (backgroundColor == null) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(finalShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF090C16).copy(alpha = 0.52f),
                                        Color(0xFF060910).copy(alpha = 0.44f),
                                        Color(0xFF030408).copy(alpha = 0.65f)
                                    )
                                )
                            )
                    ) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        accentColor.copy(alpha = 0.07f),
                                        secondaryAccent.copy(alpha = 0.03f),
                                        Color.Transparent
                                    ),
                                    center = Offset(size.width * 0.86f, size.height * 0.12f),
                                    radius = size.width * 0.72f
                                ),
                                radius = size.width * 0.72f,
                                center = Offset(size.width * 0.86f, size.height * 0.12f)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            if (!subtitle.isNullOrBlank()) {
                                Text(
                                    text = subtitle,
                                    color = Color.White.copy(alpha = 0.58f),
                                    fontSize = 13.sp,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }

                        if (showCloseButton) {
                            Text(
                                text = "Закрыть",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(start = 14.dp)
                                    .background(Color.White.copy(alpha = 0.12f), CircleShape)
                                    .softPressClick(highlightColor = accentColor, onClick = onDismiss)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    SheetDivider(Modifier.padding(vertical = 18.dp))
                    content()
                }
            }
        }
    }
}
