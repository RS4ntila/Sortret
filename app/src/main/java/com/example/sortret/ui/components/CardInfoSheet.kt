package com.example.sortret.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sortret.logic.Stats
import com.example.sortret.logic.TrackerState
import com.example.sortret.logic.loc
import com.example.sortret.logic.Loc
import com.kyant.backdrop.Backdrop
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.ceil
import androidx.compose.runtime.*
import java.time.YearMonth
import java.time.LocalDate
import java.time.LocalDateTime
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.Canvas
import com.example.sortret.ui.screens.SortretGlassCard

data class CourseStageInfo(
    val titleRu: String,
    val titleEn: String,
    val rangeRu: String,
    val rangeEn: String,
    val descriptionRu: String,
    val descriptionEn: String,
    val startProgress: Float,
    val endProgress: Float
) {
    val title: String get() = loc(titleRu, titleEn)
    val range: String get() = loc(rangeRu, rangeEn)
    val description: String get() = loc(descriptionRu, descriptionEn)
}

val courseStages = listOf(
    CourseStageInfo(
        titleRu = "Адаптация",
        titleEn = "Adaptation",
        rangeRu = "до 10%",
        rangeEn = "up to 10%",
        descriptionRu = "возможны временные обострения.",
        descriptionEn = "temporary flare-ups possible.",
        startProgress = 0f,
        endProgress = 0.10f
    ),
    CourseStageInfo(
        titleRu = "Начальное накопление",
        titleEn = "Initial Accumulation",
        rangeRu = "10-30%",
        rangeEn = "10-30%",
        descriptionRu = "снижение активности сала, начало работы изотретиноина.",
        descriptionEn = "reduction in sebum activity, isotretinoin begins working.",
        startProgress = 0.10f,
        endProgress = 0.30f
    ),
    CourseStageInfo(
        titleRu = "Активное накопление",
        titleEn = "Active Accumulation",
        rangeRu = "30-60%",
        rangeEn = "30-60%",
        descriptionRu = "ощущение кожи сильнее сохнет.",
        descriptionEn = "feeling of skin drying more intensely.",
        startProgress = 0.30f,
        endProgress = 0.60f
    ),
    CourseStageInfo(
        titleRu = "Поддерживающая фаза",
        titleEn = "Maintenance Phase",
        rangeRu = "60-90%",
        rangeEn = "60-90%",
        descriptionRu = "стабильно чистая кожа.",
        descriptionEn = "consistently clear skin.",
        startProgress = 0.60f,
        endProgress = 0.90f
    ),
    CourseStageInfo(
        titleRu = "Завершающий этап",
        titleEn = "Final Stage",
        rangeRu = "до 100%",
        rangeEn = "up to 100%",
        descriptionRu = "набор кумулятивной дозы для страховки.",
        descriptionEn = "reaching cumulative dose for insurance.",
        startProgress = 0.90f,
        endProgress = 1.01f
    )
)

fun currentCourseStage(progress: Float): CourseStageInfo {
    val safeProgress = progress.coerceIn(0f, 1f)
    return courseStages.firstOrNull { safeProgress >= it.startProgress && safeProgress < it.endProgress }
        ?: courseStages.last()
}

enum class CardInfoType {
    InitialAccumulation,
    SubstanceAccumulation,
    CourseCalendar,
    Schedule
}

