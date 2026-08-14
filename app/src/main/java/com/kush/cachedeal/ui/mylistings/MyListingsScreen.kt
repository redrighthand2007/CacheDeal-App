package com.kush.cachedeal.ui.mylistings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.kush.cachedeal.core.designsystem.component.CategoryChip
import com.kush.cachedeal.core.designsystem.component.DealButton
import com.kush.cachedeal.core.mock.MockData
import com.kush.cachedeal.core.model.Item
import com.kush.cachedeal.ui.navigation.OffersRoute
import com.kush.cachedeal.ui.navigation.PostItemRoute
import kotlinx.coroutines.delay

// ── Data ─────────────────────────────────────────────────────────────────────

private data class FilterTab(val label: String, val key: String?)

private val filterTabs = listOf(
    FilterTab("All", null),
    FilterTab("Open", "open"),
    FilterTab("Locked", "locked"),
    FilterTab("Sold", "sold"),
)

private val mockOfferCounts = mapOf(
    "item9" to 3,
    "item10" to 1,
)

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsScreen(navController: NavController) {

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val filteredItems = remember(selectedTabIndex) {
        val key = filterTabs[selectedTabIndex].key
        if (key == null) MockData.myItems else MockData.myItems.filter { it.status == key }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Listings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                .height(3.dp)
                                .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                },
                divider = {}
            ) {
                filterTabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selectedTabIndex == index)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // Content
            AnimatedContent(
                targetState = filteredItems,
                transitionSpec = {
                    fadeIn(tween(220)) togetherWith fadeOut(tween(150))
                },
                label = "listings_content"
            ) { items ->
                if (items.isEmpty()) {
                    MyListingsEmptyState(
                        onPostClick = { navController.navigate(PostItemRoute) }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp,
                            top = 12.dp, bottom = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                            val offerCount = mockOfferCounts[item.id] ?: 0
                            AnimatedListingCard(
                                item = item,
                                offerCount = offerCount,
                                index = index,
                                onClick = {
                                    if (item.status == "open") {
                                        navController.navigate(OffersRoute(item.id))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Animated Wrapper ─────────────────────────────────────────────────────────

@Composable
private fun AnimatedListingCard(
    item: Item,
    offerCount: Int,
    index: Int,
    onClick: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(item.id) {
        delay(index * 60L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { it / 2 },
            animationSpec = tween(durationMillis = 380, easing = EaseOutCubic)
        ) + fadeIn(tween(300))
    ) {
        MyListingCard(
            item = item,
            offerCount = offerCount,
            onClick = onClick
        )
    }
}

// ── Card ─────────────────────────────────────────────────────────────────────

@Composable
private fun MyListingCard(
    item: Item,
    offerCount: Int,
    onClick: () -> Unit,
) {
    val isClickable = item.status == "open"
    val cardAlpha by animateFloatAsState(
        targetValue = if (item.status == "sold") 0.55f else 1f,
        label = "card_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .clickable(
                enabled = isClickable,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            AsyncImage(
                model = item.photoUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
            )

            Spacer(Modifier.width(12.dp))

            // Info column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "₹${"%.0f".format(item.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryChip(
                        label = item.category,
                        selected = false,
                        modifier = Modifier.height(26.dp)
                    )
                    StatusBadge(status = item.status)
                }
            }

            Spacer(Modifier.width(8.dp))

            // Offer count pill
            if (offerCount > 0) {
                OfferCountPill(count = offerCount)
            }
        }
    }
}

// ── Status Badge ─────────────────────────────────────────────────────────────

@Composable
private fun StatusBadge(status: String) {
    data class BadgeColors(val bg: Color, val fg: Color, val label: String)

    val colors = when (status) {
        "open"   -> BadgeColors(Color(0xFF1B5E20), Color(0xFF69F0AE), "Open")
        "locked" -> BadgeColors(Color(0xFF4A3000), Color(0xFFFFD54F), "Locked")
        "sold"   -> BadgeColors(Color(0xFF2A2A2A), Color(0xFF9E9E9E), "Sold")
        else     -> BadgeColors(Color(0xFF2A2A2A), Color(0xFF9E9E9E), status)
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.bg)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = colors.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.fg
        )
    }
}

// ── Offer Count Pill ─────────────────────────────────────────────────────────

@Composable
private fun OfferCountPill(count: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$count offer${if (count > 1) "s" else ""}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 11.sp
        )
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
private fun MyListingsEmptyState(onPostClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "📦", fontSize = 72.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "No listings yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Items you list for sale will appear here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            DealButton(
                text = "Post Your First Item",
                onClick = onPostClick,
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
    }
}
