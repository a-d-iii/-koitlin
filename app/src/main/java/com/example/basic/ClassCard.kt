package com.example.basic

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

// Shared data model
data class ClassInfo(val id: String, val title: String, val time: String)

@Composable
fun ClassCard(info: ClassInfo, index: Int, daySchedule: List<ClassInfo>) {
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val wPx = with(density) { config.screenWidthDp.dp.toPx() }
    val hPx = with(density) { config.screenHeightDp.dp.toPx() }
    val cardW = wPx * 0.85f
    val cardH = hPx * 0.60f
    val cardWidth = with(density) { cardW.toDp() }
    val cardHeight = with(density) { cardH.toDp() }

    val gradients = listOf(
        listOf(Color(0xFF8E44AD), Color(0xFFC0392B), Color(0xFFF39C12)),
        listOf(Color(0xFF2C3E50), Color(0xFF34495E), Color(0xFF16A085)),
        listOf(Color(0xFFE74C3C), Color(0xFFD35400), Color(0xFFF1C40F)),
        listOf(Color(0xFF27AE60), Color(0xFF2980B9), Color(0xFF8E44AD)),
        listOf(Color(0xFFE67E22), Color(0xFFD35400), Color(0xFFCD6155))
    )
    val gradientColors = gradients[index % gradients.size]

    var flipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(350, easing = LinearEasing)
    )

    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .padding(vertical = 16.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp), clip = false)
            .pointerInput(Unit) { detectTapGestures(onDoubleTap = { flipped = !flipped }) }
    ) {
        // FRONT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 8 * density.density
                    alpha = if (rotation <= 90f) 1f else 0f
                }
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = gradientColors,
                        start = Offset.Zero,
                        end = Offset(cardW, cardH)
                    )
                )
        ) {
            BlobPattern(cardWidth, cardHeight)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )
            Raindrops(cardWidth, cardHeight)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    info.title,
                    color = Color.White,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(8.dp))
                Text(info.time, color = Color.White)
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Filled.Camera, contentDescription = null, tint = Color.White)
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White)
                }
            }
        }

        // BACK
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation - 180f
                    cameraDistance = 8 * density.density
                    alpha = if (rotation > 90f) 1f else 0f
                }
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = gradientColors,
                        start = Offset.Zero,
                        end = Offset(cardW, cardH)
                    )
                )
        ) {
            BlobPattern(cardWidth, cardHeight)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .padding(24.dp)
            ) {
                Column {
                    Text("Today's Schedule", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    daySchedule.forEach {
                        Text(
                            "${'$'}{it.time} • ${'$'}{it.title}",
                            color = Color.White,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BlobPattern(cardWidth: Dp, cardHeight: Dp) {
    val density = LocalDensity.current
    val w = with(density) { cardWidth.toPx() }
    val h = with(density) { cardHeight.toPx() }
    Canvas(modifier = Modifier.fillMaxSize()) {
        val p1 = Path().apply {
            moveTo(0.1f * w, 0.15f * h)
            cubicTo(-0.15f * w, -0.1f * h, 0.6f * w, 0.25f * h, 0.5f * w, 0.5f * h)
            cubicTo(0.4f * w, 0.8f * h, -0.05f * w, 0.85f * h, 0.1f * w, 0.55f * h)
            cubicTo(0.25f * w, 0.3f * h, -0.05f * w, 0.35f * h, 0.1f * w, 0.15f * h)
            close()
        }
        drawPath(p1, Color.White.copy(alpha = 0.12f))
        val p2 = Path().apply {
            moveTo(0.5f * w, 0.2f * h)
            cubicTo(0.8f * w, 0.05f * h, 1.2f * w, 0.4f * h, 0.9f * w, 0.55f * h)
            cubicTo(0.7f * w, 0.75f * h, 0.6f * w, 0.45f * h, 0.5f * w, 0.3f * h)
            cubicTo(0.4f * w, 0.15f * h, 0.3f * w, 0.25f * h, 0.5f * w, 0.2f * h)
            close()
        }
        drawPath(p2, Color.White.copy(alpha = 0.08f))
        val p3 = Path().apply {
            moveTo(0.2f * w, 0.6f * h)
            cubicTo(0f * w, 0.8f * h, 0.4f * w, 1.1f * h, 0.7f * w, 0.9f * h)
            cubicTo(1.0f * w, 0.75f * h, 0.6f * w, 0.65f * h, 0.4f * w, 0.7f * h)
            cubicTo(0.2f * w, 0.75f * h, 0.15f * w, 0.55f * h, 0.2f * w, 0.6f * h)
            close()
        }
        drawPath(p3, Color.White.copy(alpha = 0.05f))
    }
}

@Composable
private fun Raindrops(cardWidth: Dp, cardHeight: Dp) {
    val density = LocalDensity.current
    val widthPx = with(density) { cardWidth.toPx() }
    val heightPx = with(density) { cardHeight.toPx() }
    val drops = remember {
        List(12) {
            mutableStateOf(RandomDropState(
                anim = Animatable(-Random.nextFloat() * heightPx)
            ))
        }
    }

    drops.forEach { state ->
        LaunchedEffect(state) {
            while (true) {
                state.value.anim.snapTo(-20f)
                state.value.anim.animateTo(
                    targetValue = heightPx + 20f,
                    animationSpec = tween(
                        durationMillis = (1800 + Random.nextInt(800)),
                        easing = LinearEasing,
                        delayMillis = Random.nextInt(0, 1200)
                    )
                )
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drops.forEach { state ->
            val x = state.value.xPos(widthPx)
            drawRoundRect(
                color = Color.White.copy(alpha = 0.6f),
                topLeft = Offset(x, state.value.anim.value),
                size = androidx.compose.ui.geometry.Size(1f, 12f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(0.5f, 0.5f)
            )
        }
    }
}

private class RandomDropState(
    val anim: Animatable<Float, AnimationVector1D>
) {
    fun xPos(width: Float) = Random.nextFloat() * (width - 2f) + 1f
}

