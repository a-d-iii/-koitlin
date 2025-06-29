package com.example.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DoubleRingProgress(
    outerProgress: Float,
    innerProgress: Float,
    modifier: Modifier = Modifier,
    outerColor: Color = Color(0xFF69CBFF),
    innerColor: Color = outerColor,
    thickness: Dp = 8.dp,
    gap: Dp = 4.dp
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = thickness.toPx()
            val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            val gapPx = gap.toPx()
            val outerRadius = size.minDimension / 2
            val innerRadius = outerRadius - strokeWidth - gapPx
            val outerTrackColor = outerColor.copy(alpha = 0.3f)
            val innerTrackColor = innerColor.copy(alpha = 0.3f)

            // Outer track
            drawArc(
                color = outerTrackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
                size = Size(outerRadius * 2, outerRadius * 2),
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius)
            )

            // Outer progress
            drawArc(
                color = outerColor,
                startAngle = -90f,
                sweepAngle = 360f * outerProgress.coerceIn(0f, 1f),
                useCenter = false,
                style = stroke,
                size = Size(outerRadius * 2, outerRadius * 2),
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius)
            )

            // Inner track
            drawArc(
                color = innerTrackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
                size = Size(innerRadius * 2, innerRadius * 2),
                topLeft = Offset(center.x - innerRadius, center.y - innerRadius)
            )

            // Inner progress
            drawArc(
                color = innerColor,
                startAngle = -90f,
                sweepAngle = 360f * innerProgress.coerceIn(0f, 1f),
                useCenter = false,
                style = stroke,
                size = Size(innerRadius * 2, innerRadius * 2),
                topLeft = Offset(center.x - innerRadius, center.y - innerRadius)
            )
        }
        Text(
            text = "${(outerProgress * 100).toInt()}%",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}
