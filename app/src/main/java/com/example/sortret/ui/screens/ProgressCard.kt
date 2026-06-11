package com.example.sortret.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sortret.logic.Stats
import com.example.sortret.logic.TrackerState
import com.example.sortret.logic.loc
import com.example.sortret.ui.components.CourseStageInfo
import com.example.sortret.ui.components.softPressClick
import com.kyant.backdrop.Backdrop
import java.util.Locale

@Composable
internal fun ProgressCard(
    state: TrackerState,
    backdrop: Backdrop,
    accentColor: Color,
    secondaryAccent: Color,
    stats: Stats,
    currentStage: CourseStageInfo,
    progressPercent: Float,
    onClick: () -> Unit
) {
    SortretGlassCard(
        state = state,
        backdrop = backdrop,
        baseColor = accentColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .softPressClick(highlightColor = accentColor, onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = currentStage.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(24.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
                CircularProgressView(
                    progress = progressPercent,
                    accentColor = accentColor,
                    secondaryAccent = secondaryAccent
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.1f%%".format(Locale.US, progressPercent * 100),
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${stats.cumulativeDose.toInt()} / ${state.targetTotalDose.toInt()} " + loc("мг", "mg"),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
internal fun CircularProgressView(
    progress: Float,
    accentColor: Color,
    secondaryAccent: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 16.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val padding = strokeWidth / 2
        val arcSize = Size(diameter, diameter)
        val center = Offset(size.width / 2, size.height / 2)

        drawArc(
            color = Color.White.copy(alpha = 0.05f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(padding, padding),
            size = arcSize,
            style = Stroke(width = strokeWidth)
        )

        val gradient = Brush.sweepGradient(
            0.0f to accentColor,
            0.5f to secondaryAccent,
            1.0f to accentColor,
            center = center
        )

        withTransform({ rotate(-90f, center) }) {
            drawArc(
                brush = gradient,
                startAngle = 0f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(padding, padding),
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                alpha = 0.5f
            )
            drawArc(
                brush = gradient,
                startAngle = 0f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(padding, padding),
                size = arcSize,
                style = Stroke(width = strokeWidth * 0.6f, cap = StrokeCap.Round)
            )
        }
    }
}
