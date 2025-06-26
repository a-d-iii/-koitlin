package com.example.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.with
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PlannerScreen() {
    val days = WEEKLY_SCHEDULE.keys.toList()
    var dayIndex by remember { mutableStateOf(0) }
    var dragAmount by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
            .pointerInput(dayIndex) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, delta ->
                        dragAmount += delta
                    },
                    onDragEnd = {
                        if (dragAmount < -100 && dayIndex < days.lastIndex) {
                            dayIndex++
                        } else if (dragAmount > 100 && dayIndex > 0) {
                            dayIndex--
                        }
                        dragAmount = 0f
                    },
                    onDragCancel = { dragAmount = 0f }
                )
            }
    ) {
        Text(
            text = "Weekly Timetable",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 16.dp)
        )

        LazyRow(
            modifier = Modifier.padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            itemsIndexed(days) { i, d ->
                val selected = i == dayIndex
                val bgColor by animateColorAsState(
                    if (selected) Color(0xFF6C5CE7) else Color(0xFFE0E0E0),
                    animationSpec = tween(300)
                )
                val textColor by animateColorAsState(
                    if (selected) Color.White else Color(0xFF333333),
                    animationSpec = tween(300)
                )
                Text(
                    text = d.take(3),
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = textColor,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(bgColor)
                        .clickable { dayIndex = i }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        AnimatedContent(
            targetState = dayIndex,
            transitionSpec = {
                (slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) +
                        fadeIn(animationSpec = tween(300))) with
                        (slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(300)) +
                        fadeOut(animationSpec = tween(300)))
            },
            label = "classes"
        ) { index ->
            val classes = WEEKLY_SCHEDULE[days[index]].orEmpty()
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(classes) { cls ->
                    Card(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cls.course,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF333333)
                            )
                            Text(
                                text = cls.faculty,
                                fontSize = 12.sp,
                                color = Color(0xFF666666),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${cls.start} – ${cls.end}",
                                fontSize = 12.sp,
                                color = Color(0xFF333333)
                            )
                            Text(
                                text = cls.room,
                                fontSize = 10.sp,
                                color = Color(0xFF666666),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
