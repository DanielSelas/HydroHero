package com.example.hydrohero.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydrohero.data.UserData
import com.example.hydrohero.ui.components.SplashMascot
import com.example.hydrohero.ui.components.WaveBottle
import com.example.hydrohero.ui.components.WaveBottleWithMascot
import com.example.hydrohero.ui.theme.*

/**
 * HydroHero — Home screen (soft & friendly redesign).
 *
 * Drop-in replacement for the existing HomeScreen. Signature is unchanged
 * so MainActivity doesn't need edits.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userData: UserData,
    onAddWaterClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSubscriptionClick: () -> Unit,
    onDailyProgressClick: () -> Unit,
    onQuickAdd: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val goalMl = userData.dailyGoal.coerceAtLeast(1)
    val intakeMl = userData.currentIntake.coerceAtLeast(0)
    val level = (intakeMl.toFloat() / goalMl.toFloat()).coerceIn(0f, 1f)
    val remainingGlasses = ((goalMl - intakeMl).coerceAtLeast(0) / 250f).let {
        kotlin.math.ceil(it).toInt()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HydroSurface2)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 120.dp),  // space for global banner ad + bottom nav
    ) {

        // ─── Greeting header ───────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(top = 18.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "GOOD MORNING",
                    style = MaterialTheme.typography.labelSmall,
                    color = HydroInk3,
                )
                Text(
                    text = "Hey, hero 💧",
                    style = MaterialTheme.typography.headlineMedium,
                    color = HydroInk,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            CoinPill(
                value = userData.coins,
                modifier = Modifier.padding(end = 8.dp),
            )
            // Quick dark-mode toggle so the user can flip themes from the home header.
            DarkModeButton()
            Spacer(Modifier.width(4.dp))
            PremiumButton(
                premium = userData.isPremium,
                onClick = onSubscriptionClick,
            )
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onSettingsClick) {
                Text("⚙️", fontSize = 18.sp)
            }
        }

        // ─── Streak strip ──────────────────────────────────────
        Card(
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 4.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = HydroSurface),
            border = BorderStroke(1.dp, HydroLine),
            shape = RoundedCornerShape(18.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(HydroCoralSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🔥", fontSize = 14.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    buildAnnotatedStreakText(userData.streak),
                    style = MaterialTheme.typography.bodyMedium,
                    color = HydroInk2,
                    modifier = Modifier.weight(1f),
                )
                Row {
                    repeat(7) { i ->
                        Box(
                            Modifier
                                .padding(start = 2.dp)
                                .size(width = 6.dp, height = 14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (i < userData.streak) HydroPrimaryStrong
                                    else HydroPrimarySoft
                                )
                        )
                    }
                }
            }
        }

        // ─── Hero: bottle + numbers ────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "TODAY",
                    style = MaterialTheme.typography.labelSmall,
                    color = HydroInk3,
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "%.1f".format(intakeMl / 1000f),
                        style = MaterialTheme.typography.displayLarge,
                        color = HydroInk,
                    )
                    Text(
                        text = " / ${goalMl / 1000}L",
                        fontSize = 20.sp,
                        color = HydroInk3,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (remainingGlasses > 0)
                        "Just $remainingGlasses more glasses to unlock today's treat."
                    else "Goal reached! ✨",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HydroInk2,
                )
            }
            Box(contentAlignment = Alignment.Center) {
                // Selected background painted behind the bottle (only if not "none")
                if (userData.selectedBackground != "none" && userData.selectedBackground.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .size(width = 130.dp, height = 190.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                    ) {
                        BackgroundById(userData.selectedBackground)
                    }
                }
                WaveBottleWithMascot(
                    level = level,
                    mascotId = userData.selectedAvatar,
                    width = 120.dp,
                    height = 180.dp,
                )
                // Selected effect — small flourish in top-right
                if (userData.selectedEffect.isNotBlank() && userData.selectedEffect != "none") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                    ) {
                        EffectById(userData.selectedEffect, size = 36.dp)
                    }
                }
            }
        }

        // ─── Quick log chips ───────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(top = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuickChip("Small",  150, "🥃", Modifier.weight(1f)) { onQuickAdd(150) }
            QuickChip("Glass",  250, "🥛", Modifier.weight(1f)) { onQuickAdd(250) }
            QuickChip("Bottle", 500, "🍶", Modifier.weight(1f)) { onQuickAdd(500) }
        }

        // ─── Big add-water CTA ─────────────────────────────────
        Button(
            onClick = onAddWaterClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 10.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HydroPrimaryStrong),
            shape = RoundedCornerShape(28.dp),
        ) {
            Text(
                "＋  Log a drink",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        }

        // ─── Milestone card ────────────────────────────────────
        SectionHeader("Next milestone", "Complete to earn coins")
        Card(
            modifier = Modifier
                .padding(horizontal = 22.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = HydroSurface),
            border = BorderStroke(1.dp, HydroLine),
            shape = RoundedCornerShape(22.dp),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(HydroPrimarySofter),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🏆", fontSize = 26.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Hit 75% of your goal",
                        style = MaterialTheme.typography.titleMedium,
                        color = HydroInk,
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = (level / 0.75f).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = HydroPrimaryStrong,
                        trackColor = HydroPrimarySoft,
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            "${(level * 100).toInt()}% / 75%",
                            fontSize = 11.sp,
                            color = HydroInk3,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            "+50 coins",
                            fontSize = 11.sp,
                            color = HydroGold,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }

        // ─── Ad banner (only if not premium) ───────────────────
        if (!userData.isPremium) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 22.dp, vertical = 14.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(HydroSurface)
                    .border(
                        width = 1.dp,
                        color = HydroLine,
                        shape = RoundedCornerShape(18.dp),
                    )
                    .padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(HydroPrimarySoft),
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "SPONSORED",
                            style = MaterialTheme.typography.labelSmall,
                            color = HydroInk3,
                        )
                        Text(
                            "Your banner ad slot",
                            style = MaterialTheme.typography.titleMedium,
                            color = HydroInk,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        OutlinedButton(
            onClick = onDailyProgressClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, HydroLine),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = HydroPrimaryStrong),
        ) {
            Text(
                "View daily progress",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ─── Small building blocks ─────────────────────────────────────

@Composable
private fun CoinPill(value: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(HydroGoldSoft)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(HydroGold),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "$",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF5A3A10),
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(
            value.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = HydroInk,
        )
    }
}

@Composable
private fun DarkModeButton() {
    val dark = HydroThemeRuntime.dark
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(HydroSurface)
            .border(width = 1.dp, color = HydroLine, shape = CircleShape)
            .clickable { HydroThemeRuntime.applyDark(!dark) },
        contentAlignment = Alignment.Center,
    ) {
        Text(if (dark) "☀️" else "🌙", fontSize = 16.sp)
    }
}

@Composable
private fun PremiumButton(premium: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(if (premium) HydroGoldSoft else HydroSurface)
            .border(
                width = 1.dp,
                color = if (premium) HydroGold else HydroLine,
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(if (premium) "👑" else "✨", fontSize = 16.sp)
    }
}

@Composable
private fun QuickChip(
    label: String,
    ml: Int,
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(HydroSurface)
            .border(1.dp, HydroLine, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(icon, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            fontSize = 11.sp,
            color = HydroInk3,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "${ml}ml",
            fontSize = 13.sp,
            color = HydroInk,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String?) {
    Column(Modifier.padding(horizontal = 22.dp, vertical = 14.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = HydroInk,
        )
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = HydroInk3,
            )
        }
    }
}

private fun buildAnnotatedStreakText(streak: Int) =
    androidx.compose.ui.text.buildAnnotatedString {
        pushStyle(androidx.compose.ui.text.SpanStyle(
            color = HydroInk,
            fontWeight = FontWeight.SemiBold,
        ))
        append("$streak-day streak")
        pop()
        append(" — keep it up!")
    }

// Keep your existing `getBackgroundColors(...)` helper — or replace with
// Color.kt's HydroBg* lists let you drop emoji/backgrounds in later.
