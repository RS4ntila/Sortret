package com.example.sortret.ui.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sortret.logic.TrackerState
import com.kyant.backdrop.Backdrop
import kotlinx.coroutines.launch

@Composable
fun GlassSettingsSheet(
    state: TrackerState,
    backdrop: Backdrop,
    onDismiss: () -> Unit
) {
    val selectedPaletteIndex = state.backgroundPaletteIndex.coerceIn(0, backgroundPalettes.lastIndex)
    val activePalette = backgroundPalettes[selectedPaletteIndex]
    val accents = state.visualAccents(activePalette)
    var showBackgroundsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val accentScope = rememberCoroutineScope()
    val controlsMaxHeight = ((LocalConfiguration.current.screenHeightDp * 0.84f).dp - 248.dp)
        .coerceAtLeast(260.dp)
    val controlsScrollState = rememberScrollState()
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            state.staticBackgroundUri = uri.toString()
            state.staticBackgroundAccentArgb = 0
            accentScope.launch {
                extractDominantBackgroundAccentArgb(context.applicationContext, uri.toString())?.let { accentArgb ->
                    state.staticBackgroundAccentArgb = accentArgb
                }
            }
        }
    }

    fun clearStaticBackground() {
        if (state.staticBackgroundUri.isNotBlank()) {
            runCatching {
                context.contentResolver.releasePersistableUriPermission(
                    Uri.parse(state.staticBackgroundUri),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
        state.staticBackgroundUri = ""
        state.staticBackgroundAccentArgb = 0
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SortretModalBottomSheet(onDismiss = onDismiss) {
            SettingsSheetPanel(
                backdrop = backdrop,
                accentColor = accents.primary,
                secondaryAccent = accents.secondary
            ) { sheetBackdrop ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SheetDragHandle()

                    Text(
                        text = "Визуальные эффекты",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 18.dp)
                    )

                    GlassPreviewCard(
                        state = state,
                        backdrop = sheetBackdrop,
                        accentColor = accents.primary,
                        secondaryAccent = accents.secondary
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = controlsMaxHeight)
                            .verticalScroll(controlsScrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (state.staticBackgroundUri.isBlank()) {
                            SettingSectionHeader("Фон")
                            BackgroundPalettePicker(
                                selectedIndex = selectedPaletteIndex,
                                palettes = backgroundPalettes.take(CompactPaletteCount),
                                onSelect = { state.backgroundPaletteIndex = it }
                            )

                            if (backgroundPalettes.size > CompactPaletteCount) {
                                ShowMoreBackgroundsButton(
                                    backdrop = sheetBackdrop,
                                    accentColor = accents.primary,
                                    secondaryAccent = accents.secondary,
                                    onClick = { showBackgroundsDialog = true }
                                )
                            }
                        }

                        StaticBackgroundControls(
                            hasStaticBackground = state.staticBackgroundUri.isNotBlank(),
                            accentColor = accents.primary,
                            secondaryAccent = accents.secondary,
                            onPick = { imagePicker.launch(arrayOf("image/*")) },
                            onClear = { clearStaticBackground() }
                        )

                        if (state.staticBackgroundUri.isBlank()) {
                            VisualSettingItem(
                                label = "Скорость анимации",
                                value = state.backgroundAnimationSpeed,
                                range = 0.35f..1.8f,
                                backdrop = sheetBackdrop,
                                accentColor = accents.primary,
                                unit = "x",
                                decimals = 1
                            ) {
                                state.backgroundAnimationSpeed = it
                            }
                        }

                        SheetDivider(Modifier.padding(vertical = 18.dp))

                        SettingSectionHeader("Жидкое стекло")
                        VisualSettingItem("Скругление", state.cornerRadius, 0f..50f, sheetBackdrop, accents.primary, "dp") {
                            state.cornerRadius = it
                        }
                        VisualSettingItem("Высота преломления", state.refractionHeight, 0f..50f, sheetBackdrop, accents.primary, "dp") {
                            state.refractionHeight = it
                        }
                        VisualSettingItem("Смещение преломления", state.refractionOffset, 0f..120f, sheetBackdrop, accents.primary, "dp") {
                            state.refractionOffset = it
                        }

                        SheetDivider(Modifier.padding(vertical = 18.dp))

                        SettingSectionHeader("Оптика стекла")
                        VisualSettingItem("Насыщенность", state.glassChromaMultiplier, 0.45f..2.2f, sheetBackdrop, accents.primary) {
                            state.glassChromaMultiplier = it
                        }
                        VisualSettingItem("Контраст", state.glassContrast, -0.35f..0.7f, sheetBackdrop, accents.primary) {
                            state.glassContrast = it
                        }
                        VisualSettingItem("Белая точка", state.glassWhitePoint, -0.35f..0.35f, sheetBackdrop, accents.primary) {
                            state.glassWhitePoint = it
                        }
                        VisualSettingItem("Тонировка стекла", state.tintAlpha, 0f..0.35f, sheetBackdrop, accents.primary) {
                            state.tintAlpha = it
                        }

                        SheetDivider(Modifier.padding(vertical = 18.dp))

                        SettingSectionHeader("Дополнительно")
                        VisualSettingItem("Радиус размытия", state.blurRadius, 0f..50f, sheetBackdrop, accents.primary) {
                            state.blurRadius = it.coerceAtLeast(0f)
                        }
                        VisualSettingItem("Дисперсия", state.dispersion, 0f..1f, sheetBackdrop, accents.primary) {
                            state.dispersion = it
                        }

                        SheetDivider(Modifier.padding(vertical = 18.dp))

                        ResetGlassDefaultsButton(
                            accentColor = accents.primary,
                            secondaryAccent = accents.secondary,
                            onClick = state::resetGlassDefaults
                        )

                        Text(
                            text = "Made by S4ntila",
                            color = Color.White.copy(alpha = 0.36f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 22.dp)
                        )
                    }
                }
            }
        }

        if (showBackgroundsDialog) {
            BackgroundCatalogDialog(
                selectedIndex = selectedPaletteIndex,
                backdrop = backdrop,
                accentColor = accents.primary,
                secondaryAccent = accents.secondary,
                onSelect = { index ->
                    state.backgroundPaletteIndex = index
                    showBackgroundsDialog = false
                },
                onDismiss = { showBackgroundsDialog = false }
            )
        }
    }
}
