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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

private enum class EventCategory(val label: String, val color: Color) {
    Personal("Personal", Color(0xFF4CAF50)),
    Shopping("Shopping", Color(0xFFFF9800)),
    Todo("To do", Color(0xFF9C27B0)),
    Event("Event", Color(0xFFE91E63)),
    Birthday("Birthday", Color(0xFFD32F2F))
}

private data class ClassEvent(
    val start: String,
    val end: String,
    val course: String,
    val code: String,
    val room: String,
    val category: EventCategory
)

private data class DaySchedule(val date: LocalDate, val events: List<ClassEvent>)

private fun sampleSchedules(): List<DaySchedule> {
    val today = LocalDate.now()
    val start = today.minusDays(today.dayOfWeek.value.toLong() - 1) // start of week (Monday)
    val events = listOf(
        ClassEvent("08:00", "08:50", "Mathematics", "MATH101", "201", EventCategory.Personal),
        ClassEvent("09:00", "09:50", "Physics", "PHY102", "202", EventCategory.Shopping),
        ClassEvent("11:00", "12:20", "Literature", "LIT201", "303", EventCategory.Todo),
        ClassEvent("14:00", "15:00", "Computer Science", "CSE301", "Lab", EventCategory.Event),
        ClassEvent("15:10", "16:00", "Chemistry", "CHE202", "204", EventCategory.Birthday)
    )
    return (0..6).map { DaySchedule(start.plusDays(it.toLong()), events) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDetailsScreen(onBack: () -> Unit) {
    val days = remember { sampleSchedules() }
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
                                coroutineScope.launch { listState.animateScrollToItem(selected + 1) }
                            } else if (dragAmount > 100 && selected > 0) {
                                selected--
                                coroutineScope.launch { listState.animateScrollToItem(selected + 1) }
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
                    coroutineScope.launch { listState.animateScrollToItem(it + 1) }
                },
                listState = listState
            )
            CurrentDayHeader(days[selected].date)
            ScheduleList(schedule = days[selected])
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

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(month, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(end = 8.dp))
        }
        itemsIndexed(days) { index, day ->
            val isSelected = index == selected
            val textColor = if (isSelected) Color.White else Color.LightGray
            val bgColor = if (isSelected) Color(0xFF1E88E5) else Color.Transparent
            val shape = RoundedCornerShape(12.dp)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(shape)
                    .clickable { onSelect(index) }
                    .background(bgColor)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .shadow(if (isSelected) 4.dp else 0.dp, shape)
            ) {
                Text(
                    day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    day.date.dayOfMonth.toString(),
                    color = textColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun CurrentDayHeader(date: LocalDate) {
    Text(
        text = date.format(java.time.format.DateTimeFormatter.ofPattern("EEEE d MMMM yyyy")),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp)
    )
}

@Composable
private fun ScheduleList(schedule: DaySchedule) {
    val density = LocalDensity.current
    val lineX = with(density) { 32.dp.toPx() }
    val now = LocalDateTime.now()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .drawBehind {
                drawLine(
                    color = Color.LightGray,
                    start = Offset(lineX, 0f),
                    end = Offset(lineX, size.height),
                    strokeWidth = with(density) { 1.dp.toPx() }
                )
            },
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        itemsIndexed(schedule.events) { index, event ->
            val start = LocalTime.parse(event.start)
            val end = LocalTime.parse(event.end)
            val startDateTime = schedule.date.atTime(start)
            val endDateTime = schedule.date.atTime(end)
            val isPast = endDateTime.isBefore(now)
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
                    Text("${event.start} – ${event.end}", color = Color.Gray, fontSize = 12.sp)
                    val fillColor = when {
                        isCurrent -> Color(0xFF1E88E5)
                        isPast -> Color(0xFFD32F2F)
                        else -> Color.Transparent
                    }
                    val borderColor = when {
                        isCurrent -> Color(0xFF1E88E5)
                        isPast -> Color(0xFFD32F2F)
                        else -> Color.LightGray
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .graphicsLayer {
                                if (isCurrent) {
                                    scaleX = pulse
                                    scaleY = pulse
                                }
                            }
                            .clip(CircleShape)
                            .background(fillColor)
                            .border(2.dp, borderColor, CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                EventCard(event)
            }
        }
    }
}

@Composable
private fun EventCard(event: ClassEvent) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = event.category.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(event.course, color = Color.White, fontWeight = FontWeight.Bold)
                Text(event.code, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            }
            Text(event.room, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}
