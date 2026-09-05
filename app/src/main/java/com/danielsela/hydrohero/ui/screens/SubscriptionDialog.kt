package com.danielsela.hydrohero.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.danielsela.hydrohero.data.UserData
import com.danielsela.hydrohero.ui.theme.*

/**
 * Subscription dialog — soft & friendly, micro-animated.
 * Same callable signature as the original.
 *
 * Visual upgrades:
 *  • Hero crown with gentle floating animation
 *  • Animated "Best value" ribbon on lifetime
 *  • Plan cards: pill-shaped, bordered, big price + period
 *  • Feature rows with mint check chips
 *  • Footer reassurance row (cancel anytime / privacy)
 */
@Composable
fun SubscriptionDialog(
    userData: UserData,
    /** Localized price from Play; falls back to the list price when unavailable. */
    monthlyPrice: String? = null,
    lifetimePrice: String? = null,
    onDismiss: () -> Unit,
    onUpgrade: (String) -> Unit,
    onCancelMonthly: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = HydroSurface),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item { CrownHero(isPremium = userData.isPremium) }
                item { TitleStack(userData) }

                if (!userData.isPremium) {
                    item {
                        PlanCard(
                            title = "Monthly",
                            price = monthlyPrice ?: "$0.99",
                            period = "per month",
                            features = listOf(
                                "All premium avatars",
                                "All premium backgrounds",
                                "All premium effects",
                                "Ad-free experience",
                                "Cancel anytime",
                            ),
                            isRecommended = false,
                            onClick = { onUpgrade("monthly") },
                        )
                    }
                    item {
                        PlanCard(
                            title = "Lifetime",
                            price = lifetimePrice ?: "$9.99",
                            period = "one-time",
                            features = listOf(
                                "Everything in Monthly",
                                "Forever premium access",
                                "No recurring charges",
                                "Priority support",
                                "Founder badge 🏅",
                            ),
                            isRecommended = true,
                            onClick = { onUpgrade("lifetime") },
                        )
                    }
                    item { ReassuranceRow() }
                } else {
                    item { CurrentPlanCard(userData) }
                    if (userData.premiumType == "monthly") {
                        item { CancelMonthlyButton(onCancelMonthly) }
                    }
                }

                item {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, HydroLine),
                    ) {
                        Text(
                            text = if (userData.isPremium) "Close" else "Maybe later",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HydroInk2,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────

@Composable
private fun CrownHero(isPremium: Boolean) {
    val infinite = rememberInfiniteTransition(label = "crownFloat")
    val float by infinite.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "float"
    )
    val scale by infinite.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(110.dp)
            .clip(RoundedCornerShape(55.dp))
            .background(
                Brush.verticalGradient(
                    0f to HydroAccentSun,
                    1f to HydroAccentBlush,
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isPremium) "👑" else "✨",
            fontSize = 56.sp,
            modifier = Modifier
                .scale(scale)
                .offset(y = float.dp),
        )
    }
}

@Composable
private fun TitleStack(userData: UserData) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (userData.isPremium) "You're premium" else "Go premium",
            fontFamily = HydroDisplayFamily,
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = HydroInk,
            letterSpacing = (-0.6).sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (userData.isPremium) {
                when (userData.premiumType) {
                    "lifetime" -> "Lifetime · thanks for being a founder 💛"
                    "monthly" -> "Monthly · welcome to the club"
                    else -> "Welcome to premium"
                }
            } else {
                "Unlock everything. Support a tiny indie app."
            },
            fontSize = 13.sp,
            color = HydroInk2,
        )
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    period: String,
    features: List<String>,
    isRecommended: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (isRecommended) HydroPrimaryDeep else HydroLine
    val borderWidth = if (isRecommended) 2.dp else 1.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isRecommended) HydroPrimarySofter else HydroSurface)
            .border(borderWidth, borderColor, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            if (isRecommended) {
                AnimatedRibbon()
                Spacer(Modifier.height(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HydroInk3,
                        letterSpacing = 1.sp,
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = price,
                            fontFamily = HydroDisplayFamily,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HydroPrimaryDeep,
                            letterSpacing = (-1).sp,
                        )
                        Text(
                            text = "  $period",
                            fontSize = 12.sp,
                            color = HydroInk3,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            features.forEach { f ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(HydroAccentMint),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HydroInk)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(f, fontSize = 13.sp, color = HydroInk)
                }
            }
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecommended) HydroPrimaryDeep else HydroPrimary,
                ),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = if (isRecommended) "Get lifetime" else "Subscribe",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun AnimatedRibbon() {
    val infinite = rememberInfiniteTransition(label = "ribbon")
    val pulse by infinite.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse"
    )
    Box(
        modifier = Modifier
            .scale(pulse)
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.horizontalGradient(
                    0f to HydroAccentSun,
                    1f to HydroAccentBlush,
                )
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            "★ BEST VALUE · SAVE 90%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = HydroInk,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun ReassuranceRow() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ReassuranceItem("🔒", "Secure")
        ReassuranceItem("↻", "Cancel\nanytime")
        ReassuranceItem("💛", "Indie\nbuilt")
    }
}

@Composable
private fun ReassuranceItem(emoji: String, label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(HydroBackground)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 16.sp)
            Spacer(Modifier.width(6.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = HydroInk2, lineHeight = 13.sp)
        }
    }
}

@Composable
private fun CurrentPlanCard(userData: UserData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    0f to HydroPrimarySofter,
                    1f to HydroPrimarySoft,
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "CURRENT PLAN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = HydroInk3,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = when (userData.premiumType) {
                    "lifetime" -> "Lifetime Premium"
                    "monthly" -> "Monthly Premium"
                    else -> "Premium"
                },
                fontFamily = HydroDisplayFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = HydroPrimaryDeep,
            )
        }
    }
}

@Composable
private fun CancelMonthlyButton(onCancel: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, HydroCoral),
        ) {
            Text(
                "Cancel monthly subscription",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HydroCoral,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "You'll return to the free plan (ads + limits).",
            fontSize = 11.sp,
            color = HydroInk3,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}

// Compatibility shim — the original public composable is kept callable.
@Composable
fun SubscriptionOptionCard(
    title: String,
    price: String,
    period: String,
    features: List<String>,
    isRecommended: Boolean,
    onClick: () -> Unit,
) {
    PlanCard(title, price, period, features, isRecommended, onClick)
}
