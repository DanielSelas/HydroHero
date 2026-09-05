package com.danielsela.hydrohero.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Code-drawn full-card backgrounds for the shop. Each fills its parent
 * Modifier (use inside a sized Box). All free of image assets.
 *
 * IDs: none, sea, stars, rainbow, sunset, forest, beach
 */

@Composable
fun BackgroundById(id: String, modifier: Modifier = Modifier) {
    when (id.lowercase()) {
        "sea" -> SeaBg(modifier)
        "stars" -> StarsBg(modifier)
        "rainbow" -> RainbowBg(modifier)
        "sunset" -> SunsetBg(modifier)
        "forest" -> ForestBg(modifier)
        "beach" -> BeachBg(modifier)
        else -> NoneBg(modifier)
    }
}

@Composable
fun NoneBg(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(Color(0xFFF1F5F9))
        // Faint dotted pattern
        val r = size.width * 0.012f
        val step = size.width * 0.18f
        var y = step / 2
        while (y < size.height) {
            var x = step / 2
            while (x < size.width) {
                drawCircle(Color(0xFFCBD5E1), r, Offset(x, y))
                x += step
            }
            y += step
        }
    }
}

@Composable
fun SeaBg(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color(0xFF7DD3FC),
                1f to Color(0xFF0C4A6E),
            )
        )
        // Two soft wave bands
        val w = size.width; val h = size.height
        val wave1 = Path().apply {
            moveTo(0f, h * 0.55f)
            quadraticBezierTo(w * 0.25f, h * 0.45f, w * 0.5f, h * 0.55f)
            quadraticBezierTo(w * 0.75f, h * 0.65f, w, h * 0.55f)
            lineTo(w, h); lineTo(0f, h); close()
        }
        drawPath(wave1, color = Color(0xFF0E9DBA).copy(alpha = 0.5f))
        val wave2 = Path().apply {
            moveTo(0f, h * 0.72f)
            quadraticBezierTo(w * 0.30f, h * 0.62f, w * 0.55f, h * 0.74f)
            quadraticBezierTo(w * 0.80f, h * 0.84f, w, h * 0.72f)
            lineTo(w, h); lineTo(0f, h); close()
        }
        drawPath(wave2, color = Color(0xFF0369A1).copy(alpha = 0.6f))
    }
}

@Composable
fun StarsBg(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color(0xFF1E1B4B),
                1f to Color(0xFF312E81),
            )
        )
        // Hand-placed stars (deterministic, no Random)
        val w = size.width; val h = size.height
        val stars = listOf(
            0.10f to 0.18f, 0.22f to 0.30f, 0.42f to 0.12f, 0.55f to 0.28f,
            0.70f to 0.15f, 0.85f to 0.32f, 0.18f to 0.55f, 0.35f to 0.65f,
            0.52f to 0.50f, 0.66f to 0.62f, 0.80f to 0.55f, 0.92f to 0.70f,
        )
        stars.forEach { (px, py) ->
            drawCircle(Color(0xFFFEF3C7), w * 0.012f, Offset(w * px, h * py))
        }
        // Crescent moon
        drawCircle(Color(0xFFFEF3C7), w * 0.08f, Offset(w * 0.78f, h * 0.22f))
        drawCircle(Color(0xFF312E81), w * 0.07f, Offset(w * 0.74f, h * 0.20f))
    }
}

