package com.example.hydrohero.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Code-drawn mascot characters — no PNGs / no budget required.
 *
 * Each mascot is a self-contained @Composable Canvas that renders a soft
 * gradient body, rosy cheeks, dot eyes, and a smile. They gently breathe
 * (subtle scale animation) for life.
 *
 * Usage:
 *   MascotById("splash", size = 80.dp)
 *
 * IDs:
 *   splash, moss, berry, sunny, cloud, bunny, fox, bear
 */

// ─────────────────────────────────────────────────────────────────────
// Public dispatcher

@Composable
fun MascotById(id: String, size: Dp = 64.dp, modifier: Modifier = Modifier) {
    when (id.lowercase()) {
        "splash" -> SplashMascot(size, modifier)
        "moss" -> MossMascot(size, modifier)
        "berry" -> BerryMascot(size, modifier)
        "sunny" -> SunnyMascot(size, modifier)
        "cloud" -> CloudMascot(size, modifier)
        "bunny" -> BunnyMascot(size, modifier)
        "fox" -> FoxMascot(size, modifier)
        "bear" -> BearMascot(size, modifier)
        else -> SplashMascot(size, modifier)
    }
}

// ─────────────────────────────────────────────────────────────────────
// Breathing helper

@Composable
private fun rememberBreathe(): Float {
    val infinite = rememberInfiniteTransition(label = "breathe")
    val s by infinite.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale"
    )
    return s
}

// ─────────────────────────────────────────────────────────────────────
// Drawing primitives — face on a body of the given bounds

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFace(
    bounds: androidx.compose.ui.geometry.Rect,
    eyeColor: Color = Color(0xFF1F2937),
    cheekColor: Color = Color(0xFFFFB4B4),
    smileColor: Color = Color(0xFF1F2937),
) {
    val w = bounds.width
    val h = bounds.height
    val cx = bounds.center.x
    val cy = bounds.center.y

    // Eyes — two small ovals
    val eyeY = cy - h * 0.02f
    val eyeR = w * 0.045f
    drawOval(
        color = eyeColor,
        topLeft = Offset(cx - w * 0.18f - eyeR, eyeY - eyeR),
        size = Size(eyeR * 2, eyeR * 2.4f),
    )
    drawOval(
        color = eyeColor,
        topLeft = Offset(cx + w * 0.18f - eyeR, eyeY - eyeR),
        size = Size(eyeR * 2, eyeR * 2.4f),
    )

    // Cheeks — soft pink
    val cheekR = w * 0.07f
    drawOval(
        color = cheekColor.copy(alpha = 0.7f),
        topLeft = Offset(cx - w * 0.28f - cheekR, eyeY + h * 0.05f - cheekR * 0.6f),
        size = Size(cheekR * 2, cheekR * 1.2f),
    )
    drawOval(
        color = cheekColor.copy(alpha = 0.7f),
        topLeft = Offset(cx + w * 0.28f - cheekR, eyeY + h * 0.05f - cheekR * 0.6f),
        size = Size(cheekR * 2, cheekR * 1.2f),
    )

    // Smile — small arc
    val smile = Path().apply {
        val sw = w * 0.18f
        val sy = cy + h * 0.10f
        moveTo(cx - sw / 2, sy)
        quadraticBezierTo(cx, sy + h * 0.06f, cx + sw / 2, sy)
    }
    drawPath(
        path = smile,
        color = smileColor,
        style = Stroke(width = w * 0.022f, cap = androidx.compose.ui.graphics.StrokeCap.Round),
    )
}

// ─────────────────────────────────────────────────────────────────────
// SPLASH — blue droplet

