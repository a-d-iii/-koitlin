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
import androidx.compose.material.icons.filled.Check
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
        val events = entry.value.mapIndexed { i, it ->
            ClassEvent(
                start = it.start,
                end = it.end,
                course = it.course,
                code = it.course,
                room = it.room,
                category = EventCategory.values()[i % EventCategory.values().size]
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

    val extraSpace = 16.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
 
            .height(52.dp + extraSpace)
 
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Color(0xFFF5F5F5))
                .drawBehind {
                    val stroke = 1.dp.toPx()
                    drawLine(Color(0xFFBDBDBD), Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = stroke)
                    drawLine(Color(0xFFBDBDBD), Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = stroke)
                }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(start = 16.dp, end = 16.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
        Text(
            month,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
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
                val textColor = if (isSelected) Color.White else Color.Black
                val bgColor = if (isSelected) Color(0xFFB388FF) else Color.Transparent
                val shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
 
                val highlightOverlap = if (isSelected) 16.dp else 0.dp
                val itemHeight = 52.dp + highlightOverlap
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .height(itemHeight)
                        .shadow(if (isSelected) 12.dp else 0.dp, shape, clip = false)
                        .clip(shape)
                        .background(bgColor, shape)

                        .clickable { onSelect(index) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .align(Alignment.CenterVertically)
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
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
}
}
@Composable
private fun CurrentDayHeader(date: LocalDate) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp)
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF757575)
        )
        Text(
            text = date.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")),
            fontSize = 14.sp,
            color = Color(0xFF757575)
        )
    }
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
                    // Display only the start time on the timeline in 12 hour format
                    val displayTime = LocalTime.parse(event.start).format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
                    Text(displayTime, color = Color.Gray, fontSize = 12.sp)
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
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer {
                                if (isCurrent) {
                                    scaleX = pulse
                                    scaleY = pulse
                                }
                            }
                            .shadow(if (isPast) 4.dp else 0.dp, CircleShape, clip = false)
                            .clip(CircleShape)
                            .background(if (fillColor == Color.Transparent) MaterialTheme.colorScheme.background else fillColor)
                            .border(2.dp, borderColor, CircleShape)
                    ) {
                        if (isPast) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Done",
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                val widthFraction = if (index % 2 == 0) 0.9f else 1f
                EventCard(event, Modifier.fillMaxWidth(widthFraction))
            }
        }
    }
}

@Composable
private fun EventCard(event: ClassEvent, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = event.category.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
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
