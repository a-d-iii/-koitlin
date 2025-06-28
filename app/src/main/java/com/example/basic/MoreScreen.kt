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
import androidx.compose.ui.layout.offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

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
    // Use a single shade for the top and bottom dividers
    val dividerColor = Color.DarkGray

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
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

        // Calendar grid showing hours of the day
        val lineColor = Color(0xFFE0E0E0)
        val hours = (8..23).map { hour ->
            val h = if (hour > 12) hour - 12 else hour
            val ampm = if (hour < 12) "am" else "pm"
            "%02d:00 %s".format(h, ampm)
        }
        Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            hours.forEach { label ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(0.15f)
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 4.dp)
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
                                .weight(0.85f)
                                .fillMaxHeight()
                        ) {
                            Divider(
                                color = lineColor,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(start = 2.dp)
                                    .fillMaxWidth(),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    }
}
