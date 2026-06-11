package com.example.sortret.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sortret.logic.TrackerState
import com.kyant.backdrop.Backdrop
import java.util.Locale

@Composable
internal fun ResetGlassDefaultsButton(
    accentColor: Color,
    secondaryAccent: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.12f),
                        secondaryAccent.copy(alpha = 0.08f)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .softPressClick(highlightColor = accentColor, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Настройки по умолчанию",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Сброс",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .background(accentColor.copy(alpha = 0.72f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
internal fun GlassPreviewCard(
    state: TrackerState,
    backdrop: Backdrop,
    accentColor: Color,
    secondaryAccent: Color
) {
    GlassCard(
        backdrop = backdrop,
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp)
            .padding(bottom = 18.dp),
        shape = sortretGlassShape(state.cornerRadius),
        blurRadius = state.blurRadius,
        baseColor = accentColor,
        baseAlpha = state.tintAlpha,
        refractionHeight = state.refractionHeight,
        refractionOffset = state.refractionOffset,
        dispersion = state.dispersion,
        contrast = state.glassContrast,
        whitePoint = state.glassWhitePoint,
        chromaMultiplier = state.glassChromaMultiplier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Предпросмотр стекла",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PreviewPill("blur", accentColor)
                PreviewPill("lens", secondaryAccent)
                PreviewPill("tint", accentColor)
            }
        }
    }
}

@Composable
internal fun StaticBackgroundControls(
    hasStaticBackground: Boolean,
    accentColor: Color,
    secondaryAccent: Color,
    onPick: () -> Unit,
    onClear: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 14.dp, bottom = 4.dp)) {
        Text(
            text = if (hasStaticBackground) "Статичная картинка активна" else "Статичная картинка",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.72f),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StaticBackgroundButton(
                text = if (hasStaticBackground) "Заменить" else "Выбрать картинку",
                color = accentColor,
                modifier = Modifier.weight(1f),
                onClick = onPick
            )

            if (hasStaticBackground) {
                StaticBackgroundButton(
                    text = "Убрать",
                    color = secondaryAccent,
                    modifier = Modifier.weight(1f),
                    onClick = onClear
                )
            }
        }
    }
}

@Composable
private fun StaticBackgroundButton(
    text: String,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.18f), RoundedCornerShape(22.dp))
            .softPressClick(highlightColor = color, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PreviewPill(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.24f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.86f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun VisualSettingItem(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    backdrop: Backdrop,
    accentColor: Color = Color(0xFFC7A2E9),
    unit: String = "",
    decimals: Int = if (unit.isNotEmpty()) 0 else 2,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 9.dp)) {
        val formattedValue = if (decimals <= 0) {
            "%.0f".format(Locale.US, value)
        } else {
            "%.${decimals}f".format(Locale.US, value)
        }
        val valueText = if (unit.isNotEmpty()) {
            "$formattedValue $unit"
        } else {
            formattedValue
        }

        Text(
            text = "$label: $valueText",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.72f),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        LiquidSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            backdrop = backdrop,
            accentColor = accentColor
        )
    }
}
