package com.kush.cachedeal.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kush.cachedeal.core.designsystem.component.DealButton
import com.kush.cachedeal.ui.navigation.OnboardingRoute
import kotlinx.coroutines.delay

// ─── Auth step enum ──────────────────────────────────────────────────────────
private enum class AuthStep { PHONE, OTP }

// ─── Screen ──────────────────────────────────────────────────────────────────
@Composable
fun AuthScreen(navController: NavController) {
    var currentStep by remember { mutableStateOf(AuthStep.PHONE) }
    var phone by remember { mutableStateOf("") }

    // Form slide-in animation
    val formOffsetY = remember { Animatable(80f) }
    val formAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        formOffsetY.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(Unit) {
        formAlpha.animateTo(1f, tween(600))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtle top gradient accent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            // ── Header ──────────────────────────────────────────────────────
            Text(
                text = "🏫",
                fontSize = 52.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "Welcome to CacheDeal",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when (currentStep) {
                    AuthStep.PHONE -> "Enter your college phone number to get started"
                    AuthStep.OTP   -> "Enter the 6-digit OTP sent to +91 $phone"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Animated step form card ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(top = formOffsetY.value.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        val direction = if (targetState == AuthStep.OTP) 1 else -1
                        ContentTransform(
                            targetContentEnter = slideInHorizontally(
                                initialOffsetX = { it * direction },
                                animationSpec = tween(400, easing = FastOutSlowInEasing)
                            ) + fadeIn(tween(400)),
                            initialContentExit = slideOutHorizontally(
                                targetOffsetX = { -it * direction },
                                animationSpec = tween(400, easing = FastOutSlowInEasing)
                            ) + fadeOut(tween(200))
                        )
                    },
                    label = "auth_step"
                ) { step ->
                    when (step) {
                        AuthStep.PHONE -> PhoneStep(
                            phone = phone,
                            onPhoneChange = { if (it.length <= 10 && it.all(Char::isDigit)) phone = it },
                            onSendOtp = { if (phone.length == 10) currentStep = AuthStep.OTP },
                            onGuestLogin = {
                                com.kush.cachedeal.core.mock.MockData.isGuestMode = true
                                navController.navigate(com.kush.cachedeal.ui.navigation.HomeRoute) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                        AuthStep.OTP -> OtpStep(
                            phone = phone,
                            onVerify = {
                                navController.navigate(OnboardingRoute) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onBack = { currentStep = AuthStep.PHONE }
                        )
                    }
                }
            }
        }
    }
}

// ─── Step 1: Phone Input ──────────────────────────────────────────────────────
@Composable
private fun PhoneStep(
    phone: String,
    onPhoneChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onGuestLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Phone Number",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // +91 prefix chip
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = "+91",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "10-digit number",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus(); onSendOtp() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        AnimatedVisibility(visible = phone.length in 1..9) {
            Text(
                text = "${10 - phone.length} more digits needed",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        DealButton(
            text = "Send OTP →",
            onClick = onSendOtp,
            enabled = phone.length == 10
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onGuestLogin) {
            Text(
                "Continue as Guest",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Step 2: OTP Input ────────────────────────────────────────────────────────
@Composable
private fun OtpStep(
    phone: String,
    onVerify: () -> Unit,
    onBack: () -> Unit
) {
    val otpDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    var isLoading by remember { mutableStateOf(false) }

    // Countdown timer state
    var countdown by remember { mutableIntStateOf(30) }
    var canResend by remember { mutableStateOf(false) }

    LaunchedEffect(canResend) {
        if (!canResend) {
            countdown = 30
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            canResend = true
        }
    }

    // Auto-focus first box on entry
    LaunchedEffect(Unit) {
        delay(100)
        focusRequesters[0].requestFocus()
    }

    val fullOtp = otpDigits.joinToString("")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Enter OTP",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 6 individual OTP boxes
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            otpDigits.forEachIndexed { index, digit ->
                val isFilled = digit.isNotEmpty()
                OutlinedTextField(
                    value = digit,
                    onValueChange = { input ->
                        val sanitized = input.filter(Char::isDigit)
                        when {
                            sanitized.isEmpty() -> {
                                otpDigits[index] = ""
                                if (index > 0) focusRequesters[index - 1].requestFocus()
                            }
                            sanitized.length == 1 -> {
                                otpDigits[index] = sanitized
                                if (index < 5) focusRequesters[index + 1].requestFocus()
                                else focusManager.clearFocus()
                            }
                            sanitized.length > 1 -> {
                                // Handle paste: distribute across boxes
                                sanitized.forEachIndexed { offset, c ->
                                    val target = index + offset
                                    if (target < 6) otpDigits[target] = c.toString()
                                }
                                val nextFocus = minOf(index + sanitized.length, 5)
                                if (index + sanitized.length >= 6) focusManager.clearFocus()
                                else focusRequesters[nextFocus].requestFocus()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                        .focusRequester(focusRequesters[index]),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = if (index < 5) ImeAction.Next else ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (isFilled)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        unfocusedContainerColor = if (isFilled)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        else
                            Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Resend OTP countdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (canResend) {
                TextButton(onClick = {
                    otpDigits.indices.forEach { otpDigits[it] = "" }
                    canResend = false
                    focusRequesters[0].requestFocus()
                }) {
                    Text(
                        text = "Resend OTP",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            } else {
                Text(
                    text = "Resend OTP in ",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(width = 36.dp, height = 24.dp)
                        .wrapContentWidth()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${countdown}s",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Verify button
        DealButton(
            text = "Verify OTP ✓",
            onClick = {
                if (fullOtp.length == 6) {
                    isLoading = true
                    onVerify()
                }
            },
            enabled = fullOtp.length == 6,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Back to phone
        TextButton(
            onClick = {
                otpDigits.indices.forEach { otpDigits[it] = "" }
                onBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "← Change Number",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
