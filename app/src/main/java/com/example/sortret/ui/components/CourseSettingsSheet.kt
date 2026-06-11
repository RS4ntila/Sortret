package com.example.sortret.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sortret.logic.StartShift
import com.example.sortret.logic.TrackerState
import com.kyant.backdrop.Backdrop
import com.example.sortret.logic.loc
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseSettingsSheet(
    state: TrackerState,
    backdrop: Backdrop,
    onDismiss: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern(loc("d MMMM yyyy", "MMMM d, yyyy"), Locale.forLanguageTag(loc("ru", "en")))
    val activePalette = state.activeBackgroundPalette()
    val accents = state.visualAccents(activePalette)

    SortretModalBottomSheet(onDismiss = onDismiss) {
        SettingsSheetPanel(
            backdrop = backdrop,
            accentColor = accents.primary,
            secondaryAccent = accents.secondary
        ) { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SheetDragHandle()

                Text(
                    text = loc("Настройки курса", "Course Settings"),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 22.dp)
                )

                SettingSectionHeader(loc("Начало курса", "Course start"))
                SheetValueRow(
                    label = loc("Дата начала", "Start date"),
                    value = state.startDate.format(dateFormatter),
                    onClick = { showDatePicker = true }
                )

                SheetDivider(Modifier.padding(vertical = 18.dp))

                SettingSectionHeader(loc("Параметры", "Parameters"))
                GlassInput(
                    value = state.weight,
                    onValueChange = { state.weight = it },
                    label = loc("Ваш вес", "Your weight"),
                    unit = loc("кг", "kg"),
                    valueRange = 30f..150f
                )
                GlassInput(
                    value = state.targetTotalDose,
                    onValueChange = { state.targetTotalDose = it },
                    modifier = Modifier.padding(top = 14.dp),
                    label = loc("Целевая доза", "Target dose"),
                    unit = loc("мг", "mg"),
                    valueRange = 1000f..20000f
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    GlassInput(
                        value = state.tabletCount20.toFloat(),
                        onValueChange = { state.updateTabletCount20(it.roundToInt().coerceAtLeast(0)) },
                        modifier = Modifier.weight(1f),
                        label = loc("Таблетки 20 мг (куплено)", "20 mg pills (bought)"),
                        unit = loc("шт", "pcs"),
                        valueRange = 0f..5000f
                    )
                    
                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .background(accents.primary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, accents.primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .softPressClick(highlightColor = accents.primary) {
                                state.updateTabletCount20(state.tabletCount20 + 30)
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+30", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    GlassInput(
                        value = state.lostCount20.toFloat(),
                        onValueChange = { state.lostCount20 = it.roundToInt().coerceAtLeast(0) },
                        modifier = Modifier.weight(1f),
                        label = loc("Утеряно 20 мг", "Lost 20 mg"),
                        unit = loc("шт", "pcs"),
                        valueRange = 0f..5000f
                    )
                    
                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.Red.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .softPressClick(highlightColor = Color.Red) {
                                state.lostCount20 = state.lostCount20 + 1
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+1", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    GlassInput(
                        value = state.tabletCount10.toFloat(),
                        onValueChange = { state.updateTabletCount10(it.roundToInt().coerceAtLeast(0)) },
                        modifier = Modifier.weight(1f),
                        label = loc("Таблетки 10 мг (куплено)", "10 mg pills (bought)"),
                        unit = loc("шт", "pcs"),
                        valueRange = 0f..5000f
                    )

                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .background(accents.secondary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, accents.secondary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .softPressClick(highlightColor = accents.secondary) {
                                state.updateTabletCount10(state.tabletCount10 + 30)
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+30", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    GlassInput(
                        value = state.lostCount10.toFloat(),
                        onValueChange = { state.lostCount10 = it.roundToInt().coerceAtLeast(0) },
                        modifier = Modifier.weight(1f),
                        label = loc("Утеряно 10 мг", "Lost 10 mg"),
                        unit = loc("шт", "pcs"),
                        valueRange = 0f..5000f
                    )

                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.Red.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .softPressClick(highlightColor = Color.Red) {
                                state.lostCount10 = state.lostCount10 + 1
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+1", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                SheetDivider(Modifier.padding(vertical = 18.dp))

                SettingSectionHeader(loc("Первый прием", "First dose"))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ShiftOption(
                        text = loc("Утро", "Morning"),
                        selected = state.startShift == StartShift.MORNING,
                        modifier = Modifier.weight(1f),
                        accentColor = accents.primary
                    ) {
                        state.startShift = StartShift.MORNING
                    }
                    ShiftOption(
                        text = loc("Вечер", "Evening"),
                        selected = state.startShift == StartShift.EVENING,
                        modifier = Modifier.weight(1f),
                        accentColor = accents.secondary
                    ) {
                        state.startShift = StartShift.EVENING
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        CourseDateDialog(
            selectedDateMillis = state.startDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            backdrop = backdrop,
            accentColor = accents.primary,
            secondaryAccent = accents.secondary,
            onDismiss = { showDatePicker = false },
            onConfirm = { selectedMillis ->
                selectedMillis?.let {
                    state.startDate = Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CourseDateDialog(
    selectedDateMillis: Long,
    backdrop: Backdrop,
    accentColor: Color,
    secondaryAccent: Color,
    onDismiss: () -> Unit,
    onConfirm: (Long?) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

    BackdropDialogSurface(
        title = loc("Дата начала", "Start date"),
        subtitle = null,
        showCloseButton = false,
        backdrop = backdrop,
        accentColor = accentColor,
        secondaryAccent = secondaryAccent,
        onDismiss = onDismiss
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White,
                headlineContentColor = Color.White,
                weekdayContentColor = Color.White.copy(alpha = 0.62f),
                subheadContentColor = Color.White,
                navigationContentColor = Color.White,
                yearContentColor = Color.White,
                disabledYearContentColor = Color.White.copy(alpha = 0.38f),
                currentYearContentColor = Color.White,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = accentColor,
                dayContentColor = Color.White,
                disabledDayContentColor = Color.White.copy(alpha = 0.38f),
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = accentColor,
                todayContentColor = Color.White,
                todayDateBorderColor = accentColor
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onDismiss) {
                Text(
                    text = loc("Отмена", "Cancel"),
                    color = Color.White.copy(alpha = 0.62f)
                )
            }
            TextButton(onClick = { onConfirm(datePickerState.selectedDateMillis) }) {
                Text(
                    text = loc("ОК", "OK"),
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SheetValueRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .softPressClick(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.62f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 18.dp)
        )
    }
}

@Composable
private fun ShiftOption(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.softPressClick(highlightColor = accentColor, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) {
            accentColor.copy(alpha = 0.18f)
        } else {
            Color.White.copy(alpha = 0.05f)
        }
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}
