package com.example.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.alpha
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONObject
import java.util.*

private val WEEK_COLORS = listOf(Color(0xFFF0E4D7), Color(0xFFE7F0D7), Color(0xFFD7E8F0), Color(0xFFF0D7E8))
private val DAY_COLORS = listOf(Color(0xFFE5D7CB), Color(0xFFDCE5CB), Color(0xFFCBDCE5), Color(0xFFE5CBDC))

typealias MonthlyMenu = Map<String, List<Meal>>

data class DayData(val date: String, val meals: List<Meal>)

data class WeekSection(val title: String, val color: Color, val dayColor: Color, val days: List<DayData>)

private fun parseMonthlyMenu(json: String): MonthlyMenu {
    val obj = JSONObject(json)
    val keys = obj.keys()
    val result = mutableMapOf<String, List<Meal>>()
    while (keys.hasNext()) {
        val date = keys.next()
        val arr = obj.getJSONArray(date)
        val meals = mutableListOf<Meal>()
        for (i in 0 until arr.length()) {
            val mObj = arr.getJSONObject(i)
            val itemsArr = mObj.getJSONArray("items")
            val items = mutableListOf<String>()
            for (j in 0 until itemsArr.length()) {
                items.add(itemsArr.getString(j))
            }
            meals.add(
                Meal(
                    name = mObj.getString("name"),
                    startHour = mObj.getInt("startHour"),
                    startMinute = mObj.getInt("startMinute"),
                    endHour = mObj.getInt("endHour"),
                    endMinute = mObj.getInt("endMinute"),
                    items = items
                )
            )
        }
        result[date] = meals
    }
    return result.toSortedMap()
}

private fun toWeeks(menu: MonthlyMenu): List<WeekSection> {
    val dates = menu.keys.sorted()
    val weeks = mutableListOf<WeekSection>()
    var i = 0
    while (i < dates.size) {
        val slice = dates.subList(i, kotlin.math.min(i + 7, dates.size))
        val index = weeks.size
        weeks.add(
            WeekSection(
                title = "Week ${index + 1}",
                color = WEEK_COLORS[index % WEEK_COLORS.size],
                dayColor = DAY_COLORS[index % DAY_COLORS.size],
                days = slice.map { DayData(it, menu[it]!!) }
            )
        )
        i += 7
    }
    return weeks
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyMenuScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var menu by remember { mutableStateOf<MonthlyMenu?>(null) }
    val likes = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(Unit) {
        val res = context.resources
        val input = res.openRawResource(R.raw.monthly_menu_may_2025)
        val text = input.bufferedReader().use { it.readText() }
        menu = parseMonthlyMenu(text)
    }

    if (menu == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val weeks = remember(menu) { toWeeks(menu!!) }
    val today = remember { java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Monthly Menu") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            weeks.forEach { week ->
                stickyHeader {
                    WeekHeader(
                        title = week.title,
                        color = week.color,
                        dayColor = week.dayColor,
                    )
                }
                items(week.days) { day ->
                    DayBlock(
                        day = day,
                        color = week.dayColor,
                        isPast = day.date < today,
                        first = day == week.days.first(),
                        last = day == week.days.last(),
                        likes = likes,
                        toggleLike = { key -> likes[key] = !(likes[key] ?: false) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekHeader(title: String, color: Color, dayColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(8.dp)
            .animateItemPlacement()
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black, shape = RoundedCornerShape(20.dp))
                .align(Alignment.Center)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DayBlock(
    day: DayData,
    color: Color,
    isPast: Boolean,
    first: Boolean,
    last: Boolean,
    likes: MutableMap<String, Boolean>,
    toggleLike: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .then(
                Modifier
                    .padding(horizontal = 4.dp)
                    .padding(top = if (first) 4.dp else 0.dp, bottom = if (last) 8.dp else 0.dp)
            )
            .alpha(if (isPast) 0.5f else 1f)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFC0C0C0), RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(day.date, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(6.dp))
        day.meals.forEach { meal ->
            val key = "${day.date}-${meal.name}"
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(meal.name, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { toggleLike(key) }) {
                        Icon(
                            imageVector = if (likes[key] == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = if (likes[key] == true) Color.Red else Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = { /* add action */ }) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    }
                }
                Text(meal.items.joinToString(", "), color = Color(0xFF555555))
            }
        }
    }
}

