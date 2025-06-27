package com.example.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.rememberCoroutineScope
import com.example.basic.PlannerRepository
import com.example.basic.DaySchedule
import com.example.basic.ClassEntry
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale




@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AttendanceDetailsScreen(onBack: () -> Unit) {
    val days = remember { PlannerRepository.weekSchedule() }
    var selected by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var dragAmount by remember { mutableStateOf(0f) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Schedule", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(selected) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, delta ->
                            dragAmount += delta
                        },
                        onDragEnd = {
                            if (dragAmount < -100 && selected < days.lastIndex) {
                                selected++
                                coroutineScope.launch { listState.animateScrollToItem(selected) }
                            } else if (dragAmount > 100 && selected > 0) {
                                selected--
                                coroutineScope.launch { listState.animateScrollToItem(selected) }
                            }
                            dragAmount = 0f
                        },
                        onDragCancel = { dragAmount = 0f }
                    )
                }
        ) {
            DaySelector(
                days = days,
                selected = selected,
                onSelect = {
                    selected = it
                    coroutineScope.launch { listState.animateScrollToItem(it) }
                },
                listState = listState
            )
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) +
                                fadeIn(animationSpec = tween(300))) with
                                (slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(300)) +
                                fadeOut(animationSpec = tween(300)))
                    } else {
                        (slideInVertically(initialOffsetY = { -it }, animationSpec = tween(300)) +
                                fadeIn(animationSpec = tween(300))) with
                                (slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)) +
                                fadeOut(animationSpec = tween(300)))
                    }
                },
                label = "schedule"
            ) { index ->
                val selectedDate = days[index].date
                val classes = PlannerRepository.getClasses(selectedDate)
                Column {
                    CurrentDayHeader(selectedDate)
                    ScheduleList(date = selectedDate, events = classes)
                }
            }

        }
    }
}

@Composable
private fun DaySelector(
    days: List<DaySchedule>,
    selected: Int,
    onSelect: (Int) -> Unit,
    listState: LazyListState
) {
    val month = days[selected].date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA))
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = month,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF424242),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 8.dp)
        )
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            itemsIndexed(days) { index, day ->
                val isSelected = index == selected
                val bgColor = if (isSelected) Color(0xFF2979FF) else Color(0xFFF5F5F5)
                val textColor = if (isSelected) Color.White else Color(0xFF424242)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelect(index) }
                        .background(bgColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${day.date.dayOfMonth}",
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentDayHeader(date: LocalDate) {
    Text(
        text = date.format(java.time.format.DateTimeFormatter.ofPattern("EEEE d")),
        color = Color(0xFF757575),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp)
    )
}

@Composable
private fun ScheduleList(date: LocalDate, events: List<ClassEntry>) {
    val now = LocalDateTime.now()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        itemsIndexed(events) { index, event ->
            val start = LocalTime.parse(event.start)
            val end = LocalTime.parse(event.end)
            val startDateTime = date.atTime(start)
            val endDateTime = date.atTime(end)
            val isCurrent = now.isAfter(startDateTime) && now.isBefore(endDateTime)

            val infinite = rememberInfiniteTransition()
            val pulse by infinite.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(64.dp)
                ) {
                    Text(event.start, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    Canvas(modifier = Modifier.height(32.dp).width(16.dp)) {
                        val centerX = size.width / 2
                        val radius = 8.dp.toPx()
                        val lineGap = 1.dp.toPx()
                        if (index > 0) {
                            drawLine(
                                color = Color(0xFF2979FF),
                                start = Offset(centerX, 0f),
                                end = Offset(centerX, size.height / 2 - radius - lineGap),
                                strokeWidth = 4f
                            )
                        }
                        if (index < events.lastIndex) {
                            drawLine(
                                color = Color(0xFF2979FF),
                                start = Offset(centerX, size.height / 2 + radius + lineGap),
                                end = Offset(centerX, size.height),
                                strokeWidth = 4f
                            )
                        }
                        drawCircle(
                            color = Color(0xFF2979FF),
                            radius = radius,
                            center = Offset(centerX, size.height / 2)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                EventCard(event)
            }
        }
    }
}

@Composable
private fun EventCard(event: ClassEntry) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4CAF50))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Column {
                    Text(
                        text = "${event.start} – ${event.end}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = event.course,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.faculty,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = event.room,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
