package com.example.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private data class Subject(val name: String, val code: String, val attendance: Int)

private val subjects = listOf(
    Subject("Data Structures", "CS201", 95),
    Subject("Operating Systems", "CS202", 82),
    Subject("Algorithms", "CS203", 70),
    Subject("Calculus", "MA101", 60),
    Subject("Statistics", "MA201", 50),
    Subject("Networking", "CS204", 45),
    Subject("Cyber Security", "CS301", 35),
    Subject("AI", "CS302", 25),
    Subject("Database Systems", "CS205", 88),
    Subject("Software Engineering", "CS206", 77)
)

private fun backgroundColor(p: Int): Color = when {
    p >= 75 -> Color(0xFFC8E6C9)
    p >= 70 -> Color(0xFFFFF9C4)
    else -> Color(0xFFFFCDD2)
}

private fun iconFor(p: Int) = when {
    p >= 75 -> Icons.Default.CheckCircle
    p >= 70 -> Icons.Default.Warning
    else -> Icons.Default.Cancel
}

private fun iconColor(p: Int): Color = when {
    p >= 75 -> Color(0xFF2E7D32)
    p >= 70 -> Color(0xFFF9A825)
    else -> Color(0xFFC62828)
}

@Composable
private fun SubjectCard(item: Subject, isLab: Boolean) {
    val iconScale = remember { Animatable(0.5f) }
    val cardScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        iconScale.animateTo(1f, animationSpec = tween(durationMillis = 400))
    }
    Card(
        modifier = Modifier
            .padding(6.dp)
            .height(130.dp)
            .graphicsLayer(scaleX = cardScale.value, scaleY = cardScale.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                scope.launch {
                    cardScale.animateTo(0.96f, animationSpec = tween(100))
                    cardScale.animateTo(1f, animationSpec = tween(100))
                }
            },
        colors = CardDefaults.cardColors(containerColor = backgroundColor(item.attendance)),
        border = if (isLab) BorderStroke(2.dp, Color(0xFF757575)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.weight(0.4f)) {
                Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF212121))
                Text(text = item.code, fontSize = 14.sp, color = Color(0xFF212121))
            }
            Spacer(modifier = Modifier.weight(0.1f))
            Row(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxSize(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = item.attendance.toString(), fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                    Text(text = "%", fontSize = 26.sp, color = Color(0xFF212121), modifier = Modifier.padding(bottom = 2.dp, start = 2.dp))
                }
                Icon(
                    imageVector = iconFor(item.attendance),
                    contentDescription = null,
                    tint = iconColor(item.attendance),
                    modifier = Modifier.graphicsLayer(scaleX = iconScale.value, scaleY = iconScale.value)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AttendanceScreen() {
    val rotate = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Attendance",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF212121)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
            ) {
                itemsIndexed(subjects) { index, item ->
                    SubjectCard(item = item, isLab = index >= subjects.size - 4)
                }
            }
        }
        FloatingActionButton(
            onClick = {
                scope.launch {
                    launch {
                        scale.animateTo(0.8f, animationSpec = tween(100))
                        scale.animateTo(1f, animationSpec = tween(100))
                    }
                    launch {
                        rotate.animateTo(1f, animationSpec = tween(500, easing = LinearEasing))
                        rotate.snapTo(0f)
                    }
                }
            },
            containerColor = Color(0xFFF0F0F0),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .graphicsLayer {
                    rotationZ = rotate.value * 360f
                    scaleX = scale.value
                    scaleY = scale.value
                }
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF212121))
        }
    }
}
