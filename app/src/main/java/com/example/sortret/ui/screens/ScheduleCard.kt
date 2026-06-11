package com.example.sortret.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sortret.logic.TrackerState
import com.example.sortret.logic.loc
import com.example.sortret.ui.components.formatDaysLabel
import com.example.sortret.ui.components.BackgroundPalette
import com.example.sortret.ui.components.formatDose
import com.example.sortret.ui.components.primaryAccent
import com.example.sortret.ui.components.secondaryAccent
import com.example.sortret.ui.components.softPressClick
import com.kyant.backdrop.Backdrop
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.*
import java.time.LocalDate
import java.time.LocalDateTime
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import com.example.sortret.ui.components.BackdropDialogSurface
import com.example.sortret.ui.components.visualAccents
import com.example.sortret.ui.components.GlassInput
import com.example.sortret.ui.components.DosageSelector
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import java.util.Locale

@Composable
internal fun ScheduleCard(
    state: TrackerState,
    backdrop: Backdrop,
    backgroundPalette: BackgroundPalette,
    tabletCoverageDays: Int,
    onEditDate: (LocalDate) -> Unit,
    onClick: () -> Unit
) {
    val coverageText = when {
        state.dailyDose <= 0f -> loc("Укажите время и дозировку", "Specify time and dosage")
        state.tabletCount20 <= 0 && state.tabletCount10 <= 0 -> loc("Укажите запас таблеток", "Specify tablet stock")
        tabletCoverageDays > 0 -> loc("Таблеток хватит еще на ", "Tablets will last for another ") + formatDaysLabel(tabletCoverageDays)
        else -> loc("Таблеток больше не хватает", "No more tablets remaining")
    }

    val remaining20 = state.remainingTablets20
    val remaining10 = state.remainingTablets10

    SortretGlassCard(
        state = state,
        backdrop = backdrop,
        baseColor = backgroundPalette.primaryAccent(),
        modifier = Modifier
            .fillMaxWidth()
            .softPressClick(highlightColor = backgroundPalette.primaryAccent(), onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = loc("График приема", "Intake Schedule"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = coverageText,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.52f),
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 5.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Badge 20 mg
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💊 ", fontSize = 10.sp)
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color.White.copy(alpha = 0.52f))) {
                                            append(loc("20 мг: ", "20 mg: "))
                                        }
                                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                                            append("$remaining20 " + loc("шт.", "pcs."))
                                        }
                                    },
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Badge 10 mg
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💊 ", fontSize = 10.sp)
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color.White.copy(alpha = 0.52f))) {
                                            append(loc("10 мг: ", "10 mg: "))
                                        }
                                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                                            append("$remaining10 " + loc("шт.", "pcs."))
                                        }
                                    },
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    backgroundPalette.primaryAccent().copy(alpha = 0.24f),
                                    backgroundPalette.secondaryAccent().copy(alpha = 0.15f)
                                )
                            ),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 13.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${formatDose(state.dailyDose)} " + loc("мг/сутки", "mg/day"),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ScheduleRow(
                state = state,
                backdrop = backdrop,
                time = state.morningTime,
                isMorning = true,
                label = loc("Утро", "Morning"),
                amount = "${formatDose(state.morningDose)} " + loc("мг", "mg"),
                accent = backgroundPalette.primaryAccent(),
                onClick = {
                    onEditDate(LocalDate.now())
                }
            )
            Spacer(modifier = Modifier.height(18.dp))
            ScheduleRow(
                state = state,
                backdrop = backdrop,
                time = state.eveningTime,
                isMorning = false,
                label = loc("Вечер", "Evening"),
                amount = "${formatDose(state.eveningDose)} " + loc("мг", "mg"),
                accent = backgroundPalette.secondaryAccent(),
                onClick = {
                    onEditDate(LocalDate.now())
                }
            )
        }
    }
}

@Composable
internal fun ScheduleRow(
    state: TrackerState,
    backdrop: Backdrop,
    time: LocalTime,
    isMorning: Boolean,
    label: String,
    amount: String,
    accent: Color,
    onClick: (() -> Unit)? = null
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val today = LocalDate.now()
    val taken = state.isDoseTaken(today, isMorning)
    val manualEntry = state.manualDoses[today]
    val isManual = if (isMorning) manualEntry?.morningManual == true else manualEntry?.eveningManual == true

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .run {
                if (onClick != null) {
                    softPressClick(highlightColor = accent, onClick = onClick)
                } else this
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = when {
                            taken -> accent.copy(alpha = 0.28f)
                            isManual -> Color.White.copy(alpha = 0.08f)
                            else -> accent.copy(alpha = 0.12f)
                        },
                        shape = CircleShape
                    )
                    .run {
                        if (taken) {
                            border(1.dp, accent.copy(alpha = 0.92f), CircleShape)
                        } else if (isManual) {
                            border(1.dp, Color.White.copy(alpha = 0.32f), CircleShape)
                        } else this
                    },
                contentAlignment = Alignment.Center
            ) {
                if (taken) {
                    Canvas(modifier = Modifier.size(14.dp)) {
                        val w = size.width
                        val h = size.height
                        drawLine(
                            color = Color.White,
                            start = Offset(w * 0.15f, h * 0.5f),
                            end = Offset(w * 0.42f, h * 0.75f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.White,
                            start = Offset(w * 0.42f, h * 0.75f),
                            end = Offset(w * 0.85f, h * 0.25f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                } else if (isManual) {
                    Canvas(modifier = Modifier.size(12.dp)) {
                        val w = size.width
                        val h = size.height
                        drawLine(
                            color = Color.White.copy(alpha = 0.6f),
                            start = Offset(w * 0.2f, h * 0.2f),
                            end = Offset(w * 0.8f, h * 0.8f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.6f),
                            start = Offset(w * 0.8f, h * 0.2f),
                            end = Offset(w * 0.2f, h * 0.8f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(accent.copy(alpha = 0.85f), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(13.dp))

            Column {
                Text(
                    text = time.format(timeFormatter),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = label, fontSize = 13.sp, color = Color.White.copy(alpha = 0.52f))
            }
        }

        SortretGlassCard(
            state = state,
            backdrop = backdrop,
            shape = CircleShape,
            baseColor = accent
        ) {
            val displayAmount = if (isManual) {
                if (isMorning) manualEntry?.morningDose ?: 0f else manualEntry?.eveningDose ?: 0f
            } else {
                if (isMorning) state.morningDose else state.eveningDose
            }
            Text(
                text = "${formatDose(displayAmount)} " + loc("мг", "mg"),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
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
