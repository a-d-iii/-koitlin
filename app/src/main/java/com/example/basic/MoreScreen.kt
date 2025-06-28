package com.example.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            weekDates.forEach { date ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val day = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