@Composable
fun SplashMascot(size: Dp = 64.dp, modifier: Modifier = Modifier) {
    val breathe = rememberBreathe()
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width * breathe
        val h = this.size.height * breathe
        val ox = (this.size.width - w) / 2
        val oy = (this.size.height - h) / 2

        // Droplet path — pointy top, round bottom
        val path = Path().apply {
            moveTo(ox + w * 0.5f, oy + h * 0.05f)
            cubicTo(
                ox + w * 0.95f, oy + h * 0.45f,
                ox + w * 0.95f, oy + h * 0.85f,
                ox + w * 0.5f, oy + h * 0.95f,
            )
            cubicTo(
                ox + w * 0.05f, oy + h * 0.85f,
                ox + w * 0.05f, oy + h * 0.45f,
                ox + w * 0.5f, oy + h * 0.05f,
            )
            close()
        }
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                0f to Color(0xFF7DD3FC),
                1f to Color(0xFF0E9DBA),
                startY = oy,
                endY = oy + h,
            ),
        )
        // Highlight
        drawOval(
            color = Color.White.copy(alpha = 0.45f),
            topLeft = Offset(ox + w * 0.28f, oy + h * 0.20f),
            size = Size(w * 0.18f, w * 0.10f),
        )
        drawFace(androidx.compose.ui.geometry.Rect(ox, oy + h * 0.25f, ox + w, oy + h))
    }
}

// ─────────────────────────────────────────────────────────────────────
// MOSS — green blob

@Composable
fun MossMascot(size: Dp = 64.dp, modifier: Modifier = Modifier) {
    val breathe = rememberBreathe()
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width * breathe
        val h = this.size.height * breathe
        val ox = (this.size.width - w) / 2
        val oy = (this.size.height - h) / 2

        val path = Path().apply {
            moveTo(ox + w * 0.5f, oy + h * 0.10f)
            cubicTo(
                ox + w * 0.95f, oy + h * 0.20f,
                ox + w * 1.05f, oy + h * 0.75f,
                ox + w * 0.5f, oy + h * 0.95f,
            )
            cubicTo(
                ox - w * 0.05f, oy + h * 0.75f,
                ox + w * 0.05f, oy + h * 0.20f,
                ox + w * 0.5f, oy + h * 0.10f,
            )
            close()
        }
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                0f to Color(0xFF86EFAC),
                1f to Color(0xFF15803D),
                startY = oy,
                endY = oy + h,
            ),
        )
        drawOval(
            color = Color.White.copy(alpha = 0.35f),
            topLeft = Offset(ox + w * 0.30f, oy + h * 0.22f),
            size = Size(w * 0.16f, w * 0.10f),
        )
        drawFace(androidx.compose.ui.geometry.Rect(ox, oy + h * 0.20f, ox + w, oy + h))
    }
}

// ─────────────────────────────────────────────────────────────────────
// BERRY — purple round

@Composable
fun BerryMascot(size: Dp = 64.dp, modifier: Modifier = Modifier) {
    val breathe = rememberBreathe()
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width * breathe
        val h = this.size.height * breathe
        val ox = (this.size.width - w) / 2
        val oy = (this.size.height - h) / 2
        drawOval(
            brush = Brush.verticalGradient(
                0f to Color(0xFFC4B5FD),
                1f to Color(0xFF6D28D9),
                startY = oy,
                endY = oy + h,
            ),
            topLeft = Offset(ox, oy + h * 0.05f),
            size = Size(w, h * 0.95f),
        )
        drawOval(
            color = Color.White.copy(alpha = 0.4f),
            topLeft = Offset(ox + w * 0.25f, oy + h * 0.18f),
            size = Size(w * 0.20f, w * 0.10f),
        )
        // Tiny leaf on top
        val leaf = Path().apply {
            moveTo(ox + w * 0.5f, oy + h * 0.10f)
            quadraticBezierTo(
                ox + w * 0.65f, oy - h * 0.02f,
                ox + w * 0.55f, oy + h * 0.02f,
            )
            quadraticBezierTo(
                ox + w * 0.45f, oy - h * 0.02f,
                ox + w * 0.5f, oy + h * 0.10f,
            )
            close()
        }
        drawPath(leaf, color = Color(0xFF22C55E))
        drawFace(androidx.compose.ui.geometry.Rect(ox, oy + h * 0.20f, ox + w, oy + h))
    }
}

// ─────────────────────────────────────────────────────────────────────
// SUNNY — yellow sun

