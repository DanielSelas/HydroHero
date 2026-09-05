package com.danielsela.hydrohero.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
// HydroHero — Soft & Friendly palette
// Themable: primary hue and light/dark mode are runtime mutable.
// Call HydroThemeRuntime.applyHue("teal" / "green" / "blue" / "coral" / "gold")
// and HydroThemeRuntime.applyDark(true / false). Compose recomposes any
// screen that reads these vals because they're backed by mutableStateOf.
// ─────────────────────────────────────────────

object HydroThemeRuntime {
    // Backed Compose state — flips trigger full recomposition
    var hueId by mutableStateOf("teal")
        private set
    var dark by mutableStateOf(false)
        private set

    // Registered once at startup so every toggle is written through to storage.
    // A callback keeps the screens free of a DataRepository parameter — they
    // still just call applyHue()/applyDark().
    private var onChanged: ((hueId: String, dark: Boolean) -> Unit)? = null

    fun setPersistence(block: (hueId: String, dark: Boolean) -> Unit) { onChanged = block }

    /** Applies a previously saved choice without writing it straight back. */
    fun restore(hueId: String, dark: Boolean) {
        this.hueId = hueId
        this.dark = dark
    }

    fun applyHue(id: String) { hueId = id; onChanged?.invoke(hueId, dark) }
    fun applyDark(value: Boolean) { dark = value; onChanged?.invoke(hueId, dark) }
}

// ─── Hue palettes ────────────────────────────────────────────────
private data class HuePalette(
    val primary: Color,
    val primaryStrong: Color,
    val primarySoft: Color,
    val primarySofter: Color,
    val accentMint: Color,
    val swatch: Color,
)

private val HueTeal = HuePalette(
    primary = Color(0xFF2BA6B3), primaryStrong = Color(0xFF1E8591),
    primarySoft = Color(0xFFCDEFF1), primarySofter = Color(0xFFE8F7F8),
    accentMint = Color(0xFF8ED9C6), swatch = Color(0xFF2BA6B3),
)
private val HueGreen = HuePalette(
    primary = Color(0xFF16A34A), primaryStrong = Color(0xFF15803D),
    primarySoft = Color(0xFFD1FAE5), primarySofter = Color(0xFFECFDF5),
    accentMint = Color(0xFF6EE7B7), swatch = Color(0xFF22C55E),
)
private val HueBlue = HuePalette(
    primary = Color(0xFF2563EB), primaryStrong = Color(0xFF1D4ED8),
    primarySoft = Color(0xFFDBEAFE), primarySofter = Color(0xFFEFF6FF),
    accentMint = Color(0xFF93C5FD), swatch = Color(0xFF3B82F6),
)
private val HueCoral = HuePalette(
    primary = Color(0xFFE8835C), primaryStrong = Color(0xFFC2410C),
    primarySoft = Color(0xFFFBDCC9), primarySofter = Color(0xFFFEF3E2),
    accentMint = Color(0xFFFDBA74), swatch = Color(0xFFE8835C),
)
private val HueGold = HuePalette(
    primary = Color(0xFFE8B84A), primaryStrong = Color(0xFFB45309),
    primarySoft = Color(0xFFFBECC2), primarySofter = Color(0xFFFEF6E0),
    accentMint = Color(0xFFFCD34D), swatch = Color(0xFFE8B84A),
)

private fun currentHue(): HuePalette = when (HydroThemeRuntime.hueId) {
    "green" -> HueGreen
    "blue" -> HueBlue
    "coral" -> HueCoral
    "gold" -> HueGold
    else -> HueTeal
}

// Public swatch lookups for the picker UI
val HueOptions: List<Pair<String, Color>> = listOf(
    "teal" to HueTeal.swatch,
    "green" to HueGreen.swatch,
    "blue" to HueBlue.swatch,
    "coral" to HueCoral.swatch,
    "gold" to HueGold.swatch,
)

// ─── Themed primaries — recompute on every read ──────────────────
val HydroPrimary: Color get() = currentHue().primary
val HydroPrimaryStrong: Color get() = currentHue().primaryStrong
val HydroPrimarySoft: Color
    get() = if (HydroThemeRuntime.dark) currentHue().primarySoft.copy(alpha = 0.22f) else currentHue().primarySoft
val HydroPrimarySofter: Color
    get() = if (HydroThemeRuntime.dark) currentHue().primarySofter.copy(alpha = 0.12f) else currentHue().primarySofter
val HydroAccentMint: Color get() = currentHue().accentMint

// ─── Static accents (don't change with hue) ─────────────────────
val HydroGold      = Color(0xFFE8B84A)
val HydroGoldSoft  = Color(0xFFFBECC2)
val HydroCoral     = Color(0xFFE8835C)
val HydroCoralSoft = Color(0xFFFBDCC9)

// ─── Surface & ink — flip with dark mode ────────────────────────
val HydroInk: Color get() = if (HydroThemeRuntime.dark) Color(0xFFE8F1F5) else Color(0xFF0F1B24)
val HydroInk2: Color get() = if (HydroThemeRuntime.dark) Color(0xFFB7C5D0) else Color(0xFF44586A)
val HydroInk3: Color get() = if (HydroThemeRuntime.dark) Color(0xFF7B8A98) else Color(0xFF8A9AA8)
val HydroLine: Color get() = if (HydroThemeRuntime.dark) Color(0xFF1E2A35) else Color(0xFFE4EBEF)
val HydroSurface: Color get() = if (HydroThemeRuntime.dark) Color(0xFF131C24) else Color(0xFFFFFFFF)
val HydroSurface2: Color get() = if (HydroThemeRuntime.dark) Color(0xFF0A1319) else Color(0xFFF4F8FA)
val HydroSurface3: Color get() = if (HydroThemeRuntime.dark) Color(0xFF1A242D) else Color(0xFFEBF1F4)

// Dark-mode-specific (legacy refs)
val HydroInkDark      = Color(0xFFE8F1F5)
val HydroSurfaceDark  = Color(0xFF0F1B24)
val HydroSurface2Dark = Color(0xFF0A1319)

// ─── Aliases used elsewhere ─────────────────────────────────────
val HydroPrimaryDeep: Color   get() = HydroPrimaryStrong
val HydroBackground: Color    get() = HydroSurface2
val HydroAccentSun: Color     get() = HydroGoldSoft
val HydroAccentBlush: Color   get() = HydroCoralSoft

// Background gradient presets (shop items) — top, bottom
val BgBeach   = listOf(Color(0xFFFEF3E2), Color(0xFFFCDFB7))
val BgSunset  = listOf(Color(0xFFFDE1D1), Color(0xFFF9B89A))
val BgForest  = listOf(Color(0xFFDCEFD7), Color(0xFFB7DDB1))
val BgSea     = listOf(Color(0xFFD3EEF2), Color(0xFFA9DAE3))
val BgNight   = listOf(Color(0xFF3B4D7A), Color(0xFF23305A))

// ─── Legacy aliases ─────────────────────────────────────────────
val PrimaryBlue: Color      get() = HydroPrimary
val LightBlue: Color        get() = HydroAccentMint
val AccentGreen: Color      get() = HydroPrimaryStrong
val BackgroundLight: Color  get() = HydroSurface2
val BackgroundWhite: Color  get() = HydroSurface
val TextDark: Color         get() = HydroInk
val TextLight: Color        get() = HydroInk3
val BorderLight: Color      get() = HydroLine
val CardBorder: Color       get() = HydroPrimarySoft

// Material 3 defaults — unused but keep for Theme.kt
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
