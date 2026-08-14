package com.kush.cachedeal.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kush.cachedeal.ui.navigation.AuthRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
    // --- Animatables ---
    val logoScale = remember { Animatable(0.3f) }
    val logoAlpha = remember { Animatable(0f) }
    val appNameOffsetY = remember { Animatable(60f) }
    val appNameAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }

    // Pulsing glow via infinite transition
    val infiniteTransition = rememberInfiniteTransition(label = "glow_pulse")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // --- Orchestrated animation sequence ---
    LaunchedEffect(Unit) {
        // Logo reveal: scale + fade in simultaneously
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700)
            )
        }

        delay(400)

        // App name slides up from below
        launch {
            appNameOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
        }
        launch {
            appNameAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }

        delay(350)

        // Tagline fades in last
        taglineAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700)
        )

        // Total ~2.5s then navigate
        delay(900)
        navController.navigate(AuthRoute) {
            popUpTo(0) { inclusive = true }
        }
    }

    // --- UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Glow + Logo stack
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(180.dp)
            ) {
                // Outer pulsing radial glow
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(glowScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0f)
                                )
                            )
                        )
                        .blur(28.dp)
                )

                // Inner circular gradient disc + logo text
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .scale(logoScale.value)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                ) {
                    Text(
                        text = "C${'$'}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 52.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = logoAlpha.value)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name — slides up + fades in
            Box(modifier = Modifier.padding(top = appNameOffsetY.value.dp)) {
                Text(
                    text = "CacheDeal",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = appNameAlpha.value),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline — fades in after name
            Text(
                text = "Buy. Sell. Deal. On Campus.",
                style = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 0.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = taglineAlpha.value),
                textAlign = TextAlign.Center
            )
        }

        // Bottom version watermark
        Text(
            text = "v1.0.0",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.outline.copy(alpha = taglineAlpha.value * 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
