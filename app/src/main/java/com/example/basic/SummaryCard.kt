package com.example.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        WeatherCard()
        Spacer(Modifier.height(16.dp))
        TimetableSection()
        Spacer(Modifier.height(16.dp))
        MenuSection()
        Spacer(Modifier.height(16.dp))
        TasksSection()
        Spacer(Modifier.height(16.dp))
        InsightsSection()
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun WeatherCard() {
    Card(
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFFB366), Color(0xFFA060E0))
                    )
                )
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(LocalContext.current)
                            .data("https://placekitten.com/128/128")
                            .crossfade(true)
                            .size(coil.size.Size.ORIGINAL)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        placeholder = painterResource(android.R.drawable.ic_menu_report_image),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Cloudy",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "25°",
                        color = Color.White,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Feels like 27°",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.offset(x = (-10).dp, y = (-10).dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherInfo("60%", "Humidity")
                WeatherInfo("10 km/h", "Wind")
                WeatherInfo("28°", "Max")
            }
        }
    }
}

@Composable
private fun WeatherInfo(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(label, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
private fun RowScope.InfoBox(value: String, label: String) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(100.dp)
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TimetableSection() {
    SectionHeader("Today's Timetable")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        InfoBox(value = "6", label = "classes today")
        InfoBox(value = "2", label = "labs")
        InfoBox(value = "1", label = "project")
    }
}

@Composable
private fun MenuSection() {
    SectionHeader("Today's Menu")
    val meals = listOf(
        "Breakfast" to "Pancakes & Juice",
        "Lunch" to "Chicken Salad",
        "Snacks" to "Fruit & Yogurt",
        "Dinner" to "Salmon & Veggies"
    )
    Column {
        meals.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                rowItems.forEach { (label, menu) ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Fastfood, contentDescription = null, tint = Color(0xFFFF6A00))
                            Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                            Text(menu, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TasksSection() {
    SectionHeader("Tasks & Reminders")
    val tasks = listOf(
        "Finish report" to "Complete Q3 analysis",
        "Grocery shopping" to "Buy veggies and milk",
        "Call plumber" to "Fix kitchen sink leak"
    )
    tasks.forEach { (title, details) ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(details, fontSize = 12.sp, color = Color(0xFF666666))
            }
        }
    }
}

@Composable
private fun InsightsSection() {
    SectionHeader("Daily Insights")
    val insights = listOf(
        "Water Intake" to "2 / 3 L",
        "Steps" to "7.5k",
        "Free Time" to "1.5h",
        "Tasks Completed" to "4 / 6"
    )
    Column {
        insights.forEach { (title, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(value)
            }
        }
    }
}
