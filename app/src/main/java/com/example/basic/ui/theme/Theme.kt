package com.example.basic.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.GoogleFont.Provider
import androidx.compose.ui.text.googlefonts.Font
import com.google.android.gms.R as GmsR

private val provider = Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = GmsR.array.com_google_android_gms_fonts_certs
)

private val Inter = FontFamily(Font(GoogleFont("Inter"), provider))

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
        colorScheme = lightColorScheme(),
        typography = AppTypography,
        content = content
    )
}
