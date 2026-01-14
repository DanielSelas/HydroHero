package com.example.hydrohero.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydrohero.data.Reminder
import com.example.hydrohero.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RemindersScreen(
    presetReminders: List<Reminder>,
    customReminders: List<Reminder>,
    onToggleReminder: (String) -> Unit,
    onAddCustomReminder: () -> Unit,
    onDeleteReminder: (String) -> Unit = {},
    isPremium: Boolean = false,
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
                text = "Reminders",
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
                .padding(20.dp)
        ) {
            // All Reminders Section
            Text(
                text = "Your Reminders",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Scroll to see all your hydration reminders.",
                fontSize = 14.sp,
                color = TextLight,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Show reminder limit info for free users
            if (!isPremium) {
                val remainingSlots = (2 - customReminders.size).coerceAtLeast(0)
                Surface(
                    color = if (remainingSlots > 0) LightBlue.copy(alpha = 0.3f) else Color(0xFFFFE5E5),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (remainingSlots > 0) {
                                    "$remainingSlots custom reminder${if (remainingSlots == 1) "" else "s"} remaining"
                                } else {
                                    "Custom reminder limit reached"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Text(
                                text = "Upgrade to Premium for unlimited reminders",
                                fontSize = 12.sp,
                                color = TextLight
                            )
                        }
                        if (remainingSlots == 0) {
                            Text(
                                text = "👑",
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Vertical list of all reminders (preset + custom)
            val allReminders = presetReminders + customReminders
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allReminders) { reminder ->
                    if (reminder.isPreset) {
                        // Preset reminders - no swipe to delete
                        ReminderCarouselCard(
                            reminder = reminder,
                            onToggle = { onToggleReminder(reminder.id) }
                        )
                    } else {
                        // Custom reminders - swipe to delete
                        SwipeToDeleteReminder(
                            reminder = reminder,
                            onToggle = { onToggleReminder(reminder.id) },
                            onDelete = { onDeleteReminder(reminder.id) }
                        )
                    }
                }
                
                      // Add Custom Reminder Card at the end (only if limit not reached)
                      if (isPremium || customReminders.size < 2) {
                          item {
                              AddReminderCard(
                                  onClick = onAddCustomReminder
                              )
                          }
                      }
            }

            Spacer(modifier = Modifier.height(24.dp))

                  // Add Custom Reminder Button (only if limit not reached)
                  if (isPremium || customReminders.size < 2) {
                      Button(
                          onClick = onAddCustomReminder,
                          modifier = Modifier.fillMaxWidth(),
                          colors = ButtonDefaults.buttonColors(
                              containerColor = PrimaryBlue
                          ),
                          shape = RoundedCornerShape(12.dp)
                      ) {
                          Text(
                              text = "+ Add Your Own Reminder",
                              fontSize = 16.sp,
                              fontWeight = FontWeight.SemiBold,
                              modifier = Modifier.padding(vertical = 4.dp)
                          )
                      }
                  } else {
                      // Show upgrade button when limit reached
                      Button(
                          onClick = onAddCustomReminder, // This will show upgrade prompt
                          modifier = Modifier.fillMaxWidth(),
                          colors = ButtonDefaults.buttonColors(
                              containerColor = Color(0xFFFFD700)
                          ),
                          shape = RoundedCornerShape(12.dp)
                      ) {
                          Row(
                              horizontalArrangement = Arrangement.spacedBy(8.dp),
                              verticalAlignment = Alignment.CenterVertically
                          ) {
                              Text("👑", fontSize = 18.sp)
                              Text(
                                  text = "Upgrade to Premium for Unlimited Reminders",
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
fun ReminderCarouselCard(
    reminder: Reminder,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = reminder.description,
                        fontSize = 14.sp,
                        color = TextLight,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = reminder.time,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (reminder.isPreset) "Preset" else "Custom",
                    fontSize = 12.sp,
                    color = TextLight,
                    modifier = Modifier
                        .background(
                            if (reminder.isPreset) LightBlue else AccentGreen.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Switch(
                    checked = reminder.isEnabled,
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
    }
}

@Composable
fun AddReminderCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundWhite
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "+",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add Your Own",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "Custom Reminder",
                fontSize = 14.sp,
                color = TextLight
            )
        }
    }
}

@Composable
fun SwipeToDeleteReminder(
    reminder: Reminder,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var swipeOffset by remember { mutableStateOf(0f) }
    var lastDragTime by remember { mutableStateOf(0L) }
    val deleteButtonWidth = 80.dp
    val density = LocalDensity.current
    val deleteButtonWidthPx = with(density) { deleteButtonWidth.toPx() }
    val animatedOffset by animateFloatAsState(
        targetValue = swipeOffset,
        animationSpec = tween(durationMillis = 300),
        label = "swipeOffset"
    )
    
    // Check if drag ended (no updates for 200ms)
    LaunchedEffect(lastDragTime, swipeOffset) {
        if (swipeOffset != 0f) {
            delay(200)
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastDragTime >= 200) {
                // Drag ended - check if should delete
                if (swipeOffset < -deleteButtonWidthPx / 2) {
                    onDelete()
                } else {
                    swipeOffset = 0f
                }
            }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Delete button background - clickable
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(deleteButtonWidth)
                .background(Color(0xFFFF6B6B), RoundedCornerShape(16.dp))
                .clickable { onDelete() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Reminder card (swipeable)
        ReminderCarouselCard(
            reminder = reminder,
            onToggle = onToggle,
            modifier = Modifier
                .offset(x = animatedOffset.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        swipeOffset = (swipeOffset + dragAmount).coerceIn(-deleteButtonWidthPx, 0f)
                        lastDragTime = System.currentTimeMillis()
                    }
                }
        )
    }
}
