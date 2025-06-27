package com.example.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basic.ui.theme.accentBlue
import com.example.basic.ui.theme.accentOrange
import com.example.basic.ui.theme.homeSectionBg

@Composable
fun HomeScreen() {
    val timetable = remember {
        listOf(
            TimetableEntry("DSA", "Dr. Smith", "301", "1.0 h", "08:00 – 09:00"),
            TimetableEntry("Algorithms", "Prof. Rao", "204", "1.0 h", "09:15 – 10:15"),
            TimetableEntry("Networks", "Ms. Clark", "Lab 2", "1.0 h", "10:30 – 11:30")
        )
    }
    val meals = remember {
        listOf(
            "Breakfast" to "Pancakes",
            "Lunch" to "Chicken Salad",
            "Snacks" to "Fruit & Yogurt",
            "Dinner" to "Salmon"
        )
    }
    val tasks = remember {
        listOf(
            "Finish report" to "Complete Q3 analysis",
            "Grocery shopping" to "Buy veggies and milk",
            "Call plumber" to "Fix kitchen sink leak"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        GreetingBar(firstName = "Alex")
        Spacer(Modifier.height(8.dp))
        WeatherCard(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(16.dp))

        SectionBox(icon = "\uD83D\uDD52", title = "Today's Timetable") {
            timetable.forEachIndexed { i, item ->
                TimetableItem(item)
                if (i != timetable.lastIndex) Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        SectionBox(icon = "\uD83C\uDF74", title = "Today's Menu") {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(meals) { (label, menu) ->
                    MenuTile(label, menu)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        SectionBox(icon = "\uD83D\uDCDD", title = "Tasks & Reminders") {
            tasks.forEach { (title, detail) ->
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF212121))
                Text(detail, fontSize = 12.sp, color = Color(0xFF757575), modifier = Modifier.padding(bottom = 8.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun GreetingBar(firstName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hello, $firstName",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121)
            )
            Text(
                text = "Saturday, June 7, 2025",
                fontSize = 14.sp,
                color = Color(0xFF757575),
                modifier = Modifier.padding(top = 2.dp)
            )
            Row(modifier = Modifier.padding(top = 4.dp)) {
                listOf("\uD83C\uDFA8", "\u2600\uFE0F", "\uD83C\uDF1C", "\uD83C\uDFA8", "\u2600\uFE0F").forEach { e ->
                    Text(text = e, fontSize = 20.sp, modifier = Modifier.padding(end = 4.dp))
                }
            }
        }
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = Color(0xFF757575),
            modifier = Modifier
                .size(56.dp)
                .clip(shape = RoundedCornerShape(28.dp))
                .clickable { }
                .padding(4.dp)
        )
    }
}

@Composable
fun SectionBox(
    icon: String,
    title: String,
    onViewAll: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(homeSectionBg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(icon, fontSize = 20.sp)
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            Spacer(Modifier.weight(1f))
            if (onViewAll != null) {
                Text(
                    text = "View All",
                    fontSize = 14.sp,
                    color = accentBlue,
                    modifier = Modifier.clickable { onViewAll() }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
fun MenuTile(title: String, subtitle: String) {
    Card(
        modifier = Modifier.size(width = 150.dp, height = 100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Fastfood,
                contentDescription = null,
                tint = accentOrange,
                modifier = Modifier.size(32.dp)
            )
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF757575), modifier = Modifier.padding(top = 2.dp))
        }
    }
}