@Composable
fun CardInfoSheet(
    type: CardInfoType,
    state: TrackerState,
    stats: Stats,
    backdrop: Backdrop,
    onDismiss: () -> Unit
) {
    val activePalette = state.activeBackgroundPalette()
    val accents = state.visualAccents(activePalette)

    var selectedDateToEdit by remember { mutableStateOf<LocalDate?>(null) }

    val isSubDialogOpen = selectedDateToEdit != null
    val subDialogBlurRadius = if (isSubDialogOpen) 4.dp else 0.dp

    Box(modifier = Modifier.fillMaxSize()) {
        SortretModalBottomSheet(
            onDismiss = onDismiss,
            scrimAlpha = 0.18f
        ) {
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

                    Column(modifier = Modifier.fillMaxWidth()) {
                        when (type) {
                            CardInfoType.InitialAccumulation -> InitialAccumulationContent(
                                progress = (stats.cumulativeDose / state.targetTotalDose).coerceIn(0f, 1f),
                                accentColor = accents.primary,
                                secondaryAccent = accents.secondary
                            )
                            CardInfoType.SubstanceAccumulation -> SubstanceAccumulationContent(
                                state = state,
                                stats = stats
                            )
                            CardInfoType.CourseCalendar -> CourseCalendarContent(
                                state = state,
                                stats = stats,
                                selectedDateToEdit = selectedDateToEdit,
                                onDateClick = { selectedDateToEdit = it }
                            )
                            CardInfoType.Schedule -> ScheduleContent(state = state)
                        }
                    }
                }
            }
        }

        selectedDateToEdit?.let { date ->
            val norm = state.getScheduledNorm(date)
            val manualEntry = state.manualDoses[date]

            var morningTaken by remember(date) {
                mutableStateOf(
                    if (manualEntry?.morningManual == true) manualEntry.morningTaken 
                    else state.isDoseTaken(date, isMorning = true)
                )
            }
            var morningDose by remember(date) {
                mutableStateOf(
                    if (manualEntry?.morningManual == true) manualEntry.morningDose 
                    else norm.first
                )
            }
            var morningIsManual by remember(date) {
                mutableStateOf(manualEntry?.morningManual == true)
            }
            var morningCount20 by remember(date) {
                mutableStateOf(manualEntry?.morningCount20)
            }
            var morningCount10 by remember(date) {
                mutableStateOf(manualEntry?.morningCount10)
            }

            var eveningTaken by remember(date) {
                mutableStateOf(
                    if (manualEntry?.eveningManual == true) manualEntry.eveningTaken 
                    else state.isDoseTaken(date, isMorning = false)
                )
            }
            var eveningDose by remember(date) {
                mutableStateOf(
                    if (manualEntry?.eveningManual == true) manualEntry.eveningDose 
                    else norm.second
                )
            }
            var eveningIsManual by remember(date) {
                mutableStateOf(manualEntry?.eveningManual == true)
            }
            var eveningCount20 by remember(date) {
                mutableStateOf(manualEntry?.eveningCount20)
            }
            var eveningCount10 by remember(date) {
                mutableStateOf(manualEntry?.eveningCount10)
            }

            val formattedDate = date.format(DateTimeFormatter.ofPattern(loc("d MMMM", "d MMMM"), Locale.forLanguageTag(loc("ru", "en"))))
            val relativeDayText = when (val daysDiff = java.time.temporal.ChronoUnit.DAYS.between(date, LocalDate.now())) {
                0L -> loc("Сегодня", "Today")
                1L -> loc("Вчера", "Yesterday")
                2L -> loc("Позавчера", "Day before yesterday")
                else -> {
                    if (daysDiff > 0) loc("$daysDiff дн. назад", "$daysDiff days ago")
                    else loc("${-daysDiff} дн. вперед", "${-daysDiff} days ahead")
                }
            }

            BackdropDialogSurface(
                title = loc("Прием", "Dose") + " $formattedDate",
                subtitle = null,
                showCloseButton = false,
                backdrop = backdrop,
                accentColor = accents.primary,
                secondaryAccent = accents.secondary,
                onDismiss = { selectedDateToEdit = null }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Beautiful date confirmation badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(accents.primary.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, accents.primary.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = accents.primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern(loc("d MMMM yyyy", "d MMMM yyyy"), Locale.forLanguageTag(loc("ru", "en")))),
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag(loc("ru", "en"))))
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + " • $relativeDayText",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (norm.first > 0f || morningIsManual) {
                        DosageSelector(
                            label = loc("Утренний прием", "Morning dose"),
                            scheduledDose = norm.first,
                            isManual = morningIsManual,
                            taken = morningTaken,
                            currentDose = morningDose,
                            accent = accents.primary,
                            initialCount20 = morningCount20,
                            initialCount10 = morningCount10,
                            onStateChange = { newIsManual, newTaken, newDose, newC20, newC10 ->
                                morningIsManual = newIsManual
                                morningTaken = newTaken
                                morningDose = newDose
                                morningCount20 = newC20
                                morningCount10 = newC10
                            }
                        )
                    }

                    if (norm.second > 0f || eveningIsManual) {
                        DosageSelector(
                            label = loc("Вечерний прием", "Evening dose"),
                            scheduledDose = norm.second,
                            isManual = eveningIsManual,
                            taken = eveningTaken,
                            currentDose = eveningDose,
                            accent = accents.secondary,
                            initialCount20 = eveningCount20,
                            initialCount10 = eveningCount10,
                            onStateChange = { newIsManual, newTaken, newDose, newC20, newC10 ->
                                eveningIsManual = newIsManual
                                eveningTaken = newTaken
                                eveningDose = newDose
                                eveningCount20 = newC20
                                eveningCount10 = newC10
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                .softPressClick(highlightColor = accents.primary) {
                                    state.resetManualDay(date)
                                    selectedDateToEdit = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(loc("Отмена", "Cancel"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .height(50.dp)
                                .background(
                                    brush = Brush.horizontalGradient(listOf(accents.primary, accents.secondary)),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .softPressClick(highlightColor = accents.secondary) {
                                    if (morningIsManual) {
                                        state.setManualDose(
                                            date = date,
                                            isMorning = true,
                                            taken = morningTaken,
                                            dose = morningDose,
                                            count20 = morningCount20,
                                            count10 = morningCount10
                                        )
                                    } else {
                                        state.resetManualDose(date, isMorning = true)
                                    }
                                    if (eveningIsManual) {
                                        state.setManualDose(
                                            date = date,
                                            isMorning = false,
                                            taken = eveningTaken,
                                            dose = eveningDose,
                                            count20 = eveningCount20,
                                            count10 = eveningCount10
                                        )
                                    } else {
                                        state.resetManualDose(date, isMorning = false)
                                    }
                                    selectedDateToEdit = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(loc("Сохранить", "Save"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InitialAccumulationContent(
    progress: Float,
    accentColor: Color,
    secondaryAccent: Color
) {
    val currentStage = currentCourseStage(progress)

    SheetTitle(currentStage.title)

    InfoTextBlock {
        Text(
            text = loc("Стадии курса:", "Course stages:"),
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        courseStages.forEach { stage ->
            StageLine(
                stage = stage,
                active = stage == currentStage,
                accentColor = accentColor,
                secondaryAccent = secondaryAccent
            )
        }
    }
}

@Composable
private fun SubstanceAccumulationContent(
    state: TrackerState,
    stats: Stats
) {
    SheetTitle(loc("Накопление вещества", "Substance accumulation"))

    InfoRow(
        label = loc("Текущий показатель:", "Current indicator:"),
        value = "%.1f ".format(Locale.US, stats.cumulativePerKg) + loc("мг/кг", "mg/kg")
    )
    SheetDivider(Modifier.padding(vertical = 18.dp))

    InfoTextBlock {
        ParagraphText(
            text = loc(
                "Золотой стандарт завершения курса - набрать в организме в сумме от 120 мг/кг до 150 мг/кг. Это снижает шанс рецидива к нулю. Цель вашей терапии - выпить ${state.targetTotalDose.toInt()} мг.",
                "The gold standard of course completion is to accumulate between 120 mg/kg and 150 mg/kg in the body. This reduces the chance of relapse to zero. Your therapy target is to take ${state.targetTotalDose.toInt()} mg."
            )
        )
    }
}

@Composable
private fun CourseCalendarContent(
    state: TrackerState,
    stats: Stats,
    selectedDateToEdit: LocalDate?,
    onDateClick: (LocalDate) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern(loc("d MMMM yyyy 'г.'", "MMMM d, yyyy"), Locale.forLanguageTag(loc("ru", "en")))
    val dailyDose = state.dailyDose.coerceAtLeast(1f)
    val courseDays = ceil(state.targetTotalDose / dailyDose).toInt().coerceAtLeast(1)
    val endDate = state.startDate.plusDays((courseDays - 1).toLong())

    val activePalette = state.activeBackgroundPalette()
    val accents = state.visualAccents(activePalette)

    SheetTitle(loc("Календарь курса", "Course calendar"))

    InfoRow(
        label = loc("Начало:", "Start:"),
        value = state.startDate.format(dateFormatter)
    )
    InfoRow(
        label = loc("Окончание:", "Finish:"),
        value = endDate.format(dateFormatter)
    )
    val daysLeft = (courseDays - stats.daysPassed).coerceAtLeast(0)
    InfoRow(
        label = loc("Осталось дней:", "Days remaining:"),
        value = formatDaysLabel(daysLeft),
        accent = true
    )
    SheetDivider(Modifier.padding(vertical = 18.dp))

    InfoTextBlock {
        ParagraphText(
            text = loc(
                "Весь примерный курс займет $courseDays дней при стабильном приеме ${formatDose(state.dailyDose)} мг в сутки. Возможно сдвинется, если вы пропустите таблетку или измените дозировку.",
                "The entire approximate course will take $courseDays days with a stable intake of ${formatDose(state.dailyDose)} mg per day. It may shift if you miss a pill or change the dosage."
            )
        )
    }

    SheetDivider(Modifier.padding(vertical = 18.dp))

    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                .clickable { currentYearMonth = currentYearMonth.minusMonths(1) }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("<", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        
        val monthLabel = currentYearMonth.format(DateTimeFormatter.ofPattern(loc("LLLL yyyy", "LLLL yyyy"), Locale.forLanguageTag(loc("ru", "en"))))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        Text(
            text = monthLabel,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        
        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                .clickable { currentYearMonth = currentYearMonth.plusMonths(1) }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(">", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        val weekDays = if (Loc.isEnglish) listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su") else listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        weekDays.forEach { dayName ->
            Text(
                text = dayName,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }

    val firstDayOfWeek = currentYearMonth.atDay(1).dayOfWeek.value
    val daysInMonth = currentYearMonth.lengthOfMonth()
    val leadingEmptySpaces = firstDayOfWeek - 1
    val totalCells = leadingEmptySpaces + daysInMonth
    val rowsCount = (totalCells + 6) / 7

    Column(modifier = Modifier.fillMaxWidth()) {
        for (rowIndex in 0 until rowsCount) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                for (colIndex in 0..6) {
                    val cellIndex = rowIndex * 7 + colIndex
                    if (cellIndex < leadingEmptySpaces || cellIndex >= totalCells) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val dayNum = cellIndex - leadingEmptySpaces + 1
                        val date = currentYearMonth.atDay(dayNum)
                        DayCell(
                            date = date,
                            state = state,
                            primaryAccent = accents.primary,
                            secondaryAccent = accents.secondary,
                            isSelected = selectedDateToEdit == date,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onDateClick(date)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    state: TrackerState,
    primaryAccent: Color,
    secondaryAccent: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val today = LocalDate.now()
    val isToday = date == today
    val isBeforeCourseStart = date.isBefore(state.startDate)
    val isBeforeManualTracking = date.isBefore(TrackerState.MANUAL_TRACKING_START_DATE)
    
    val norm = state.getScheduledNorm(date)
    val morningDosePlanned = norm.first
    val eveningDosePlanned = norm.second

    val morningTaken = state.isDoseTaken(date, isMorning = true)
    val eveningTaken = state.isDoseTaken(date, isMorning = false)

    val manualEntry = state.manualDoses[date]
    val morningManual = manualEntry?.morningManual == true
    val eveningManual = manualEntry?.eveningManual == true

    val cellAlpha = if (isBeforeCourseStart) 0.25f else 1.0f
    val isFuture = date.isAfter(today)

    Box(
        modifier = modifier
            .padding(2.dp)
            .height(52.dp)
            .background(
                color = when {
                    isSelected -> primaryAccent.copy(alpha = 0.85f)
                    isToday -> Color.White.copy(alpha = 0.12f)
                    manualEntry != null -> Color.White.copy(alpha = 0.05f)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(10.dp)
            )
            .run {
                if (isSelected) {
                    border(2.dp, Color.White, RoundedCornerShape(10.dp))
                } else if (isToday) {
                    border(1.dp, primaryAccent.copy(alpha = 0.42f), RoundedCornerShape(10.dp))
                } else this
            }
            .run {
                if (!isBeforeCourseStart && !isFuture) {
                    clickable(onClick = onClick)
                } else this
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = if (isSelected) Color.White else Color.White.copy(alpha = cellAlpha),
                fontSize = 14.sp,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Medium
            )
            
            if (!isBeforeCourseStart) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val morningDoseVal = state.getDoseAmount(date, isMorning = true)
                    if (morningDosePlanned > 0f || morningDoseVal > 0f) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = when {
                                        morningTaken -> primaryAccent
                                        isBeforeManualTracking -> primaryAccent.copy(alpha = 0.5f)
                                        morningManual && !morningTaken -> Color.White.copy(alpha = 0.15f)
                                        else -> Color.White.copy(alpha = 0.3f)
                                    },
                                    shape = CircleShape
                                )
                                .run {
                                    if (morningManual && !morningTaken) {
                                        border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                    } else this
                                }
                        )
                    }
                    val eveningDoseVal = state.getDoseAmount(date, isMorning = false)
                    if (eveningDosePlanned > 0f || eveningDoseVal > 0f) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = when {
                                        eveningTaken -> secondaryAccent
                                        isBeforeManualTracking -> secondaryAccent.copy(alpha = 0.5f)
                                        eveningManual && !eveningTaken -> Color.White.copy(alpha = 0.15f)
                                        else -> Color.White.copy(alpha = 0.3f)
                                    },
                                    shape = CircleShape
                                )
                                .run {
                                    if (eveningManual && !eveningTaken) {
                                        border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                    } else this
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DoseStatusButton(
    text: String,
    selected: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(
                color = if (selected) accent.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(12.dp)
            )
            .run {
                if (selected) {
                    border(1.dp, accent.copy(alpha = 0.82f), RoundedCornerShape(12.dp))
                } else this
            }
            .softPressClick(highlightColor = accent, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun ScheduleContent(state: TrackerState) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    SheetTitle(loc("Расписание приема", "Intake schedule"))

    ScheduleInfoRow(
        time = state.morningTime.format(timeFormatter),
        label = loc("Утро", "Morning"),
        amount = "${formatDose(state.morningDose)} " + loc("мг", "mg")
    )
    ScheduleInfoRow(
        time = state.eveningTime.format(timeFormatter),
        label = loc("Вечер", "Evening"),
        amount = "${formatDose(state.eveningDose)} " + loc("мг", "mg")
    )
    SheetDivider(Modifier.padding(vertical = 18.dp))

    InfoTextBlock {
        ParagraphText(
            text = loc(
                "Суточная схема сейчас рассчитана на ${formatDose(state.dailyDose)} мг. Лучше принимать капсулы вместе с едой, чтобы препарат усваивался ровнее.",
                "The daily schedule is currently designed for ${formatDose(state.dailyDose)} mg. It is best to take capsules with food for more even absorption."
            )
        )
    }
}

@Composable
private fun SheetTitle(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 22.dp)
    )
}

@Composable
private fun InfoTextBlock(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.075f),
                        Color.White.copy(alpha = 0.045f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        content = content
    )
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    accent: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.58f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = if (accent) Color(0xFFC7A2E9) else Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 18.dp)
        )
    }
}

@Composable
private fun ScheduleInfoRow(
    time: String,
    label: String,
    amount: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = time,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.54f),
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = amount,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StageLine(
    stage: CourseStageInfo,
    active: Boolean,
    accentColor: Color,
    secondaryAccent: Color
) {
    val lineModifier = if (active) {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.24f),
                        secondaryAccent.copy(alpha = 0.13f),
                        Color.White.copy(alpha = 0.035f)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    }

    Text(
        text = buildAnnotatedString {
            append("• ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(stage.title)
            }
            append(" (${stage.range}): ${stage.description}")
        },
        color = if (active) Color.White else Color.White.copy(alpha = 0.78f),
        fontSize = 13.sp,
        lineHeight = 18.sp,
        modifier = lineModifier
    )
}

@Composable
private fun ParagraphText(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.78f),
        fontSize = 13.sp,
        lineHeight = 19.sp
    )
}
