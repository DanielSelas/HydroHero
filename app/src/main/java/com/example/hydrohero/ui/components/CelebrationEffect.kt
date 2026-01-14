package com.example.hydrohero.ui.components

import androidx.compose.animation.core.*
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.hydrohero.ui.theme.*
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.*
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Composable
fun CelebrationOverlay(
    show: Boolean,
    avatarIcon: String,
    onDismiss: () -> Unit
) {
    if (show) {
        LaunchedEffect(show) {
            delay(4000)
            onDismiss()
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            // Star confetti effect in background
            StarConfettiEffect()
            
            // Also add Konfetti for extra effect (reduced particles to prevent OutOfMemoryError)
            KonfettiView(
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 50f,
                        damping = 0.9f,
                        angle = Angle.BOTTOM,
                        spread = Spread.WIDE,
                        colors = listOf(
                            Color(0xFF4A90E2).toArgb(),
                            Color(0xFF50C878).toArgb(),
                            Color(0xFFFFD700).toArgb(),
                            Color(0xFFFF6B6B).toArgb(),
                            Color(0xFF9B59B6).toArgb()
                        ),
                        position = Position.Relative(0.5, 0.0),
                        emitter = Emitter(duration = 300, TimeUnit.MILLISECONDS).max(50), // Reduced from 300 to 50
                        timeToLive = 4000
                    ),
                    Party(
                        speed = 0f,
                        maxSpeed = 50f,
                        damping = 0.9f,
                        angle = Angle.TOP,
                        spread = Spread.WIDE,
                        colors = listOf(
                            Color(0xFF4A90E2).toArgb(),
                            Color(0xFF50C878).toArgb(),
                            Color(0xFFFFD700).toArgb(),
                            Color(0xFFFF6B6B).toArgb(),
                            Color(0xFF9B59B6).toArgb()
                        ),
                        position = Position.Relative(0.5, 1.0),
                        emitter = Emitter(duration = 300, TimeUnit.MILLISECONDS).max(50), // Reduced from 300 to 50
                        timeToLive = 4000
                    )
                ),
                modifier = Modifier.fillMaxSize()
            )
            
            // Avatar in center with celebration message
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "celebration")
                
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.9f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )
                
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.8f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )
                
                // Avatar
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale)
                        .alpha(alpha)
                        .clip(RoundedCornerShape(100.dp))
                        .background(LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(avatarIcon, fontSize = 120.sp)
                }
                
                // Celebration message
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎉 Goal Achieved!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A90E2),
                        modifier = Modifier.alpha(alpha)
                    )
                    Text(
                        text = "You've reached your daily hydration goal!",
                        fontSize = 18.sp,
                        color = BackgroundWhite,
                        modifier = Modifier.alpha(alpha)
                    )
                }
            }
        }
    }
}

@Composable
fun StarConfettiEffect() {
    val density = LocalDensity.current
    var screenSize by remember { mutableStateOf(IntSize.Zero) }
    val starIcons = listOf("⭐", "✨", "🌟", "💫", "⭐")
    val starCount = 50 // Reduced from 150 to 50 to prevent OutOfMemoryError
    
    val stars = remember {
        List(starCount) {
            StarAnimationData(
                startX = Random.nextFloat(),
                startY = -0.1f,
                rotation = Random.nextFloat() * 360f,
                size = Random.nextFloat() * 0.03f + 0.02f,
                duration = Random.nextLong(3000, 6000)
            )
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                screenSize = coordinates.size
            }
    ) {
        // Render stars in batches to reduce memory usage
        stars.chunked(10).forEachIndexed { batchIndex, batch ->
            batch.forEachIndexed { index, starData ->
                val globalIndex = batchIndex * 10 + index
                val infiniteTransition = rememberInfiniteTransition(label = "star_$globalIndex")
                
                val y by infiniteTransition.animateFloat(
                    initialValue = starData.startY,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = starData.duration.toInt(),
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "y_$globalIndex"
                )
                
                val rotation by infiniteTransition.animateFloat(
                    initialValue = starData.rotation,
                    targetValue = starData.rotation + 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 2000,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation_$globalIndex"
                )
                
                val calculatedAlpha = when {
                    y < 0 -> 0f
                    y > 1f -> 0f
                    else -> 1f
                }
                
                if (screenSize.width > 0 && screenSize.height > 0 && y >= -0.1f && y <= 1.1f) {
                    Text(
                        text = starIcons[globalIndex % starIcons.size],
                        fontSize = with(density) { 
                            (screenSize.height * starData.size).toDp().toSp() 
                        },
                        modifier = Modifier
                            .offset(
                                x = with(density) { 
                                    (screenSize.width * starData.startX).toDp() 
                                },
                                y = with(density) { 
                                    (screenSize.height * y).toDp() 
                                }
                            )
                            .rotate(rotation)
                            .alpha(calculatedAlpha),
                        color = Color.White
                    )
                }
            }
        }
    }
}

data class StarAnimationData(
    val startX: Float,
    val startY: Float,
    val rotation: Float,
    val size: Float,
    val duration: Long
)

@Composable
fun ProgressFeedbackOverlay(
    show: Boolean,
    message: String,
    avatarIcon: String,
    onDismiss: () -> Unit
) {
    if (show) {
        LaunchedEffect(show) {
            delay(2000)
            onDismiss()
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "progress")
            
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )
            
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.7f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(scale)
                        .alpha(alpha)
                        .clip(RoundedCornerShape(75.dp))
                        .background(LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(avatarIcon, fontSize = 90.sp)
                }
                
                // Feedback message
                Text(
                    text = message,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BackgroundWhite,
                    modifier = Modifier.alpha(alpha)
                )
            }
        }
    }
}

@Composable
fun CoinsEarnedOverlay(
    show: Boolean,
    amount: Int,
    onDismiss: () -> Unit
) {
    if (show) {
        LaunchedEffect(show) {
            delay(2000)
            onDismiss()
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "coins")
            
            val yOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -100f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "yOffset"
            )
            
            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 2000
                        1.0f at 0
                        1.0f at 1000
                        0.0f at 2000
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "alpha"
            )
            
            Column(
                modifier = Modifier
                    .offset(y = yOffset.dp)
                    .alpha(alpha)
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "💰 +$amount Coins!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
                Text(
                    text = "Goal Completed!",
                    fontSize = 18.sp,
                    color = BackgroundWhite
                )
            }
        }
    }
}
