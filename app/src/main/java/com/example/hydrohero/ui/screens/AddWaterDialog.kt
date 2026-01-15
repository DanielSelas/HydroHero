package com.example.hydrohero.ui.screens

import android.os.Build
import android.view.HapticFeedbackConstants
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.hydrohero.ui.theme.*

@Composable
fun AddWaterDialog(
    onDismiss: () -> Unit,
    onAddWater: (Int) -> Unit
) {
    val view = LocalView.current
    var customAmount by remember { mutableStateOf("") }
    
    fun performHapticFeedback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            @Suppress("DEPRECATION")
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }
    
    fun addWaterWithFeedback(amount: Int) {
        performHapticFeedback()
        onAddWater(amount)
        onDismiss()
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("←", fontSize = 18.sp, color = PrimaryBlue)
                    }
                    Text(
                        text = "Add Water",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select amount:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Water Options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    WaterOptionButton(
                        icon = "🥤",
                        label = "Small",
                        amount = "250 ml",
                        onClick = {
                            addWaterWithFeedback(250)
                        }
                    )
                    WaterOptionButton(
                        icon = "🥤",
                        label = "Medium",
                        amount = "500 ml",
                        onClick = {
                            addWaterWithFeedback(500)
                        },
                        isMedium = true
                    )
                    WaterOptionButton(
                        icon = "🥤",
                        label = "Large",
                        amount = "750 ml",
                        onClick = {
                            addWaterWithFeedback(750)
                        },
                        isLarge = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Custom Amount
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Custom Amount (ml)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        val amountInt = customAmount.toIntOrNull()
                        val isValidAmount = amountInt != null && amountInt > 0

                        OutlinedTextField(
                            value = customAmount,
                            onValueChange = { customAmount = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Enter amount",
                                    color = TextLight,
                                    fontSize = 16.sp
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = BorderLight,
                                focusedTextColor = Color(0xFF000000),
                                unfocusedTextColor = Color(0xFF000000),
                                focusedPlaceholderColor = TextLight,
                                unfocusedPlaceholderColor = TextLight,
                                cursorColor = PrimaryBlue
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 18.sp,
                                color = Color(0xFF000000)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Always-visible button (disabled until amount is valid)
                        Button(
                            onClick = {
                                if (isValidAmount) {
                                    addWaterWithFeedback(amountInt!!)
                                    customAmount = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = isValidAmount,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = BackgroundWhite,
                                disabledContainerColor = PrimaryBlue.copy(alpha = 0.35f),
                                disabledContentColor = BackgroundWhite.copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Add Custom Amount",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = BackgroundWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WaterOptionButton(
    icon: String,
    label: String,
    amount: String,
    onClick: () -> Unit,
    isMedium: Boolean = false,
    isLarge: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, BorderLight, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = icon,
                fontSize = if (isLarge) 48.sp else if (isMedium) 42.sp else 36.sp
            )
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            Text(
                text = amount,
                fontSize = 14.sp,
                color = TextLight
            )
        }
    }
}
