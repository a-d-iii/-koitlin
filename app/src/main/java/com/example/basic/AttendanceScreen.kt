package com.example.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Simple attendance dashboard showing attendance percentages for each subject.
 */

data class SubjectProgress(
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
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(subjects) { _, item ->
            AttendanceCard(item)
        }
    }
}

@Composable
private fun AttendanceCard(item: SubjectProgress) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 128.dp)
            .wrapContentHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
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
                color = Color(0xFF6E7480),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.wrapContentHeight()
        ) {
            DoubleRingGauge(
                currentPercent = item.current.coerceAtLeast(0),
                lastPercent = item.lastMonth.coerceAtLeast(0),
                color = item.color,
                gaugeSize = 96.dp,
                trackColor = Color(0xFFD9DADD)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Last month: ${item.lastMonth.coerceAtLeast(0)}%",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6E7480),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DoubleRingGauge(
    currentPercent: Int,
    lastPercent: Int,
    color: Color,
    gaugeSize: Dp,
    trackColor: Color
) {
    val gapDegrees = 12f
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale.coerceAtMost(1.3f)

    val outerStrokePx = with(LocalDensity.current) { 12.dp.toPx() }
    val innerStrokePx = with(LocalDensity.current) { 6.dp.toPx() }

    Box(
        modifier = Modifier
            .requiredSize(gaugeSize)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diameter = size.minDimension
            val paddingOuter = outerStrokePx / 2f
            val paddingInner = outerStrokePx + 8.dp.toPx() + innerStrokePx / 2f

            drawArc(
                color = trackColor,
                startAngle = -90f + gapDegrees / 2,
                sweepAngle = 360f - gapDegrees,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(paddingOuter, paddingOuter),
                size = androidx.compose.ui.geometry.Size(diameter - outerStrokePx, diameter - outerStrokePx),
                style = Stroke(width = outerStrokePx, cap = StrokeCap.Butt)
            )
            drawArc(
                color = trackColor,
                startAngle = -90f + gapDegrees / 2,
                sweepAngle = 360f - gapDegrees,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(paddingInner, paddingInner),
                size = androidx.compose.ui.geometry.Size(diameter - 2 * paddingInner, diameter - 2 * paddingInner),
                style = Stroke(width = innerStrokePx, cap = StrokeCap.Butt)
            )

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = lastPercent * 3.6f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(paddingOuter, paddingOuter),
                size = androidx.compose.ui.geometry.Size(diameter - outerStrokePx, diameter - outerStrokePx),
                style = Stroke(width = outerStrokePx, cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = currentPercent * 3.6f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(paddingInner, paddingInner),
                size = androidx.compose.ui.geometry.Size(diameter - 2 * paddingInner, diameter - 2 * paddingInner),
                style = Stroke(width = innerStrokePx, cap = StrokeCap.Round)
            )
        }
        Text(
            text = "$currentPercent%",
            fontSize = 32.sp / fontScale,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111111),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun PreviewAttendance() {
    MaterialTheme {
        Surface(color = Color(0xFFF4F5F7)) { AttendanceScreen() }
    }
}
