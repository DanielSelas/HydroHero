package com.danielsela.hydrohero.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danielsela.hydrohero.ui.theme.HydroInk
import com.danielsela.hydrohero.ui.theme.HydroInk2
import com.danielsela.hydrohero.ui.theme.HydroInk3
import com.danielsela.hydrohero.ui.theme.HydroLine
import com.danielsela.hydrohero.ui.theme.HydroPrimaryDeep
import com.danielsela.hydrohero.ui.theme.HydroPrimarySoft
import com.danielsela.hydrohero.ui.theme.HydroSurface2

private data class OnboardingStep(
    val mascotId: String,
    val title: String,
    val body: String,
)

private val steps = listOf(
    OnboardingStep(
        mascotId = "splash",
        title = "Meet Splash",
        body = "Your hydration buddy. Splash fills up as you drink and cheers you on all day.",
    ),
    OnboardingStep(
        mascotId = "sunny",
        title = "Sip, log, celebrate",
        body = "Tap a cup size to log a drink. Hit your daily goal to earn coins and build a streak.",
    ),
    OnboardingStep(
        mascotId = "berry",
        title = "Earn and decorate",
        body = "Spend coins in the shop on new characters, backgrounds and effects. Make it yours.",
    ),
)

/**
 * First-run intro. Shown once, then never again — the flag lives in
 * DataRepository and deliberately survives "Reset progress".
 */
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    val step = steps[index]
    val isLast = index == steps.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HydroSurface2)
            // enableEdgeToEdge() is on, so the intro has to inset itself or
            // "Skip" lands under the status bar.
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Skip stays available on every step except the last, where the primary
        // button already dismisses.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            if (!isLast) {
                Text(
                    "Skip",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroInk3,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onFinish)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                )
            } else {
                Spacer(Modifier.height(44.dp))
            }
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(HydroPrimarySoft),
            contentAlignment = Alignment.Center,
        ) {
            MascotById(id = step.mascotId, size = 120.dp)
        }

        Spacer(Modifier.height(40.dp))

        Text(
            step.title,
            style = MaterialTheme.typography.headlineLarge,
            color = HydroInk,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            step.body,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = HydroInk2,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.semantics {
                contentDescription = "Step ${index + 1} of ${steps.size}"
            },
        ) {
            steps.indices.forEach { dot ->
                val active = dot == index
                val width by animateFloatAsState(
                    targetValue = if (active) 26f else 8f,
                    animationSpec = tween(220),
                    label = "dotWidth",
                )
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width.dp)
                        .clip(CircleShape)
                        .background(if (active) HydroPrimaryDeep else HydroLine)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { if (isLast) onFinish() else index++ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HydroPrimaryDeep),
        ) {
            Text(
                if (isLast) "Get started" else "Next",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}
