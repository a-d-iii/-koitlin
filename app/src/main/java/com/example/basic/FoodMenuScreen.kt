package com.example.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        "Dinner",
        19,
        0,
        21,
        0,
        listOf("Chapathi", "Dal", "Curd")
    )
)

@Composable
fun FoodMenuScreen(onShowSummary: () -> Unit) {
    val now by produceState(initialValue = Date()) {
        while (true) {
            value = Date()
            delay(1000)
        }
    }
    var likes by remember { mutableStateOf(setOf<String>()) }
    var ratingMeal by remember { mutableStateOf<Meal?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Today's Menu",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        sampleMenu.forEach { meal ->
            MealCard(meal = meal, now = now, liked = likes.contains(meal.name), onLike = {
                likes = if (likes.contains(meal.name)) likes - meal.name else likes + meal.name
            }, onRate = { ratingMeal = meal })
            Spacer(modifier = Modifier.height(12.dp))
        }
        Button(
            onClick = onShowSummary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) { Text("Food Summary") }
    }
    ratingMeal?.let { meal ->
        RatingDialog(meal = meal, onDismiss = { ratingMeal = null }) { ratingMeal = null }
    }
}

@Composable
private fun MealCard(meal: Meal, now: Date, liked: Boolean, onLike: () -> Unit, onRate: () -> Unit) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val start = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, meal.startHour); set(Calendar.MINUTE, meal.startMinute) }
    val end = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, meal.endHour); set(Calendar.MINUTE, meal.endMinute) }
    val status = when {
        now.after(end.time) -> "Done"
        now.after(start.time) -> "Ongoing"
        else -> "Upcoming"
    }
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Restaurant, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = meal.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (liked) Color.Red else Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer(scaleX = scale.value, scaleY = scale.value)
                        .clickable {
                            scope.launch {
                                scale.snapTo(1.2f)
                                onLike()
                                scale.animateTo(1f, animationSpec = tween(300))
                            }
                        }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = meal.items.joinToString(", "), style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = status, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.weight(1f))
                OutlinedButton(onClick = onRate) { Text("Rate") }
            }
        }
    }
}

@Composable
fun RatingDialog(meal: Meal, onDismiss: () -> Unit, onSubmit: (Int) -> Unit) {
    var rating by remember { mutableStateOf(0) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Rate ${'$'}{meal.name}") },
        text = {
            Row {
                (1..5).forEach { i ->
                    Icon(
                        imageVector = if (i <= rating) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { rating = i }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(rating); onDismiss() }) { Text("Submit") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

