package com.example.hydrohero.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Code-drawn effect/cosmetic icons for the shop. Soft gradients to match
 * the mascot aesthetic. Render in 80dp tiles by default.
 *
 * IDs: whale, bottle1, bottle2, cup
 */

@Composable
fun EffectById(id: String, size: Dp = 80.dp, modifier: Modifier = Modifier) {
    when (id.lowercase()) {
        "whale" -> WhaleEffect(size, modifier)
        "bottle1" -> ClassicBottleEffect(size, modifier)
        "bottle2" -> SportsBottleEffect(size, modifier)
        "cup" -> MagicCupEffect(size, modifier)
        else -> ClassicBottleEffect(size, modifier)
    }
}

// Whale — blue body with arched spout
@Composable
fun WhaleEffect(size: Dp = 80.dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width; val h = this.size.height
        // Body
        val body = Path().apply {
            moveTo(w * 0.10f, h * 0.55f)
            cubicTo(w * 0.10f, h * 0.30f, w * 0.50f, h * 0.30f, w * 0.70f, h * 0.45f)
            lineTo(w * 0.92f, h * 0.30f)
            lineTo(w * 0.85f, h * 0.55f)
            lineTo(w * 0.92f, h * 0.78f)
            lineTo(w * 0.70f, h * 0.65f)
            cubicTo(w * 0.50f, h * 0.85f, w * 0.10f, h * 0.85f, w * 0.10f, h * 0.55f)
            close()
        }
        drawPath(body, brush = Brush.verticalGradient(0f to Color(0xFF60A5FA), 1f to Color(0xFF1E40AF)))
        // Belly
        drawOval(Color(0xFFE0F2FE).copy(alpha = 0.5f), Offset(w * 0.20f, h * 0.55f), Size(w * 0.45f, h * 0.20f))
        // Eye
        drawCircle(Color(0xFF0F172A), w * 0.025f, Offset(w * 0.30f, h * 0.50f))
        // Spout
        for (i in 0..2) {
            val sx = w * 0.30f + i * w * 0.04f
            drawLine(
                Color(0xFF7DD3FC), Offset(sx, h * 0.30f), Offset(sx + w * 0.02f, h * 0.05f),
                strokeWidth = w * 0.015f, cap = StrokeCap.Round,
            )
        }
        drawCircle(Color(0xFFE0F2FE), w * 0.018f, Offset(w * 0.34f, h * 0.10f))
        drawCircle(Color(0xFFE0F2FE), w * 0.014f, Offset(w * 0.40f, h * 0.06f))
    }
}

// Classic bottle — milk-bottle silhouette
@Composable
fun ClassicBottleEffect(size: Dp = 80.dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width; val h = this.size.height
        val bottle = Path().apply {
            moveTo(w * 0.42f, h * 0.05f)
            lineTo(w * 0.58f, h * 0.05f)
            lineTo(w * 0.58f, h * 0.20f)
            quadraticBezierTo(w * 0.78f, h * 0.25f, w * 0.78f, h * 0.40f)
            lineTo(w * 0.78f, h * 0.88f)
            quadraticBezierTo(w * 0.78f, h * 0.95f, w * 0.70f, h * 0.95f)
            lineTo(w * 0.30f, h * 0.95f)
            quadraticBezierTo(w * 0.22f, h * 0.95f, w * 0.22f, h * 0.88f)
            lineTo(w * 0.22f, h * 0.40f)
            quadraticBezierTo(w * 0.22f, h * 0.25f, w * 0.42f, h * 0.20f)
            close()
        }
        drawPath(bottle, brush = Brush.verticalGradient(0f to Color(0xFFE0F2FE), 1f to Color(0xFFBAE6FD)))
        drawPath(bottle, color = Color(0xFF0E9DBA), style = Stroke(width = w * 0.018f))
        // Cap
        drawRect(Color(0xFFEC4899), Offset(w * 0.40f, h * 0.0f), Size(w * 0.20f, h * 0.08f))
        // Liquid
        drawRect(Color(0xFF60A5FA).copy(alpha = 0.6f), Offset(w * 0.24f, h * 0.50f), Size(w * 0.52f, h * 0.43f))
        // Highlight
        drawOval(Color.White.copy(alpha = 0.5f), Offset(w * 0.30f, h * 0.30f), Size(w * 0.06f, h * 0.20f))
    }
}

