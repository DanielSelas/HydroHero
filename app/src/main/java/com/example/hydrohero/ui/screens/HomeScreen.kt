package com.example.hydrohero.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydrohero.data.UserData
import com.example.hydrohero.ui.theme.*

@Composable
fun HomeScreen(
    userData: UserData,
    onAddWaterClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSubscriptionClick: () -> Unit,
    onDailyProgressClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite)
                .padding(16.dp, 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💧",
                fontSize = 20.sp
            )
            Text(
                text = "Home",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Subscription status/upgrade button
                Surface(
                    onClick = onSubscriptionClick,
                    shape = RoundedCornerShape(20.dp),
                    color = if (userData.isPremium) Color(0xFFFFD700) else BackgroundWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (userData.isPremium) Color(0xFFFFD700) else BorderLight)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (userData.isPremium) "👑" else "⭐",
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (userData.isPremium) {
                                when (userData.premiumType) {
                                    "lifetime" -> "Premium"
                                    else -> "Premium"
                                }
                            } else "Upgrade",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (userData.isPremium) TextDark else PrimaryBlue
                        )
                    }
                }
                IconButton(onClick = onSettingsClick) {
                    Text("⚙️", fontSize = 20.sp)
                }
            }
        }
        
        // Streak Display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥",
                    fontSize = 24.sp
                )
                Column {
                    Text(
                        text = "${userData.streak} Day Streak",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = if (userData.streak > 0) "Keep it up!" else "Start your streak today!",
                        fontSize = 12.sp,
                        color = TextLight
                    )
                }
            }
            Text(
                text = "💰 ${userData.coins}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                // Extra padding so the bottom button never sits behind the global ad + bottom nav
                .padding(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Journey Section
            Text(
                text = "Your Hydration Journey",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Journey Card - with selected background
            val backgroundColors = getBackgroundColors(userData.selectedBackground)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = backgroundColors
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Character - shows selected avatar
                    val progressPercentage = if (userData.dailyGoal > 0) {
                        (userData.currentIntake.toFloat() / userData.dailyGoal.toFloat() * 100f).coerceIn(0f, 100f)
                    } else 0f
                    
                    val message = when {
                        progressPercentage >= 100 -> "Goal Achieved!"
                        progressPercentage >= 75 -> "Almost there!"
                        progressPercentage >= 50 -> "You're doing great!"
                        progressPercentage >= 25 -> "Keep going!"
                        else -> "Let's start!"
                    }

                          val nextMilestoneHint = when {
                              progressPercentage < 25f -> "Next: 25% → Start Your Day ✅"
                              progressPercentage < 50f -> "Next: 50% → Midday Check ✅"
                              progressPercentage < 75f -> "Next: 75% → Evening Wind-down ✅"
                              progressPercentage < 100f -> "Next: 100% → Daily Goal 🎉"
                              else -> "All milestones completed today ✅"
                          }
                    
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(60.dp))
                            .background(LightBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(userData.selectedAvatar, fontSize = 80.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (progressPercentage >= 100) PrimaryBlue else AccentGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            message,
                            color = BackgroundWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                          Spacer(modifier = Modifier.height(12.dp))

                          Text(
                              text = nextMilestoneHint,
                              fontSize = 12.sp,
                              color = TextLight
                          )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Circle
            val progress = if (userData.dailyGoal > 0) {
                (userData.currentIntake.toFloat() / userData.dailyGoal.toFloat()).coerceIn(0f, 1f)
            } else 0f
            
            // Calculate glasses based on daily goal (assuming 250ml per glass)
            val glassesPerGoal = (userData.dailyGoal / 250f).toInt().coerceAtLeast(1)
            val glassesProgress = if (userData.dailyGoal > 0) {
                (userData.currentIntake / 250f).toInt().coerceAtMost(glassesPerGoal)
            } else 0

            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(120.dp),
                    color = PrimaryBlue,
                    strokeWidth = 12.dp,
                    trackColor = BorderLight
                )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$glassesProgress/$glassesPerGoal",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Glasses",
                            fontSize = 12.sp,
                            color = TextLight
                        )
                    }
            }

            // Celebration layer (persistent badge after confetti ends)
            if (progress >= 1f) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = AccentGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = "Goal Complete ✅",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        color = AccentGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Add Water Button
            Button(
                onClick = onAddWaterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "+ Add Water",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BackgroundWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onDailyProgressClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
            ) {
                Text(
                    text = "View Daily Progress",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // (Banner ad is shown globally in MainActivity above the bottom nav)
        }
    }
}

@Composable
fun getBackgroundColors(backgroundId: String): List<Color> {
    return when (backgroundId) {
        "none" -> listOf(
            Color(0xFFFFFFFF),
            Color(0xFFFFFFFF)
        )
        "sea" -> listOf(
            Color(0xFFE0F2FE), // Light blue
            Color(0xFFBAE6FD)  // Lighter blue
        )
        "stars" -> listOf(
            Color(0xFF1E1B4B), // Dark blue
            Color(0xFF312E81)  // Darker blue
        )
        "rainbow" -> listOf(
            Color(0xFFFFE5E5), // Light pink
            Color(0xFFFFF4E5), // Light orange
            Color(0xFFFFFBE5), // Light yellow
            Color(0xFFE5FFE5), // Light green
            Color(0xFFE5F5FF)  // Light blue
        )
        "sunset" -> listOf(
            Color(0xFFFF6B6B), // Coral
            Color(0xFFFFA07A)  // Light salmon
        )
        "forest" -> listOf(
            Color(0xFFD4E4C5), // Light green
            Color(0xFFB8D4A0)  // Medium green
        )
        "beach" -> listOf(
            Color(0xFFFFF5E6), // Light sand
            Color(0xFFFFE5CC)  // Light tan
        )
        else -> listOf(
            Color(0xFFF0F9FF), // Default light blue
            Color(0xFFE0F2FE)  // Default lighter blue
        )
    }
}
