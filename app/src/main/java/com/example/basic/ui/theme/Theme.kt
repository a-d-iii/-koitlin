package com.example.basic.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun VitStudentAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(), content = content)
}

val homeSectionBg = Color(0xFFEEF1F8)
val accentBlue = Color(0xFF2979FF)
val accentOrange = Color(0xFFFF6D00)
val shadowSmall = 2.dp
