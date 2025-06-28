package com.example.basic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background

private data class Subject(
    val name: String,
    val code: String,
    val attendance: Int,
    val lastMonth: Int,
    val color: Color
)

private val subjects = listOf(
    Subject("Physics", "PHY-101", 92, 84, Color(0xFF2D72F4)),
    Subject("Mathematics", "MAT-201", 80, 68, Color(0xFFF39B32)),
    Subject("Chemistry", "CHE-102", 88, 90, Color(0xFF2D72F4))
)

private const val GAP_ANGLE = 0f

@Composable
private fun ProgressRing(percent: Int, color: Color, modifier: Modifier = Modifier) {
    Box(modifier.size(96.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 12.dp.toPx()
            val sweepTrack = 360f - GAP_ANGLE
            val startTrack = 270f + GAP_ANGLE / 2
            drawArc(
                color = Color(0xFFD9DADD),
                startAngle = startTrack,
                sweepAngle = sweepTrack,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            val sweep = (percent.coerceIn(0, 100)) * 3.6f
            drawArc(
                color = color,
                startAngle = 270f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // small inner circle for additional visual depth
            drawCircle(
                color = Color.White,
                radius = size.minDimension * 0.15f,
                center = center
            )
        }
        Text(
            text = "$percent %",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111111)
        )
    }
}

@Composable
private fun SubjectCard(item: Subject, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(128.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            val (title, ring, last) = createRefs()
            Column(
                modifier = Modifier.constrainAs(title) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111111)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.code.uppercase(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6E7480)
                )
            }
            Column(
                modifier = Modifier.constrainAs(ring) {
                    end.linkTo(parent.end)
                    top.linkTo(title.top)
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProgressRing(percent = item.attendance, color = item.color)
            }
            Row(
                modifier = Modifier.constrainAs(last) {
                    start.linkTo(ring.start)
                    top.linkTo(ring.bottom, margin = 8.dp)
                }
            ) {
                Text(
                    text = "Last month:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6E7480)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.lastMonth} %",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111111)
                )
            }
        }
    }
}

@Composable
fun AttendanceScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        subjects.forEach { subject ->
            SubjectCard(item = subject, modifier = Modifier.padding(horizontal = 0.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewAttendance() {
    AttendanceScreen()
}