@Composable
fun RainbowBg(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color(0xFFE0F2FE),
                1f to Color(0xFFFEF3C7),
            )
        )
        // 5 arched bands
        val w = size.width; val h = size.height
        val cx = w / 2; val cy = h * 1.2f
        val colors = listOf(
            Color(0xFFEF4444), Color(0xFFFB923C), Color(0xFFFBBF24),
            Color(0xFF34D399), Color(0xFF60A5FA),
        )
        colors.forEachIndexed { i, c ->
            val outerR = h * 1.0f - i * h * 0.07f
            val innerR = outerR - h * 0.06f
            val arc = Path().apply {
                addArc(androidx.compose.ui.geometry.Rect(cx - outerR, cy - outerR, cx + outerR, cy + outerR), 180f, 180f)
                arcTo(androidx.compose.ui.geometry.Rect(cx - innerR, cy - innerR, cx + innerR, cy + innerR), 0f, -180f, false)
                close()
            }
            drawPath(arc, color = c.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun SunsetBg(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color(0xFFFCA5A5),
                0.5f to Color(0xFFFDBA74),
                1f to Color(0xFFFDE68A),
            )
        )
        val w = size.width; val h = size.height
        // Sun
        drawCircle(Color(0xFFFEF3C7), w * 0.12f, Offset(w * 0.5f, h * 0.55f))
        // Mountain silhouettes
        val mtn = Path().apply {
            moveTo(0f, h)
            lineTo(0f, h * 0.78f)
            lineTo(w * 0.20f, h * 0.62f)
            lineTo(w * 0.40f, h * 0.74f)
            lineTo(w * 0.60f, h * 0.58f)
            lineTo(w * 0.80f, h * 0.70f)
            lineTo(w, h * 0.65f)
            lineTo(w, h); close()
        }
        drawPath(mtn, color = Color(0xFF7C2D12).copy(alpha = 0.65f))
    }
}

@Composable
fun ForestBg(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color(0xFFA7F3D0),
                1f to Color(0xFF065F46),
            )
        )
        val w = size.width; val h = size.height
        // Tree triangles
        fun tree(cx: Float, baseY: Float, scale: Float, color: Color) {
            val tw = w * 0.14f * scale
            val th = h * 0.42f * scale
            val tri = Path().apply {
                moveTo(cx, baseY - th)
                lineTo(cx - tw / 2, baseY)
                lineTo(cx + tw / 2, baseY); close()
            }
            drawPath(tri, color = color)
            val tri2 = Path().apply {
                moveTo(cx, baseY - th * 1.3f)
                lineTo(cx - tw * 0.4f, baseY - th * 0.3f)
                lineTo(cx + tw * 0.4f, baseY - th * 0.3f); close()
            }
            drawPath(tri2, color = color)
        }
        tree(w * 0.20f, h * 0.92f, 1.0f, Color(0xFF064E3B))
        tree(w * 0.50f, h * 0.95f, 1.2f, Color(0xFF065F46))
        tree(w * 0.78f, h * 0.92f, 1.0f, Color(0xFF064E3B))
        tree(w * 0.10f, h * 0.85f, 0.7f, Color(0xFF047857).copy(alpha = 0.8f))
        tree(w * 0.88f, h * 0.86f, 0.7f, Color(0xFF047857).copy(alpha = 0.8f))
    }
}

@Composable
fun BeachBg(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        // Sky
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color(0xFF7DD3FC),
                0.5f to Color(0xFFFEF3C7),
                1f to Color(0xFFFEF3C7),
            ),
            size = androidx.compose.ui.geometry.Size(w, h * 0.7f),
        )
        // Sand
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color(0xFFFDE68A),
                1f to Color(0xFFFCD34D),
                startY = h * 0.7f, endY = h,
            ),
            topLeft = Offset(0f, h * 0.7f),
            size = androidx.compose.ui.geometry.Size(w, h * 0.3f),
        )
        // Sun
        drawCircle(Color(0xFFFEF3C7), w * 0.10f, Offset(w * 0.78f, h * 0.22f))
        // Palm tree silhouette
        val palmX = w * 0.20f
        val baseY = h * 0.72f
        // trunk
        drawLine(
            Color(0xFF92400E), Offset(palmX, baseY), Offset(palmX + w * 0.04f, baseY - h * 0.35f),
            strokeWidth = w * 0.02f, cap = StrokeCap.Round,
        )
        // leaves
        val tipX = palmX + w * 0.04f; val tipY = baseY - h * 0.35f
        for (i in 0 until 5) {
            val a = -90f + (i - 2) * 30f
            val rad = Math.toRadians(a.toDouble())
            val ex = tipX + kotlin.math.cos(rad).toFloat() * w * 0.18f
            val ey = tipY + kotlin.math.sin(rad).toFloat() * h * 0.10f
            drawLine(
                Color(0xFF15803D), Offset(tipX, tipY), Offset(ex, ey),
                strokeWidth = w * 0.018f, cap = StrokeCap.Round,
            )
        }
    }
}
