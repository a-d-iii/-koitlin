package com.example.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class PanelState { None, Top, Bottom }

data class ClassInfo(val id: String, val title: String, val time: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen() {
    val baseCards = listOf(
        ClassInfo("1", "ECE1001 @ Lab 3", "08:00 – 08:50"),
        ClassInfo("2", "MAT1002 @ Room 105", "09:00 – 09:50"),
        ClassInfo("3", "PHY1003 @ Hall A", "10:00 – 10:50"),
        ClassInfo("4", "CHE1004 @ Lab 2", "11:00 – 11:50"),
        ClassInfo("5", "CSE1005 @ Room 201", "12:00 – 12:50"),
    )
    val pagerState = rememberPagerState()
    var panel by remember { mutableStateOf(PanelState.None) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                pageCount = baseCards.size + 1,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                if (page == 0) {
                    SummaryCard()
                } else {
                    ClassCard(info = baseCards[page - 1])
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { panel = PanelState.Top }) { Text("Top Panel") }
                Button(onClick = { panel = PanelState.Bottom }) { Text("Bottom Panel") }
            }
        }

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
private fun ClassCard(info: ClassInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .height(180.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = info.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = info.time, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F4FF)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
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
            Text("Next meal in 00:00:00")
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
                "Utilities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) { Text("Close") }
        }
    }
}
