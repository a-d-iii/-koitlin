package com.example.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
 
import androidx.compose.material.icons.filled.Person
 
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
 
 
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        HomeHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DayProgressBar(modifier = Modifier.width(screenWidth * 0.4f))
                WeatherCard()
            }
            Spacer(Modifier.height(16.dp))
            ClassSummaryBar()
 
            Spacer(Modifier.height(16.dp))
            UtilitiesSection()
 
 
            Spacer(Modifier.height(16.dp))
            MenuSection(contentPadding = 16.dp)
            Spacer(Modifier.height(16.dp))
            TasksSection()
            Spacer(Modifier.height(16.dp))
            InsightsSection()
        }
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

private enum class Priority(val label: String) { LOW("Low Priority"), MEDIUM("Medium Priority"), HIGH("High Priority") }

private data class Task(
    val title: String,
    val details: String,
    val priority: Priority,
    val color: Color
)

private fun Color.darken(factor: Float = 0.8f): Color {
    return Color(red * factor, green * factor, blue * factor, alpha)
}

@Composable
private fun WeatherCard(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier
            .width(LocalConfiguration.current.screenWidthDp.dp * 0.3f)
            .height(200.dp)
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
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "25°",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Filled.Cloud,
                    contentDescription = "Cloudy",
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(vertical = 4.dp)
                )
                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WeatherInfo(Icons.Filled.WaterDrop, "60%")
                    WeatherInfo(Icons.Filled.Speed, "10 km/h")
                }
            }
        }
    }
}

@Composable
private fun WeatherInfo(icon: ImageVector, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        Text(
            value,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
 
private fun DayProgressBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
 
    ) {
        Text(
            "30% day left",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun ClassSummaryBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "6 classes",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFF448AFF), CircleShape)
            )
            Text(
                "2 labs",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFF80D8FF), CircleShape)
            )
            Text(
                "3 project",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun UtilityBox(label: String) {
    Card(
        modifier = Modifier
            .width(72.dp)
            .height(96.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
 
 
private fun UtilitiesSection() {
    SectionHeader("Utilities")
    var expanded by remember { mutableStateOf(false) }
    val utilities = listOf(
        "Clock", "Calendar", "Notes", "Files",
        "Camera", "Maps", "Gallery", "Music"
    )
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.UnfoldLess else Icons.Filled.UnfoldMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                utilities.take(4).forEach { label ->
                    UtilityBox(label)
                }
            }
        }
        if (expanded) {
            utilities.drop(4).chunked(4).forEach { rowItems ->
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowItems.forEach { label ->
                        UtilityBox(label)
                    }
                    if (rowItems.size < 4) {
                        repeat(4 - rowItems.size) {
                            Spacer(modifier = Modifier.width(72.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuSection(contentPadding: Dp) {
    SectionHeader("Today's Menu")
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * 0.35f
    val cardHeight = 120.dp
    val meals = listOf(
        "Breakfast" to "Pancakes & Juice",
        "Lunch" to "Chicken Salad",
        "Snacks" to "Fruit & Yogurt",
        "Dinner" to "Salmon & Veggies"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = contentPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        meals.forEach { (label, menu) ->
            Card(
                modifier = Modifier
                    .width(cardWidth)
                    .height(cardHeight)
                    .padding(vertical = 8.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = Color(0xFFFF6A00)
                    )
                    Text(
                        label,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        menu,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TasksSection() {
    SectionHeader("Tasks & Reminders")
    val tasks = listOf(
        Task(
            title = "Finish report",
            details = "Complete Q3 analysis",
            priority = Priority.HIGH,
            color = Color(0xFFFFCDD2)
        ),
        Task(
            title = "Grocery shopping",
            details = "Buy veggies and milk",
            priority = Priority.LOW,
            color = Color(0xFFC8E6C9)
        ),
        Task(
            title = "Call plumber",
            details = "Fix kitchen sink leak",
            priority = Priority.MEDIUM,
            color = Color(0xFFBBDEFB)
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        tasks.forEach { task ->
            var checked by remember { mutableStateOf(false) }
            val accent = task.color.darken()
            val interaction = remember { MutableInteractionSource() }
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp)
                    .height(96.dp),
                colors = CardDefaults.cardColors(containerColor = task.color),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Box(Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(6.dp)
                            .background(accent)
                            .align(Alignment.CenterStart)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 14.dp, end = 8.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            interactionSource = interaction,
                            modifier = Modifier.indication(interaction, null),
                            colors = CheckboxDefaults.colors(
                                checkedColor = accent,
                                uncheckedColor = accent,
                                checkmarkColor = Color.White
                            )
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                task.title,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
                            )
                            Text(
                                task.details,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666),
                                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                    Text(
                        task.priority.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .border(1.dp, accent, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
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
