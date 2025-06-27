package com.example.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import java.time.LocalDate
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
    val start = LocalDate.of(2017, 8, 28)
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
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            DaySelector(days = days, selected = selected, onSelect = { selected = it })
            ScheduleList(schedule = days[selected])
        }
    }
}

@Composable
private fun DaySelector(days: List<DaySchedule>, selected: Int, onSelect: (Int) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(days) { index, day ->
            val isSelected = index == selected
            val bg = if (isSelected) Color(0xFF3B72FF) else Color.Transparent
            val textColor = if (isSelected) Color.White else Color.Gray
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelect(index) }
                    .background(bg)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    color = textColor,
                    fontSize = 12.sp
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

@Composable
private fun ScheduleList(schedule: DaySchedule) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        itemsIndexed(schedule.events) { index, event ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(64.dp)
                ) {
                    Text(event.time, color = Color.Gray, fontSize = 12.sp)
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (index == 0) Color(0xFF3B72FF) else Color.Transparent)
                            .border(2.dp, if (index == 0) Color(0xFF3B72FF) else Color.LightGray, CircleShape)
                    )
                }
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(event.title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(event.category.label, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
        }
    }
}
