package com.example.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

enum class PanelState { None, Top, Bottom }

@Composable
fun HomeScreen() {
    val baseCards = listOf(
        ClassInfo("1", "ECE1001 @ Lab 3", "08:00 – 08:50"),
        ClassInfo("2", "MAT1002 @ Room 105", "09:00 – 09:50"),
        ClassInfo("3", "PHY1003 @ Hall A", "10:00 – 10:50"),
        ClassInfo("4", "CHE1004 @ Lab 2", "11:00 – 11:50"),
        ClassInfo("5", "CSE1005 @ Room 201", "12:00 – 12:50"),
    )
    val cards = listOf(ClassInfo("overview", "", "")) + baseCards
    var panel by remember { mutableStateOf(PanelState.None) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
 
        CardCarousel(
            cards = cards,
            onSwipeDown = {
                if (panel == PanelState.None) panel = PanelState.Top
                else if (panel == PanelState.Bottom) panel = PanelState.None
            },
            onSwipeUp = {
                if (panel == PanelState.None) panel = PanelState.Bottom
                else if (panel == PanelState.Top) panel = PanelState.None
            },
            locationName = "Amaravati"
        )
 

        if (panel != PanelState.None) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
                    .clickable { panel = PanelState.None }
            )
        }

        AnimatedVisibility(
            visible = panel == PanelState.Top,
            enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(150)),
            exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(150))
        ) {
            WhatsNextPanel(onDismiss = { panel = PanelState.None })
        }

        AnimatedVisibility(
            visible = panel == PanelState.Bottom,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(150)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(150))
        ) {
            BottomPanel(onDismiss = { panel = PanelState.None })
        }
    }
}

@Composable
private fun WhatsNextPanel(onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "What’s Next",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            val meal = remember { mutableStateOf("Breakfast") }
            val range = remember { mutableStateOf("08:00 – 09:00") }
            val items = remember { mutableStateOf(listOf("Pancakes", "Juice")) }
            var countdown by remember { mutableStateOf("00:00:00") }

            LaunchedEffect(Unit) {
                while (true) {
                    val now = System.currentTimeMillis()
                    val target = now + 3600000 // +1h
                    val diff = target - now
                    val h = diff / 3600000
                    val m = diff % 3600000 / 60000
                    val s = diff % 60000 / 1000
                    countdown = String.format("%02d:%02d:%02d", h, m, s)
                    kotlinx.coroutines.delay(1000)
                }
            }

            Text("${meal.value} · ${range.value}")
            Text("Starts in $countdown")
            Text(items.value.joinToString(), modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) { Text("Close") }
        }
    }
}

@Composable
private fun BottomPanel(onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shadowElevation = 8.dp,
        color = Color(0xFFF0F2F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Reminders / To‑Do",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            val reminders = listOf(
                "Finish ECE1001 Lab report",
                "Review MAT1002 notes",
                "PHY1003 problem set",
                "CHE1004 lab prep",
                "CSE1005 assignment"
            )
            reminders.forEach {
                Text("• $it", modifier = Modifier.padding(vertical = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Utilities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            val utilities = listOf(
                "Calculator",
                "Attendance Tracker",
                "Grade Predictor",
                "Campus Map",
                "Help Desk"
            )
            utilities.forEach {
                Text(it, modifier = Modifier.padding(vertical = 2.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) { Text("Close") }
        }
    }
}

