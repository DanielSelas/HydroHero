package com.danielsela.hydrohero.ui.screens

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.danielsela.hydrohero.ui.theme.*

/**
 * Re-styled Add Water dialog — soft & friendly aesthetic.
 *
 * Same callable signature as the original (onDismiss / onAddWater) so MainActivity
 * doesn't change. Internally we now show a big amount display, a stepper +
 * slider-like row, six preset chips, and a primary "Add" button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWaterDialog(
    onDismiss: () -> Unit,
    onAddWater: (Int) -> Unit
) {
    val view = LocalView.current
    fun haptic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            @Suppress("DEPRECATION")
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    var amount by rememberSaveable { mutableIntStateOf(250) }
    var customText by rememberSaveable { mutableStateOf("") }
    val customInt = customText.toIntOrNull()
    val effectiveAmount = customInt ?: amount

    val presets = listOf(
        Preset(150, "Small glass", "🥃"),
        Preset(250, "Glass", "🥛"),
        Preset(350, "Mug", "☕"),
        Preset(500, "Bottle", "🍶"),
        Preset(750, "Large bottle", "💦"),
        Preset(1000, "1 liter", "🚰"),
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = HydroSurface),
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(horizontal = 22.dp, vertical = 18.dp),
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(HydroLine)
                    )
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Log a drink",
                        fontFamily = HydroDisplayFamily,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HydroInk,
                        letterSpacing = (-0.4).sp,
                    )
                    Text(
                        text = "How much did you sip?",
                        fontSize = 13.sp,
                        color = HydroInk3,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(Modifier.height(16.dp))

                    // Big amount display card
                    AmountDisplay(
                        amount = effectiveAmount,
                        onMinus = {
                            customText = ""
                            amount = (amount - 50).coerceAtLeast(50)
                            haptic()
                        },
                        onPlus = {
                            customText = ""
                            amount = (amount + 50).coerceAtMost(2000)
                            haptic()
                        },
                    )

                    Spacer(Modifier.height(20.dp))

                    // Quick picks header
                    Text(
                        text = "QUICK PICKS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HydroInk3,
                        letterSpacing = 1.sp,
                    )
                    Spacer(Modifier.height(10.dp))

                    // 3x2 preset grid (manual; avoids LazyVerticalGrid for simplicity)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.chunked(3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                row.forEach { p ->
                                    PresetChip(
                                        preset = p,
                                        active = customInt == null && amount == p.ml,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            customText = ""
                                            amount = p.ml
                                            haptic()
                                        }
                                    )
                                }
                                // pad row if fewer than 3
                                repeat(3 - row.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // Custom amount field
                    Text(
                        text = "OR ENTER A CUSTOM AMOUNT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HydroInk3,
                        letterSpacing = 1.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customText,
                        onValueChange = { txt: String ->
                            val digits: String = txt.filter { ch -> ch.isDigit() }
                            customText = if (digits.length > 5) digits.substring(0, 5) else digits
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("ml", color = HydroInk3) },
                        suffix = { Text("ml", color = HydroInk3) },
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HydroPrimaryStrong,
                            unfocusedBorderColor = HydroLine,
                            cursorColor = HydroPrimaryStrong,
                            focusedTextColor = HydroInk,
                            unfocusedTextColor = HydroInk,
                        ),
                    )

                    Spacer(Modifier.height(20.dp))

                    // Action row: Cancel + Add
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, HydroLine),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = HydroInk2),
                        ) {
                            Text("Cancel", fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = {
                                if (effectiveAmount in 1..5000) {
                                    haptic()
                                    onAddWater(effectiveAmount)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier
                                .weight(2f)
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HydroPrimaryStrong,
                                contentColor = Color.White,
                            ),
                        ) {
                            Text(
                                "Add ${effectiveAmount}ml",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

private data class Preset(val ml: Int, val label: String, val icon: String)

@Composable
private fun AmountDisplay(
    amount: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    val glasses = amount / 250f
    val glassesText = remember(amount) {
        val rounded = (glasses * 10).toInt() / 10f
        if (amount == 250) "about 1 glass"
        else "about ${"%.1f".format(rounded)} glasses"
    }

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
            .padding(horizontal = 20.dp, vertical = 22.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$amount",
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
                text = glassesText,
                fontSize = 12.sp,
                color = HydroInk2,
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StepperButton("−", onMinus)
                // Visual "track" — purely cosmetic, the real adjuster is the +/- buttons
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(HydroPrimarySoft),
                ) {
                    val pct = (amount / 1500f).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(pct)
                            .clip(RoundedCornerShape(3.dp))
                            .background(HydroPrimaryDeep),
                    )
                }
                StepperButton("+", onPlus)
            }
        }
    }
}

@Composable
private fun StepperButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(HydroSurface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = HydroPrimaryDeep,
        )
    }
}

@Composable
private fun PresetChip(
    preset: Preset,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bg = if (active) HydroPrimaryDeep else HydroSurface3
    val fg = if (active) Color.White else HydroInk
    val border = if (active) HydroPrimaryDeep else HydroLine

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(preset.icon, fontSize = 22.sp)
        Text(
            text = "${preset.ml}ml",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
        )
        Text(
            text = preset.label,
            fontSize = 10.sp,
            color = if (active) Color.White.copy(alpha = 0.8f) else HydroInk3,
        )
    }
}
