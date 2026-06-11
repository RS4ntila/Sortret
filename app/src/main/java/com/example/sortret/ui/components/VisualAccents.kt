package com.example.sortret.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.sortret.logic.TrackerState

data class VisualAccents(
    val primary: Color,
    val secondary: Color
)

fun TrackerState.visualAccents(
    palette: BackgroundPalette = activeBackgroundPalette()
): VisualAccents {
    val imageAccent = staticImageAccentColor()
    val primary = imageAccent ?: palette.primaryAccent()
    val secondary = imageAccent?.shiftHue(34f) ?: palette.secondaryAccent()

    return VisualAccents(
        primary = primary,
        secondary = secondary
    )
}

private fun TrackerState.staticImageAccentColor(): Color? {
    if (staticBackgroundUri.isBlank() || staticBackgroundAccentArgb == 0) return null
    return Color(staticBackgroundAccentArgb)
}

private fun Color.shiftHue(degrees: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(toArgb(), hsv)
    hsv[0] = (hsv[0] + degrees).floorMod(360f)
    hsv[1] = hsv[1].coerceAtLeast(0.42f)
    hsv[2] = hsv[2].coerceAtLeast(0.66f)
    return Color(android.graphics.Color.HSVToColor(hsv))
}

private fun Float.floorMod(modulus: Float): Float {
    val result = this % modulus
    return if (result < 0f) result + modulus else result
}
