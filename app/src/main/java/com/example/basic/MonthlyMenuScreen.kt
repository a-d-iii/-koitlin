package com.example.basic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle


typealias MonthlyMenu = Map<String, List<Meal>>

data class DayData(val date: String, val meals: List<Meal>)

data class WeekSection(val title: String, val color: Color, val dayColor: Color, val days: List<DayData>)

private val WEEK_COLORS = listOf(
    Color(0xFFF0E4D7),
    Color(0xFFE7F0D7),
    Color(0xFFD7E8F0),
    Color(0xFFF0D7E8)
)

private val DAY_COLORS = listOf(
    Color(0xFFE5D7CB),
    Color(0xFFDCE5CB),
    Color(0xFFCBDCE5),
    Color(0xFFE5CBDC)
)

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

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

private fun dayLabel(date: String): String {
    val ld = LocalDate.parse(date, dateFormatter)
    val day = ld.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    return "$day, $date"
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MonthlyMenuScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var menu by remember { mutableStateOf<MonthlyMenu?>(null) }
    // Store wishlist entries by unique meal key "date-name"
    var wishlist by remember { mutableStateOf(setOf<String>()) }
    var showWishlist by remember { mutableStateOf(false) }

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

    LaunchedEffect(weeks) {
        val today = LocalDate.now().format(dateFormatter)
        var index = 0
        for (week in weeks) {
            index += 1 // account for header
            val dayIdx = week.days.indexOfFirst { it.date == today }
            if (dayIdx >= 0) {
                index += dayIdx
                listState.scrollToItem(index)
                break
            }
            index += week.days.size
        }
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Monthly Menu",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { showWishlist = true }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Wishlist",
                            tint = Color.Red
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            weeks.forEach { week ->
                stickyHeader {
                    WeekHeader(
                        title = week.title,
                        color = week.dayColor
                    )
                }
                items(week.days) { day ->
                    DayCard(
                        day = day,
                        wishlist = wishlist,
                        background = week.dayColor,
                        onToggleLike = { key ->
                            wishlist = if (wishlist.contains(key)) wishlist - key else wishlist + key
                        },
                        onAdd = {}
                    )
                }
            }
        }
    }

    if (showWishlist) {
        AlertDialog(
            onDismissRequest = { showWishlist = false },
            confirmButton = {
                TextButton(onClick = { showWishlist = false }) { Text("Close") }
            },
            title = { Text("Wishlist") },
            text = {
                if (wishlist.isEmpty()) {
                    Text("No items yet")
                } else {
                    Column { wishlist.forEach { Text(it) } }
                }
            }
        )
    }
}

@Composable
private fun WeekHeader(
    title: String,
    color: Color
) {
    val shape = RoundedCornerShape(50.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .zIndex(1f)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(shape)
                .border(2.dp, color, shape)
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DayCard(
    day: DayData,
    wishlist: Set<String>,
    background: Color,
    onToggleLike: (String) -> Unit,
    onAdd: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    dayLabel(day.date),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(0xFF333333), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            day.meals.forEachIndexed { index, meal ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(meal.name, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    val key = "${day.date}-${meal.name}"
                    val liked = wishlist.contains(key)
                    Icon(
                        imageVector = if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (liked) Color.Red else Color.Black,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onToggleLike(key) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onAdd(key) }
                    )
                }
                Text(meal.items.joinToString(", "), style = MaterialTheme.typography.bodySmall)
                if (index != day.meals.lastIndex) Spacer(Modifier.height(8.dp))
            }
        }
    }
}

