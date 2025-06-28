package com.example.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class SubjectProgress(
    val title: String,
    val code: String,
    val current: Int,
    val lastMonth: Int,
    val color: Color
)

@Composable
fun AttendanceScreen() {
    val subjects = listOf(
        SubjectProgress("Physics", "PHY-101", 92, 84, Color(0xFF2D72F4)),
        SubjectProgress("Mathematics", "MATH-201", 80, 68, Color(0xFFF39B32)),
        SubjectProgress("Chemistry", "CHEM-103", 88, 90, Color(0xFF2D72F4))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7)),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(subjects.size) { index ->
            AttendanceCard(item = subjects[index])
        }
    }
}

@Composable
private fun AttendanceCard(item: SubjectProgress) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111111),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.code,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6E7480)
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            DoubleRingGauge(
                currentPercent = item.current,
                lastPercent = item.lastMonth,
                color = item.color,
                size = 96.dp,
                trackColor = Color(0xFFD9DADD)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Last month: ${item.lastMonth}%",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6E7480)
            )
        }
    }
}

@Composable
private fun DoubleRingGauge(
    currentPercent: Int,
    lastPercent: Int,
    color: Color,
    size: Dp,
    trackColor: Color
) {
    val gapDeg = 12f
    val startDeg = -90f
    val outerSweep = lastPercent * 3.6f - gapDeg
    val innerSweep = currentPercent * 3.6f - gapDeg

    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val outerStroke = 12.dp.toPx()
            val innerStroke = 6.dp.toPx()

            drawArc(
                color = trackColor,
                startAngle = startDeg + gapDeg / 2,
                sweepAngle = 360f - gapDeg,
                useCenter = false,
                style = Stroke(width = outerStroke, cap = StrokeCap.Butt)
            )
            val arcInset = (outerStroke - innerStroke) / 2 + outerStroke / 2
            inset(arcInset, arcInset) {
                drawArc(
                    color = trackColor,
                    startAngle = startDeg + gapDeg / 2,
                    sweepAngle = 360f - gapDeg,
                    useCenter = false,
                    style = Stroke(width = innerStroke, cap = StrokeCap.Butt)
                )
            }

            drawArc(
                color = color,
                startAngle = startDeg,
                sweepAngle = outerSweep.coerceAtLeast(0f),
                useCenter = false,
                style = Stroke(width = outerStroke, cap = StrokeCap.Round)
            )
            inset(arcInset, arcInset) {
                drawArc(
                    color = color,
                    startAngle = startDeg,
                    sweepAngle = innerSweep.coerceAtLeast(0f),
                    useCenter = false,
                    style = Stroke(width = innerStroke, cap = StrokeCap.Round)
                )
            }
        }
        Text(
            text = "$currentPercent%",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111111)
        )
    }
}

@Preview
@Composable
private fun PreviewAttendance() {
    AttendanceScreen()
}
