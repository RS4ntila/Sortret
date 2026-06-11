package com.example.sortret.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.sortret.logic.loc
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sortret.logic.TrackerState
import com.example.sortret.ui.components.BackgroundPalette
import com.example.sortret.ui.components.primaryAccent
import com.kyant.backdrop.Backdrop

@Composable
internal fun TrackerHeader(
    state: TrackerState,
    backdrop: Backdrop,
    backgroundPalette: BackgroundPalette,
    displayedDate: String,
    currentTimeText: String,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = loc("Сортрет", "Sortret"),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = displayedDate,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            SortretGlassCard(
                state = state,
                backdrop = backdrop,
                shape = CircleShape,
                baseColor = backgroundPalette.primaryAccent()
            ) {
                Text(
                    text = currentTimeText,
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
