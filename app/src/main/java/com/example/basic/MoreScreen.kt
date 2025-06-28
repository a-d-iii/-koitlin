package com.example.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

// Simple schedule model for the calendar
private enum class ClassType { THEORY, LAB, BREAK, LUNCH }

private data class DayClass(
    val title: String,
    val start: LocalTime,
    val end: LocalTime,
    val type: ClassType
)

private val SAMPLE_DAY = listOf(
    DayClass("Math", LocalTime.of(9, 0), LocalTime.of(9, 50), ClassType.THEORY),
    DayClass("Physics Lab", LocalTime.of(10, 0), LocalTime.of(10, 50), ClassType.LAB),
    DayClass("Break", LocalTime.of(11, 0), LocalTime.of(12, 0), ClassType.BREAK),
    DayClass("Lunch", LocalTime.of(12, 0), LocalTime.of(13, 30), ClassType.LUNCH),
    DayClass("Algorithms", LocalTime.of(13, 30), LocalTime.of(14, 20), ClassType.THEORY),
    DayClass("Electronics", LocalTime.of(14, 30), LocalTime.of(15, 10), ClassType.LAB),
    DayClass("Databases", LocalTime.of(15, 20), LocalTime.of(16, 0), ClassType.THEORY)
)

private val WEEK_CLASSES: Map<DayOfWeek, List<DayClass>> =
    DayOfWeek.values().associateWith { SAMPLE_DAY }

@Composable
fun MoreScreen() {
    val today = LocalDate.now()
    val monthLabel = remember {
        today.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }
    val startOfWeek = remember {
        today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    }
    val weekDates = remember {
        (0..6).map { startOfWeek.plusDays(it.toLong()) }
    }
    var selectedDay by remember { mutableStateOf(today.dayOfWeek) }
    // Use a single shade for the top and bottom dividers
    val dividerColor = Color.DarkGray

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = monthLabel,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Divider(
            color = dividerColor,
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDates.forEach { date ->
                val selected = date.dayOfWeek == selectedDay
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (selected) Color(0xFFBBDEFB) else Color.Transparent)
                        .clickable { selectedDay = date.dayOfWeek }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    val day = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Divider(
            color = dividerColor,
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        // Compact information bar with a toggle icon on the right
        var expanded by remember { mutableStateOf(false) }
        val collapsedHeight = 32.dp
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(collapsedHeight)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE7E7E7))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "5 classes left \u00B7 4 hr 55 min free",
                    modifier = Modifier.weight(1f),
                    color = Color.Gray
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
            if (expanded) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(y = collapsedHeight)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                        .background(Color(0xFFE7E7E7))
                ) {
                    Text(
                        text = "More details about classes...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                    )
                }
            }
        }

}
        // Calendar grid showing hours of the day with class blocks
        val lineColor = Color(0xFFE0E0E0)
        val hourHeight = 96.dp
        val hours = (0..23).map { hour ->
            val displayHour = if (hour % 12 == 0) 12 else hour % 12
            val ampm = if (hour < 12) "am" else "pm"
            "%02d:00 %s".format(displayHour, ampm)
        }
        val calendarScroll = rememberScrollState()
        val dayClasses = WEEK_CLASSES[selectedDay] ?: emptyList()
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(hourHeight * hours.size)
                .verticalScroll(calendarScroll)
        ) {
            val labelWidth = maxWidth * 0.2f
            val contentWidth = maxWidth - labelWidth - 1.dp

            Box {
                Column {
                    hours.forEach { label ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(hourHeight)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(labelWidth)
                                    .fillMaxHeight()
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .offset(y = 8.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                                    .background(lineColor)
                            )
                            Box(
                                modifier = Modifier
                                    .width(contentWidth)
                                    .fillMaxHeight()
                            ) {
                                Divider(
                                    color = lineColor,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(start = 4.dp)
                                        .fillMaxWidth(),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.width(labelWidth))
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .background(lineColor)
                        )
                        Box(
                            modifier = Modifier
                                .width(contentWidth)
                        ) {
                            Divider(
                                color = lineColor,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .fillMaxWidth(),
                                thickness = 1.dp
                            )
                        }
                    }
                }

                dayClasses.forEach { cls ->
                    val startMinutes = cls.start.hour * 60 + cls.start.minute
                    val endMinutes = cls.end.hour * 60 + cls.end.minute
                    val top = hourHeight * (startMinutes / 60f)
                    val height = hourHeight * ((endMinutes - startMinutes) / 60f)
                    val color = when (cls.type) {
                        ClassType.THEORY -> Color(0xFFD7E8FF)
                        ClassType.LAB -> Color(0xFFFFF9C4)
                        ClassType.BREAK, ClassType.LUNCH -> Color(0xFFE0E0E0)
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = labelWidth + 1.dp + 4.dp, y = top)
                            .width(contentWidth - 8.dp)
                            .height(height - 8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color)
                    ) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            Text(cls.title, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${cls.start} – ${cls.end}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
