package com.danielsela.hydrohero.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danielsela.hydrohero.data.UserData
import com.danielsela.hydrohero.ui.theme.*

/**
 * Settings screen — soft & friendly, micro-animated.
 *
 * Same callable signature as the original. Visual upgrades:
 *  • Hero "daily goal" card with stepper, animated ml number, glasses count
 *  • Sectioned cards with caps section labels (matches Progress screen)
 *  • Each row has a small icon bubble in HydroPrimarySofter
 *  • Toggles use HydroPrimaryDeep
 *  • Reset/Crash rows in coral
 *  • Footer stamp with version + tagline
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userData: UserData,
    notificationsEnabled: Boolean,
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    quietHoursEnabled: Boolean,
    syncEnabled: Boolean,
    onBackClick: () -> Unit,
    onGoalChange: (Int) -> Unit,
    onToggleNotifications: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    onToggleQuietHours: () -> Unit,
    onToggleSync: () -> Unit,
    onExportData: () -> Unit,
    onResetProgress: () -> Unit,
    onRateApp: () -> Unit,
    onShareApp: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTermsOfService: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showContact by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HydroBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton("←", onBackClick)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Tune your hydration",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydroInk3,
                    letterSpacing = 1.sp,
                )
                Text(
                    "Settings",
                    fontFamily = HydroDisplayFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroInk,
                    letterSpacing = (-0.4).sp,
                )
            }
            Spacer(Modifier.width(44.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            DailyGoalHero(currentGoal = userData.dailyGoal, onGoalChange = onGoalChange)

            SoftSection(label = "NOTIFICATIONS") {
                IconRow(emoji = "🔔", title = "Enable notifications") {
                    SoftSwitch(checked = notificationsEnabled, onToggle = onToggleNotifications)
                }
                Divider()
                IconRow(emoji = "🔊", title = "Sound alerts") {
                    SoftSwitch(checked = soundEnabled, onToggle = onToggleSound)
                }
                Divider()
                IconRow(emoji = "📳", title = "Vibration") {
                    SoftSwitch(checked = vibrationEnabled, onToggle = onToggleVibration)
                }
                Divider()
                IconRow(emoji = "🌙", title = "Quiet hours", subtitle = "10pm – 7am") {
                    SoftSwitch(checked = quietHoursEnabled, onToggle = onToggleQuietHours)
                }
            }

            SoftSection(label = "APPEARANCE") {
                ThemeHueRow()
                Divider()
                IconRow(emoji = "🌙", title = "Dark mode") {
                    SoftSwitch(
                        checked = HydroThemeRuntime.dark,
                        onToggle = { HydroThemeRuntime.applyDark(!HydroThemeRuntime.dark) },
                    )
                }
            }

            SoftSection(label = "DISPLAY") {
                IconRow(emoji = "📐", title = "Units") {
                    Text("Glasses", fontSize = 13.sp, color = HydroInk3)
                }
            }

            SoftSection(label = "ACCOUNT") {
                IconRow(emoji = "☁️", title = "Sync data") {
                    SoftSwitch(checked = syncEnabled, onToggle = onToggleSync)
                }
                Divider()
                IconRow(emoji = "📤", title = "Export data", clickable = true, onClick = onExportData) {
                    Text("→", fontSize = 16.sp, color = HydroInk3)
                }
                Divider()
                IconRow(
                    emoji = "🗑️",
                    title = "Reset progress",
                    titleColor = HydroCoral,
                    clickable = true,
                    onClick = onResetProgress,
                ) {
                    Text("→", fontSize = 16.sp, color = HydroCoral)
                }
            }

            SoftSection(label = "ABOUT HYDRO HERO") {
                LinkRow("⭐", "Rate Hydro Hero", onRateApp)
                Divider()
                LinkRow("📨", "Share with a friend", onShareApp)
                Divider()
                LinkRow("🛡️", "Privacy policy", onOpenPrivacyPolicy)
                Divider()
                LinkRow("📜", "Terms of service", onOpenTermsOfService)
                Divider()
                LinkRow("💬", "Contact support", { showContact = true })
                Divider()
                LinkRow("⚠️", "Test crash (demo)", { throw RuntimeException("Test Crash") }, danger = true)
            }

            // Footer stamp
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("💧", fontSize = 24.sp)
                Text(
                    "Hydro Hero · v1.0.0",
                    fontFamily = HydroDisplayFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroInk2,
                )
                Text(
                    "Stay hydrated, stay heroic.",
                    fontSize = 11.sp,
                    color = HydroInk3,
                )
            }
        }
    }

    if (showContact) {
        AlertDialog(
            onDismissRequest = { showContact = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = HydroSurface,
            title = {
                Text(
                    "Contact support",
                    fontFamily = HydroDisplayFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroInk,
                )
            },
            text = {
                Text(
                    "Creator: Daniel Sela\n" +
                        "Email: danielsela96@gmail.com\n\n" +
                        "Send a message with your Android version, what you tried, " +
                        "and a screenshot if possible.",
                    fontSize = 14.sp,
                    color = HydroInk2,
                )
            },
            confirmButton = {
                Button(
                    onClick = { showContact = false },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HydroPrimaryDeep),
                ) { Text("Close", fontWeight = FontWeight.Bold) }
            },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────
// Daily goal hero with animated stepper

@Composable
private fun DailyGoalHero(currentGoal: Int, onGoalChange: (Int) -> Unit) {
    var goal by remember(currentGoal) { mutableIntStateOf(currentGoal) }
    val animGoal by animateIntAsState(
        targetValue = goal,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "goal"
    )
    val glasses = (goal / 250f).toInt()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    0f to HydroPrimarySofter,
                    1f to HydroPrimarySoft,
                )
            )
            .padding(20.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "DAILY GOAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = HydroInk2,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$animGoal",
                    fontFamily = HydroDisplayFamily,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroPrimaryDeep,
                    letterSpacing = (-2).sp,
                )
                Text(
                    text = "ml",
                    fontFamily = HydroDisplayFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = HydroPrimaryDeep,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                )
            }
            Text(
                text = "≈ $glasses glasses · 250ml each",
                fontSize = 12.sp,
                color = HydroInk2,
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Stepper("−") {
                    goal = (goal - 250).coerceAtLeast(500)
                    onGoalChange(goal)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(HydroPrimarySoft),
                ) {
                    val pct = (goal / 4000f).coerceIn(0f, 1f)
                    val animPct by animateFloatAsState(
                        targetValue = pct,
                        animationSpec = tween(400),
                        label = "pct"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animPct)
                            .clip(RoundedCornerShape(3.dp))
                            .background(HydroPrimaryDeep),
                    )
                }
                Stepper("+") {
                    goal = (goal + 250).coerceAtMost(5000)
                    onGoalChange(goal)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                listOf(1500, 2000, 2500, 3000).forEach { preset ->
                    val active = goal == preset
                    Surface(
                        onClick = {
                            goal = preset
                            onGoalChange(preset)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (active) HydroPrimaryDeep else HydroSurface,
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "${preset}ml",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) Color.White else HydroInk,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Stepper(label: String, onClick: () -> Unit) {
    val pressed = remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed.value) 0.88f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "press",
    )
    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(HydroSurface)
            .clickable {
                pressed.value = true
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = HydroPrimaryDeep)
    }
    LaunchedEffect(pressed.value) {
        if (pressed.value) {
            kotlinx.coroutines.delay(120)
            pressed.value = false
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Sectioned cards

@Composable
private fun SoftSection(label: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = HydroInk3,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = HydroSurface),
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
        }
    }
}

@Composable
private fun IconRow(
    emoji: String,
    title: String,
    subtitle: String? = null,
    titleColor: Color = HydroInk,
    clickable: Boolean = false,
    onClick: () -> Unit = {},
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (clickable) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(HydroPrimarySofter),
            contentAlignment = Alignment.Center,
        ) { Text(emoji, fontSize = 16.sp) }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = titleColor)
            if (subtitle != null) {
                Text(subtitle, fontSize = 11.sp, color = HydroInk3)
            }
        }
        trailing()
    }
}

@Composable
private fun LinkRow(emoji: String, title: String, onClick: () -> Unit, danger: Boolean = false) {
    IconRow(
        emoji = emoji,
        title = title,
        titleColor = if (danger) HydroCoral else HydroInk,
        clickable = true,
        onClick = onClick,
    ) {
        Text("→", fontSize = 16.sp, color = if (danger) HydroCoral else HydroInk3)
    }
}

@Composable
private fun SoftSwitch(checked: Boolean, onToggle: () -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = { onToggle() },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = HydroPrimaryDeep,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = HydroLine,
        )
    )
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 58.dp, end = 14.dp)
            .height(1.dp)
            .background(HydroLine)
    )
}

@Composable
private fun CircleIconButton(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = HydroSurface,
        modifier = Modifier.size(44.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(label, fontSize = 18.sp, color = HydroInk)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Theme hue picker — five color swatches; tap to change app primary

@Composable
private fun ThemeHueRow() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(HydroPrimarySofter),
                contentAlignment = Alignment.Center,
            ) { Text("🎨", fontSize = 18.sp) }
            Spacer(Modifier.width(12.dp))
            Text(
                "Primary color",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HydroInk,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HueOptions.forEach { (id, color) ->
                val selected = HydroThemeRuntime.hueId == id
                Box(
                    modifier = Modifier
                        .size(if (selected) 38.dp else 32.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(color)
                        .border(
                            width = if (selected) 3.dp else 0.dp,
                            color = HydroInk,
                            shape = RoundedCornerShape(20.dp),
                        )
                        .clickable { HydroThemeRuntime.applyHue(id) },
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Compatibility shims — kept so the original public helpers still compile if
// referenced elsewhere in the project.

@Composable
fun SettingsCard(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    SoftSection(label = title.uppercase()) { content() }
    if (subtitle != null) Spacer(Modifier.height(0.dp)) // ignored — kept for sig parity
}

@Composable
fun SettingsToggleItem(label: String, isChecked: Boolean, onToggle: () -> Unit) {
    IconRow(emoji = "•", title = label) { SoftSwitch(checked = isChecked, onToggle = onToggle) }
}

@Composable
fun SettingsTextItem(label: String, value: String) {
    IconRow(emoji = "•", title = label) { Text(value, fontSize = 13.sp, color = HydroInk3) }
}

@Composable
fun SettingsButtonItem(label: String, onClick: () -> Unit, isDestructive: Boolean = false) {
    LinkRow(emoji = "•", title = label, onClick = onClick, danger = isDestructive)
}
