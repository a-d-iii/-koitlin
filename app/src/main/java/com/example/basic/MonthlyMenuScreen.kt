package com.example.basic

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

private data class DayMeals(val date: String, val meals: List<Meal>)

@Composable
fun MonthlyMenuScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val menu = remember { loadMenu(context) }
    var likes by remember { mutableStateOf(setOf<String>()) }
    val weeks = remember(menu) { toWeeks(menu) }
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Monthly Menu") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F2))
        ) {
            weeks.forEachIndexed { index, week ->
                item {
                    WeekHeader(
                        title = "Week ${index + 1}",
                        color = weekColors[index % weekColors.size]
                    )
                }
                itemsIndexed(week) { i, day ->
                    DayBlock(
                        day = day,
                        isPast = day.date < today,
                        isFirst = i == 0,
                        isLast = i == week.lastIndex,
                        dayColor = dayColors[index % dayColors.size],
                        liked = likes,
                        toggleLike = { key ->
                            likes = if (likes.contains(key)) likes - key else likes + key
                        }
                    )
                }
            }
        }
    }
}

private fun loadMenu(context: Context): Map<String, List<Meal>> {
    val text = context.assets.open("monthly-menu-may-2025.json").bufferedReader().use { it.readText() }
    val obj = JSONObject(text)
    val map = mutableMapOf<String, List<Meal>>()
    val dates = obj.keys().asSequence().toList().sorted()
    for (d in dates) {
        val arr = obj.getJSONArray(d)
        val meals = mutableListOf<Meal>()
        for (i in 0 until arr.length()) {
            val m = arr.getJSONObject(i)
            val itemsArr = m.getJSONArray("items")
            val items = List(itemsArr.length()) { itemsArr.getString(it) }
            meals.add(
                Meal(
                    name = m.getString("name"),
                    startHour = m.getInt("startHour"),
                    startMinute = m.getInt("startMinute"),
                    endHour = m.getInt("endHour"),
                    endMinute = m.getInt("endMinute"),
                    items = items
                )
            )
        }
        map[d] = meals
    }
    return map
}

private fun toWeeks(menu: Map<String, List<Meal>>): List<List<DayMeals>> {
    val dates = menu.keys.sorted()
    val list = dates.map { DayMeals(it, menu[it]!!) }
    return list.chunked(7)
}

private val weekColors = listOf(
    Color(0xFFF0E4D7),
    Color(0xFFE7F0D7),
    Color(0xFFD7E8F0),
    Color(0xFFF0D7E8)
)

private val dayColors = listOf(
    Color(0xFFE5D7CB),
    Color(0xFFDCE5CB),
    Color(0xFFCBDCE5),
    Color(0xFFE5CBDC)
)

@Composable
private fun WeekHeader(title: String, color: Color) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .background(color, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DayBlock(
    day: DayMeals,
    isPast: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    dayColor: Color,
    liked: Set<String>,
    toggleLike: (String) -> Unit
) {
    val shape = RoundedCornerShape(
        topStart = if (isFirst) 12.dp else 0.dp,
        topEnd = if (isFirst) 12.dp else 0.dp,
        bottomStart = if (isLast) 12.dp else 0.dp,
        bottomEnd = if (isLast) 12.dp else 0.dp
    )
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .background(dayColor, shape)
            .then(if (isPast) Modifier.alpha(0.5f) else Modifier)
            .padding(12.dp)
    ) {
        Surface(
            color = Color(0xFFC0C0C0),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Text(
                text = day.date,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                fontWeight = FontWeight.SemiBold
            )
        }
        day.meals.forEach { m ->
            val key = "${day.date}-${m.name}"
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(m.name, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = { toggleLike(key) }, modifier = Modifier.size(16.dp)) {
                        Icon(
                            imageVector = if (liked.contains(key)) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (liked.contains(key)) Color.Red else Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(m.items.joinToString(", "), color = Color(0xFF555555))
            }
        }
    }
}
