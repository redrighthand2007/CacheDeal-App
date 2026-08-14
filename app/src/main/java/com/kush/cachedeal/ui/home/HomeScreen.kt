package com.kush.cachedeal.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kush.cachedeal.core.designsystem.component.CategoryChip
import com.kush.cachedeal.core.designsystem.component.ItemCard
import com.kush.cachedeal.core.mock.MockData
import com.kush.cachedeal.core.model.Category
import com.kush.cachedeal.ui.navigation.DealsRoute
import com.kush.cachedeal.ui.navigation.ItemDetailRoute
import com.kush.cachedeal.ui.navigation.MyListingsRoute
import com.kush.cachedeal.ui.navigation.PostItemRoute
import com.kush.cachedeal.ui.navigation.ProfileRoute

// ─── Nav destinations ─────────────────────────────────────────────────────────

private data class NavDestination(val label: String, val emoji: String)

private val navDestinations = listOf(
    NavDestination("Browse", "🏠"),
    NavDestination("Listings", "📦"),
    NavDestination("Deals", "🤝"),
    NavDestination("Profile", "👤")
)

// ─── HomeScreen ───────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(navController: NavController) {

    // Bottom nav state
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Category filter state — null means "All"
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    // Near-me toggle state
    var nearMeEnabled by remember { mutableStateOf(false) }

    // Notification badge
    var hasNotification by remember { mutableStateOf(true) }

    // Grid scroll state — used for FAB hide/show and top bar shrink
    val gridState = rememberLazyGridState()
    val isFabVisible by remember {
        derivedStateOf { gridState.firstVisibleItemIndex == 0 && !MockData.isGuestMode }
    }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isScrolled by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 10
        }
    }
    val isScrollingDown by remember {
        derivedStateOf {
            gridState.firstVisibleItemScrollOffset > 120 || gridState.firstVisibleItemIndex > 1
        }
    }

    // Filtered items
    val filteredItems = remember(selectedCategory) {
        if (selectedCategory == null) MockData.items
        else MockData.items.filter { it.category == selectedCategory!!.displayName }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        bottomBar = {
            CacheDealBottomBar(
                selectedIndex = selectedTabIndex,
                onTabSelected = { index ->
                    if (MockData.isGuestMode && index != 0) {
                        scope.launch { snackbarHostState.showSnackbar("Sign in to access this feature") }
                    } else {
                        selectedTabIndex = index
                        when (index) {
                            0 -> { /* Stay on HomeScreen */ }
                            1 -> navController.navigate(MyListingsRoute) { launchSingleTop = true }
                            2 -> navController.navigate(DealsRoute) { launchSingleTop = true }
                            3 -> navController.navigate(ProfileRoute) { launchSingleTop = true }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isScrollingDown,
                enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                exit = scaleOut(tween(200)) + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate(PostItemRoute) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Post Item",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // ── Animated Collapsing Top Header ──────────────────────────────
            CacheDealTopHeader(
                isScrolled = isScrolled,
                hasNotification = hasNotification,
                onNotificationClick = { hasNotification = false }
            )

            // ── Scrollable grid with inlined header items ────────────────────
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Search bar
                item(span = { GridItemSpan(2) }) {
                    SearchBarUI()
                }

                // Category chips
                item(span = { GridItemSpan(2) }) {
                    CategoryChipRow(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                }

                // Near Me toggle
                item(span = { GridItemSpan(2) }) {
                    NearMeToggle(
                        nearMeEnabled = nearMeEnabled,
                        onToggle = { nearMeEnabled = it }
                    )
                }

                // Section label + count
                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedCategory == null) "All Items"
                            else selectedCategory!!.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        AnimatedContent(
                            targetState = filteredItems.size,
                            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                            label = "item_count"
                        ) { count ->
                            Text(
                                text = "$count items",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Empty state or item cards
                if (filteredItems.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        EmptyStateUI(category = selectedCategory)
                    }
                } else {
                    items(
                        items = filteredItems,
                        key = { it.id }
                    ) { item ->
                        ItemCard(
                            item = item,
                            onClick = {
                            if (MockData.isGuestMode) {
                                scope.launch { snackbarHostState.showSnackbar("Sign in to view details or buy items") }
                            } else {
                                navController.navigate(ItemDetailRoute(item.id))
                            }
                        },
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(300),
                                fadeOutSpec = tween(200)
                            )
                        )
                    }
                }
            }
        }
    }
}

// ─── Top Header ───────────────────────────────────────────────────────────────

@Composable
private fun CacheDealTopHeader(
    isScrolled: Boolean,
    hasNotification: Boolean,
    onNotificationClick: () -> Unit
) {
    val headerElevation by animateDpAsState(
        targetValue = if (isScrolled) 8.dp else 0.dp,
        label = "header_elevation"
    )
    val greetingAlpha by animateFloatAsState(
        targetValue = if (isScrolled) 0f else 1f,
        animationSpec = tween(250),
        label = "greeting_alpha"
    )
    val headerHeight by animateDpAsState(
        targetValue = if (isScrolled) 64.dp else 130.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "header_height"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = headerElevation, clip = false),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = if (isScrolled) 4.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .height(headerHeight)
        ) {
            // Branding row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "Cache",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Deal",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = (-0.5).sp
                    )
                }

                // Notification bell with dot
                Box {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = if (hasNotification) Icons.Filled.Notifications
                            else Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = if (hasNotification) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    if (hasNotification) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 8.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5252))
                        )
                    }
                }
            }

            // Greeting row (collapses on scroll)
            if (greetingAlpha > 0f) {
                Spacer(Modifier.height(8.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Hey Kush! \uD83D\uDC4B",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = greetingAlpha)
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = MockData.currentUser.block,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = greetingAlpha),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

// ─── Search Bar ───────────────────────────────────────────────────────────────

@Composable
private fun SearchBarUI() {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        animationSpec = tween(200),
        label = "search_border_color"
    )
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 1.dp,
        label = "search_border_width"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(width = borderWidth, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { isFocused = !isFocused },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "\uD83D\uDD0D", fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Search cycles, notes, food\u2026",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

// ─── Category Chip Row ────────────────────────────────────────────────────────

@Composable
private fun CategoryChipRow(
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            CategoryChip(
                label = "All",
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
        }
        items(Category.entries) { category ->
            CategoryChip(
                label = category.displayName,
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

// ─── Near Me Segmented Toggle ─────────────────────────────────────────────────

@Composable
private fun NearMeToggle(
    nearMeEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val options = listOf("All Campus", "Near Me \uD83D\uDCCD")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                val isSelected = if (nearMeEnabled) index == 1 else index == 0

                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                    else Color.Transparent,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "toggle_bg_$index"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "toggle_text_$index"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(46.dp))
                        .background(bgColor)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onToggle(index == 1) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = textColor
                    )
                }
            }
        }
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────

@Composable
private fun EmptyStateUI(category: Category?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "\uD83E\uDEB9", fontSize = 56.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Nothing here yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (category != null) "No ${category.displayName} listed on campus"
            else "Be the first to post something!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── Bottom Navigation Bar ────────────────────────────────────────────────────

@Composable
private fun CacheDealBottomBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            navDestinations.forEachIndexed { index, dest ->
                val isSelected = selectedIndex == index
                val iconScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "nav_scale_$index"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "nav_label_$index"
                )

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            // Selection indicator pill
                            AnimatedVisibility(
                                visible = isSelected,
                                enter = slideInVertically { it } + fadeIn(),
                                exit = slideOutVertically { it } + fadeOut()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(32.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                            if (!isSelected) Spacer(Modifier.height(3.dp))
                            Spacer(Modifier.height(4.dp))
                            // Emoji icon
                            Text(
                                text = dest.emoji,
                                fontSize = (20 * iconScale).sp
                            )
                        }
                    },
                    label = {
                        Text(
                            text = dest.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = labelColor
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ),
                    alwaysShowLabel = true
                )
            }
        }
    }
}
