package com.example.sortret.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sortret.logic.TrackerState
import com.example.sortret.logic.loc
import com.example.sortret.logic.Loc
import com.kyant.backdrop.Backdrop
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.blur
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import com.example.sortret.ui.screens.CalendarIcon
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@Composable
fun ScheduleSettingsSheet(
    state: TrackerState,
    backdrop: Backdrop,
    onDismiss: () -> Unit
) {
    val activePalette = state.activeBackgroundPalette()
    val accents = state.visualAccents(activePalette)
    var exactTimeSlot by remember { mutableStateOf<ScheduleTimeSlot?>(null) }
    var showAddPeriodDialog by remember { mutableStateOf(false) }

    val isSubDialogOpen = showAddPeriodDialog || exactTimeSlot != null
    val subDialogBlurRadius = if (isSubDialogOpen) 4.dp else 0.dp

    SortretModalBottomSheet(onDismiss = onDismiss) {
        SettingsSheetPanel(
            backdrop = backdrop,
            accentColor = accents.primary,
            secondaryAccent = accents.secondary
        ) { _ ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(subDialogBlurRadius)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SheetDragHandle()

                Text(
                    text = loc("График приема", "Intake schedule"),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = loc("Итого ${formatDose(state.dailyDose)} мг в сутки", "Total ${formatDose(state.dailyDose)} mg per day"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accents.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 22.dp)
                )

                SettingSectionHeader(loc("Утренний прием", "Morning intake"))
                ScheduleDoseEditor(
                    time = state.morningTime,
                    dose = state.morningDose,
                    accent = accents.primary,
                    onExactTimeClick = { exactTimeSlot = ScheduleTimeSlot.Morning },
                    onTimeChange = { state.morningTime = it },
                    onDoseChange = { state.morningDose = it }
                )

                SheetDivider(Modifier.padding(vertical = 18.dp))

                SettingSectionHeader(loc("Вечерний прием", "Evening intake"))
                ScheduleDoseEditor(
                    time = state.eveningTime,
                    dose = state.eveningDose,
                    accent = accents.secondary,
                    onExactTimeClick = { exactTimeSlot = ScheduleTimeSlot.Evening },
                    onTimeChange = { state.eveningTime = it },
                    onDoseChange = { state.eveningDose = it }
                )

                SheetDivider(Modifier.padding(vertical = 18.dp))

                SettingSectionHeader(loc("История автоматических норм", "Automatic target history"))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    state.dosePeriods.sortedBy { it.startDate }.forEachIndexed { index, period ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val dateFormatter = remember { DateTimeFormatter.ofPattern(loc("d MMMM yyyy 'г.'", "MMMM d, yyyy"), Locale.forLanguageTag(loc("ru", "en"))) }
                                Text(
                                    text = loc("С ", "From ") + period.startDate.format(dateFormatter),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = loc("Утро: ", "Morning: ") + formatDose(period.morningDose) + loc(" мг", " mg") + "  •  " + loc("Вечер: ", "Evening: ") + formatDose(period.eveningDose) + loc(" мг", " mg"),
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            }
                            if (index > 0) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.Red.copy(alpha = 0.15f), CircleShape)
                                        .softPressClick(highlightColor = Color.Red) {
                                            state.removeDosePeriod(period.startDate)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = loc("Удалить", "Delete"),
                                        color = Color(0xFFEF5350),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                brush = Brush.horizontalGradient(listOf(accents.primary, accents.secondary)),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .softPressClick(highlightColor = accents.secondary) {
                                showAddPeriodDialog = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = loc("+ Добавить период", "+ Add period"),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                SheetDivider(Modifier.padding(vertical = 18.dp))

                Text(
                    text = loc("Изменения сразу применяются к расчету накопления и календарю курса.", "Changes are immediately applied to the accumulation calculation and course calendar."),
                    color = Color.White.copy(alpha = 0.48f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    exactTimeSlot?.let { slot ->
        val currentTime = when (slot) {
            ScheduleTimeSlot.Morning -> state.morningTime
            ScheduleTimeSlot.Evening -> state.eveningTime
        }
        val accent = when (slot) {
            ScheduleTimeSlot.Morning -> accents.primary
            ScheduleTimeSlot.Evening -> accents.secondary
        }
        val secondaryAccent = when (slot) {
            ScheduleTimeSlot.Morning -> accents.secondary
            ScheduleTimeSlot.Evening -> accents.primary
        }

        ExactTimeDialog(
            time = currentTime,
            backdrop = backdrop,
            accent = accent,
            secondaryAccent = secondaryAccent,
            onDismiss = { exactTimeSlot = null },
            onConfirm = { selectedTime ->
                when (slot) {
                    ScheduleTimeSlot.Morning -> state.morningTime = selectedTime
                    ScheduleTimeSlot.Evening -> state.eveningTime = selectedTime
                }
                exactTimeSlot = null
            }
        )
    }

    if (showAddPeriodDialog) {
        AddPeriodDialog(
            state = state,
            backdrop = backdrop,
            accentColor = accents.primary,
            secondaryAccent = accents.secondary,
            onDismiss = { showAddPeriodDialog = false },
            onConfirm = { date, morning, evening ->
                state.addDosePeriod(date, morning, evening)
                showAddPeriodDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPeriodDialog(
    state: TrackerState,
    backdrop: Backdrop,
    accentColor: Color,
    secondaryAccent: Color,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, Float, Float) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var morningVal by remember { mutableStateOf(state.morningDose) }
    var eveningVal by remember { mutableStateOf(state.eveningDose) }
    var showDatePicker by remember { mutableStateOf(false) }

    val isDatePickerOpen = showDatePicker
    val datePickerBlurRadius = if (isDatePickerOpen) 4.dp else 0.dp

    val dateFormatter = remember { DateTimeFormatter.ofPattern(loc("d MMMM yyyy 'г.'", "MMMM d, yyyy"), Locale.forLanguageTag(loc("ru", "en"))) }

    BackdropDialogSurface(
        title = loc("Новый период", "New period"),
        subtitle = null,
        showCloseButton = false,
        backdrop = backdrop,
        accentColor = accentColor,
        secondaryAccent = secondaryAccent,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier.blur(datePickerBlurRadius),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = loc("ДАТА НАЧАЛА ДЕЙСТВИЯ", "EFFECTIVE START DATE"),
                    color = Color.White.copy(alpha = 0.42f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                        .clickable { showDatePicker = true }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(accentColor.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CalendarIcon(modifier = Modifier.size(20.dp))
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedDate.format(dateFormatter),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag(loc("ru", "en"))))
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassInput(
                    value = morningVal,
                    onValueChange = { morningVal = it },
                    label = loc("Утренняя норма", "Morning target"),
                    unit = loc("мг", "mg"),
                    valueRange = 0f..120f,
                    modifier = Modifier.weight(1f)
                )

                GlassInput(
                    value = eveningVal,
                    onValueChange = { eveningVal = it },
                    label = loc("Вечерняя норма", "Evening target"),
                    unit = loc("мг", "mg"),
                    valueRange = 0f..120f,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                        .softPressClick(highlightColor = accentColor, onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text(loc("Отмена", "Cancel"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .height(50.dp)
                        .background(
                            brush = Brush.horizontalGradient(listOf(accentColor, secondaryAccent)),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .softPressClick(highlightColor = secondaryAccent) {
                            onConfirm(selectedDate, morningVal, eveningVal)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(loc("Добавить", "Add"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    if (showDatePicker) {
        PeriodDatePickerDialog(
            selectedDateMillis = selectedDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            backdrop = backdrop,
            accentColor = accentColor,
            secondaryAccent = secondaryAccent,
            onDismiss = { showDatePicker = false },
            onConfirm = { selectedMillis ->
                selectedMillis?.let {
                    selectedDate = Instant.ofEpochMilli(it)
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
private fun PeriodDatePickerDialog(
    selectedDateMillis: Long,
    backdrop: Backdrop,
    accentColor: Color,
    secondaryAccent: Color,
    onDismiss: () -> Unit,
    onConfirm: (Long?) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

    BackdropDialogSurface(
        title = loc("Выберите дату", "Select date"),
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

private enum class ScheduleTimeSlot {
    Morning,
    Evening
}

@Composable
private fun ScheduleDoseEditor(
    time: LocalTime,
    dose: Float,
    accent: Color,
    onExactTimeClick: () -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onDoseChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = loc("Время", "Time"),
            color = Color.White.copy(alpha = 0.42f),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        TimePickerRow(
            time = time,
            accent = accent,
            onExactTimeClick = onExactTimeClick,
            onTimeChange = onTimeChange
        )
        GlassInput(
            value = dose,
            onValueChange = onDoseChange,
            label = loc("Норма приема", "Intake target"),
            unit = loc("мг", "mg"),
            valueRange = 0f..120f,
            modifier = Modifier.padding(top = 14.dp)
        )
    }
}

@Composable
private fun TimePickerRow(
    time: LocalTime,
    accent: Color,
    onExactTimeClick: () -> Unit,
    onTimeChange: (LocalTime) -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeStepButton(
            text = "-15",
            accent = accent,
            modifier = Modifier.weight(0.82f),
            onClick = { onTimeChange(time.plusMinutes(-15)) }
        )

        Box(
            modifier = Modifier
                .weight(1.32f)
                .height(58.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.28f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .softPressClick(highlightColor = accent, onClick = onExactTimeClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = time.format(formatter),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        TimeStepButton(
            text = "+15",
            accent = accent,
            modifier = Modifier.weight(0.82f),
            onClick = { onTimeChange(time.plusMinutes(15)) }
        )
    }
}

@Composable
private fun ExactTimeDialog(
    time: LocalTime,
    backdrop: Backdrop,
    accent: Color,
    secondaryAccent: Color,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    var hourText by remember(time) {
        mutableStateOf(TextFieldValue(formatTimePart(time.hour), selection = TextRange(2)))
    }
    var minuteText by remember(time) {
        mutableStateOf(TextFieldValue(formatTimePart(time.minute), selection = TextRange(2)))
    }
    val selectedTime = parseExactTimeOrNull(hourText.text, minuteText.text)

    fun closeWithCurrentInput() {
        if (selectedTime != null) {
            onConfirm(selectedTime)
        } else {
            onDismiss()
        }
    }

    BackdropDialogSurface(
        title = loc("Точное время", "Exact time"),
        subtitle = loc("Введите время и закройте окно.", "Enter time and close the window."),
        backdrop = backdrop,
        accentColor = accent,
        secondaryAccent = secondaryAccent,
        onDismiss = ::closeWithCurrentInput
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeNumberField(
                    value = hourText,
                    onValueChange = { hourText = it.filterTimeDigits() },
                    label = loc("Часы", "Hours"),
                    accent = accent,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = ":",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 22.dp)
                )

                TimeNumberField(
                    value = minuteText,
                    onValueChange = { minuteText = it.filterTimeDigits() },
                    label = loc("Минуты", "Minutes"),
                    accent = secondaryAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            if (selectedTime == null && (hourText.text.isNotBlank() || minuteText.text.isNotBlank())) {
                Text(
                    text = loc("Проверьте время: часы 0-23, минуты 0-59.", "Check time: hours 0-23, minutes 0-59."),
                    color = Color.White.copy(alpha = 0.46f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }
        }
    }
}

@Composable
private fun TimeNumberField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.42f),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.24f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(Color.White),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (value.text.isBlank()) {
                        Text(
                            text = "--",
                            color = Color.White.copy(alpha = 0.24f),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

private fun TextFieldValue.filterTimeDigits(): TextFieldValue {
    val filtered = text.filter(Char::isDigit).take(2)
    val selectionEnd = selection.end.coerceAtMost(filtered.length)
    return copy(text = filtered, selection = TextRange(selectionEnd))
}

private fun parseExactTimeOrNull(hour: String, minute: String): LocalTime? {
    if (hour.isBlank() || minute.isBlank()) return null
    val parsedHour = hour.toIntOrNull() ?: return null
    val parsedMinute = minute.toIntOrNull() ?: return null
    if (parsedHour !in 0..23 || parsedMinute !in 0..59) return null
    return LocalTime.of(parsedHour, parsedMinute)
}

private fun formatTimePart(value: Int): String {
    return value.toString().padStart(2, '0')
}

@Composable
private fun TimeStepButton(
    text: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(Color.White.copy(alpha = 0.075f), RoundedCornerShape(14.dp))
            .softPressClick(highlightColor = accent, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.84f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
