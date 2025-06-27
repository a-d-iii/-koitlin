package com.example.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.LocalDensity
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

private data class PlannerEvent(val time: String, val title: String, val category: EventCategory)
private data class DaySchedule(val date: LocalDate, val events: List<PlannerEvent>)

private fun sampleSchedules(): List<DaySchedule> {
    val today = LocalDate.now()
    val start = today.minusDays(today.dayOfWeek.value.toLong() - 1) // start of week (Monday)
    val events = listOf(
        PlannerEvent("08:00", "Breakfast with my family", EventCategory.Personal),
        PlannerEvent("09:00", "Buy groceries for lunch", EventCategory.Shopping),
        PlannerEvent("17:00", "Daily workout routine", EventCategory.Todo),
        PlannerEvent("20:00", "Watch a movie", EventCategory.Event),
        PlannerEvent("23:00", "Sister's birthday party", EventCategory.Birthday)
    )
    return (0..6).map { DaySchedule(start.plusDays(it.toLong()), events) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDetailsScreen(onBack: () -> Unit) {
    val days = remember { sampleSchedules() }
    var selected by remember { mutableStateOf(0) }

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
        ) {
            DaySelector(days = days, selected = selected, onSelect = { selected = it })
            CurrentDayHeader(days[selected].date)
            ScheduleList(schedule = days[selected])
        }
    }
}

@Composable
private fun DaySelector(days: List<DaySchedule>, selected: Int, onSelect: (Int) -> Unit) {
    val month = days[selected].date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())

    LazyRow(
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
            val bgColor = if (isSelected) Color(0xFF3B72FF) else Color.Transparent
            val shape = RoundedCornerShape(12.dp)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(shape)
                    .clickable { onSelect(index) }
                    .background(bgColor)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .shadow(if (isSelected) 2.dp else 0.dp, shape)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = date.format(java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy")),
            color = Color.Gray
        )
    }
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
            val eventTime = LocalTime.parse(event.time)
            val eventDateTime = schedule.date.atTime(eventTime)
            val isPast = eventDateTime.isBefore(now)
            val isCurrent = schedule.date == now.toLocalDate() && eventTime.hour == now.hour

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
                    Text(event.time, color = Color.Gray, fontSize = 12.sp)
                    val fillColor = when {
                        isCurrent -> Color(0xFF3B72FF)
                        isPast -> Color(0xFFD32F2F)
                        else -> Color.Transparent
                    }
                    val borderColor = when {
                        isCurrent -> Color(0xFF3B72FF)
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
private fun EventCard(event: PlannerEvent) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = event.category.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(event.title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(event.category.label, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
        }
    }
}
