package com.example.sortret.ui.components

import com.example.sortret.logic.TrackerState

fun TrackerState.activeBackgroundPalette(): BackgroundPalette {
    return backgroundPalettes[backgroundPaletteIndex.coerceIn(0, backgroundPalettes.lastIndex)]
}
