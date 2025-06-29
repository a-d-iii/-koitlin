package com.example.basic.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.GoogleFont.Provider
import com.example.basic.R

private val googleFontProvider = Provider(
    authority = "com.google.android.gms.fonts",
    package = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val interFontName = GoogleFont("Inter")

private val Inter = FontFamily(
    Font(googleFont = interFontName, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = interFontName, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = interFontName, fontProvider = googleFontProvider, weight = FontWeight.Bold),
)

private val AppTypography = Typography(defaultFontFamily = Inter)

@Composable
fun VitStudentAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(), typography = AppTypography, content = content)
}
