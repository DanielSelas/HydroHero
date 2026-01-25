package com.example.hydrohero.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydrohero.data.UserData
import com.example.hydrohero.ui.theme.*

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
    modifier: Modifier = Modifier
) {
    var showContact by remember { mutableStateOf(false) }

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
            TextButton(onClick = onBackClick) {
                Text("←", fontSize = 20.sp, color = PrimaryBlue)
            }
            Text(
                text = "💧",
                fontSize = 20.sp
            )
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.width(40.dp))
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .padding(bottom = 140.dp)
        ) {
            // Daily Goal Section
            SettingsCard(
                title = "Daily Water Goal",
                subtitle = "Set your daily hydration target"
            ) {
                var goalText by remember { mutableStateOf(userData.dailyGoal.toString()) }
                var isEditing by remember { mutableStateOf(false) }
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = goalText,
                            onValueChange = { 
                                if (it.all { char -> char.isDigit() }) {
                                    goalText = it
                                }
                            },
                            label = { Text("Daily Goal (ml)") },
                            suffix = { Text("ml", color = TextLight) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF000000),
                                unfocusedTextColor = Color(0xFF000000),
                                cursorColor = PrimaryBlue
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 16.sp,
                                color = Color(0xFF000000)
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val newGoal = goalText.toIntOrNull() ?: userData.dailyGoal
                                    if (newGoal > 0) {
                                        onGoalChange(newGoal)
                                    }
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = {
                                    goalText = userData.dailyGoal.toString()
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel", fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${userData.dailyGoal} ml",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "${(userData.dailyGoal / 250f).toInt()} Glasses",
                                    fontSize = 14.sp,
                                    color = TextLight
                                )
                            }
                            Button(
                                onClick = { isEditing = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Edit",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notification Settings
            SettingsCard(
                title = "Notification Settings",
                subtitle = "Manage how and when you receive reminders"
            ) {
                SettingsToggleItem(
                    label = "Enable Notifications",
                    isChecked = notificationsEnabled,
                    onToggle = onToggleNotifications
                )
                SettingsToggleItem(
                    label = "Sound Alerts",
                    isChecked = soundEnabled,
                    onToggle = onToggleSound
                )
                SettingsToggleItem(
                    label = "Vibration",
                    isChecked = vibrationEnabled,
                    onToggle = onToggleVibration
                )
                SettingsToggleItem(
                    label = "Quiet Hours",
                    isChecked = quietHoursEnabled,
                    onToggle = onToggleQuietHours
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Preferences
            SettingsCard(
                title = "Display Preferences",
                subtitle = "Customize how information is shown"
            ) {
                SettingsTextItem("Units", "Glasses")
                SettingsTextItem("Theme", "Light")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account Section
            SettingsCard(
                title = "Account",
                subtitle = "Manage your account and data"
            ) {
                SettingsToggleItem(
                    label = "Sync Data",
                    isChecked = syncEnabled,
                    onToggle = onToggleSync
                )
                SettingsButtonItem(
                    label = "Export Data",
                    onClick = onExportData,
                    isDestructive = false
                )
                SettingsButtonItem(
                    label = "Reset Progress",
                    onClick = onResetProgress,
                    isDestructive = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About Section
            SettingsCard(
                title = "About Hydro Hero"
            ) {
                Text(
                    text = "v1.0.0",
                    fontSize = 14.sp,
                    color = TextLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Stay hydrated, stay heroic!",
                    fontSize = 14.sp,
                    color = TextLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TextButton(
                    onClick = onRateApp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            "Rate Hydro Hero",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                TextButton(
                    onClick = onShareApp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            "Share Hydro Hero",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                TextButton(
                    onClick = onOpenPrivacyPolicy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            "Privacy Policy",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                TextButton(
                    onClick = onOpenTermsOfService,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            "Terms of Service",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                TextButton(
                    onClick = { showContact = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            "Contact Support",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                // Crashlytics Test Button (for presentation demo)
                TextButton(
                    onClick = { throw RuntimeException("Test Crash") }, // Force a crash
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            "Test Crash (Demo)",
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // (Banner ad is shown globally in MainActivity above the bottom nav)
        }
    }

    if (showContact) {
        AlertDialog(
            onDismissRequest = { showContact = false },
            title = { Text("Contact Support") },
            text = {
                Text(
                    "Creator: Daniel Sela\n" +
                        "Email: danielsela96@#gmail.com\n\n" +
                        "For help or feedback, send a message with:\n" +
                        "- Your device/emulator Android version\n" +
                        "- What you tried to do\n" +
                        "- A screenshot (if possible)"
                )
            },
            confirmButton = {
                Button(onClick = { showContact = false }) { Text("Close") }
            }
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = TextLight,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            content()
        }
    }
}

@Composable
fun SettingsToggleItem(
    label: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextDark
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = BackgroundWhite,
                checkedTrackColor = PrimaryBlue,
                uncheckedThumbColor = BackgroundWhite,
                uncheckedTrackColor = BorderLight
            )
        )
    }
}

@Composable
fun SettingsTextItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextDark
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = TextLight
        )
    }
}

@Composable
fun SettingsButtonItem(
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, 0.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDestructive) Color(0xFFEF4444) else PrimaryBlue
        )
    }
}
