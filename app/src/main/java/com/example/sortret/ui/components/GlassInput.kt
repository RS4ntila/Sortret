package com.example.sortret.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlassInput(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    unit: String = "",
    label: String = "",
    valueRange: ClosedFloatingPointRange<Float> = 0f..Float.MAX_VALUE
) {
    var isFocused by remember { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val focusScope = rememberCoroutineScope()
    var textValue by remember {
        mutableStateOf(TextFieldValue(formatInputValue(value)))
    }

    LaunchedEffect(value, isFocused) {
        if (!isFocused) {
            textValue = TextFieldValue(formatInputValue(value))
        }
    }

    fun commitText(text: String) {
        val parsed = text.normalizedNumberOrNull()
        val committed = (parsed ?: value).coerceIn(valueRange)
        textValue = TextFieldValue(
            text = formatInputValue(committed),
            selection = TextRange(formatInputValue(committed).length)
        )
        if (committed != value) {
            onValueChange(committed)
        }
    }

    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            androidx.compose.material3.Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .bringIntoViewRequester(bringIntoViewRequester)
                .background(
                    if (isFocused) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f),
                    RoundedCornerShape(16.dp)
                )
                .onFocusChanged {
                    val wasFocused = isFocused
                    isFocused = it.isFocused
                    if (it.isFocused) {
                        focusScope.launch {
                            delay(220)
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                    if (wasFocused && !it.isFocused) {
                        commitText(textValue.text)
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = textValue,
                    onValueChange = { incoming ->
                        val filtered = incoming.filterNumberInput()
                        textValue = filtered

                        val parsed = filtered.text.normalizedNumberOrNull()
                        if (parsed != null && parsed in valueRange) {
                            onValueChange(parsed)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (textValue.text.isEmpty() && !isFocused) {
                            androidx.compose.material3.Text(
                                text = "0",
                                color = Color.White.copy(alpha = 0.3f),
                                fontSize = 18.sp
                            )
                        }
                        innerTextField()
                    }
                )

                if (unit.isNotEmpty()) {
                    androidx.compose.material3.Text(
                        text = unit,
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

private fun TextFieldValue.filterNumberInput(): TextFieldValue {
    val normalized = text.replace(',', '.')
    val filtered = buildString {
        var dotAdded = false
        normalized.forEach { char ->
            when {
                char.isDigit() -> append(char)
                char == '.' && !dotAdded -> {
                    append(char)
                    dotAdded = true
                }
            }
        }
    }
    val selectionEnd = selection.end.coerceAtMost(filtered.length)
    return copy(text = filtered, selection = TextRange(selectionEnd))
}

private fun String.normalizedNumberOrNull(): Float? {
    val normalized = trim().replace(',', '.')
    if (normalized.isEmpty() || normalized == ".") return null
    return normalized.toFloatOrNull()
}

private fun formatInputValue(value: Float): String {
    return if (value % 1f == 0f) {
        value.roundToInt().toString()
    } else {
        "%.1f".format(java.util.Locale.US, value)
    }
}
