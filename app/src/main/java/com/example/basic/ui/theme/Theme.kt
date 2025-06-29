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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import com.example.basic.R

private val Inter = FontFamily(Font(R.font.inter))

private val DefaultTypography = Typography()

private val AppTypography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = Inter),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = Inter),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = Inter),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = Inter),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = Inter),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = Inter),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = Inter),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = Inter),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = Inter),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = Inter),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = Inter),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = Inter),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = Inter),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = Inter),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = Inter),
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
            onSurface = mdOnSurface
        ),
        typography = AppTypography,
        content = content
    )
}
