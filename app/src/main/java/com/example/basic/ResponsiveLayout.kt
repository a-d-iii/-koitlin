package com.example.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * Scales the provided [content] so it maintains the same aspect ratio on all
 * screen sizes. The layout uses [designWidth] and [designHeight] as the base
 * dimensions of the UI and scales the content uniformly to fit within the
 * available space.
 */
@Composable
fun ScaledLayout(
    designWidth: Dp = 360.dp,
    designHeight: Dp = 640.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val scale = min(maxWidth / designWidth, maxHeight / designHeight)
        Box(
            modifier = Modifier
                .size(designWidth, designHeight)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .align(Alignment.Center),
            content = content
        )
    }
}
