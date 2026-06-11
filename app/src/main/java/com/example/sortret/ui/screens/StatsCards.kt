package com.example.sortret.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sortret.logic.Stats
import com.example.sortret.logic.TrackerState
import com.example.sortret.logic.loc
import com.example.sortret.ui.components.BackgroundPalette
import com.example.sortret.ui.components.primaryAccent
import com.example.sortret.ui.components.secondaryAccent
import com.example.sortret.ui.components.softPressClick
import com.kyant.backdrop.Backdrop
import java.util.Locale

@Composable
internal fun StatsCards(
    state: TrackerState,
    backdrop: Backdrop,
    backgroundPalette: BackgroundPalette,
    stats: Stats,
    courseTotalDays: Int,
    onSubstanceClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SortretGlassCard(
            state = state,
            backdrop = backdrop,
            baseColor = backgroundPalette.primaryAccent(),
            modifier = Modifier
                .weight(1f)
                .height(140.dp)
                .softPressClick(highlightColor = backgroundPalette.primaryAccent(), onClick = onSubstanceClick)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                PillIcon(modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "%.1f".format(Locale.US, stats.cumulativePerKg),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = loc("мг / кг", "mg / kg"),
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }

        SortretGlassCard(
            state = state,
            backdrop = backdrop,
            baseColor = backgroundPalette.secondaryAccent(),
            modifier = Modifier
                .weight(1f)
                .height(140.dp)
                .softPressClick(highlightColor = backgroundPalette.secondaryAccent(), onClick = onCalendarClick)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CalendarIcon(modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${stats.daysPassed}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = " / $courseTotalDays",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = loc("дней курса", "course days"),
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}
