package com.danielsela.hydrohero.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danielsela.hydrohero.data.WaterEntry
import com.danielsela.hydrohero.ui.theme.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.sin

/**
 * Daily progress — re-styled in the soft & friendly aesthetic.
 *
 * Same callable signature as the original so MainActivity wiring stays identical.
 * Adds: a circular wave-ring with the % filled, three quick stats, and a
 * timeline-style log with relative times.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyProgressScreen(
    dailyGoal: Int,
    currentIntake: Int,
    entries: List<WaterEntry>,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (dailyGoal > 0)
        (currentIntake.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f) else 0f
    val timeFmt = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
    val sorted = remember(entries) { entries.sortedByDescending { it.timestamp } }
    val drinks = entries.size
    val avgMl = if (drinks > 0) currentIntake / drinks else 0
    val lastDrink = sorted.firstOrNull()?.let { timeFmt.format(Instant.ofEpochMilli(it.timestamp)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HydroBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(label = "←", onClick = onBackClick)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Today",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydroInk3,
                    letterSpacing = 1.sp,
                )
                Text(
                    "Progress",
                    fontFamily = HydroDisplayFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroInk,
                    letterSpacing = (-0.4).sp,
                )
            }
            CircleIconButton(label = "⚙", onClick = onSettingsClick)
        }

        Spacer(Modifier.height(8.dp))

        // Hero ring card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = HydroSurface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WaveRing(progress = progress, size = 220.dp) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(
                            "${(progress * 100).toInt()}%",
                            fontFamily = HydroDisplayFamily,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HydroInk,
                            letterSpacing = (-1.5).sp,
                        )
                        Text(
                            "$currentIntake / $dailyGoal ml",
                            fontSize = 13.sp,
                            color = HydroInk2,
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatPill("Drinks", "$drinks", Modifier.weight(1f))
                    StatPill("Avg", "${avgMl}ml", Modifier.weight(1f))
                    StatPill("Last", lastDrink ?: "—", Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Timeline
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "TIMELINE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = HydroInk3,
                letterSpacing = 1.sp,
            )
            Text(
                "${entries.size} ${if (entries.size == 1) "entry" else "entries"}",
                fontSize = 11.sp,
                color = HydroInk3,
            )
        }
        Spacer(Modifier.height(10.dp))

        if (entries.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = HydroSurface),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("💧", fontSize = 36.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No drinks logged yet",
                        fontFamily = HydroDisplayFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HydroInk,
                    )
                    Text(
                        "Tap + on Home to start your day",
                        fontSize = 13.sp,
                        color = HydroInk3,
                    )
                }
            }
        } else {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                sorted.forEachIndexed { idx, entry ->
                    TimelineRow(
                        entry = entry,
                        timeFmt = timeFmt,
                        isLast = idx == sorted.lastIndex,
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────
// Wave ring

@Composable
private fun WaveRing(
    progress: Float,
    size: androidx.compose.ui.unit.Dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val infinite = rememberInfiniteTransition(label = "wave")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing)
        ),
        label = "phase"
    )

    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = this.size.minDimension / 2f
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            // Outer track
            drawCircle(
                color = HydroPrimarySofter,
                radius = r,
                style = Stroke(width = 18f),
            )
            // Soft inner fill behind wave
            drawCircle(
                color = HydroPrimarySofter.copy(alpha = 0.45f),
                radius = r - 14f,
            )

            // Clip to inner circle and draw wave
            val innerR = r - 14f
            val waveLevelY = cy + innerR - (innerR * 2f * progress)
            val amp = 7f
            val wavelen = innerR * 2f

            val path = Path().apply {
                moveTo(cx - innerR, cy + innerR)
                var x = cx - innerR
                while (x <= cx + innerR) {
                    val y = waveLevelY + sin((x / wavelen) * Math.PI.toFloat() * 2f + phase) * amp
                    lineTo(x, y)
                    x += 4f
                }
                lineTo(cx + innerR, cy + innerR)
                close()
            }

            // Clip path to the inner circle
            clipPath(circlePath(cx, cy, innerR)) {
                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        0f to HydroPrimary,
                        1f to HydroPrimaryDeep,
                        startY = waveLevelY - amp,
                        endY = cy + innerR,
                    )
                )
                // Lighter highlight wave
                val highlight = Path().apply {
                    moveTo(cx - innerR, cy + innerR)
                    var x = cx - innerR
                    while (x <= cx + innerR) {
                        val y = waveLevelY + sin((x / wavelen) * Math.PI.toFloat() * 2f + phase + 1.4f) * (amp - 2f) - 2f
                        lineTo(x, y)
                        x += 4f
                    }
                    lineTo(cx + innerR, cy + innerR)
                    close()
                }
                drawPath(highlight, color = Color.White.copy(alpha = 0.18f))
            }

            // Outer ring stroke (in front)
            drawCircle(
                color = HydroPrimaryDeep,
                radius = r,
                style = Stroke(width = 4f),
            )
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}

private fun circlePath(cx: Float, cy: Float, r: Float): Path = Path().apply {
    addOval(androidx.compose.ui.geometry.Rect(Offset(cx - r, cy - r), androidx.compose.ui.geometry.Size(r * 2, r * 2)))
}

// ─────────────────────────────────────────────────────────────────────
// Stat pill

@Composable
private fun StatPill(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(HydroSurface3)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            value,
            fontFamily = HydroDisplayFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = HydroInk,
        )
        Text(
            label,
            fontSize = 11.sp,
            color = HydroInk3,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────
// Timeline row

@Composable
private fun TimelineRow(
    entry: WaterEntry,
    timeFmt: DateTimeFormatter,
    isLast: Boolean,
) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Rail with dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp).fillMaxHeight(),
        ) {
            Spacer(Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(HydroPrimarySofter)
                    .border(2.dp, HydroPrimaryDeep, RoundedCornerShape(7.dp))
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(HydroLine)
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = HydroSurface),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${entry.amount} ml",
                        fontFamily = HydroDisplayFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HydroInk,
                    )
                    Text(
                        text = timeFmt.format(Instant.ofEpochMilli(entry.timestamp)),
                        fontSize = 12.sp,
                        color = HydroInk3,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(HydroPrimarySofter),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("💧", fontSize = 18.sp)
                }
            }
        }
    }
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
