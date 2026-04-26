package com.example.hydrohero.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
// HydroHero — Soft & Friendly palette
// Derived from the hi-fi prototype. Cool-neutral surfaces,
// aqua primary, warm gold for rewards, coral for streaks.
// ─────────────────────────────────────────────

// Primary — aqua droplet
val HydroPrimary       = Color(0xFF2BA6B3)   // deep aqua — buttons, emphasis
val HydroPrimaryStrong = Color(0xFF1E8591)   // pressed / text on soft bg
val HydroPrimarySoft   = Color(0xFFCDEFF1)   // chip bg, track
val HydroPrimarySofter = Color(0xFFE8F7F8)   // card interior
val HydroAccentMint    = Color(0xFF8ED9C6)   // secondary aqua

// Warm accents
val HydroGold          = Color(0xFFE8B84A)   // coin / premium / milestone
val HydroGoldSoft      = Color(0xFFFBECC2)
val HydroCoral         = Color(0xFFE8835C)   // streak flame
val HydroCoralSoft     = Color(0xFFFBDCC9)

// Neutrals — cool-tinted, subtly warm black
val HydroInk           = Color(0xFF0F1B24)   // primary text
val HydroInk2          = Color(0xFF44586A)   // secondary text
val HydroInk3          = Color(0xFF8A9AA8)   // tertiary / captions
val HydroLine          = Color(0xFFE4EBEF)   // card borders

val HydroSurface       = Color(0xFFFFFFFF)   // cards
val HydroSurface2      = Color(0xFFF4F8FA)   // app bg
val HydroSurface3      = Color(0xFFEBF1F4)   // inset surfaces

// Dark mode (for later)
val HydroInkDark       = Color(0xFFE8F1F5)
val HydroSurfaceDark   = Color(0xFF0F1B24)
val HydroSurface2Dark  = Color(0xFF0A1319)

// Aliases used by the newer screens (AddWater / DailyProgress / Celebration).
// Defined as separate vals (not just renames) so the existing tokens stay
// untouched and other screens keep compiling.
val HydroPrimaryDeep   = HydroPrimaryStrong
val HydroBackground    = HydroSurface2
val HydroAccentSun     = HydroGoldSoft
val HydroAccentBlush   = HydroCoralSoft

// Background gradient presets (shop items) — top, bottom
val BgBeach   = listOf(Color(0xFFFEF3E2), Color(0xFFFCDFB7))
val BgSunset  = listOf(Color(0xFFFDE1D1), Color(0xFFF9B89A))
val BgForest  = listOf(Color(0xFFDCEFD7), Color(0xFFB7DDB1))
val BgSea     = listOf(Color(0xFFD3EEF2), Color(0xFFA9DAE3))
val BgNight   = listOf(Color(0xFF3B4D7A), Color(0xFF23305A))

// ─── Legacy aliases (so the rest of the app keeps compiling) ───
val PrimaryBlue      = HydroPrimary
val LightBlue        = HydroAccentMint
val AccentGreen      = HydroPrimaryStrong
val BackgroundLight  = HydroSurface2
val BackgroundWhite  = HydroSurface
val TextDark         = HydroInk
val TextLight        = HydroInk3
val BorderLight      = HydroLine
val CardBorder       = HydroPrimarySoft

// Material 3 defaults — unused but keep for Theme.kt
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
