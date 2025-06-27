package com.example.basic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basic.ui.theme.accentBlue

data class TimetableEntry(
    val course: String,
    val lecturer: String,
    val room: String,
    val duration: String,
    val time: String
)

@Composable
fun TimetableItem(entry: TimetableEntry, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(entry.course, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF212121))
                Text(entry.lecturer, fontSize = 14.sp, color = Color(0xFF757575), modifier = Modifier.padding(top = 2.dp))
                Text(entry.room, fontSize = 12.sp, color = Color(0xFF9E9E9E), modifier = Modifier.padding(top = 4.dp))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(entry.duration, fontSize = 14.sp, color = accentBlue)
                Text(entry.time, fontSize = 12.sp, color = Color(0xFF757575), modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}
