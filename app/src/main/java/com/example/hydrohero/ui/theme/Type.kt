package com.example.hydrohero.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * HydroHero typography — soft & friendly.
 *
 * NOTE: To match the prototype exactly, add these two Google Fonts to
 * app/src/main/res/font/ and swap FontFamily.Default below:
 *   • Plus Jakarta Sans (UI / body)
 *   • Fraunces (display / numbers — the big "1.5 / 2L" and title stamps)
 *
 * Until you add the fonts, Roboto (FontFamily.Default) is a safe stand-in.
 */

// private val JakartaSans = FontFamily(
//     Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
//     Font(R.font.plus_jakarta_sans_medium,  FontWeight.Medium),
//     Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
//     Font(R.font.plus_jakarta_sans_bold,    FontWeight.Bold),
// )
// private val Fraunces = FontFamily(
//     Font(R.font.fraunces_regular, FontWeight.Normal),
//     Font(R.font.fraunces_semibold, FontWeight.SemiBold),
// )

private val Sans    = FontFamily.Default
private val Display = FontFamily.Serif  // Fraunces-like placeholder until you add the font

// Public aliases — used by the new screens (AddWaterDialog, DailyProgressScreen,
// CelebrationEffect). Swap these to your custom FontFamily blocks above when you
// add Plus Jakarta Sans + Fraunces to res/font/.
val HydroSansFamily: FontFamily    = Sans
val HydroDisplayFamily: FontFamily = Display

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.SemiBold,
        fontSize = 46.sp, lineHeight = 48.sp, letterSpacing = (-1).sp
    ),
    displayMedium = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp, lineHeight = 38.sp, letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp, lineHeight = 32.sp, letterSpacing = (-0.3).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Display, fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp, lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp, lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 15.sp, lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal,
        fontSize = 13.sp, lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.8.sp
    ),
)
