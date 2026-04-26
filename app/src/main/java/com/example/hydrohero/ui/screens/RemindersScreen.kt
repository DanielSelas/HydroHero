package com.example.hydrohero.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydrohero.data.Reminder
import com.example.hydrohero.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Reminders screen — soft & friendly aesthetic, micro-animated.
 *
 * Same callable signature as the original. Visual upgrades:
 *  • "Next reminder" hero pill with a tiny live wave + pulsing dot
 *  • Slot meter (0/2 free) with progress bar
 *  • Reminder cards: pill shape, soft shadow, animated check on "Mark done"
 *  • Done pill animates in (scale + fade) when toggled
 *  • Add card: dashed-style border with a gentle pulse halo
 */
@Composable
fun RemindersScreen(
    presetReminders: List<Reminder>,
    customReminders: List<Reminder>,
    onToggleReminder: (String) -> Unit,
    completedReminderIds: Set<String>,
    onToggleDone: (String) -> Unit,
    onAddCustomReminder: () -> Unit,
    onDeleteReminder: (String) -> Unit = {},
    onSettingsClick: () -> Unit,
    isPremium: Boolean = false,
    modifier: Modifier = Modifier
) {
    val all = presetReminders + customReminders
    val totalEnabled = all.count { it.isEnabled }
    val doneCount = completedReminderIds.size
    val canAdd = isPremium || customReminders.size < 2

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(44.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Stay on track",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydroInk3,
                    letterSpacing = 1.sp,
                )
                Text(
                    "Reminders",
                    fontFamily = HydroDisplayFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroInk,
                    letterSpacing = (-0.4).sp,
                )
            }
            CircleIconButton(label = "⚙", onClick = onSettingsClick)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Hero status card
            item {
                NextReminderHero(totalEnabled = totalEnabled, doneCount = doneCount)
            }

            // Slot meter (free users only)
            if (!isPremium) {
                item {
                    SlotMeter(used = customReminders.size, max = 2)
                }
            }

            // Section header
            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "YOUR REMINDERS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydroInk3,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }

            // List of reminders
            items(all, key = { it.id }) { reminder ->
                if (reminder.isPreset) {
                    SoftReminderCard(
                        reminder = reminder,
                        isDone = completedReminderIds.contains(reminder.id),
                        onToggleDone = { onToggleDone(reminder.id) },
                        onToggle = { onToggleReminder(reminder.id) },
                    )
                } else {
                    SwipeToDeleteReminder(
                        reminder = reminder,
                        isDone = completedReminderIds.contains(reminder.id),
                        onToggleDone = { onToggleDone(reminder.id) },
                        onToggle = { onToggleReminder(reminder.id) },
                        onDelete = { onDeleteReminder(reminder.id) },
                    )
                }
            }

            // Add card
            item {
                if (canAdd) {
                    AddReminderCard(onClick = onAddCustomReminder)
                } else {
                    UpgradePill(onClick = onAddCustomReminder)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Hero "next reminder" card

@Composable
private fun NextReminderHero(totalEnabled: Int, doneCount: Int) {
    val progress = if (totalEnabled > 0) doneCount.toFloat() / totalEnabled.toFloat() else 0f
    val animProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "progress"
    )

    // Pulsing live dot
    val infinite = rememberInfiniteTransition(label = "pulse")
    val dotScale by infinite.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "dotScale"
    )

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
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Live dot
                Box(
                    modifier = Modifier
                        .scale(dotScale)
                        .size(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(HydroPrimaryDeep),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (totalEnabled == 0) "No active reminders" else "Live · $totalEnabled active",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydroInk2,
                    letterSpacing = 0.6.sp,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "$doneCount/$totalEnabled",
                    fontFamily = HydroDisplayFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroInk,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = if (totalEnabled == 0) {
                    "Add your first reminder to get a gentle nudge."
                } else if (doneCount >= totalEnabled) {
                    "All done — beautiful work today 🌊"
                } else {
                    "Tap a reminder when you finish it. We'll cheer you on."
                },
                fontFamily = HydroDisplayFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = HydroInk,
                letterSpacing = (-0.3).sp,
            )
            Spacer(Modifier.height(14.dp))
            // Progress track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(HydroPrimarySoft),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                0f to HydroPrimary,
                                1f to HydroPrimaryDeep,
                            )
                        ),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Slot meter