// Sports bottle — taller with grip line
@Composable
fun SportsBottleEffect(size: Dp = 80.dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width; val h = this.size.height
        // Cap
        drawRect(brush = Brush.verticalGradient(0f to Color(0xFF1F2937), 1f to Color(0xFF374151)),
            topLeft = Offset(w * 0.32f, h * 0.0f), size = Size(w * 0.36f, h * 0.12f))
        // Sip nozzle
        drawRect(Color(0xFF1F2937), Offset(w * 0.45f, -h * 0.05f), Size(w * 0.10f, h * 0.08f))
        // Body
        val body = Path().apply {
            moveTo(w * 0.25f, h * 0.12f)
            lineTo(w * 0.75f, h * 0.12f)
            lineTo(w * 0.78f, h * 0.92f)
            quadraticBezierTo(w * 0.78f, h * 0.97f, w * 0.72f, h * 0.97f)
            lineTo(w * 0.28f, h * 0.97f)
            quadraticBezierTo(w * 0.22f, h * 0.97f, w * 0.22f, h * 0.92f)
            close()
        }
        drawPath(body, brush = Brush.verticalGradient(0f to Color(0xFF34D399), 1f to Color(0xFF047857)))
        // Grip indent
        drawLine(Color(0xFF065F46).copy(alpha = 0.6f), Offset(w * 0.24f, h * 0.55f), Offset(w * 0.78f, h * 0.55f), strokeWidth = w * 0.012f)
        // Highlight
        drawRect(Color.White.copy(alpha = 0.25f), Offset(w * 0.30f, h * 0.20f), Size(w * 0.06f, h * 0.40f))
    }
}

// Magic cup — coffee/tea cup with sparkle
@Composable
fun MagicCupEffect(size: Dp = 80.dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width; val h = this.size.height
        // Saucer
        drawOval(Color(0xFFF5F5F4), Offset(w * 0.05f, h * 0.78f), Size(w * 0.90f, h * 0.12f))
        drawOval(Color(0xFFD6D3D1), Offset(w * 0.15f, h * 0.80f), Size(w * 0.70f, h * 0.06f))
        // Cup body
        val cup = Path().apply {
            moveTo(w * 0.25f, h * 0.40f)
            lineTo(w * 0.75f, h * 0.40f)
            lineTo(w * 0.70f, h * 0.80f)
            quadraticBezierTo(w * 0.50f, h * 0.84f, w * 0.30f, h * 0.80f)
            close()
        }
        drawPath(cup, brush = Brush.verticalGradient(0f to Color(0xFFFEF3C7), 1f to Color(0xFFFBBF24)))
        // Handle
        val handle = Path().apply {
            moveTo(w * 0.75f, h * 0.50f)
            cubicTo(w * 0.95f, h * 0.50f, w * 0.95f, h * 0.70f, w * 0.72f, h * 0.70f)
        }
        drawPath(handle, color = Color(0xFFFBBF24), style = Stroke(width = w * 0.05f, cap = StrokeCap.Round))
        // Liquid surface
        drawOval(Color(0xFF7C2D12), Offset(w * 0.24f, h * 0.36f), Size(w * 0.52f, h * 0.10f))
        drawOval(Color(0xFF92400E), Offset(w * 0.27f, h * 0.38f), Size(w * 0.46f, h * 0.06f))
        // Sparkles
        drawCircle(Color(0xFFFEF08A), w * 0.025f, Offset(w * 0.18f, h * 0.20f))
        drawCircle(Color(0xFFFEF08A), w * 0.018f, Offset(w * 0.85f, h * 0.30f))
        drawCircle(Color(0xFFFDE68A), w * 0.022f, Offset(w * 0.70f, h * 0.10f))
    }
}
