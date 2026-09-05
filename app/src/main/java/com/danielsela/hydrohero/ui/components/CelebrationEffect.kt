package com.danielsela.hydrohero.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danielsela.hydrohero.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Re-styled celebration overlays — soft & friendly aesthetic.
 *
 * Same signatures as the originals:
 *   CelebrationOverlay(show, effectIcon, onDismiss)
 *   StarConfettiEffect()
 *   ProgressFeedbackOverlay(show, message, onDismiss)
 *   CoinsEarnedOverlay(show, amount, onDismiss)
 *
 * Changes: gentle pastel confetti instead of hard primary stars, mascot droplet
 * pop-in, and a soft "you did it" pill in the brand display face.
 */
@Composable
fun CelebrationOverlay(
    show: Boolean,
    effectIcon: String,
    onDismiss: () -> Unit,
) {
    if (!show) return
    LaunchedEffect(show) {
        delay(3500)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.32f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        // Pastel confetti behind everything
        SoftConfetti(particleCount = 28)

        // Center hero card
        val infinite = rememberInfiniteTransition(label = "celebrate")
        val scale by infinite.animateFloat(
            initialValue = 0.96f,
            targetValue = 1.04f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "scale",
        )
        val popIn = remember { Animatable(0.4f) }
        LaunchedEffect(Unit) {
            popIn.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(popIn.value)
                .padding(horizontal = 32.dp),
        ) {
            // Mascot
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(90.dp))
                    .background(
                        Brush.radialGradient(
                            0f to HydroPrimarySofter,
                            1f to HydroPrimary.copy(alpha = 0.35f),
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                // Use the existing mascot if you have it imported — otherwise emoji
                Text(
                    effectIcon.ifEmpty { "💧" },
                    fontSize = 110.sp,
                )
            }

            Spacer(Modifier.height(22.dp))

            // Title pill
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = HydroSurface,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Goal reached!",
                        fontFamily = HydroDisplayFamily,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HydroInk,
                        letterSpacing = (-0.4).sp,
                    )
                    Text(
                        text = "You hit your hydration goal — nice sip 🎉",
                        fontSize = 13.sp,
                        color = HydroInk2,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Soft confetti (pastel circles & droplets falling with gentle drift)

@Composable
fun StarConfettiEffect() {
    SoftConfetti(particleCount = 22)
}

@Composable
private fun SoftConfetti(particleCount: Int) {
    val density = LocalDensity.current
    var size by remember { mutableStateOf(IntSize.Zero) }

    val palette = listOf(
        HydroPrimary,
        HydroPrimaryDeep,
        HydroAccentMint,
        HydroAccentSun,
        HydroAccentBlush,
    )

    val particles = remember(particleCount) {
        List(particleCount) { idx ->
            ConfettiParticle(
                seed = idx,
                color = palette[idx % palette.size],
                xPct = Random.nextFloat(),
                size = Random.nextFloat() * 10f + 8f,
                rotateSpeed = Random.nextFloat() * 2f + 0.5f,
                fallDuration = Random.nextInt(2400, 3800),
                delay = Random.nextInt(0, 1000),
                kind = if (idx % 3 == 0) ConfettiKind.Drop else ConfettiKind.Circle,
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
    ) {
        if (size.height > 0) {
            particles.forEach { p ->
                FallingConfetti(particle = p, height = size.height, density = density)
            }
        }
    }
}

private enum class ConfettiKind { Circle, Drop }

private data class ConfettiParticle(
    val seed: Int,
    val color: Color,
    val xPct: Float,
    val size: Float,
    val rotateSpeed: Float,
    val fallDuration: Int,
    val delay: Int,
    val kind: ConfettiKind,
)

@Composable
private fun FallingConfetti(
    particle: ConfettiParticle,
    height: Int,
    density: androidx.compose.ui.unit.Density,
) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(particle.seed) {
        delay(particle.delay.toLong())
        anim.animateTo(
            1f,
            animationSpec = tween(
                durationMillis = particle.fallDuration,
                easing = LinearEasing,
            )
        )
    }
    val infinite = rememberInfiniteTransition(label = "rot${particle.seed}")
    val rot by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f * particle.rotateSpeed,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "r",
    )

    val driftX = sin((anim.value * 6f).toDouble()).toFloat() * 18f
    val px = (particle.xPct * 360f).dp // approximate; will be clipped to screen
    val py = with(density) { (height.toFloat() * anim.value).toDp() }

    Box(
        modifier = Modifier
            .offset(x = px + driftX.dp, y = py - 60.dp)
            .size(particle.size.dp)
            .rotate(rot),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (particle.kind) {
                ConfettiKind.Circle -> drawCircle(color = particle.color)
                ConfettiKind.Drop -> {
                    val w = this.size.width
                    val h = this.size.height
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w / 2f, 0f)
                        cubicTo(w, h * 0.4f, w * 0.85f, h, w / 2f, h)
                        cubicTo(w * 0.15f, h, 0f, h * 0.4f, w / 2f, 0f)
                        close()
                    }
                    drawPath(path, color = particle.color)
                }
            }
        }
    }
}

// (onSizeChanged is imported directly — no wrapper needed)

// ─────────────────────────────────────────────────────────────────────
// Smaller toast-style overlays

@Composable
fun ProgressFeedbackOverlay(
    show: Boolean,
    message: String,
    effectIcon: String,
    onDismiss: () -> Unit,
) {
    if (!show) return
    LaunchedEffect(show) {
        delay(1800)
        onDismiss()
    }
    val popIn = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) {
        popIn.animateTo(
            1f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        )
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Surface(
            modifier = Modifier
                .padding(top = 80.dp)
                .scale(popIn.value),
            shape = RoundedCornerShape(28.dp),
            color = HydroSurface,
            tonalElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(HydroPrimarySofter),
                    contentAlignment = Alignment.Center,
                ) { Text(effectIcon.ifEmpty { "💧" }, fontSize = 16.sp) }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = message,
                    fontFamily = HydroDisplayFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroInk,
                )
            }
        }
    }
}

@Composable
fun CoinsEarnedOverlay(
    show: Boolean,
    amount: Int,
    subtitle: String,
    onDismiss: () -> Unit,
) {
    if (!show) return
    LaunchedEffect(show) {
        delay(1600)
        onDismiss()
    }
    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        anim.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }
    val rise = (1f - anim.value) * 30f
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Surface(
            modifier = Modifier
                .padding(top = 130.dp)
                .offset(y = rise.dp),
            shape = RoundedCornerShape(24.dp),
            color = HydroAccentSun,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🪙", fontSize = 20.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "+$amount",
                        fontFamily = HydroDisplayFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HydroInk,
                    )
                }
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = HydroInk2,
                    )
                }
            }
        }
    }
}
