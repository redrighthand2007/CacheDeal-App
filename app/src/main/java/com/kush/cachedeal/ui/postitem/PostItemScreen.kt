@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.kush.cachedeal.ui.postitem

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kush.cachedeal.core.designsystem.component.DealButton
import com.kush.cachedeal.core.model.Category
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PostItemScreen(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Form state
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Error states
    var categoryError by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }

    // Submission state
    var isSubmitting by remember { mutableStateOf(false) }

    // Staggered animation — 5 form sections
    val fieldCount = 5
    val offsetY = remember { List(fieldCount) { Animatable(80f) } }
    val alphas = remember { List(fieldCount) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        offsetY.forEachIndexed { index, anim ->
            delay(index * 80L)
            launch {
                anim.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                alphas[index].animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "List an Item",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {

            // ── 0: Photo Picker ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        translationY = offsetY[0].value,
                        alpha = alphas[0].value
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionLabel(text = "Photo")
                    Spacer(Modifier.height(8.dp))
                    DashedPhotoPickerBox(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("📷 Photo picker coming with backend!")
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── 1: Category Dropdown ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        translationY = offsetY[1].value,
                        alpha = alphas[1].value
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionLabel(text = "Category")
                    Spacer(Modifier.height(8.dp))
                    CategoryDropdown(
                        selectedCategory = selectedCategory,
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it },
                        onCategorySelected = {
                            selectedCategory = it
                            categoryError = false
                            dropdownExpanded = false
                        },
                        isError = categoryError
                    )
                    if (categoryError) {
                        Text(
                            text = "Please select a category",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── 2: Title ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        translationY = offsetY[2].value,
                        alpha = alphas[2].value
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionLabel(text = "Title")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (it.isNotBlank()) titleError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("What are you selling?") },
                        isError = titleError,
                        supportingText = if (titleError) {
                            { Text("Title cannot be empty", color = MaterialTheme.colorScheme.error) }
                        } else null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = MaterialTheme.shapes.medium,
                        colors = dealTextFieldColors()
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── 3: Description ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        translationY = offsetY[3].value,
                        alpha = alphas[3].value
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionLabel(text = "Description")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            if (it.isNotBlank()) descriptionError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Describe your item…") },
                        isError = descriptionError,
                        supportingText = if (descriptionError) {
                            { Text("Description cannot be empty", color = MaterialTheme.colorScheme.error) }
                        } else null,
                        minLines = 4,
                        maxLines = 6,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = MaterialTheme.shapes.medium,
                        colors = dealTextFieldColors()
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── 4: Price ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        translationY = offsetY[4].value,
                        alpha = alphas[4].value
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionLabel(text = "Price")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = price,
                        onValueChange = { input ->
                            val filtered = input.filter { it.isDigit() || it == '.' }
                            price = filtered
                            if (filtered.isNotBlank()) priceError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Amount") },
                        prefix = {
                            Text(
                                text = "₹",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        isError = priceError,
                        supportingText = if (priceError) {
                            { Text("Please enter a valid price", color = MaterialTheme.colorScheme.error) }
                        } else null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        shape = MaterialTheme.shapes.medium,
                        colors = dealTextFieldColors()
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Post Item Button ──────────────────────────────────────────────
            DealButton(
                text = "Post Item",
                isLoading = isSubmitting,
                onClick = {
                    categoryError = selectedCategory == null
                    titleError = title.isBlank()
                    descriptionError = description.isBlank()
                    priceError = price.isBlank() ||
                            price.toDoubleOrNull() == null ||
                            (price.toDoubleOrNull() ?: 0.0) <= 0.0

                    if (!categoryError && !titleError && !descriptionError && !priceError) {
                        isSubmitting = true
                        scope.launch {
                            delay(1200L)
                            isSubmitting = false
                            snackbarHostState.showSnackbar("🎉 Item listed successfully!")
                            delay(600L)
                            navController.navigateUp()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun DashedPhotoPickerBox(onClick: () -> Unit) {
    val dashedColor = MaterialTheme.colorScheme.outline
    val tealColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .drawBehind {
                drawRoundRect(
                    color = dashedColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(12f, 8f),
                            phase = 0f
                        )
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AddAPhoto,
                contentDescription = "Add photo",
                modifier = Modifier.size(48.dp),
                tint = tealColor
            )
            Text(
                text = "Tap to add photo",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "JPG · PNG · up to 5 MB",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun CategoryDropdown(
    selectedCategory: Category?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCategorySelected: (Category) -> Unit,
    isError: Boolean
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCategory?.displayName ?: "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            label = { Text("Select category") },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (expanded) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            isError = isError,
            shape = MaterialTheme.shapes.medium,
            colors = dealTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Category.entries.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (category == selectedCategory)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = { onCategorySelected(category) },
                    modifier = Modifier.background(
                        if (category == selectedCategory)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        else Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
private fun dealTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    errorBorderColor = MaterialTheme.colorScheme.error,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    errorLabelColor = MaterialTheme.colorScheme.error,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    errorTextColor = MaterialTheme.colorScheme.onSurface,
    errorSupportingTextColor = MaterialTheme.colorScheme.error
)