@Composable
fun SunnyMascot(size: Dp = 64.dp, modifier: Modifier = Modifier) {
    val breathe = rememberBreathe()
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width * breathe
        val h = this.size.height * breathe
        val ox = (this.size.width - w) / 2
        val oy = (this.size.height - h) / 2
        val cx = ox + w / 2
        val cy = oy + h / 2

        // Rays (8 small triangles around the body)
        val rayLen = w * 0.10f
        val bodyR = w * 0.36f
        for (i in 0 until 8) {
            val a = Math.toRadians(i * 45.0).toFloat()
            val rx = cx + kotlin.math.cos(a) * (bodyR + rayLen * 0.5f)
            val ry = cy + kotlin.math.sin(a) * (bodyR + rayLen * 0.5f)
            drawOval(
                color = Color(0xFFFBBF24),
                topLeft = Offset(rx - rayLen * 0.5f, ry - rayLen * 0.25f),
                size = Size(rayLen, rayLen * 0.5f),
            )
        }
        drawOval(
            brush = Brush.verticalGradient(
                0f to Color(0xFFFEF08A),
                1f to Color(0xFFF59E0B),
                startY = cy - bodyR,
                endY = cy + bodyR,
            ),
            topLeft = Offset(cx - bodyR, cy - bodyR),
            size = Size(bodyR * 2, bodyR * 2),
        )
        drawOval(
            color = Color.White.copy(alpha = 0.5f),
            topLeft = Offset(cx - bodyR * 0.5f, cy - bodyR * 0.7f),
            size = Size(bodyR * 0.4f, bodyR * 0.22f),
        )
        drawFace(androidx.compose.ui.geometry.Rect(cx - bodyR, cy - bodyR, cx + bodyR, cy + bodyR))
    }
}

// ─────────────────────────────────────────────────────────────────────
// CLOUD — white cloud

@Composable
fun CloudMascot(size: Dp = 64.dp, modifier: Modifier = Modifier) {
    val breathe = rememberBreathe()
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width * breathe
        val h = this.size.height * breathe
        val ox = (this.size.width - w) / 2
        val oy = (this.size.height - h) / 2

        // 3 overlapping ovals form a cloud
        val gradient = Brush.verticalGradient(
            0f to Color(0xFFF1F5F9),
            1f to Color(0xFFCBD5E1),
            startY = oy,
            endY = oy + h,
        )
        drawOval(brush = gradient, topLeft = Offset(ox + w * 0.05f, oy + h * 0.35f), size = Size(w * 0.45f, h * 0.45f))
        drawOval(brush = gradient, topLeft = Offset(ox + w * 0.3f, oy + h * 0.20f), size = Size(w * 0.50f, h * 0.55f))
        drawOval(brush = gradient, topLeft = Offset(ox + w * 0.55f, oy + h * 0.35f), size = Size(w * 0.40f, h * 0.45f))
        // Base
        drawOval(brush = gradient, topLeft = Offset(ox + w * 0.10f, oy + h * 0.55f), size = Size(w * 0.80f, h * 0.30f))
        drawFace(androidx.compose.ui.geometry.Rect(ox + w * 0.20f, oy + h * 0.30f, ox + w * 0.80f, oy + h * 0.85f))
    }
}

// ─────────────────────────────────────────────────────────────────────
// BUNNY — peach blob with ears

@Composable
fun BunnyMascot(size: Dp = 64.dp, modifier: Modifier = Modifier) {
    val breathe = rememberBreathe()
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width * breathe
        val h = this.size.height * breathe
        val ox = (this.size.width - w) / 2
        val oy = (this.size.height - h) / 2

        val bodyGradient = Brush.verticalGradient(
            0f to Color(0xFFFED7AA),
            1f to Color(0xFFEA9F65),
            startY = oy,
            endY = oy + h,
        )
        // Ears
        drawOval(brush = bodyGradient, topLeft = Offset(ox + w * 0.18f, oy), size = Size(w * 0.14f, h * 0.45f))
        drawOval(brush = bodyGradient, topLeft = Offset(ox + w * 0.68f, oy), size = Size(w * 0.14f, h * 0.45f))
        // Inner ears
        drawOval(color = Color(0xFFFFB4B4).copy(alpha = 0.6f), topLeft = Offset(ox + w * 0.22f, oy + h * 0.08f), size = Size(w * 0.06f, h * 0.30f))
        drawOval(color = Color(0xFFFFB4B4).copy(alpha = 0.6f), topLeft = Offset(ox + w * 0.72f, oy + h * 0.08f), size = Size(w * 0.06f, h * 0.30f))
        // Head
        drawOval(brush = bodyGradient, topLeft = Offset(ox + w * 0.10f, oy + h * 0.30f), size = Size(w * 0.80f, h * 0.65f))
        drawFace(androidx.compose.ui.geometry.Rect(ox + w * 0.10f, oy + h * 0.40f, ox + w * 0.90f, oy + h * 0.95f))
    }
}

