package com.example.sortret.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

suspend fun decodeBackgroundImageBitmap(
    context: Context,
    uriString: String
): ImageBitmap? = withContext(Dispatchers.IO) {
    decodeBackgroundBitmap(
        context = context,
        uriString = uriString,
        maxDimension = 2200
    )?.asImageBitmap()
}

suspend fun extractDominantBackgroundAccentArgb(
    context: Context,
    uriString: String
): Int? = withContext(Dispatchers.IO) {
    val bitmap = decodeBackgroundBitmap(
        context = context,
        uriString = uriString,
        maxDimension = 96
    ) ?: return@withContext null

    runCatching {
        bitmap.findAccentArgb()
    }.also {
        bitmap.recycle()
    }.getOrNull()
}

private fun decodeBackgroundBitmap(
    context: Context,
    uriString: String,
    maxDimension: Int
): Bitmap? {
    if (uriString.isBlank()) return null

    return runCatching {
        val uri = Uri.parse(uriString)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            decodeModernBitmap(context, uri, maxDimension)
        } else {
            decodeLegacyBitmap(context, uri, maxDimension)
        }
    }.getOrNull()
}

private fun decodeModernBitmap(
    context: Context,
    uri: Uri,
    maxDimension: Int
): Bitmap {
    val source = ImageDecoder.createSource(context.contentResolver, uri)
    return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
        val width = info.size.width.coerceAtLeast(1)
        val height = info.size.height.coerceAtLeast(1)
        val scale = maxOf(width, height).toFloat() / maxDimension

        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        if (scale > 1f) {
            decoder.setTargetSize(
                (width / scale).roundToInt().coerceAtLeast(1),
                (height / scale).roundToInt().coerceAtLeast(1)
            )
        }
    }
}

private fun decodeLegacyBitmap(
    context: Context,
    uri: Uri,
    maxDimension: Int
): Bitmap? {
    val bounds = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }

    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream, null, bounds)
    }

    val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight, maxDimension)
    }

    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream, null, decodeOptions)
    }
}

private fun calculateSampleSize(
    width: Int,
    height: Int,
    maxDimension: Int
): Int {
    var sampleSize = 1
    var sampledWidth = width
    var sampledHeight = height

    while (sampledWidth / 2 >= maxDimension || sampledHeight / 2 >= maxDimension) {
        sampleSize *= 2
        sampledWidth /= 2
        sampledHeight /= 2
    }

    return sampleSize.coerceAtLeast(1)
}

private fun Bitmap.findAccentArgb(): Int? {
    return findAccentArgb(requireSaturation = true) ?: findAccentArgb(requireSaturation = false)
}

private fun Bitmap.findAccentArgb(requireSaturation: Boolean): Int? {
    val buckets = HashMap<Int, AccentBucket>()
    val step = (minOf(width, height) / 48).coerceAtLeast(1)
    val hsv = FloatArray(3)

    for (y in 0 until height step step) {
        for (x in 0 until width step step) {
            val pixel = getPixel(x, y)
            val alpha = android.graphics.Color.alpha(pixel)
            if (alpha < 80) continue

            val red = android.graphics.Color.red(pixel)
            val green = android.graphics.Color.green(pixel)
            val blue = android.graphics.Color.blue(pixel)
            android.graphics.Color.RGBToHSV(red, green, blue, hsv)

            val saturation = hsv[1]
            val value = hsv[2]
            if (value < 0.14f) continue
            if (requireSaturation && saturation < 0.18f) continue

            val key = ((red / 32) shl 16) or ((green / 32) shl 8) or (blue / 32)
            val weight = (0.35f + saturation * 1.65f) * (0.45f + value)
            val bucket = buckets.getOrPut(key) { AccentBucket() }
            bucket.red += red * weight
            bucket.green += green * weight
            bucket.blue += blue * weight
            bucket.weight += weight
        }
    }

    val bestBucket = buckets.values.maxByOrNull { it.weight } ?: return null
    val red = (bestBucket.red / bestBucket.weight).roundToInt().coerceIn(0, 255)
    val green = (bestBucket.green / bestBucket.weight).roundToInt().coerceIn(0, 255)
    val blue = (bestBucket.blue / bestBucket.weight).roundToInt().coerceIn(0, 255)

    return boostAccent(red, green, blue)
}

private fun boostAccent(
    red: Int,
    green: Int,
    blue: Int
): Int {
    val hsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(red, green, blue, hsv)
    hsv[1] = hsv[1].coerceAtLeast(0.42f).coerceAtMost(0.92f)
    hsv[2] = hsv[2].coerceAtLeast(0.66f).coerceAtMost(0.96f)
    return android.graphics.Color.HSVToColor(hsv)
}

private data class AccentBucket(
    var red: Float = 0f,
    var green: Float = 0f,
    var blue: Float = 0f,
    var weight: Float = 0f
)
