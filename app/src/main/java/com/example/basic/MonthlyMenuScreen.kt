package com.example.basic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.json.JSONObject
import java.util.*


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
        weeks.add(
            WeekSection(
                title = "Week ${weeks.size + 1}",
                color = Color.White,
                dayColor = Color.White,
                days = slice.map { DayData(it, menu[it]!!) }
            )
        )
        i += 7
    }
    return weeks
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MonthlyMenuScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var menu by remember { mutableStateOf<MonthlyMenu?>(null) }

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
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Monthly Menu",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFEDEE))
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            weeks.forEach { week ->
                stickyHeader { WeekHeader(week.title) }
                items(week.days) { day ->
                    DayCard(day)
                }
            }
        }
    }
}

@Composable
private fun WeekHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFEDEE))
            .padding(vertical = 8.dp)
            .zIndex(1f)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DayCard(day: DayData) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(day.date, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            day.meals.forEachIndexed { index, meal ->
                Text(meal.name, fontWeight = FontWeight.SemiBold)
                Text(meal.items.joinToString(", "), style = MaterialTheme.typography.bodySmall)
                if (index != day.meals.lastIndex) Spacer(Modifier.height(8.dp))
            }
        }
    }
}