// ─────────────────────────────────────────────────────────────────────
// FOX — orange triangle blob

@Composable
fun FoxMascot(size: Dp = 64.dp, modifier: Modifier = Modifier) {
    val breathe = rememberBreathe()
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width * breathe
        val h = this.size.height * breathe
        val ox = (this.size.width - w) / 2
        val oy = (this.size.height - h) / 2

        val gradient = Brush.verticalGradient(
            0f to Color(0xFFFB923C),
            1f to Color(0xFFC2410C),
            startY = oy,
            endY = oy + h,
        )
        // Ears (triangles)
        val leftEar = Path().apply {
            moveTo(ox + w * 0.15f, oy + h * 0.40f)
            lineTo(ox + w * 0.25f, oy + h * 0.05f)
            lineTo(ox + w * 0.40f, oy + h * 0.30f)
            close()
        }
        val rightEar = Path().apply {
            moveTo(ox + w * 0.85f, oy + h * 0.40f)
            lineTo(ox + w * 0.75f, oy + h * 0.05f)
            lineTo(ox + w * 0.60f, oy + h * 0.30f)
            close()
        }
        drawPath(leftEar, brush = gradient)
        drawPath(rightEar, brush = gradient)
        // Head
        drawOval(brush = gradient, topLeft = Offset(ox + w * 0.10f, oy + h * 0.25f), size = Size(w * 0.80f, h * 0.70f))
        // White muzzle
        drawOval(color = Color(0xFFFFF7ED), topLeft = Offset(ox + w * 0.30f, oy + h * 0.55f), size = Size(w * 0.40f, h * 0.30f))
        drawFace(androidx.compose.ui.geometry.Rect(ox + w * 0.10f, oy + h * 0.35f, ox + w * 0.90f, oy + h * 0.90f))
    }
}

// ─────────────────────────────────────────────────────────────────────
// BEAR — brown round

@Composable
fun BearMascot(size: Dp = 64.dp, modifier: Modifier = Modifier) {
    val breathe = rememberBreathe()
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width * breathe
        val h = this.size.height * breathe
        val ox = (this.size.width - w) / 2
        val oy = (this.size.height - h) / 2

        val gradient = Brush.verticalGradient(
            0f to Color(0xFFA78368),
            1f to Color(0xFF6B4423),
            startY = oy,
            endY = oy + h,
        )
        // Ears
        drawOval(brush = gradient, topLeft = Offset(ox + w * 0.10f, oy + h * 0.10f), size = Size(w * 0.25f, h * 0.25f))
        drawOval(brush = gradient, topLeft = Offset(ox + w * 0.65f, oy + h * 0.10f), size = Size(w * 0.25f, h * 0.25f))
        // Inner ears
        drawOval(color = Color(0xFFD4A574), topLeft = Offset(ox + w * 0.16f, oy + h * 0.16f), size = Size(w * 0.13f, h * 0.13f))
        drawOval(color = Color(0xFFD4A574), topLeft = Offset(ox + w * 0.71f, oy + h * 0.16f), size = Size(w * 0.13f, h * 0.13f))
        // Head
        drawOval(brush = gradient, topLeft = Offset(ox + w * 0.05f, oy + h * 0.20f), size = Size(w * 0.90f, h * 0.75f))
        // Muzzle
        drawOval(color = Color(0xFFD4A574), topLeft = Offset(ox + w * 0.28f, oy + h * 0.55f), size = Size(w * 0.44f, h * 0.30f))
        drawFace(androidx.compose.ui.geometry.Rect(ox + w * 0.05f, oy + h * 0.30f, ox + w * 0.95f, oy + h * 0.90f))
    }
}
