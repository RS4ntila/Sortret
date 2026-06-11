package com.example.sortret.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun CapsuleDrawing(
    color: Color,
    isLarge: Boolean,
    modifier: Modifier = Modifier
) {
    val width = if (isLarge) 20.dp else 16.dp
    val height = if (isLarge) 9.dp else 7.dp
    
    Canvas(modifier = modifier.size(width = width, height = height)) {
        val w = size.width
        val h = size.height
        val r = h / 2f
        
        // Left half (accent color)
        val leftPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w / 2f, 0f)
            lineTo(r, 0f)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(0f, 0f, h, h),
                startAngleDegrees = 270f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            lineTo(w / 2f, h)
            close()
        }
        drawPath(leftPath, color = color)
        
        // Right half (semi-transparent white)
        val rightPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w / 2f, 0f)
            lineTo(w - r, 0f)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(w - h, 0f, w, h),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(w / 2f, h)
            close()
        }
        drawPath(rightPath, color = Color.White.copy(alpha = 0.45f))
        
        // Middle divider line
        drawLine(
            color = Color.Black.copy(alpha = 0.2f),
            start = Offset(w / 2f, 0f),
            end = Offset(w / 2f, h),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
fun CapsulesRow(dose: Float, color: Color) {
    val doseInt = (dose / 10f).roundToInt() * 10
    val count20 = doseInt / 20
    val count10 = (doseInt % 20) / 10
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count20) {
            CapsuleDrawing(color = color, isLarge = true)
        }
        repeat(count10) {
            CapsuleDrawing(color = color.copy(alpha = 0.7f), isLarge = false)
        }
        if (count20 == 0 && count10 == 0) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            )
        }
    }
}

