package com.example.sortret.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

@Composable
fun StaticImageBackground(
    uriString: String,
    fallbackPalette: BackgroundPalette,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageBitmap by produceState<ImageBitmap?>(initialValue = null, uriString, context) {
        value = decodeBackgroundImageBitmap(context, uriString)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.linearGradient(fallbackPalette.gradientA))
    ) {
        imageBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.10f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.24f)
                        )
                    )
                )
        )
    }
}
