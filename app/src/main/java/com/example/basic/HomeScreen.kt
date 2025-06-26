package com.example.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class PanelState { None, Top, Bottom }

data class ClassInfo(val id: String, val title: String, val time: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen() {
    val baseCards = listOf(
        ClassInfo("1", "ECE1001 @ Lab 3", "08:00 – 08:50"),
        ClassInfo("2", "MAT1002 @ Room 105", "09:00 – 09:50"),
        ClassInfo("3", "PHY1003 @ Hall A", "10:00 – 10:50"),
        ClassInfo("4", "CHE1004 @ Lab 2", "11:00 – 11:50"),
        ClassInfo("5", "CSE1005 @ Room 201", "12:00 – 12:50"),
    )
    val cards = listOf(ClassInfo("overview", "", "")) + baseCards
    val pagerState = rememberPagerState(pageCount = { cards.size })
    var panel by remember { mutableStateOf(PanelState.None) }
    val scope = rememberCoroutineScope()
    var dragAmount by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(panel) {
                        detectDragGestures(
                            onDragEnd = {
                                if (panel == PanelState.None) {
                                    if (dragAmount > 40f) panel = PanelState.Top
                                    else if (dragAmount < -40f) panel = PanelState.Bottom
                                } else if (panel == PanelState.Top && dragAmount < -40f) {
                                    panel = PanelState.None
                                } else if (panel == PanelState.Bottom && dragAmount > 40f) {
                                    panel = PanelState.None
                                }
                                dragAmount = 0f
                            },
                            onDrag = { change, drag ->
                                dragAmount += drag.y
                            }
                        )
                    }
            ) { page ->
                if (page == 0) {
                    SummaryCard()
                } else {
                    ClassCard(
                        info = baseCards[page - 1],
                        index = page - 1,
                        daySchedule = baseCards
                    )
                }
            }

            if (pagerState.currentPage > 0) {
                NumberRow(
                    count = baseCards.size,
                    activeIndex = pagerState.currentPage - 1,
                    onTap = { idx -> scope.launch { pagerState.animateScrollToPage(idx + 1) } }
                )
            }
        }

        if (panel != PanelState.None) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
                    .clickable { panel = PanelState.None }
            )
        }

        AnimatedVisibility(
            visible = panel == PanelState.Top,
            enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(150)),
            exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(150))
        ) {
            WhatsNextPanel(onDismiss = { panel = PanelState.None })
        }

        AnimatedVisibility(
            visible = panel == PanelState.Bottom,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(150)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(150))
        ) {
            BottomPanel(onDismiss = { panel = PanelState.None })
        }
    }
}

@Composable
private fun ClassCard(info: ClassInfo, index: Int, daySchedule: List<ClassInfo>) {
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
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = gradientColors,
                            start = Offset.Zero,
                            end = Offset(cardW, cardH)
                        )
                    )
            )
            Raindrops(cardWidth = cardWidth, cardHeight = cardHeight)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(info.title, color = Color.White, fontSize = MaterialTheme.typography.headlineSmall.fontSize, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                Text(info.time, color = Color.White)
                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = gradientColors,
                            start = Offset.Zero,
                            end = Offset(cardW, cardH)
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text("Today's Schedule", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                daySchedule.forEach {
                    Text("${it.time} • ${it.title}", color = Color.White, fontSize = MaterialTheme.typography.bodyMedium.fontSize)
                }
            }
        }
    }
}

@Composable
private fun SummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F4FF)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun NumberRow(count: Int, activeIndex: Int, onTap: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(count) { idx ->
            val selected = idx == activeIndex
            Text(
                text = "${idx + 1}",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onTap(idx) },
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun WhatsNextPanel(onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "What’s Next",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            val meal = remember { mutableStateOf("Breakfast") }
            val range = remember { mutableStateOf("08:00 – 09:00") }
            val items = remember { mutableStateOf(listOf("Pancakes", "Juice")) }
            var countdown by remember { mutableStateOf("00:00:00") }

            LaunchedEffect(Unit) {
                while (true) {
                    val now = System.currentTimeMillis()
                    val target = now + 3600000 // +1h
                    val diff = target - now
                    val h = diff / 3600000
                    val m = diff % 3600000 / 60000
                    val s = diff % 60000 / 1000
                    countdown = String.format("%02d:%02d:%02d", h, m, s)
                    kotlinx.coroutines.delay(1000)
                }
            }

            Text("${meal.value} · ${range.value}")
            Text("Starts in $countdown")
            Text(items.value.joinToString(), modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) { Text("Close") }
        }
    }
}

@Composable
private fun BottomPanel(onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shadowElevation = 8.dp,
        color = Color(0xFFF0F2F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Reminders / To‑Do",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            val reminders = listOf(
                "Finish ECE1001 Lab report",
                "Review MAT1002 notes",
                "PHY1003 problem set",
                "CHE1004 lab prep",
                "CSE1005 assignment"
            )
            reminders.forEach {
                Text("• $it", modifier = Modifier.padding(vertical = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Utilities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            val utilities = listOf(
                "Calculator",
                "Attendance Tracker",
                "Grade Predictor",
                "Campus Map",
                "Help Desk"
            )
            utilities.forEach {
                Text(it, modifier = Modifier.padding(vertical = 2.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) { Text("Close") }
        }
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