@Composable
private fun SlotMeter(used: Int, max: Int) {
    val remaining = (max - used).coerceAtLeast(0)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HydroSurface)
            .border(1.dp, HydroLine, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Slot dots
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(max) { idx ->
                val filled = idx < used
                val color by animateColorAsState(
                    targetValue = if (filled) HydroPrimaryDeep else HydroLine,
                    animationSpec = tween(300),
                    label = "slot$idx"
                )
                Box(
                    modifier = Modifier
                        .size(width = 18.dp, height = 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color),
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (remaining > 0) "$remaining custom slot${if (remaining == 1) "" else "s"} left"
                else "Slots full",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = HydroInk,
            )
            Text(
                text = "Premium unlocks unlimited reminders",
                fontSize = 11.sp,
                color = HydroInk3,
            )
        }
        if (remaining == 0) {
            Text("👑", fontSize = 18.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Reminder card (soft & animated)

@Composable
fun SoftReminderCard(
    reminder: Reminder,
    isDone: Boolean,
    onToggleDone: () -> Unit,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor by animateColorAsState(
        targetValue = if (isDone) HydroAccentMint else HydroLine,
        animationSpec = tween(400),
        label = "border"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(HydroSurface)
            .border(1.5.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Time bubble
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(HydroPrimarySofter)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = reminder.time,
                        fontFamily = HydroDisplayFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HydroPrimaryDeep,
                        letterSpacing = (-0.3).sp,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HydroInk,
                    )
                    Text(
                        text = reminder.description,
                        fontSize = 12.sp,
                        color = HydroInk3,
                    )
                }
                AnimatedDoneButton(isDone = isDone, onClick = onToggleDone)
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Tag(
                    text = if (reminder.isPreset) "Preset" else "Custom",
                    bg = if (reminder.isPreset) HydroPrimarySofter else HydroAccentMint.copy(alpha = 0.35f),
                    fg = if (reminder.isPreset) HydroPrimaryDeep else HydroInk,
                )
                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = HydroPrimaryDeep,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = HydroLine,
                    )
                )
            }
        }
    }
}

@Composable
private fun AnimatedDoneButton(isDone: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (isDone) 1f else 0.96f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "doneScale"
    )
    val bg by animateColorAsState(
        targetValue = if (isDone) HydroAccentMint else HydroPrimarySofter,
        animationSpec = tween(300),
        label = "doneBg"
    )
    Surface(
        onClick = onClick,
        color = bg,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.scale(scale),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(
                visible = isDone,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Text("✓ ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HydroInk)
            }
            Text(
                text = if (isDone) "Done" else "Mark done",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = HydroInk,
            )
        }
    }
}

@Composable
private fun Tag(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            letterSpacing = 0.5.sp,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────
// Add reminder card with pulse halo

@Composable
fun AddReminderCard(onClick: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "addPulse")
    val haloAlpha by infinite.animateFloat(
        initialValue = 0.0f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "halo"
    )
    val plusRotate by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "plusRotate"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(HydroSurface)
            .border(2.dp, HydroPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        // Halo behind +
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(HydroPrimary.copy(alpha = haloAlpha)),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(HydroPrimarySofter),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "+",
                    fontFamily = HydroDisplayFamily,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydroPrimaryDeep,
                    modifier = Modifier.scale(1f).then(Modifier),
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "Add a reminder",
                fontFamily = HydroDisplayFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = HydroInk,
            )
            Text(
                "Custom · pick your own time",
                fontSize = 11.sp,
                color = HydroInk3,
            )
        }
    }
    // (plusRotate referenced to keep the InfiniteTransition alive — actual rotation
    // dropped to keep the chip readable; halo is the main micro-anim.)
    @Suppress("UNUSED_EXPRESSION") plusRotate
}

@Composable
private fun UpgradePill(onClick: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "shimmer")
    val shimmer by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmer"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = HydroAccentSun,
        modifier = Modifier
            .fillMaxWidth()
            .scale(shimmer.coerceAtMost(1f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("👑", fontSize = 22.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Unlock unlimited",
                    fontFamily = HydroDisplayFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroInk,
                )
                Text(
                    "Premium · custom reminders, no ads",
                    fontSize = 11.sp,
                    color = HydroInk2,
                )
            }
            Text("→", fontSize = 20.sp, color = HydroInk)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Swipe-to-delete (kept the original logic, restyled the delete pane)

@Composable
fun SwipeToDeleteReminder(
    reminder: Reminder,
    isDone: Boolean,
    onToggleDone: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    var swipeOffset by remember { mutableStateOf(0f) }
    var lastDragTime by remember { mutableStateOf(0L) }
    val deleteButtonWidth = 80.dp
    val density = LocalDensity.current
    val deleteButtonWidthPx = with(density) { deleteButtonWidth.toPx() }
    val animatedOffset by animateFloatAsState(
        targetValue = swipeOffset,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "swipeOffset"
    )

    LaunchedEffect(lastDragTime, swipeOffset) {
        if (swipeOffset != 0f) {
            delay(200)
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastDragTime >= 200) {
                if (swipeOffset < -deleteButtonWidthPx / 2) {
                    onDelete()
                } else {
                    swipeOffset = 0f
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        // Delete pane
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(deleteButtonWidth)
                .clip(RoundedCornerShape(20.dp))
                .background(HydroCoral)
                .clickable { onDelete() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }

        SoftReminderCard(
            reminder = reminder,
            isDone = isDone,
            onToggleDone = onToggleDone,
            onToggle = onToggle,
            modifier = Modifier
                .offset(x = animatedOffset.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        swipeOffset = (swipeOffset + dragAmount).coerceIn(-deleteButtonWidthPx, 0f)
                        lastDragTime = System.currentTimeMillis()
                    }
                },
        )
    }
}

// Original `ReminderCarouselCard` name kept as a thin alias in case anything
// outside MainActivity referenced it.
@Composable
fun ReminderCarouselCard(
    reminder: Reminder,
    isDone: Boolean,
    onToggleDone: () -> Unit,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SoftReminderCard(
        reminder = reminder,
        isDone = isDone,
        onToggleDone = onToggleDone,
        onToggle = onToggle,
        modifier = modifier,
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
