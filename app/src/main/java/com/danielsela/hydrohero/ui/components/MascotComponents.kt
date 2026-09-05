package com.danielsela.hydrohero.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import com.danielsela.hydrohero.ui.theme.*
import kotlin.math.sin
import androidx.compose.ui.graphics.drawscope.clipPath

/**
 * Smiling droplet mascot — "Splash".
 * Pure Compose Canvas, no image assets. Animates a subtle bob.
 */
@Composable
fun SplashMascot(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 140.dp,
    tint: Color = HydroPrimary,
    tintDeep: Color = HydroPrimaryStrong,
    premium: Boolean = false,
    mood: MascotMood = MascotMood.Smile,
) {
    val infinite = rememberInfiniteTransition(label = "bob")
    val bob by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "bobAnim"
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val dy = (bob - 0.5f) * 6f  // ±3px

            // Body — teardrop path
            val body = Path().apply {
                moveTo(w / 2f, h * 0.08f + dy)
                cubicTo(
                    w * 0.15f, h * 0.45f + dy,
                    w * 0.1f,  h * 0.85f + dy,
                    w / 2f,    h * 0.92f + dy
                )
                cubicTo(
                    w * 0.9f,  h * 0.85f + dy,
                    w * 0.85f, h * 0.45f + dy,
                    w / 2f,    h * 0.08f + dy
                )
                close()
            }
            drawPath(
                path = body,
                brush = Brush.verticalGradient(
                    0f to tint.copy(alpha = 0.9f),
                    1f to tintDeep
                )
            )

            // Shine
            drawOval(
                color = Color.White.copy(alpha = 0.35f),
                topLeft = Offset(w * 0.24f, h * 0.22f + dy),
                size = Size(w * 0.22f, h * 0.28f)
            )

            // Blush cheeks
            val blush = Color(0xFFE8835C).copy(alpha = 0.35f)
            drawCircle(blush, radius = w * 0.055f, center = Offset(w * 0.30f, h * 0.60f + dy))
            drawCircle(blush, radius = w * 0.055f, center = Offset(w * 0.70f, h * 0.60f + dy))

            // Eyes
            val eyeY = h * 0.50f + dy
            val eyeDx = w * 0.12f
            val ink = Color(0xFF1A2B3A)
            if (mood == MascotMood.Sleepy) {
                drawArc(ink, 10f, 160f, false,
                    topLeft = Offset(w / 2f - eyeDx - 8f, eyeY - 4f),
                    size = Size(16f, 10f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(2.5f))
                drawArc(ink, 10f, 160f, false,
                    topLeft = Offset(w / 2f + eyeDx - 8f, eyeY - 4f),
                    size = Size(16f, 10f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(2.5f))
            } else {
                drawOval(
                    ink,
                    topLeft = Offset(w / 2f - eyeDx - w * 0.035f, eyeY - w * 0.045f),
                    size = Size(w * 0.07f, w * 0.09f)
                )
                drawOval(
                    ink,
                    topLeft = Offset(w / 2f + eyeDx - w * 0.035f, eyeY - w * 0.045f),
                    size = Size(w * 0.07f, w * 0.09f)
                )
                // Sparkle highlights
                drawCircle(Color.White, radius = w * 0.012f,
                    center = Offset(w / 2f - eyeDx + w * 0.01f, eyeY - w * 0.02f))
                drawCircle(Color.White, radius = w * 0.012f,
                    center = Offset(w / 2f + eyeDx + w * 0.01f, eyeY - w * 0.02f))
            }

            // Mouth
            val mouthY = h * 0.64f + dy
            if (mood == MascotMood.Wow) {
                drawOval(ink,
                    topLeft = Offset(w / 2f - w * 0.04f, mouthY - w * 0.06f),
                    size = Size(w * 0.08f, w * 0.12f))
            } else {
                val mouth = Path().apply {
                    moveTo(w / 2f - 10f, mouthY - 2f)
                    quadraticBezierTo(w / 2f, mouthY + 8f, w / 2f + 10f, mouthY - 2f)
                }
                drawPath(
                    mouth,
                    color = ink,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2.8f,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        if (premium) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = (-8).dp)
                    .clip(RoundedCornerShape(50))
            ) {
                // Tiny crown chip
                androidx.compose.material3.Text(
                    text = "👑",
                    fontSize = androidx.compose.ui.unit.TextUnit(size.value * 0.22f, androidx.compose.ui.unit.TextUnitType.Sp)
                )
            }
        }
    }
}

enum class MascotMood { Smile, Wow, Sleepy }

/**
 * Wave-filled bottle showing hydration level.
 * Animates the water surface with a sine wave.
 */
@Composable
fun WaveBottle(
    level: Float,  // 0f..1f
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp = 148.dp,
    height: androidx.compose.ui.unit.Dp = 220.dp,
    water: Color = HydroPrimary,
    waterDeep: Color = HydroPrimaryStrong,
    glass: Color = HydroPrimarySofter,
) {
    val animatedLevel by animateFloatAsState(
        targetValue = level.coerceIn(0f, 1f),
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "level"
    )
    val infinite = rememberInfiniteTransition(label = "wave")
    val phase by infinite.animateFloat(
        initialValue = 0f, targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing)),
        label = "phase"
    )

    Box(modifier = modifier.size(width, height)) {
        Canvas(Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Bottle silhouette (rounded rect with narrowed neck)
            val bottle = Path().apply {
                // neck
                moveTo(w * 0.38f, 0f)
                lineTo(w * 0.62f, 0f)
                lineTo(w * 0.62f, h * 0.12f)
                // shoulder curve
                quadraticBezierTo(w * 0.9f, h * 0.18f, w * 0.92f, h * 0.30f)
                lineTo(w * 0.92f, h * 0.92f)
                quadraticBezierTo(w * 0.92f, h, w * 0.80f, h)
                lineTo(w * 0.20f, h)
                quadraticBezierTo(w * 0.08f, h, w * 0.08f, h * 0.92f)
                lineTo(w * 0.08f, h * 0.30f)
                quadraticBezierTo(w * 0.10f, h * 0.18f, w * 0.38f, h * 0.12f)
                close()
            }

            clipPath(bottle) {
                // Glass bg
                drawRect(glass)
                // Water level
                val waterTop = h * (0.12f + (1f - animatedLevel) * 0.78f)
                // Wave
                val wavePath = Path().apply {
                    moveTo(0f, waterTop)
                    val segments = 20
                    for (i in 0..segments) {
                        val x = w * i / segments
                        val y = waterTop + sin(phase + i * 0.6f).toFloat() * 4f
                        lineTo(x, y)
                    }
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    wavePath,
                    brush = Brush.verticalGradient(
                        0f to water,
                        1f to waterDeep
                    )
                )
                // Highlight bubble
                drawCircle(
                    Color.White.copy(alpha = 0.18f),
                    radius = w * 0.06f,
                    center = Offset(w * 0.32f, h * 0.35f)
                )
            }
            // Outline
            drawPath(
                bottle,
                color = HydroLine,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )
        }
    }
}
