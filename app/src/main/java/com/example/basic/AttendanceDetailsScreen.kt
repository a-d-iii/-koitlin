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
import com.example.basic.WEEKLY_SCHEDULE
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

private fun plannerSchedules(): List<DaySchedule> {
    val start = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
    return WEEKLY_SCHEDULE.entries.mapIndexed { index, entry ->
        val events = entry.value.map {
            ClassEvent(
                start = it.start,
                end = it.end,
                course = it.course,
                code = it.course,
                room = it.room,
                category = EventCategory.Personal
            )
        }
        DaySchedule(start.plusDays(index.toLong()), events)
    }
}

private object PlannerRepository {
    private val schedules = plannerSchedules()
    fun getTodayClasses(date: LocalDate): List<ClassEvent> {
        return schedules.firstOrNull { it.date.dayOfWeek == date.dayOfWeek }?.events ?: emptyList()

    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AttendanceDetailsScreen(onBack: () -> Unit) {
    val days = remember { plannerSchedules() }
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
                val classes = PlannerRepository.getTodayClasses(selectedDate)
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
            .background(Color(0xFFF7F7F7))
            .height(52.dp)
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            month,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
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
                val textColor = if (isSelected) Color.White else Color.LightGray
                val bgColor = if (isSelected) Color(0xFF1E88E5) else Color.Transparent
                val shape = RoundedCornerShape(12.dp)
                val highlightOffset = if (isSelected) 6.dp else 0.dp
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .offset(y = highlightOffset)
                        .clip(shape)
                        .clickable { onSelect(index) }
                        .background(bgColor)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .padding(bottom = highlightOffset)
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
}

@Composable
private fun CurrentDayHeader(date: LocalDate) {
    Text(
        text = date.format(java.time.format.DateTimeFormatter.ofPattern("EEEE d")),
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF757575),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp)
    )
}

@Composable
private fun ScheduleList(date: LocalDate, events: List<ClassEvent>) {
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
        itemsIndexed(events) { index, event ->
            val start = LocalTime.parse(event.start)
            val end = LocalTime.parse(event.end)
            val startDateTime = date.atTime(start)
            val endDateTime = date.atTime(end)
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
                    // Display only the start time on the timeline
                    Text(event.start, color = Color.Gray, fontSize = 12.sp)
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
            .padding(vertical = 8.dp)
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(event.course, color = Color.White, fontWeight = FontWeight.Bold)
                Text(event.category.label, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            }
        }
    }
}
