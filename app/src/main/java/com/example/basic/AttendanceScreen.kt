package com.example.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AttendanceScreen() {
    Box(modifier = Modifier.fillMaxSize())
}

@Preview
@Composable
private fun PreviewAttendance() {
    AttendanceScreen()
}
