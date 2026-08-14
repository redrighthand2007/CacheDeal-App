package com.kush.cachedeal.ui.deals

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.kush.cachedeal.core.designsystem.component.DealButton
import com.kush.cachedeal.core.designsystem.component.DealOutlinedButton
import com.kush.cachedeal.core.designsystem.theme.GreenDot
import com.kush.cachedeal.core.designsystem.theme.RedDot
import com.kush.cachedeal.core.mock.MockData
import com.kush.cachedeal.core.model.Deal
import com.kush.cachedeal.ui.navigation.HomeRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ────────────────────────────────────────────────────────────────────────────
// Data helpers
// ────────────────────────────────────────────────────────────────────────────

private enum class DealTab(val label: String) {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    EXPIRED("Expired")
}

/** Milliseconds remaining until deadline. Negative = past due. */
private fun Deal.msRemaining(): Long =
    completionDeadline.toDate().time - System.currentTimeMillis()

/** 0.0 → 1.0 fraction of time already elapsed since lock. */
private fun Deal.elapsedFraction(): Float {
    val total = completionDeadline.toDate().time - lockedAt.toDate().time
    val elapsed = System.currentTimeMillis() - lockedAt.toDate().time
    return if (total <= 0L) 1f else (elapsed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
}

private fun formatCountdown(ms: Long): String {
    if (ms <= 0L) return "Deadline passed"
    val totalSeconds = ms / 1000L
    val days = totalSeconds / 86400L
    val hours = (totalSeconds % 86400L) / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    return when {
        days > 0 -> "${days}d ${hours}h remaining"
        hours > 0 -> "${hours}h ${minutes}m remaining"
        else -> "${minutes}m remaining"
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Screen
// ────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsScreen(navController: NavController) {

    val currentUser = MockData.currentUser

    // Mutable local state so we can flip deal.status when user completes a deal
    val activeDeals = remember { mutableStateListOf(*MockData.activeDeals.toTypedArray()) }
    val completedDeals = remember { mutableStateListOf<Deal>() }
    val expiredDeals = remember { mutableStateListOf<Deal>() }

    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dialog state
    var completeDialogDeal by remember { mutableStateOf<Deal?>(null) }
    var relistDialogDeal by remember { mutableStateOf<Deal?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Deals",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(HomeRoute) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ── Animated Tab Row ──────────────────────────────────────────
            DealTabRow(
                tabs = DealTab.entries,
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // ── Tab Content ───────────────────────────────────────────────
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (slideInVertically(tween(280)) { it * direction } + fadeIn(tween(280)))
                        .togetherWith(slideOutVertically(tween(200)) { -it * direction } + fadeOut(tween(200)))
                },
                label = "tab_content"
            ) { tabIndex ->
                when (DealTab.entries[tabIndex]) {
                    DealTab.ACTIVE -> {
                        if (activeDeals.isEmpty()) {
                            ActiveEmptyState(onBrowse = { navController.navigate(HomeRoute) })
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                            ) {
                                itemsIndexed(activeDeals, key = { _, d -> d.id }) { index, deal ->
                                    AnimatedDealCard(
                                        deal = deal,
                                        currentUserId = currentUser.uid,
                                        index = index,
                                        onMarkComplete = { completeDialogDeal = it },
                                        onRelist = { relistDialogDeal = it },
                                        onWhatsApp = {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Opening WhatsApp... 💬")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    DealTab.COMPLETED -> {
                        if (completedDeals.isEmpty()) {
                            TabEmptyState(
                                emoji = "✅",
                                title = "No completed deals yet",
                                subtitle = "Deals you mark as completed will appear here."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(completedDeals, key = { _, d -> d.id }) { _, deal ->
                                    FinishedDealRow(deal = deal)
                                }
                            }
                        }
                    }

                    DealTab.EXPIRED -> {
                        if (expiredDeals.isEmpty()) {
                            TabEmptyState(
                                emoji = "⌛",
                                title = "No expired deals",
                                subtitle = "Deals that passed their deadline without completion show here."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(expiredDeals, key = { _, d -> d.id }) { _, deal ->
                                    FinishedDealRow(deal = deal)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Confirm Complete Dialog ───────────────────────────────────────────
    completeDialogDeal?.let { deal ->
        AlertDialog(
            onDismissRequest = { completeDialogDeal = null },
            title = {
                Text(
                    "Confirm Deal Completion",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to mark \"${deal.itemTitle}\" as completed? " +
                            "This will award you a Green Dot 🟢.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idx = activeDeals.indexOfFirst { it.id == deal.id }
                        if (idx >= 0) {
                            val updated = activeDeals[idx].copy(status = "completed")
                            activeDeals.removeAt(idx)
                            completedDeals.add(0, updated)
                        }
                        completeDialogDeal = null
                        scope.launch {
                            snackbarHostState.showSnackbar("🎉 Deal completed! +1 Green Dot")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDot)
                ) {
                    Text("Confirm ✅", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { completeDialogDeal = null }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }

    // ── Confirm Re-list Dialog ────────────────────────────────────────────
    relistDialogDeal?.let { deal ->
        AlertDialog(
            onDismissRequest = { relistDialogDeal = null },
            title = {
                Text(
                    "Re-list Item?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Re-listing \"${deal.itemTitle}\" will mark this deal as expired. " +
                            "The buyer will receive a 🔴 Red Dot for not completing the deal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idx = activeDeals.indexOfFirst { it.id == deal.id }
                        if (idx >= 0) {
                            val updated = activeDeals[idx].copy(status = "expired")
                            activeDeals.removeAt(idx)
                            expiredDeals.add(0, updated)
                        }
                        relistDialogDeal = null
                        scope.launch {
                            snackbarHostState.showSnackbar("🔴 Item re-listed. Buyer gets a red dot.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDot)
                ) {
                    Text("Re-list 🔄", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { relistDialogDeal = null }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Animated Tab Row with sliding underline
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun DealTabRow(
    tabs: List<DealTab>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabCount = tabs.size

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab labels
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedIndex
                val alphaAnim by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0.45f,
                    animationSpec = tween(220),
                    label = "tab_alpha_$index"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = alphaAnim)
                    )
                }
            }
        }

        // Animated underline indicator
        val underlineFraction by animateFloatAsState(
            targetValue = selectedIndex.toFloat() / tabCount.toFloat(),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "underline_position"
        )

        // We need screen width to compute pixel offset, so use BoxWithConstraints approach via fillMaxWidth fraction
        // The underline width is 1/tabCount of the full row; we shift it by underlineFraction * full width
        // This is achieved by nesting inside a full-width Box and using fillMaxWidth(fraction) + offset trick
        val tabWidthFraction = 1f / tabCount
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
        ) {
            // Leading spacer whose width equals the animated underline offset
            Spacer(
                modifier = Modifier.fillMaxWidth(underlineFraction.coerceIn(0f, 1f - tabWidthFraction))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(tabWidthFraction / (1f - underlineFraction.coerceIn(0f, 1f - tabWidthFraction)).coerceAtLeast(tabWidthFraction))
                    .height(3.dp)
                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
            )
        }

        // Bottom divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomStart)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        )
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Deal Card with entrance animation
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun AnimatedDealCard(
    deal: Deal,
    currentUserId: String,
    index: Int,
    onMarkComplete: (Deal) -> Unit,
    onRelist: (Deal) -> Unit,
    onWhatsApp: () -> Unit
) {
    val visible = remember { mutableStateOf(false) }
    val scale = remember { Animatable(0.88f) }
    val slideOffset = remember { Animatable(48f) }

    LaunchedEffect(Unit) {
        delay(index * 90L)
        visible.value = true
        launch {
            scale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        slideOffset.animateTo(0f, animationSpec = tween(340, easing = FastOutSlowInEasing))
    }

    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn(tween(240))
    ) {
        Box(
            modifier = Modifier
                .scale(scale.value)
                .offset(y = slideOffset.value.dp)
        ) {
            ActiveDealCard(
                deal = deal,
                currentUserId = currentUserId,
                onMarkComplete = onMarkComplete,
                onRelist = onRelist,
                onWhatsApp = onWhatsApp
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Active Deal Card
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun ActiveDealCard(
    deal: Deal,
    currentUserId: String,
    onMarkComplete: (Deal) -> Unit,
    onRelist: (Deal) -> Unit,
    onWhatsApp: () -> Unit
) {
    val isBuyer = deal.buyerId == currentUserId
    val counterpartyName = if (isBuyer) deal.sellerName else deal.buyerName

    // Live countdown — ticks every second
    var msRemaining by remember { mutableLongStateOf(deal.msRemaining()) }
    LaunchedEffect(deal.id) {
        while (true) {
            delay(1000L)
            msRemaining = deal.msRemaining()
        }
    }

    val isUrgent = msRemaining in 1..43_200_000L   // < 12 hours
    val isPastDeadline = msRemaining <= 0L
    val elapsedFraction = deal.elapsedFraction()

    // Circular arc sweep — animated once on enter
    val animatedSweep by animateFloatAsState(
        targetValue = elapsedFraction * 360f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "arc_sweep_${deal.id}"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header: thumbnail + info + arc ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AsyncImage(
                    model = deal.itemPhotoUrl.ifBlank { null },
                    contentDescription = deal.itemTitle,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            MaterialTheme.shapes.medium
                        ),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deal.itemTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = counterpartyName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        RolePill(isBuyer = isBuyer)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "₹${"%.0f".format(deal.finalPrice)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Circular progress arc
                CircularTimeArc(
                    sweepAngle = animatedSweep,
                    isUrgent = isUrgent || isPastDeadline,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            // ── Countdown Timer Row ───────────────────────────────────────
            val timerColor = when {
                isPastDeadline -> MaterialTheme.colorScheme.error
                isUrgent -> Color(0xFFFF6F00)
                else -> MaterialTheme.colorScheme.primary
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(timerColor.copy(alpha = 0.12f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isPastDeadline) "⚠️ Deadline passed" else "⏳ ${formatCountdown(msRemaining)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = timerColor,
                    fontWeight = FontWeight.SemiBold
                )
                if (isUrgent && !isPastDeadline) {
                    PulsingDot(color = Color(0xFFFF6F00))
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Action Buttons ────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                DealOutlinedButton(
                    text = "Open WhatsApp 💬",
                    onClick = onWhatsApp
                )

                DealButton(
                    text = "Mark as Completed ✅",
                    onClick = { onMarkComplete(deal) }
                )

                // Re-list only for seller after deadline
                if (!isBuyer && isPastDeadline) {
                    OutlinedButton(
                        onClick = { onRelist(deal) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text(
                            text = "Re-list Item 🔄",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Circular Time Arc  (Canvas)
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun CircularTimeArc(
    sweepAngle: Float,
    isUrgent: Boolean,
    modifier: Modifier = Modifier
) {
    val arcColor = if (isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Canvas(modifier = modifier) {
        val strokePx = 6.dp.toPx()
        val inset = strokePx / 2f
        val arcRect = Rect(inset, inset, size.width - inset, size.height - inset)

        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = arcRect.topLeft,
            size = arcRect.size,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )
        if (sweepAngle > 0f) {
            drawArc(
                color = arcColor,
                startAngle = -90f,
                sweepAngle = sweepAngle.coerceAtMost(360f),
                useCenter = false,
                topLeft = arcRect.topLeft,
                size = arcRect.size,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Role Pill — BUYER (teal) / SELLER (amber)
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun RolePill(isBuyer: Boolean) {
    val label = if (isBuyer) "BUYER" else "SELLER"
    val bg = if (isBuyer) Color(0xFF004F58) else Color(0xFF3B2800)
    val fg = if (isBuyer) Color(0xFF00E5FF) else Color(0xFFFFB300)

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = fg,
            letterSpacing = 0.8.sp
        )
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Pulsing urgency dot
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun PulsingDot(color: Color) {
    val alpha = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        while (true) {
            alpha.animateTo(0.15f, animationSpec = tween(700, easing = LinearEasing))
            alpha.animateTo(1f, animationSpec = tween(700, easing = LinearEasing))
        }
    }
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha.value))
    )
}

// ────────────────────────────────────────────────────────────────────────────
// Completed / Expired mini row
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun FinishedDealRow(deal: Deal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = deal.itemPhotoUrl.ifBlank { null },
                contentDescription = deal.itemTitle,
                modifier = Modifier
                    .size(52.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deal.itemTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "₹${"%.0f".format(deal.finalPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = if (deal.status == "completed") "✅" else "⌛",
                fontSize = 22.sp
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Empty States
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun TabEmptyState(emoji: String, title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = emoji, fontSize = 56.sp)
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ActiveEmptyState(onBrowse: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "🤝", fontSize = 64.sp)
            Text(
                text = "No active deals",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Browse the marketplace and make your first deal on campus!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onBrowse,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Browse Marketplace 🛒",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
