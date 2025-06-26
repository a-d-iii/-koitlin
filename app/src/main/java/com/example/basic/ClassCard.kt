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
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random

// Shared data model
data class ClassInfo(val id: String, val title: String, val time: String)

@Composable
fun ClassCard(
    info: ClassInfo,
    index: Int,
    daySchedule: List<ClassInfo>,
    locationName: String
) {
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val wPx = with(density) { config.screenWidthDp.dp.toPx() }
    val hPx = with(density) { config.screenHeightDp.dp.toPx() }
    val cardW = wPx * 0.85f
    val cardH = hPx * 0.7f
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

    val today = remember { LocalDate.now() }
    val weekday = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US)
    val dateNum = today.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))

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
                .background(
                    Brush.linearGradient(
                        colors = gradientColors,
                        start = Offset.Zero,
                        end = Offset(cardW, cardH)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            BlobPattern(cardWidth, cardHeight)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )
            Raindrops(cardWidth, cardHeight)

            // Weekday and date
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.dp, top = 20.dp)
            ) {
                Text(weekday, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(dateNum, color = Color.White, fontSize = 12.sp)
            }

            // Weather badge
            TemperatureBadge(
                value = "27°C",
                humidity = "65%",
                wind = "12 km/h",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 24.dp)
            )

            Text(
                locationName,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 88.dp, end = 24.dp)
            )

            val parts = remember(info.title) {
                if (info.title.contains("@")) info.title.split("@").map { it.trim() } else listOf(info.title)
            }
            val courseCode = parts.firstOrNull() ?: ""
            val roomDetail = parts.getOrNull(1) ?: ""

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(courseCode, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(start = 20.dp, bottom = 8.dp))
                if (roomDetail.isNotEmpty()) {
                    Text(roomDetail, color = Color(0xFFF0F0F0), fontSize = 28.sp, modifier = Modifier.padding(start = 20.dp, bottom = 8.dp))
                }
                Text(info.time, color = Color(0xFFF0F0F0), fontSize = 24.sp, modifier = Modifier.padding(start = 20.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 24.dp, end = 24.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Camera, contentDescription = null, tint = Color.White)
                    Text("Capture", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 6.dp))
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White)
                    Text("Rate", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 6.dp))
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
                .background(
                    Brush.linearGradient(
                        colors = gradientColors,
                        start = Offset.Zero,
                        end = Offset(cardW, cardH)
                    ),
                    shape = RoundedCornerShape(20.dp)
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
                        val parts = if (it.title.contains("@")) it.title.split("@").map { p -> p.trim() } else listOf(it.title)
                        val display = if (parts.size > 1) "${'$'}{parts[0]} • ${'$'}{parts[1]}" else it.title
                        val past = isClassOver(it.time)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                it.time,
                                color = Color(0xFFE0F0FF),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.width(90.dp)
                            )
                            Text(
                                display,
                                color = if (past) Color(0xFFFF6666) else Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
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
                        durationMillis = (6000 + Random.nextInt(4000)),
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

@Composable
private fun TemperatureBadge(
    value: String,
    humidity: String,
    wind: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.DeviceThermostat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 6.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.WaterDrop, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Text(humidity, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(start = 6.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Speed, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            Text(wind, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(start = 6.dp))
        }
    }
}

private fun isClassOver(timeRange: String): Boolean {
    val parts = timeRange.split("–").map { it.trim() }
    if (parts.size != 2) return false
    val end = parts[1]
    val endParts = end.split(":").map { it.toIntOrNull() ?: 0 }
    val endMinutes = endParts[0] * 60 + endParts.getOrElse(1) { 0 }
    val now = java.time.LocalTime.now()
    val nowMinutes = now.hour * 60 + now.minute
    return nowMinutes > endMinutes
}