@Composable
fun CapsuleBreakdown(dose: Float, accent: Color) {
    val doseInt = (dose / 5f).roundToInt() * 5
    val count20 = doseInt / 20
    val count10 = (doseInt % 20) / 10
    val rem = doseInt % 10
    
    if (doseInt > 0) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Капсулы:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.4f)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(count20) {
                        CapsuleDrawing(color = accent, isLarge = true)
                    }
                    repeat(count10) {
                        CapsuleDrawing(color = accent.copy(alpha = 0.7f), isLarge = false)
                    }
                    if (rem > 0) {
                        Box(
                            modifier = Modifier
                                .background(accent.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "+$rem мг",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            val parts = mutableListOf<String>()
            if (count20 > 0) parts.add("$count20 × 20 мг")
            if (count10 > 0) parts.add("$count10 × 10 мг")
            if (rem > 0) parts.add("$rem мг")
            
            Text(
                text = parts.joinToString(" + "),
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DosageSelector(
    label: String,
    scheduledDose: Float,
    isManual: Boolean,
    taken: Boolean,
    currentDose: Float,
    accent: Color,
    initialCount20: Int? = null,
    initialCount10: Int? = null,
    onStateChange: (newIsManual: Boolean, newTaken: Boolean, newDose: Float, newCount20: Int?, newCount10: Int?) -> Unit
) {
    val isCustom = taken && currentDose != scheduledDose
    
    var showCustomInput by remember(isCustom) { mutableStateOf(isCustom) }
    var customDoseValue by remember(currentDose) { mutableStateOf(if (isCustom) currentDose else scheduledDose) }

    var overrideCapsules by remember(initialCount20, initialCount10) {
        mutableStateOf(initialCount20 != null || initialCount10 != null)
    }
    
    val currentDoseInt = currentDose.toInt()
    var count20 by remember(initialCount20, currentDose) {
        mutableStateOf(initialCount20 ?: (currentDoseInt / 20))
    }
    var count10 by remember(initialCount10, currentDose) {
        mutableStateOf(initialCount10 ?: ((currentDoseInt % 20) / 10))
    }

    fun notifyChange(newIsManual: Boolean, newTaken: Boolean, newDose: Float, c20: Int?, c10: Int?) {
        onStateChange(newIsManual, newTaken, newDose, c20, c10)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = accent,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 1. Status switcher (Принято / Пропуск)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val isSkip = !taken
            
            // "Принято" button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .background(
                        if (taken) accent.copy(alpha = 0.18f) else Color.Transparent,
                        RoundedCornerShape(9.dp)
                    )
                    .run {
                        if (taken) border(1.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(9.dp)) else this
                    }
                    .softPressClick(highlightColor = accent) {
                        showCustomInput = false
                        overrideCapsules = false
                        notifyChange(true, true, scheduledDose, null, null)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Принято",
                    color = if (taken) Color.White else Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            
            // "Пропуск" button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .background(
                        if (isSkip) Color.Red.copy(alpha = 0.18f) else Color.Transparent,
                        RoundedCornerShape(9.dp)
                    )
                    .run {
                        if (isSkip) border(1.dp, Color.Red.copy(alpha = 0.6f), RoundedCornerShape(9.dp)) else this
                    }
                    .softPressClick(highlightColor = Color.Red) {
                        showCustomInput = false
                        overrideCapsules = false
                        notifyChange(true, false, 0f, null, null)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Пропуск",
                    color = if (isSkip) Color.White else Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
        
        if (taken) {
            Spacer(modifier = Modifier.height(10.dp))
            
            // Toggle for capsule layout override
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Своя раскладка капсул",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                
                val targetToggleBg = if (overrideCapsules) accent.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f)
                val targetToggleBorder = if (overrideCapsules) accent else Color.White.copy(alpha = 0.15f)
                Box(
                    modifier = Modifier
                        .width(52.dp)
                        .height(28.dp)
                        .background(targetToggleBg, RoundedCornerShape(14.dp))
                        .border(1.dp, targetToggleBorder, RoundedCornerShape(14.dp))
                        .softPressClick(highlightColor = accent) {
                            val newOverride = !overrideCapsules
                            overrideCapsules = newOverride
                            if (!newOverride) {
                                val d = if (showCustomInput) customDoseValue else currentDose
                                val dInt = d.toInt()
                                count20 = dInt / 20
                                count10 = (dInt % 20) / 10
                                notifyChange(true, true, d, null, null)
                            } else {
                                val d = (count20 * 20 + count10 * 10).toFloat()
                                notifyChange(true, true, d, count20, count10)
                            }
                        },
                    contentAlignment = if (overrideCapsules) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }
            
            if (overrideCapsules) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CapsuleCounterRow(
                        label = "Капсулы 20 мг",
                        count = count20,
                        accentColor = accent,
                        onCountChange = { newVal ->
                            count20 = newVal
                            val d = (newVal * 20 + count10 * 10).toFloat()
                            notifyChange(true, true, d, newVal, count10)
                        }
                    )
                    
                    CapsuleCounterRow(
                        label = "Капсулы 10 мг",
                        count = count10,
                        accentColor = accent.copy(alpha = 0.7f),
                        onCountChange = { newVal ->
                            count10 = newVal
                            val d = (count20 * 20 + newVal * 10).toFloat()
                            notifyChange(true, true, d, count20, newVal)
                        }
                    )
                }
                
                val totalDose = (count20 * 20 + count10 * 10).toFloat()
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(accent.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .border(1.dp, accent.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Фактическая доза:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${formatDose(totalDose)} мг",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                
                // 2. Grid of dosage pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isScheduledSelected = currentDose == scheduledDose && !showCustomInput
                    DosagePill(
                        text = "${formatDose(scheduledDose)} мг",
                        subtext = "план",
                        selected = isScheduledSelected,
                        accentColor = accent,
                        modifier = Modifier.weight(1f)
                    ) {
                        showCustomInput = false
                        notifyChange(true, true, scheduledDose, null, null)
                    }
                    
                    val isCustomSelected = showCustomInput
                    DosagePill(
                        text = "Другая...",
                        selected = isCustomSelected,
                        accentColor = accent,
                        modifier = Modifier.weight(1f)
                    ) {
                        showCustomInput = true
                        notifyChange(true, true, customDoseValue, null, null)
                    }
                }
                
                if (showCustomInput) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                                .softPressClick(highlightColor = accent) {
                                    val newVal = (customDoseValue - 5f).coerceIn(0f, 120f)
                                    customDoseValue = newVal
                                    notifyChange(true, true, newVal, null, null)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }

                        GlassInput(
                            value = customDoseValue,
                            onValueChange = {
                                customDoseValue = it
                                notifyChange(true, true, it, null, null)
                            },
                            label = "Фактическая доза",
                            unit = "мг",
                            valueRange = 0f..120f,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                                .softPressClick(highlightColor = accent) {
                                    val newVal = (customDoseValue + 5f).coerceIn(0f, 120f)
                                    customDoseValue = newVal
                                    notifyChange(true, true, newVal, null, null)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                // 3. Dynamic Capsule Breakdown at the bottom (only if taken)
                val displayDose = if (showCustomInput) customDoseValue else currentDose
                if (displayDose > 0f) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CapsuleBreakdown(displayDose, accent)
                }
            }
        }
    }
}

@Composable
private fun DosagePill(
    text: String,
    subtext: String? = null,
    selected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val targetBgColor = if (selected) accentColor.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.04f)
    val targetBorderColor = if (selected) accentColor.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.08f)
    val targetTextColor = if (selected) Color.White else Color.White.copy(alpha = 0.65f)
    val targetSubtextColor = if (selected) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.38f)
    
    val bgColor by animateColorAsState(targetValue = targetBgColor, label = "bg")
    val borderColor by animateColorAsState(targetValue = targetBorderColor, label = "border")
    val textColor by animateColorAsState(targetValue = targetTextColor, label = "text")
    val subtextColor by animateColorAsState(targetValue = targetSubtextColor, label = "subtext")
    
    Column(
        modifier = modifier
            .height(52.dp)
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .softPressClick(highlightColor = accentColor, onClick = onClick)
            .padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        if (subtext != null) {
            Text(
                text = subtext,
                color = subtextColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}

@Composable
private fun ClockIcon(accentColor: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val r = size.minDimension / 2f
        drawCircle(
            color = accentColor,
            radius = r - 1.dp.toPx(),
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawLine(
            color = accentColor,
            start = center,
            end = Offset(center.x, center.y - r * 0.5f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = accentColor,
            start = center,
            end = Offset(center.x + r * 0.4f, center.y + r * 0.1f),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun CheckIcon(accentColor: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        drawLine(
            color = accentColor,
            start = Offset(w * 0.15f, h * 0.5f),
            end = Offset(w * 0.42f, h * 0.78f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = accentColor,
            start = Offset(w * 0.42f, h * 0.78f),
            end = Offset(w * 0.85f, h * 0.22f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun CrossIcon(accentColor: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        drawLine(
            color = accentColor,
            start = Offset(w * 0.25f, h * 0.25f),
            end = Offset(w * 0.75f, h * 0.75f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = accentColor,
            start = Offset(w * 0.75f, h * 0.25f),
            end = Offset(w * 0.25f, h * 0.75f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun SlidersIcon(accentColor: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        
        drawLine(
            color = accentColor.copy(alpha = 0.4f),
            start = Offset(0f, h * 0.3f),
            end = Offset(w, h * 0.3f),
            strokeWidth = 1.5.dp.toPx()
        )
        drawCircle(
            color = accentColor,
            radius = 3.dp.toPx(),
            center = Offset(w * 0.7f, h * 0.3f)
        )
        
        drawLine(
            color = accentColor.copy(alpha = 0.4f),
            start = Offset(0f, h * 0.7f),
            end = Offset(w, h * 0.7f),
            strokeWidth = 1.5.dp.toPx()
        )
        drawCircle(
            color = accentColor,
            radius = 3.dp.toPx(),
            center = Offset(w * 0.3f, h * 0.7f)
        )
    }
}

@Composable
fun CapsuleCounterRow(
    label: String,
    count: Int,
    accentColor: Color,
    onCountChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(14.dp))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .softPressClick(highlightColor = accentColor) {
                        onCountChange((count - 1).coerceAtLeast(0))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("-", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Text(
                text = count.toString(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.width(20.dp),
                textAlign = TextAlign.Center
            )
            
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .softPressClick(highlightColor = accentColor) {
                        onCountChange(count + 1)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
