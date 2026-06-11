package com.example.sortret.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp

@Composable
internal fun CalendarIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawRoundRect(
            color = Color.White,
            size = Size(w * 0.8f, h * 0.8f),
            topLeft = Offset(w * 0.1f, h * 0.2f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )

        clipRect(
            left = w * 0.1f,
            top = h * 0.2f,
            right = w * 0.9f,
            bottom = h * 0.45f
        ) {
            drawRoundRect(
                color = Color(0xFFEF5350),
                size = Size(w * 0.8f, h * 0.8f),
                topLeft = Offset(w * 0.1f, h * 0.2f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }

        drawLine(
            color = Color.White,
            start = Offset(w * 0.3f, h * 0.32f),
            end = Offset(w * 0.7f, h * 0.32f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color.Black.copy(alpha = 0.7f),
            start = Offset(w * 0.35f, h * 0.65f),
            end = Offset(w * 0.65f, h * 0.65f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
internal fun PillIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val pillWidth = w * 0.45f
        val pillHeight = h * 0.9f

        withTransform({ rotate(45f, pivot = Offset(w / 2, h / 2)) }) {
            drawRoundRect(
                color = Color.White,
                topLeft = Offset((w - pillWidth) / 2, (h - pillHeight) / 2),
                size = Size(pillWidth, pillHeight),
                cornerRadius = CornerRadius(pillWidth / 2, pillWidth / 2)
            )
            clipRect(left = 0f, top = h / 2, right = w, bottom = h) {
                drawRoundRect(
                    color = Color(0xFFFFB74D),
                    topLeft = Offset((w - pillWidth) / 2, (h - pillHeight) / 2),
                    size = Size(pillWidth, pillHeight),
                    cornerRadius = CornerRadius(pillWidth / 2, pillWidth / 2)
                )
            }
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset((w - pillWidth) / 2 + pillWidth * 0.25f, (h - pillHeight) / 2 + pillHeight * 0.15f),
                end = Offset((w - pillWidth) / 2 + pillWidth * 0.25f, h / 2 - pillHeight * 0.05f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
