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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens

internal const val CompactPaletteCount = 3

@Composable
internal fun BackgroundPalettePicker(
    selectedIndex: Int,
    palettes: List<BackgroundPalette>,
    onSelect: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        palettes.forEachIndexed { index, palette ->
            PaletteOption(
                palette = palette,
                selected = index == selectedIndex,
                onClick = { onSelect(index) }
            )

            if (index != palettes.lastIndex) {
                SheetDivider(Modifier.padding(vertical = 2.dp))
            }
        }
    }
}

@Composable
private fun PaletteOption(
    palette: BackgroundPalette,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .softPressClick(highlightColor = palette.primaryAccent(), onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(58.dp)
                    .height(34.dp)
                    .background(
                        brush = Brush.linearGradient(palette.previewColors),
                        shape = RoundedCornerShape(8.dp)
                    )
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = palette.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (palette.experimental) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "эксп",
                            color = Color.White.copy(alpha = 0.82f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(
                                    palette.primaryAccent().copy(alpha = 0.18f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = palette.description,
                    color = Color.White.copy(alpha = 0.52f),
                    fontSize = 12.sp,
                    lineHeight = 15.sp
                )
            }
        }

        if (selected) {
            Text(
                text = "Активно",
                color = palette.primaryAccent(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
internal fun ShowMoreBackgroundsButton(
    backdrop: Backdrop,
    accentColor: Color,
    secondaryAccent: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 4.dp)
            .clip(shape)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    colorControls(
                        brightness = 0.02f,
                        saturation = 1.25f
                    )
                    blur(8.dp.toPx())
                    lens(
                        refractionHeight = 10.dp.toPx(),
                        refractionAmount = 30.dp.toPx(),
                        depthEffect = true,
                        chromaticAberration = true
                    )
                },
                highlight = null,
                shadow = null,
                innerShadow = null,
                onDrawSurface = {
                    drawRect(accentColor.copy(alpha = 0.13f))
                }
            )
            .softPressClick(highlightColor = accentColor, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Text(
            text = "Расширить список",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
internal fun BackgroundCatalogDialog(
    selectedIndex: Int,
    backdrop: Backdrop,
    accentColor: Color,
    secondaryAccent: Color,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    BackdropDialogSurface(
        title = "Все фоны",
        backdrop = backdrop,
        accentColor = accentColor,
        secondaryAccent = secondaryAccent,
        onDismiss = onDismiss
    ) {
        backgroundPalettes.forEachIndexed { index, palette ->
            PaletteOption(
                palette = palette,
                selected = index == selectedIndex,
                onClick = { onSelect(index) }
            )

            if (index != backgroundPalettes.lastIndex) {
                SheetDivider(Modifier.padding(vertical = 2.dp))
            }
        }
    }
}
