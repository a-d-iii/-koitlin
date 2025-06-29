package com.example.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.basic.DoubleRingProgress
import com.example.basic.MiniLineGraph
import com.example.basic.ui.theme.gradientBottom
import com.example.basic.ui.theme.gradientTop

@Composable
private fun colorForAttendance(value: Float): Color = when {
    value >= 0.75f -> MaterialTheme.colorScheme.primary
    value >= 0.70f -> MaterialTheme.colorScheme.secondary
    else -> MaterialTheme.colorScheme.error
}

private data class Subject(
    val name: String,
    val code: String,
    val total: Float,
    val between: Float
)

@Composable
fun AttendanceScreen() {
    val subjects = listOf(
        Subject("Mathematics", "MAT101", total = 1.0f, between = 1.0f),
        Subject("Physics", "PHY102", total = 0.72f, between = 0.69f),
        Subject("Chemistry", "CHE103", total = 0.60f, between = 0.50f),
        Subject("Computer Science", "CSE104", total = 0.95f, between = 0.85f),
        Subject("English", "ENG105", total = 0.80f, between = 0.70f),
        Subject("Electronics", "ELE106", total = 0.50f, between = 0.40f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(gradientTop, gradientBottom)))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Attendance",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        subjects.forEach { subject ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp)
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = subject.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = subject.code,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        MiniLineGraph(
                            data = listOf(0.6f, 0.8f, 0.7f, 0.9f, 0.65f),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .width(80.dp)
                                .height(32.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val outerColor = colorForAttendance(subject.total)
                        val innerColor = colorForAttendance(subject.between)
                        DoubleRingProgress(
                            outerProgress = subject.total,
                            innerProgress = subject.between,
                            modifier = Modifier.size(96.dp),
                            outerColor = outerColor,
                            innerColor = innerColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Btw exams: ${(subject.between * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewAttendance() {
    AttendanceScreen()
}
