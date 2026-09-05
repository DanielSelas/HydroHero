package com.danielsela.hydrohero.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danielsela.hydrohero.ui.theme.*
import kotlin.math.sin

/**
 * WaveBottle with an optional mascot floating inside.
 *
 * The mascot renders inside the bottle, bobbing gently and tracking the
 * water level (sits a bit above the waterline, partially submerged when
 * the bottle is more than half full). Pass `mascotId = userData.selectedAvatar`
 * to wire the home screen up.
 *
 * Supported mascotIds: splash, moss, berry, sunny, cloud, bunny, fox, bear.
 * Pass null to render the bottle without a character.
 */
@Composable
fun WaveBottleWithMascot(
    level: Float,
    mascotId: String?,
    modifier: Modifier = Modifier,
    width: Dp = 148.dp,
    height: Dp = 220.dp,
    // Water now tints with the active primary hue so swapping themes recolors the bottle.
    water: Color = HydroPrimary.copy(alpha = 0.45f),
    waterDeep: Color = HydroPrimaryStrong.copy(alpha = 0.65f),
    glass: Color = Color.White.copy(alpha = 0.20f),  // see-through bottle
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
    val bob by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ), label = "bob"
    )

    Box(modifier = modifier.size(width, height)) {
        Canvas(Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Bottle silhouette
            val bottle = Path().apply {
                moveTo(w * 0.38f, 0f)
                lineTo(w * 0.62f, 0f)
                lineTo(w * 0.62f, h * 0.12f)
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
                drawRect(glass)

                val waterTop = h * (0.12f + (1f - animatedLevel) * 0.78f)

                // ─── Mascot bobs on top of the water (only bottom edge submerged) ───
                if (mascotId != null) {
                    val mascotSize = w * 0.50f
                    val bobDy = (bob - 0.5f) * 4f
                    // Anchor: mascot's BOTTOM ~5% submerged into water surface
                    // Pull slightly right of center for character
                    val mascotCx = w * 0.58f
                    val mascotBottom = waterTop + mascotSize * 0.12f + bobDy
                    val rect = Rect(
                        left = mascotCx - mascotSize / 2,
                        top = mascotBottom - mascotSize,
                        right = mascotCx + mascotSize / 2,
                        bottom = mascotBottom,
                    )
                    drawMascotById(mascotId, rect)
                }

                // Wave (drawn AFTER mascot so it submerges the bottom of mascot)
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
                    brush = Brush.verticalGradient(0f to water, 1f to waterDeep),
                )
                drawCircle(
                    Color.White.copy(alpha = 0.18f),
                    radius = w * 0.06f,
                    center = Offset(w * 0.32f, h * 0.35f),
                )
            }
            drawPath(bottle, color = HydroLine, style = Stroke(width = 2f))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Mascot drawing primitives — bound to a Rect so they can sit anywhere.
// Lightweight static versions of the Mascots.kt designs (no breathing —
// the bottle handles bobbing).
// ─────────────────────────────────────────────────────────────────────

private fun DrawScope.drawMascotById(id: String, r: Rect) {
    when (id.lowercase()) {
        "splash" -> drawSplash(r)
        "moss" -> drawMoss(r)
        "berry" -> drawBerry(r)
        "sunny" -> drawSunny(r)
        "cloud" -> drawCloud(r)
        "bunny" -> drawBunny(r)
        "fox" -> drawFox(r)
        "bear" -> drawBear(r)
        else -> drawSplash(r)
    }
}

private fun DrawScope.drawFace(r: Rect) {
    val w = r.width
    val h = r.height
    val cx = r.center.x
    val cy = r.center.y
    val eyeY = cy - h * 0.02f
    val eyeR = w * 0.045f
    val ink = Color(0xFF1F2937)
    drawOval(ink, Offset(cx - w * 0.18f - eyeR, eyeY - eyeR), Size(eyeR * 2, eyeR * 2.4f))
    drawOval(ink, Offset(cx + w * 0.18f - eyeR, eyeY - eyeR), Size(eyeR * 2, eyeR * 2.4f))
    val cheek = Color(0xFFFFB4B4).copy(alpha = 0.7f)
    val cR = w * 0.07f
    drawOval(cheek, Offset(cx - w * 0.28f - cR, eyeY + h * 0.05f - cR * 0.6f), Size(cR * 2, cR * 1.2f))
    drawOval(cheek, Offset(cx + w * 0.28f - cR, eyeY + h * 0.05f - cR * 0.6f), Size(cR * 2, cR * 1.2f))
    val smile = Path().apply {
        val sw = w * 0.18f
        val sy = cy + h * 0.10f
        moveTo(cx - sw / 2, sy)
        quadraticBezierTo(cx, sy + h * 0.06f, cx + sw / 2, sy)
    }
    drawPath(smile, ink, style = Stroke(width = w * 0.022f, cap = StrokeCap.Round))
}

private fun DrawScope.drawSplash(r: Rect) {
    val w = r.width; val h = r.height; val ox = r.left; val oy = r.top
    val path = Path().apply {
        moveTo(ox + w * 0.5f, oy + h * 0.05f)
        cubicTo(ox + w * 0.95f, oy + h * 0.45f, ox + w * 0.95f, oy + h * 0.85f, ox + w * 0.5f, oy + h * 0.95f)
        cubicTo(ox + w * 0.05f, oy + h * 0.85f, ox + w * 0.05f, oy + h * 0.45f, ox + w * 0.5f, oy + h * 0.05f)
        close()
    }
    drawPath(path, brush = Brush.verticalGradient(0f to Color(0xFF7DD3FC), 1f to Color(0xFF0E9DBA), startY = oy, endY = oy + h))
    drawOval(Color.White.copy(alpha = 0.45f), Offset(ox + w * 0.28f, oy + h * 0.20f), Size(w * 0.18f, w * 0.10f))
    drawFace(Rect(ox, oy + h * 0.25f, ox + w, oy + h))
}

private fun DrawScope.drawMoss(r: Rect) {
    val w = r.width; val h = r.height; val ox = r.left; val oy = r.top
    val path = Path().apply {
        moveTo(ox + w * 0.5f, oy + h * 0.10f)
        cubicTo(ox + w * 0.95f, oy + h * 0.20f, ox + w * 1.05f, oy + h * 0.75f, ox + w * 0.5f, oy + h * 0.95f)
        cubicTo(ox - w * 0.05f, oy + h * 0.75f, ox + w * 0.05f, oy + h * 0.20f, ox + w * 0.5f, oy + h * 0.10f)
        close()
    }
    drawPath(path, brush = Brush.verticalGradient(0f to Color(0xFF86EFAC), 1f to Color(0xFF15803D), startY = oy, endY = oy + h))
    drawOval(Color.White.copy(alpha = 0.35f), Offset(ox + w * 0.30f, oy + h * 0.22f), Size(w * 0.16f, w * 0.10f))
    drawFace(Rect(ox, oy + h * 0.20f, ox + w, oy + h))
}

private fun DrawScope.drawBerry(r: Rect) {
    val w = r.width; val h = r.height; val ox = r.left; val oy = r.top
    drawOval(brush = Brush.verticalGradient(0f to Color(0xFFC4B5FD), 1f to Color(0xFF6D28D9), startY = oy, endY = oy + h),
        topLeft = Offset(ox, oy + h * 0.05f), size = Size(w, h * 0.95f))
    drawOval(Color.White.copy(alpha = 0.4f), Offset(ox + w * 0.25f, oy + h * 0.18f), Size(w * 0.20f, w * 0.10f))
    drawFace(Rect(ox, oy + h * 0.20f, ox + w, oy + h))
}

private fun DrawScope.drawSunny(r: Rect) {
    val w = r.width; val h = r.height; val cx = r.center.x; val cy = r.center.y
    val rayLen = w * 0.10f
    val bodyR = w * 0.36f
    for (i in 0 until 8) {
        val a = Math.toRadians(i * 45.0).toFloat()
        val rx = cx + kotlin.math.cos(a) * (bodyR + rayLen * 0.5f)
        val ry = cy + kotlin.math.sin(a) * (bodyR + rayLen * 0.5f)
        drawOval(Color(0xFFFBBF24), Offset(rx - rayLen * 0.5f, ry - rayLen * 0.25f), Size(rayLen, rayLen * 0.5f))
    }
    drawOval(brush = Brush.verticalGradient(0f to Color(0xFFFEF08A), 1f to Color(0xFFF59E0B), startY = cy - bodyR, endY = cy + bodyR),
        topLeft = Offset(cx - bodyR, cy - bodyR), size = Size(bodyR * 2, bodyR * 2))
    drawOval(Color.White.copy(alpha = 0.5f), Offset(cx - bodyR * 0.5f, cy - bodyR * 0.7f), Size(bodyR * 0.4f, bodyR * 0.22f))
    drawFace(Rect(cx - bodyR, cy - bodyR, cx + bodyR, cy + bodyR))
}

private fun DrawScope.drawCloud(r: Rect) {
    val w = r.width; val h = r.height; val ox = r.left; val oy = r.top
    val g = Brush.verticalGradient(0f to Color(0xFFF1F5F9), 1f to Color(0xFFCBD5E1), startY = oy, endY = oy + h)
    drawOval(brush = g, topLeft = Offset(ox + w * 0.05f, oy + h * 0.35f), size = Size(w * 0.45f, h * 0.45f))
    drawOval(brush = g, topLeft = Offset(ox + w * 0.3f, oy + h * 0.20f), size = Size(w * 0.50f, h * 0.55f))
    drawOval(brush = g, topLeft = Offset(ox + w * 0.55f, oy + h * 0.35f), size = Size(w * 0.40f, h * 0.45f))
    drawOval(brush = g, topLeft = Offset(ox + w * 0.10f, oy + h * 0.55f), size = Size(w * 0.80f, h * 0.30f))
    drawFace(Rect(ox + w * 0.20f, oy + h * 0.30f, ox + w * 0.80f, oy + h * 0.85f))
}

private fun DrawScope.drawBunny(r: Rect) {
    val w = r.width; val h = r.height; val ox = r.left; val oy = r.top
    val g = Brush.verticalGradient(0f to Color(0xFFFED7AA), 1f to Color(0xFFEA9F65), startY = oy, endY = oy + h)
    drawOval(brush = g, topLeft = Offset(ox + w * 0.18f, oy), size = Size(w * 0.14f, h * 0.45f))
    drawOval(brush = g, topLeft = Offset(ox + w * 0.68f, oy), size = Size(w * 0.14f, h * 0.45f))
    drawOval(Color(0xFFFFB4B4).copy(alpha = 0.6f), Offset(ox + w * 0.22f, oy + h * 0.08f), Size(w * 0.06f, h * 0.30f))
    drawOval(Color(0xFFFFB4B4).copy(alpha = 0.6f), Offset(ox + w * 0.72f, oy + h * 0.08f), Size(w * 0.06f, h * 0.30f))
    drawOval(brush = g, topLeft = Offset(ox + w * 0.10f, oy + h * 0.30f), size = Size(w * 0.80f, h * 0.65f))
    drawFace(Rect(ox + w * 0.10f, oy + h * 0.40f, ox + w * 0.90f, oy + h * 0.95f))
}

private fun DrawScope.drawFox(r: Rect) {
    val w = r.width; val h = r.height; val ox = r.left; val oy = r.top
    val g = Brush.verticalGradient(0f to Color(0xFFFB923C), 1f to Color(0xFFC2410C), startY = oy, endY = oy + h)
    val leftEar = Path().apply {
        moveTo(ox + w * 0.15f, oy + h * 0.40f); lineTo(ox + w * 0.25f, oy + h * 0.05f); lineTo(ox + w * 0.40f, oy + h * 0.30f); close()
    }
    val rightEar = Path().apply {
        moveTo(ox + w * 0.85f, oy + h * 0.40f); lineTo(ox + w * 0.75f, oy + h * 0.05f); lineTo(ox + w * 0.60f, oy + h * 0.30f); close()
    }
    drawPath(leftEar, brush = g); drawPath(rightEar, brush = g)
    drawOval(brush = g, topLeft = Offset(ox + w * 0.10f, oy + h * 0.25f), size = Size(w * 0.80f, h * 0.70f))
    drawOval(Color(0xFFFFF7ED), Offset(ox + w * 0.30f, oy + h * 0.55f), Size(w * 0.40f, h * 0.30f))
    drawFace(Rect(ox + w * 0.10f, oy + h * 0.35f, ox + w * 0.90f, oy + h * 0.90f))
}

private fun DrawScope.drawBear(r: Rect) {
    val w = r.width; val h = r.height; val ox = r.left; val oy = r.top
    val g = Brush.verticalGradient(0f to Color(0xFFA78368), 1f to Color(0xFF6B4423), startY = oy, endY = oy + h)
    drawOval(brush = g, topLeft = Offset(ox + w * 0.10f, oy + h * 0.10f), size = Size(w * 0.25f, h * 0.25f))
    drawOval(brush = g, topLeft = Offset(ox + w * 0.65f, oy + h * 0.10f), size = Size(w * 0.25f, h * 0.25f))
    drawOval(Color(0xFFD4A574), Offset(ox + w * 0.16f, oy + h * 0.16f), Size(w * 0.13f, h * 0.13f))
    drawOval(Color(0xFFD4A574), Offset(ox + w * 0.71f, oy + h * 0.16f), Size(w * 0.13f, h * 0.13f))
    drawOval(brush = g, topLeft = Offset(ox + w * 0.05f, oy + h * 0.20f), size = Size(w * 0.90f, h * 0.75f))
    drawOval(Color(0xFFD4A574), Offset(ox + w * 0.28f, oy + h * 0.55f), Size(w * 0.44f, h * 0.30f))
    drawFace(Rect(ox + w * 0.05f, oy + h * 0.30f, ox + w * 0.95f, oy + h * 0.90f))
}
