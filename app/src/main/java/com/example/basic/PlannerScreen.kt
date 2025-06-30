package com.example.basic

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.basic.ui.theme.gradientBottom
import com.example.basic.ui.theme.gradientTop
import com.example.basic.network.ApiService

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PlannerScreen() {
    var semesters by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedSemester by remember { mutableStateOf<String?>(null) }
    var weeklySchedule by remember { mutableStateOf<Timetable>(emptyMap()) }
    val days = weeklySchedule.keys.toList()
    var dayIndex by remember { mutableStateOf(0) }
    var dragAmount by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        semesters = ApiService.getSemesters()
        if (semesters.isNotEmpty()) {
            selectedSemester = semesters.first()
            weeklySchedule = ApiService.getTimetable(selectedSemester!!)
        }
    }

    LaunchedEffect(selectedSemester) {
        selectedSemester?.let {
            weeklySchedule = ApiService.getTimetable(it)
            dayIndex = 0
        }
    }

    val classes by remember(dayIndex, weeklySchedule) {
        derivedStateOf { weeklySchedule[days.getOrNull(dayIndex)].orEmpty() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(gradientTop, gradientBottom)))
            .padding(16.dp)
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
        if (semesters.isNotEmpty()) {
            var expanded by remember { mutableStateOf(false) }
            Box {
                Text(
                    text = selectedSemester ?: "Select Semester",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { expanded = true }
                        .padding(12.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    semesters.forEach { sem ->
                        DropdownMenuItem(text = { Text(sem) }, onClick = {
                            selectedSemester = sem
                            expanded = false
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = "Weekly Timetable",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyRow(
            modifier = Modifier.padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            itemsIndexed(days) { i, d ->
                val selected = i == dayIndex
                val bgColor by animateColorAsState(
                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    animationSpec = tween(300)
                )
                val textColor by animateColorAsState(
                    if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                    animationSpec = tween(300)
                )
                Text(
                    text = d.take(3),
                    style = MaterialTheme.typography.labelLarge,
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
                if (targetState > initialState) {
                    (slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))) with
                            (slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300)))
                } else {
                    (slideInVertically(initialOffsetY = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))) with
                            (slideOutVertically(targetOffsetY = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300)))
                }
            },
            label = "classes"
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(classes) { cls ->
                    Card(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = cls.faculty,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${cls.start} – ${cls.end}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = cls.room,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
}
