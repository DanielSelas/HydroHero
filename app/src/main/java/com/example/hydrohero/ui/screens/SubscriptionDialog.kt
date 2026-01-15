package com.example.hydrohero.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.hydrohero.data.UserData
import com.example.hydrohero.ui.theme.*

@Composable
fun SubscriptionDialog(
    userData: UserData,
    onDismiss: () -> Unit,
    onUpgrade: (String) -> Unit, // "monthly" or "lifetime"
    onCancelMonthly: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = if (userData.isPremium) "👑 Premium Member" else "⭐ Upgrade to Premium",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = if (userData.isPremium) {
                            when (userData.premiumType) {
                                "lifetime" -> "You have Lifetime Premium!"
                                "monthly" -> "You have Monthly Premium!"
                                else -> "You are Premium!"
                            }
                        } else {
                            "Unlock all premium features"
                        },
                        fontSize = 16.sp,
                        color = TextLight
                    )
                }

                if (!userData.isPremium) {
                    item {
                        SubscriptionOptionCard(
                            title = "Monthly Premium",
                            price = "$0.99",
                            period = "per month",
                            features = listOf(
                                "✨ All premium avatars",
                                "🎨 All premium backgrounds",
                                "✨ All premium effects",
                                "🚫 Ad-free experience",
                                "🔄 Cancel anytime"
                            ),
                            isRecommended = false,
                            onClick = { onUpgrade("monthly") }
                        )
                    }

                    item {
                        SubscriptionOptionCard(
                            title = "Lifetime Premium",
                            price = "$9.99",
                            period = "one-time",
                            features = listOf(
                                "✨ All premium avatars",
                                "🎨 All premium backgrounds",
                                "✨ All premium effects",
                                "🚫 Ad-free experience",
                                "👑 Forever premium access"
                            ),
                            isRecommended = true,
                            onClick = { onUpgrade("lifetime") }
                        )
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = LightBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Current Plan",
                                    fontSize = 14.sp,
                                    color = TextLight,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = when (userData.premiumType) {
                                        "lifetime" -> "Lifetime Premium"
                                        "monthly" -> "Monthly Premium"
                                        else -> "Premium"
                                    },
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                            }
                        }
                    }

                    // Cancel monthly subscription (prototype)
                    if (userData.premiumType == "monthly") {
                        item {
                            Button(
                                onClick = onCancelMonthly,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Cancel Monthly Subscription",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BackgroundWhite,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "You’ll return to the free plan (ads + limits).",
                                fontSize = 12.sp,
                                color = TextLight,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (userData.isPremium) "Close" else "Maybe Later",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionOptionCard(
    title: String,
    price: String,
    period: String,
    features: List<String>,
    isRecommended: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isRecommended) 2.dp else 1.dp,
                color = if (isRecommended) PrimaryBlue else BorderLight,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Recommended badge
            if (isRecommended) {
                Surface(
                    color = PrimaryBlue,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "⭐ BEST VALUE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = BackgroundWhite,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = price,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = period,
                            fontSize = 12.sp,
                            color = TextLight,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Features list
            features.forEach { feature ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feature,
                        fontSize = 14.sp,
                        color = TextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subscribe button
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecommended) PrimaryBlue else AccentGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Subscribe",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
