package com.example.sortret.ui.screens

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.example.sortret.logic.TrackerState
import com.example.sortret.ui.components.GlassCard
import com.example.sortret.ui.components.sortretGlassShape
import com.kyant.backdrop.Backdrop

@Composable
internal fun SortretGlassCard(
    state: TrackerState,
    backdrop: Backdrop,
    baseColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = sortretGlassShape(state.cornerRadius),
    content: @Composable BoxScope.() -> Unit
) {
    GlassCard(
        backdrop = backdrop,
        modifier = modifier,
        shape = shape,
        blurRadius = state.blurRadius,
        baseAlpha = state.tintAlpha,
        refractionHeight = state.refractionHeight,
        refractionOffset = state.refractionOffset,
        dispersion = state.dispersion,
        baseColor = baseColor,
        contrast = state.glassContrast,
        whitePoint = state.glassWhitePoint,
        chromaMultiplier = state.glassChromaMultiplier,
        content = content
    )
}
