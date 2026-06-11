package com.example.sortret.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SortretNoOverscroll(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalOverscrollFactory provides null) {
        content()
    }
}

@Composable
fun SortretModalBottomSheet(
    onDismiss: () -> Unit,
    scrimAlpha: Float = 0.16f,
    content: @Composable ColumnScope.() -> Unit
) {
    val entrance = remember { Animatable(0f) }
    val scrimInteraction = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        entrance.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 180,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(20f)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = scrimAlpha * entrance.value))
                .clickable(
                    interactionSource = scrimInteraction,
                    indication = null,
                    onClick = onDismiss
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
                .graphicsLayer {
                    val progress = entrance.value
                    alpha = 0.94f + progress * 0.06f
                    translationY = (1f - progress) * 56.dp.toPx()
                    scaleX = 0.992f + progress * 0.008f
                    scaleY = 0.992f + progress * 0.008f
                }
        ) {
            val columnScope = this
            SortretNoOverscroll {
                with(columnScope) {
                    content()
                }
            }
        }
    }
}

@Composable
fun SettingsSheetPanel(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFFFF4FB8),
    secondaryAccent: Color = Color(0xFF5EE7FF),
    content: @Composable ColumnScope.(Backdrop) -> Unit
) {
    val panelShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    val maxPanelHeight = (LocalConfiguration.current.screenHeightDp * 0.84f).dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxPanelHeight)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { panelShape },
                effects = {
                    colorControls(
                        brightness = 0.02f,
                        saturation = 1.32f
                    )
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
                    drawRect(Color(0xFF101014).copy(alpha = 0.42f))
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(panelShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF10131F).copy(alpha = 0.34f),
                            Color(0xFF111020).copy(alpha = 0.20f),
                            Color(0xFF070911).copy(alpha = 0.42f)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            accentColor.copy(alpha = 0.12f),
                            secondaryAccent.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        start = Offset(-size.width * 0.36f, 0f),
                        end = Offset(size.width * 1.14f, size.height * 0.58f)
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.08f),
                            secondaryAccent.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.82f, size.height * 0.18f),
                        radius = size.width * 0.74f
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
            ) {
                content(backdrop)
            }
        }
    }
}

@Composable
fun SheetDragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 26.dp)
            .size(42.dp, 4.dp)
            .background(Color.White.copy(alpha = 0.34f), CircleShape)
    )
}

@Composable
fun SheetDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.14f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun SettingSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White.copy(alpha = 0.48f),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}
