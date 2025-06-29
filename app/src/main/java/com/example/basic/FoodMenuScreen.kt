package com.example.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.basic.ui.theme.gradientBottom
import com.example.basic.ui.theme.gradientTop
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Simplified food menu screen inspired by the vit-student-app implementation.
 */

data class Meal(
    val name: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val items: List<String>
)

private val sampleMenu = listOf(
    Meal(
        "Breakfast",
        7,
        0,
        8,
        30,
        listOf("Aloo Paratha", "Bread", "Tea")
    ),
    Meal(
        "Lunch",
        12,
        0,
        14,
        0,
        listOf("Rice", "Paneer Curry", "Salad")
    ),
    Meal(
        "Snacks",
        16,
        0,
        17,
        0,
        listOf("Samosa", "Juice", "Biscuits")
    ),
    Meal(
        "Dinner",
        19,
        0,
        21,
        0,
        listOf("Chapathi", "Dal", "Curd")
    )
)

@Composable
fun FoodMenuScreen(onShowSummary: () -> Unit, onViewMonth: () -> Unit = {}) {
    val now by produceState(initialValue = Date()) {
        while (true) {
            value = Date()
            delay(1000)
        }
    }
    val dayLabel = remember { java.text.SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }
    var likes by remember { mutableStateOf(setOf<String>()) }

    val menu by produceState(initialValue = emptyList<Meal>()) {
        withContext(Dispatchers.IO) { value = sampleMenu }
    }

    val iconAnim = rememberInfiniteTransition()
    val iconScale by iconAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse)
    )
    val iconRotate by iconAnim.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(gradientTop, gradientBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 72.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Today's Menu",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.alignByBaseline()
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                            rotationZ = iconRotate
                        }
                        .alignByBaseline()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = dayLabel,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            menu.forEachIndexed { index, meal ->
                MealCard(
                    meal = meal,
                    now = now,
                    liked = likes.contains(meal.name),
                    onLike = {
                        likes = if (likes.contains(meal.name)) likes - meal.name else likes + meal.name
                    },
                    background = mealColors[index % mealColors.size]
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Button(
                onClick = onShowSummary,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Food Summary", color = MaterialTheme.colorScheme.onSecondary)
            }
        }

        MonthBar(onClick = onViewMonth, modifier = Modifier.align(Alignment.BottomCenter))

        // Removed rating dialog as the rate button is no longer shown
    }
}

private val mealColors = listOf(
    Color.White,
    Color.White,
    Color.White,
    Color.White
)

private fun mealIcon(name: String) = when (name.lowercase()) {
    "breakfast" -> Icons.Default.WbSunny
    "lunch" -> Icons.Default.LunchDining
    "snacks" -> Icons.Default.Icecream
    "dinner" -> Icons.Default.NightsStay
    else -> Icons.Default.Restaurant
}

@Composable
private fun MealCard(
    meal: Meal,
    now: Date,
    liked: Boolean,
    onLike: () -> Unit,
    background: Color
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val iconAnim = rememberInfiniteTransition()
    val mealIconRotation by iconAnim.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse)
    )
    val start = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, meal.startHour); set(Calendar.MINUTE, meal.startMinute) }
    val end = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, meal.endHour); set(Calendar.MINUTE, meal.endMinute) }
    val status = when {
        now.after(end.time) -> "Done"
        now.after(start.time) -> "Ongoing"
        else -> "Upcoming"
    }
    val ended = now.after(end.time)
    val containerColor = background
    var showRating by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf(0) }
    val rateScale = remember { Animatable(1f) }
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 110.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    mealIcon(meal.name),
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.graphicsLayer { rotationZ = mealIconRotation }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = meal.name,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format("%02d:%02d - %02d:%02d", meal.startHour, meal.startMinute, meal.endHour, meal.endMinute),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (ended) Icons.Default.CheckCircle else Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (ended) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(text = if (ended) "Done" else status, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground)
                    if (!ended) {
                        Spacer(Modifier.width(8.dp))
                        val target = if (status == "Upcoming") start.timeInMillis else end.timeInMillis
                        val diff = (target - now.time).coerceAtLeast(0)
                        val h = diff / 3600000
                        val m = (diff % 3600000) / 60000
                        val s = (diff % 60000) / 1000
                        val timer = if (h > 0) String.format("%02d:%02d:%02d", h, m, s) else String.format("%02d:%02d", m, s)
                        Text(timer, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (liked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                scope.launch {
                                    scale.snapTo(1.2f)
                                    onLike()
                                    scale.animateTo(1f, animationSpec = tween(300))
                                }
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = meal.items.joinToString(", "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (showRating) {
                    Row {
                        (1..5).forEach { i ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star $i",
                                tint = if (i <= rating) Color(0xFFFFD700) else Color.LightGray,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { rating = i }
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                rateScale.animateTo(1.2f, animationSpec = tween(200))
                                showRating = true
                            }
                        },
                        modifier = Modifier
                            .scale(rateScale.value)
                            .height(32.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBBDEFB))
                    ) {
                        Text("Rate", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }

        }
    }
}



@Composable
private fun MonthBar(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.primary, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "View Full Month",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

