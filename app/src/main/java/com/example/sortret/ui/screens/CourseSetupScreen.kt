package com.example.sortret.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sortret.logic.DosePeriod
import com.example.sortret.logic.StartShift
import com.example.sortret.logic.TrackerState
import com.example.sortret.logic.loc
import com.example.sortret.ui.components.CapsuleBreakdown
import com.example.sortret.ui.components.CourseDateDialog
import com.example.sortret.ui.components.GlassInput
import com.example.sortret.ui.components.activeBackgroundPalette
import com.example.sortret.ui.components.primaryAccent
import com.example.sortret.ui.components.softPressClick
import com.example.sortret.ui.components.visualAccents
import com.kyant.backdrop.Backdrop
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun CourseSetupScreen(
    state: TrackerState,
    backdrop: Backdrop,
    modifier: Modifier = Modifier
) {
    val activePalette = state.activeBackgroundPalette()
    val accents = state.visualAccents(activePalette)
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag(loc("ru", "en")))

    // Local state for setup wizard values
    var chosenWeight by remember { mutableStateOf(68f) }
    var selectedRatioPreset by remember { mutableStateOf(150f) } // 120f, 150f, or -1f for custom
    var customRatio by remember { mutableStateOf(150f) }
    var chosenStartDate by remember { mutableStateOf(LocalDate.now()) }
    var chosenStartShift by remember { mutableStateOf(StartShift.EVENING) }
    var chosenMorningDose by remember { mutableStateOf(20f) }
    var chosenEveningDose by remember { mutableStateOf(30f) }

    var showDatePicker by remember { mutableStateOf(false) }

    val activeRatio = if (selectedRatioPreset == -1f) customRatio else selectedRatioPreset
    val targetTotalDose = chosenWeight * activeRatio

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // Brand Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            PillIcon(modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Sortret",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.5.sp
            )
        }

        Text(
            text = loc("Настройка нового курса приема", "Setting up a new intake course"),
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.65f),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        SortretGlassCard(
            state = state,
            backdrop = backdrop,
            baseColor = activePalette.primaryAccent(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section 1: Weight Setup
                Column {
                    Text(
                        text = loc("1. Ваш вес", "1. Your weight").uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accents.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    GlassInput(
                        value = chosenWeight,
                        onValueChange = { chosenWeight = it },
                        unit = loc("кг", "kg"),
                        valueRange = 30f..150f
                    )
                }

                // Section 2: Target Ratio Preset
                Column {
                    Text(
                        text = loc("2. Целевая кумулятивная доза", "2. Target cumulative dose").uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accents.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf(
                            Triple(loc("120 мг/кг", "120 mg/kg"), 120f, loc("минимальная", "minimum")),
                            Triple(loc("150 мг/кг", "150 mg/kg"), 150f, loc("оптимальная", "optimal")),
                            Triple(loc("Своя", "Custom"), -1f, "")
                        )
                        presets.forEach { (label, ratio, sub) ->
                            val selected = (selectedRatioPreset == ratio)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .background(
                                        color = if (selected) accents.primary.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.04f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) accents.primary else Color.White.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .softPressClick(highlightColor = accents.primary) {
                                        selectedRatioPreset = ratio
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = label,
                                        color = if (selected) Color.White else Color.White.copy(alpha = 0.65f),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (sub.isNotEmpty()) {
                                        Text(
                                            text = sub,
                                            color = if (selected) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.38f),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (selectedRatioPreset == -1f) {
                        Spacer(modifier = Modifier.height(12.dp))
                        GlassInput(
                            value = customRatio,
                            onValueChange = { customRatio = it },
                            label = loc("Кумулятивный коэффициент", "Cumulative coefficient"),
                            unit = loc("мг/кг", "mg/kg"),
                            valueRange = 50f..300f
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Dose glowing badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        accents.primary.copy(alpha = 0.15f),
                                        accents.secondary.copy(alpha = 0.15f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        accents.primary.copy(alpha = 0.4f),
                                        accents.secondary.copy(alpha = 0.4f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = loc("ЦЕЛЕВАЯ ДОЗА НА КУРС", "TARGET COURSE DOSE"),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White.copy(alpha = 0.5f),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${targetTotalDose.roundToInt()} " + loc("мг", "mg"),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = loc(
                                    "(${activeRatio.toInt()} мг/кг при весе ${chosenWeight.roundToInt()} кг)",
                                    "(${activeRatio.toInt()} mg/kg for weight ${chosenWeight.roundToInt()} kg)"
                                ),
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Section 3: Start Date
                Column {
                    Text(
                        text = loc("3. Дата начала курса", "3. Course start date").uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accents.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val isToday = chosenStartDate == LocalDate.now()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(
                                    color = if (isToday) accents.primary.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isToday) accents.primary else Color.White.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .softPressClick(highlightColor = accents.primary) {
                                    chosenStartDate = LocalDate.now()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = loc("Прямо сейчас", "Right now"),
                                color = if (isToday) Color.White else Color.White.copy(alpha = 0.65f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val isCustomDate = !isToday
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(
                                    color = if (isCustomDate) accents.secondary.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isCustomDate) accents.secondary else Color.White.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .softPressClick(highlightColor = accents.secondary) {
                                    showDatePicker = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = loc("Выбрать дату", "Select date"),
                                color = if (isCustomDate) Color.White else Color.White.copy(alpha = 0.65f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = loc("Старт: ", "Start: ") + chosenStartDate.format(dateFormatter),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Section 4: First shift option
                Column {
                    Text(
                        text = loc("4. Первый прием", "4. First intake").uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accents.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(
                                    color = if (chosenStartShift == StartShift.MORNING) accents.primary.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (chosenStartShift == StartShift.MORNING) accents.primary else Color.White.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .softPressClick(highlightColor = accents.primary) {
                                    chosenStartShift = StartShift.MORNING
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = loc("Утро", "Morning"),
                                color = if (chosenStartShift == StartShift.MORNING) Color.White else Color.White.copy(alpha = 0.65f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(
                                    color = if (chosenStartShift == StartShift.EVENING) accents.secondary.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (chosenStartShift == StartShift.EVENING) accents.secondary else Color.White.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .softPressClick(highlightColor = accents.secondary) {
                                    chosenStartShift = StartShift.EVENING
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = loc("Вечер", "Evening"),
                                color = if (chosenStartShift == StartShift.EVENING) Color.White else Color.White.copy(alpha = 0.65f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Section 5: Dose Schedule
                Column {
                    Text(
                        text = loc("5. Расписание приема", "5. Intake schedule").uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accents.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GlassInput(
                            value = chosenMorningDose,
                            onValueChange = { chosenMorningDose = it },
                            label = loc("Утренний прием", "Morning dose"),
                            unit = loc("мг", "mg"),
                            valueRange = 0f..120f,
                            modifier = Modifier.weight(1f)
                        )

                        GlassInput(
                            value = chosenEveningDose,
                            onValueChange = { chosenEveningDose = it },
                            label = loc("Вечерний прием", "Evening dose"),
                            unit = loc("мг", "mg"),
                            valueRange = 0f..120f,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (chosenMorningDose > 0f) {
                        Spacer(modifier = Modifier.height(10.dp))
                        CapsuleBreakdown(chosenMorningDose, accents.primary)
                    }

                    if (chosenEveningDose > 0f) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CapsuleBreakdown(chosenEveningDose, accents.secondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Launch Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(accents.primary, accents.secondary)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .softPressClick(highlightColor = accents.secondary) {
                            // Save configurations
                            state.weight = chosenWeight
                            state.startDate = chosenStartDate
                            state.startShift = chosenStartShift
                            state.targetTotalDose = targetTotalDose
                            state.morningDose = chosenMorningDose
                            state.eveningDose = chosenEveningDose

                            // Reset and initialize dose periods
                            state.dosePeriods.clear()
                            state.dosePeriods.add(DosePeriod(chosenStartDate, chosenMorningDose, chosenEveningDose))

                            // Finish setup navigation
                            state.courseSetupCompleted = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = loc("Запустить курс", "Start course"),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showDatePicker) {
        CourseDateDialog(
            selectedDateMillis = chosenStartDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            backdrop = backdrop,
            accentColor = accents.primary,
            secondaryAccent = accents.secondary,
            onDismiss = { showDatePicker = false },
            onConfirm = { selectedMillis ->
                selectedMillis?.let {
                    chosenStartDate = Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }
                showDatePicker = false
            }
        )
    }
}
