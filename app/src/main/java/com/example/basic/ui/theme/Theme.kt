package com.example.basic.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import com.example.basic.ui.theme.mdBackground
import com.example.basic.ui.theme.mdOnBackground
import com.example.basic.ui.theme.mdOnPrimary
import com.example.basic.ui.theme.mdOnSecondary
import com.example.basic.ui.theme.mdOnSurface
import com.example.basic.ui.theme.mdPrimary
import com.example.basic.ui.theme.mdSecondary
import com.example.basic.ui.theme.mdSurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import com.example.basic.R

private val Inter = FontFamily(Font(R.font.inter))

private val DefaultTypography = Typography()

private val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = Inter, fontSize = 40.sp),
    displayMedium = TextStyle(fontFamily = Inter, fontSize = 34.sp),
    displaySmall = TextStyle(fontFamily = Inter, fontSize = 30.sp),
    headlineLarge = TextStyle(fontFamily = Inter, fontSize = 28.sp),
    headlineMedium = TextStyle(fontFamily = Inter, fontSize = 24.sp),
    headlineSmall = TextStyle(fontFamily = Inter, fontSize = 20.sp),
    titleLarge = TextStyle(fontFamily = Inter, fontSize = 24.sp),
    titleMedium = TextStyle(fontFamily = Inter, fontSize = 20.sp),
    titleSmall = TextStyle(fontFamily = Inter, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = Inter, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = Inter, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = Inter, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = Inter, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = Inter, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = Inter, fontSize = 10.sp),
)

@Composable
fun VitStudentAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = mdPrimary,
            onPrimary = mdOnPrimary,
            secondary = mdSecondary,
            onSecondary = mdOnSecondary,
            background = mdBackground,
            onBackground = mdOnBackground,
            surface = mdSurface,
            onSurface = mdOnSurface,
            error = mdError,
            onError = mdOnError
        ),
        typography = AppTypography,
        content = content
    )
}
