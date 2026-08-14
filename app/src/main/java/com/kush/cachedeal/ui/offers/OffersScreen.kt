package com.kush.cachedeal.ui.offers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.kush.cachedeal.core.designsystem.component.DealButton
import com.kush.cachedeal.core.designsystem.component.DotBadge
import com.kush.cachedeal.core.mock.MockData
import com.kush.cachedeal.core.model.Offer
import kotlinx.coroutines.launch

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffersScreen(
    itemId: String,
    navController: NavController
) {
    val item = remember(itemId) { MockData.items.find { it.id == itemId } }

    // Local mutable offer state
    var offers by remember {
        mutableStateOf(
            MockData.offersForItem1.sortedByDescending { it.amount }
        )
    }

    var acceptedOfferId by remember { mutableStateOf<String?>(null) }
    var pendingOffer by remember { mutableStateOf<Offer?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Offers",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (item != null) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (item == null) {
            // Item not found state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Item not found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = 12.dp, bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Item Summary Card ──────────────────────────────────────────
            item(key = "item_summary") {
                ItemSummaryCard(
                    title = item.title,
                    photoUrl = item.photoUrl,
                    price = item.price
                )
            }

            // ── Sorting indicator ─────────────────────────────────────────
            item(key = "sort_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${offers.size} offer${if (offers.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Sorted by amount ↓",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Offer cards ───────────────────────────────────────────────
            if (offers.isEmpty()) {
                item(key = "empty") {
                    OffersEmptyState()
                }
            } else {
                itemsIndexed(offers, key = { _, o -> o.id }) { index, offer ->
                    val isAccepted = offer.id == acceptedOfferId
                    val isRejected = acceptedOfferId != null && !isAccepted
                    val targetAlpha = if (isRejected) 0.30f else 1f
                    val cardAlpha by animateFloatAsState(
                        targetValue = targetAlpha,
                        animationSpec = tween(600),
                        label = "offer_alpha_${offer.id}"
                    )

                    // Slide-in stagger
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(offer.id) {
                        kotlinx.coroutines.delay(index * 70L)
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(360, easing = EaseOutCubic)
                        ) + fadeIn(tween(280))
                    ) {
                        Box(modifier = Modifier.alpha(cardAlpha)) {
                            AnimatedOfferCard(
                                offer = offer,
                                isAccepted = isAccepted,
                                onAcceptClick = {
                                    if (acceptedOfferId == null) {
                                        pendingOffer = offer
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Confirmation Dialog ────────────────────────────────────────────────────
    pendingOffer?.let { offer ->
        AlertDialog(
            onDismissRequest = { pendingOffer = null },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = {
                Text(
                    text = "Accept this offer?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Accept ₹${"%.0f".format(offer.amount)} from ${offer.buyerName}?\n\nThis will reject all other offers and lock the deal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        acceptedOfferId = offer.id
                        offers = offers.map { o ->
                            when {
                                o.id == offer.id -> o.copy(status = "accepted")
                                else -> o.copy(status = "rejected")
                            }
                        }
                        pendingOffer = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Deal locked! 🔒")
                        }
                    }
                ) {
                    Text(
                        text = "Confirm",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingOffer = null }) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

// ── Item Summary Card ─────────────────────────────────────────────────────────

@Composable
private fun ItemSummaryCard(
    title: String,
    photoUrl: String,
    price: Double,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = photoUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Asking ₹${"%.0f".format(price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ── Animated Offer Card ───────────────────────────────────────────────────────

@Composable
private fun AnimatedOfferCard(
    offer: Offer,
    isAccepted: Boolean,
    onAcceptClick: () -> Unit,
) {
    val cardBg by animateColorAsState(
        targetValue = when {
            isAccepted -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
            offer.status == "rejected" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(600),
        label = "card_bg_${offer.id}"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isAccepted) 6.dp else 2.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Buyer info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = offer.buyerName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    DotBadge(
                        greenDots = offer.buyerGreenDots,
                        redDots = offer.buyerRedDots
                    )
                }
                Spacer(Modifier.width(12.dp))
                // Amount
                Text(
                    text = "₹${"%.0f".format(offer.amount)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Optional note
            if (!offer.note.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "\"${offer.note}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Accept button (only for pending offers when nothing accepted yet)
            if (offer.status == "pending") {
                Spacer(Modifier.height(12.dp))
                DealButton(
                    text = "Accept Offer",
                    onClick = onAcceptClick
                )
            }

            // Accepted label with animation
            AnimatedVisibility(
                visible = isAccepted,
                enter = expandVertically() + fadeIn()
            ) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "✅", fontSize = 14.sp)
                        Text(
                            text = "Offer Accepted — Deal Locked",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
private fun OffersEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "🤷", fontSize = 64.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "No offers yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "When buyers make offers, they'll appear here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}
