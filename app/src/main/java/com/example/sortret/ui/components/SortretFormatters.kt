package com.example.sortret.ui.components

import java.util.Locale

fun formatDose(value: Float): String {
    return if (value % 1f == 0f) {
        value.toInt().toString()
    } else {
        "%.1f".format(Locale.US, value)
    }
}

fun formatDaysLabel(value: Int): String {
    val absValue = kotlin.math.abs(value)
    val mod100 = absValue % 100
    val mod10 = absValue % 10
    val word = when {
        mod100 in 11..14 -> "дней"
        mod10 == 1 -> "день"
        mod10 in 2..4 -> "дня"
        else -> "дней"
    }
    return "$value $word"
}
