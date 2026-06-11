package com.example.sortret.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sortret.logic.TrackerState
import com.example.sortret.logic.loc
import com.example.sortret.ui.components.activeBackgroundPalette
import com.example.sortret.ui.components.primaryAccent
import com.example.sortret.ui.components.softPressClick
import com.kyant.backdrop.Backdrop
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun CourseSettingsBlock(
    state: TrackerState,
    backdrop: Backdrop,
    onClick: () -> Unit
) {
    val shortFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.forLanguageTag("ru"))
    val activePalette = state.activeBackgroundPalette()

    SortretGlassCard(
        state = state,
        backdrop = backdrop,
        baseColor = activePalette.primaryAccent(),
        modifier = Modifier
            .fillMaxWidth()
            .softPressClick(highlightColor = activePalette.primaryAccent(), onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = loc("Настройка курса", "Course Settings"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${state.startDate.format(shortFormatter)} · ${state.weight.toInt()} ${loc("кг", "kg")} · ${state.targetTotalDose.toInt()} ${loc("мг", "mg")}",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.62f)
                )
            }

            Text(
                text = loc("Изменить", "Edit"),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .background(activePalette.primaryAccent().copy(alpha = 0.18f), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}
