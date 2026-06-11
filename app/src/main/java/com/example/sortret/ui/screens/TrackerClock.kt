package com.example.sortret.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun rememberTrackerNow(): LocalDateTime {
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            val current = LocalDateTime.now()
            now = current

            val millisToNextMinute = 60_000L - (current.second * 1000L + current.nano / 1_000_000L)
            delay(millisToNextMinute.coerceAtLeast(250L) + 80L)
        }
    }

    return now
}

internal fun LocalDateTime.formatTrackerClock(): String {
    return format(DateTimeFormatter.ofPattern("HH:mm"))
}
