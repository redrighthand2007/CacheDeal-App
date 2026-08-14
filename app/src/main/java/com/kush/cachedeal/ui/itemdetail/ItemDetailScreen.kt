package com.kush.cachedeal.ui.itemdetail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.kush.cachedeal.core.designsystem.component.CategoryChip
import com.kush.cachedeal.core.designsystem.component.DealButton
import com.kush.cachedeal.core.designsystem.component.DotBadge
import com.kush.cachedeal.core.mock.MockData
import com.kush.cachedeal.core.model.Item
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CURRENT_USER_ID = "user_kush"

@Composable
fun ItemDetailScreen(
    itemId: String,
    navController: NavController
) {
    val item = remember(itemId) {
        MockData.items.find { it.id == itemId }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Slide-up animation for content
    val contentOffsetY = remember { Animatable(120f) }
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            contentOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        launch {
            contentAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400)
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (item == null) {
            // Item not found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "😕",
                        fontSize = 48.sp
                    )
                    Text(
                        text = "Item not found",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "This listing may have been removed.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                // ── Scrollable content ──────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .navigationBarsPadding()
                ) {
                    // ── Parallax Hero Image ─────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        AsyncImage(
                            model = item.photoUrl.ifBlank { null },
                            contentDescription = item.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Gradient overlay at bottom of image
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                            MaterialTheme.colorScheme.background
                                        ),
                                        startY = 0f,
                                        endY = Float.POSITIVE_INFINITY
                                    )
                                )
                        )
                        // Top gradient for status bar legibility
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .align(Alignment.TopStart)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.35f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // ── Animated Content ────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(
                                translationY = contentOffsetY.value,
                                alpha = contentAlpha.value
                            )
                            .padding(horizontal = 20.dp)
                    ) {
                        Spacer(Modifier.height(4.dp))

                        // Category chip
                        CategoryChip(
                            label = item.category,
                            selected = true
                        )

                        Spacer(Modifier.height(10.dp))

                        // Title
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(Modifier.height(10.dp))

                        // Price
                        Text(
                            text = "₹${"%.0f".format(item.price)}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(16.dp))

                        // Seller row
                        SellerRow(item = item)

                        Spacer(Modifier.height(20.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            thickness = 1.dp
                        )

                        Spacer(Modifier.height(20.dp))

                        // Description
                        Text(
                            text = "About this item",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 24.sp
                        )

                        Spacer(Modifier.height(24.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            thickness = 1.dp
                        )

                        Spacer(Modifier.height(24.dp))

                        // ── Make an Offer / Owner section ───────────────────
                        if (item.sellerId == CURRENT_USER_ID) {
                            YourListingCard()
                        } else {
                            MakeOfferSection(
                                item = item,
                                snackbarHostState = snackbarHostState,
                                scope = scope
                            )
                        }

                        Spacer(Modifier.height(32.dp))
                    }
                }

                // ── Floating back button overlaid on image ──────────────────
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(start = 12.dp, top = 8.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Seller row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SellerRow(item: Item) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Avatar circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.sellerName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = item.sellerBlock,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        DotBadge(
            greenDots = item.sellerGreenDots,
            redDots = item.sellerRedDots
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Make an Offer section
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MakeOfferSection(
    item: Item,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    var offerAmount by remember { mutableStateOf("") }
    var offerNote by remember { mutableStateOf("") }
    var amountError by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "💸",
                fontSize = 22.sp
            )
            Text(
                text = "Make an Offer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Listed at ₹${"%.0f".format(item.price)} — negotiate your price below",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        // Amount field
        OutlinedTextField(
            value = offerAmount,
            onValueChange = { input ->
                val filtered = input.filter { it.isDigit() || it == '.' }
                offerAmount = filtered
                if (filtered.isNotBlank()) amountError = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Your offer amount") },
            prefix = {
                Text(
                    text = "₹",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            isError = amountError,
            supportingText = if (amountError) {
                { Text("Please enter a valid offer amount", color = MaterialTheme.colorScheme.error) }
            } else null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            shape = MaterialTheme.shapes.medium,
            colors = offerTextFieldColors()
        )

        Spacer(Modifier.height(12.dp))

        // Note field (optional)
        OutlinedTextField(
            value = offerNote,
            onValueChange = { offerNote = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Add a note (optional)") },
            placeholder = { Text("e.g. I can pick it up from your hostel") },
            minLines = 2,
            maxLines = 3,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            shape = MaterialTheme.shapes.medium,
            colors = offerTextFieldColors()
        )

        Spacer(Modifier.height(20.dp))

        DealButton(
            text = "Submit Offer",
            isLoading = isSubmitting,
            onClick = {
                val amount = offerAmount.toDoubleOrNull()
                amountError = amount == null || amount <= 0.0

                if (!amountError) {
                    isSubmitting = true
                    scope.launch {
                        delay(1000L)
                        isSubmitting = false
                        offerAmount = ""
                        offerNote = ""
                        snackbarHostState.showSnackbar("✅ Offer submitted!")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Your listing card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun YourListingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "🏷️",
                fontSize = 32.sp
            )
            Column {
                Text(
                    text = "This is your listing",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Buyers can browse and send you offers. Check your listings tab to manage it.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Text field colors
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun offerTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    errorBorderColor = MaterialTheme.colorScheme.error,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    errorLabelColor = MaterialTheme.colorScheme.error,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    errorSupportingTextColor = MaterialTheme.colorScheme.error
)
