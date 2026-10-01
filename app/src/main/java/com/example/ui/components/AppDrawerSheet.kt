package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.domain.model.AppCategory
import com.example.domain.model.AppIconSize
import com.example.domain.model.AppItem
import com.example.domain.model.BuiltInWallpaper
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCard
import com.example.ui.theme.AtlasCardElevated
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassBorderWhite
import com.example.ui.theme.GlassCardBackgroundBrush
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.SoftSageGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmSand

@Composable
fun AppDrawerSheet(
    apps: List<AppItem>,
    searchQuery: String,
    selectedCategory: AppCategory,
    iconSize: AppIconSize = AppIconSize.DEFAULT,
    hideAppLabels: Boolean = false,
    drawerGridColumns: Int = 4,
    wallpaper: BuiltInWallpaper = BuiltInWallpaper.OBSIDIAN_VOID,
    customWallpaperPath: String? = null,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (AppCategory) -> Unit,
    onLaunchApp: (AppItem) -> Unit,
    onTogglePin: (AppItem) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerAccent = CrimsonElectric

    // Dynamic gradient backdrop tinted with the active color palette and wallpaper theme
    val drawerScrimBrush = remember(drawerAccent) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFA08080C),
                Color(0xEE0D0D14),
                drawerAccent.copy(alpha = 0.14f),
                Color(0xF8050508)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("app_drawer_sheet")
    ) {
        // 1. Wallpaper Backdrop: changes dynamically with chosen wallpaper
        AtlasWallpaperBackdrop(
            wallpaper = wallpaper,
            customImagePath = customWallpaperPath,
            dimAlpha = 0.65f
        )

        // 2. Tinted Frosted Scrim responding to active color palette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(drawerScrimBrush)
        )

        // 3. Ambient glassmorphic glowing light orbs adapting to selected theme
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(drawerAccent.copy(alpha = 0.35f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.12f),
                    radius = size.width * 0.75f
                ),
                radius = size.width * 0.75f,
                center = Offset(size.width * 0.85f, size.height * 0.12f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(drawerAccent.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.82f),
                    radius = size.width * 0.8f
                ),
                radius = size.width * 0.8f,
                center = Offset(size.width * 0.15f, size.height * 0.82f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(drawerAccent.copy(alpha = 0.14f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.45f),
                    radius = size.width * 0.55f
                ),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.5f, size.height * 0.45f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top: Search Bar with Frosted Glass styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = "Search apps or actions...",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrimsonElectric,
                        unfocusedBorderColor = GlassBorderWhite,
                        focusedContainerColor = GlassSurfaceElevated,
                        unfocusedContainerColor = GlassSurface,
                        focusedTextColor = TextPureWhite,
                        unfocusedTextColor = TextPureWhite,
                        cursorColor = CrimsonElectric
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("app_search_input")
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GlassSurfaceElevated)
                        .border(1.dp, GlassBorderWhite, CircleShape)
                        .testTag("close_app_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Drawer",
                        tint = TextPureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Below Search: Category Chips (All, Work, Social, Media, Tools)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(AppCategory.entries) { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CrimsonElectric else GlassSurface)
                            .border(
                                1.dp,
                                if (isSelected) CrimsonElectric else GlassBorderSubtle,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelectCategory(category) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("category_chip_${category.name.lowercase()}")
                    ) {
                        Text(
                            text = category.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            color = if (isSelected) TextPureWhite else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle Count & Instructions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedCategory.label.uppercase()} (${apps.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Long press to pin to dock",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val gridState = rememberLazyGridState()
            val coroutineScope = rememberCoroutineScope()
            var manualSelectedLetter by remember { mutableStateOf<Char?>(null) }

            // Clear manual selection when user manually scrolls the grid
            LaunchedEffect(gridState.isScrollInProgress) {
                if (gridState.isScrollInProgress) {
                    manualSelectedLetter = null
                }
            }

            // Proper alphabetical comparator: A..Z in order, '#' always placed last
            val alphabetComparator = remember {
                Comparator<Char> { a, b ->
                    when {
                        a == b -> 0
                        a == '#' -> 1
                        b == '#' -> -1
                        else -> a.compareTo(b)
                    }
                }
            }

            // Group apps alphabetically with '#' guaranteed at the end
            val groupedApps = remember(apps) {
                apps.groupBy { app ->
                    val firstChar = app.name.trim().firstOrNull()?.uppercaseChar() ?: '#'
                    if (firstChar in 'A'..'Z') firstChar else '#'
                }.toSortedMap(alphabetComparator)
            }

            // Precompute index mapping for O(1) lookups in both directions
            val indexData = remember(groupedApps) {
                val map = mutableMapOf<Char, Int>()
                val letterList = mutableListOf<Char>()
                var currentIndex = 0

                groupedApps.forEach { (letter, appList) ->
                    map[letter] = currentIndex
                    letterList.add(letter) // For section header
                    repeat(appList.size) {
                        letterList.add(letter) // For each app card
                    }
                    currentIndex += 1 + appList.size
                }

                AlphabetIndexData(
                    alphabetToItemMap = map,
                    itemToLetterArray = letterList.toTypedArray(),
                    availableLetters = groupedApps.keys.toSet()
                )
            }

            // Bi-directional letter synchronization
            val currentVisibleLetter by remember(indexData, apps) {
                derivedStateOf {
                    if (apps.isEmpty()) return@derivedStateOf null
                    val isAtBottom = !gridState.canScrollForward
                    if (isAtBottom && groupedApps.isNotEmpty()) {
                        // At the absolute bottom of the list, highlight the last section
                        groupedApps.keys.last()
                    } else {
                        val firstIdx = gridState.firstVisibleItemIndex
                        indexData.getLetterForIndex(firstIdx)
                    }
                }
            }

            val activeRailLetter = manualSelectedLetter ?: currentVisibleLetter

            fun scrollToLetter(letter: Char, isDragging: Boolean) {
                manualSelectedLetter = letter
                val targetIndex = indexData.findTargetIndex(letter)

                coroutineScope.launch {
                    val currentIdx = gridState.firstVisibleItemIndex
                    if (isDragging) {
                        // Immediate jump for 120 FPS fluid drag tracking
                        gridState.scrollToItem(targetIndex)
                    } else {
                        // Physical animated scroll on tap
                        if (abs(currentIdx - targetIndex) > 18) {
                            val intermediate = if (targetIndex > currentIdx) targetIndex - 5 else targetIndex + 5
                            val total = gridState.layoutInfo.totalItemsCount
                            if (total > 0) {
                                gridState.scrollToItem(intermediate.coerceIn(0, total - 1))
                            }
                            gridState.animateScrollToItem(targetIndex)
                        } else {
                            gridState.animateScrollToItem(targetIndex)
                        }
                    }
                }
            }

            // Below Category Chips: Beautiful App Grid with rounded monochrome icons & minimal labels
            if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "No applications in this category",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(drawerGridColumns),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = if (searchQuery.isEmpty() && apps.isNotEmpty()) 24.dp else 0.dp)
                            .testTag("app_drawer_grid"),
                        contentPadding = PaddingValues(bottom = 450.dp), // Guarantees all letters including V, W, X, Y, Z can scroll to top
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (searchQuery.isNotEmpty()) {
                            // Flat grid when searching
                            items(apps, key = { it.uniqueKey }) { app ->
                                AppGridCell(
                                    app = app,
                                    iconSize = iconSize,
                                    hideLabel = hideAppLabels,
                                    onLaunch = { onLaunchApp(app) },
                                    onTogglePin = { onTogglePin(app) }
                                )
                            }
                        } else {
                            // Alphabetically grouped with sticky section headers
                            groupedApps.forEach { (letter, appList) ->
                                item(
                                    key = "section_header_$letter",
                                    span = { GridItemSpan(maxLineSpan) },
                                    contentType = "header"
                                ) {
                                    AlphabetSectionHeader(
                                        letter = letter,
                                        accentColor = drawerAccent
                                    )
                                }

                                items(
                                    items = appList,
                                    key = { it.uniqueKey },
                                    contentType = { "app" }
                                ) { app ->
                                    AppGridCell(
                                        app = app,
                                        iconSize = iconSize,
                                        hideLabel = hideAppLabels,
                                        onLaunch = { onLaunchApp(app) },
                                        onTogglePin = { onTogglePin(app) }
                                    )
                                }
                            }
                        }
                    }

                    // Vertical Alphabet Fast Scroller Rail along the right edge
                    if (searchQuery.isEmpty() && apps.isNotEmpty()) {
                        AlphabetFastScroller(
                            availableLetters = indexData.availableLetters,
                            currentSelectedLetter = activeRailLetter,
                            accentColor = drawerAccent,
                            onLetterSelected = { letter, isDragging ->
                                scrollToLetter(letter, isDragging)
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppGridCell(
    app: AppItem,
    iconSize: AppIconSize = AppIconSize.DEFAULT,
    hideLabel: Boolean = false,
    onLaunch: () -> Unit,
    onTogglePin: () -> Unit
) {
    val boxSize = (iconSize.dpSize + 6).dp
    val iconDp = iconSize.dpSize.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onLaunch,
                onLongClick = onTogglePin
            )
            .padding(vertical = 4.dp)
            .testTag("app_cell_${app.uniqueKey}")
    ) {
        Box(
            modifier = Modifier.size(boxSize),
            contentAlignment = Alignment.Center
        ) {
            val imageBitmap = remember(app.uniqueKey) {
                com.example.util.AppIconCache.getOrConvert(app.uniqueKey, app.icon)
            }

            if (imageBitmap != null) {
                // Authentic, vibrant Android application icon
                Image(
                    bitmap = imageBitmap,
                    contentDescription = app.name,
                    modifier = Modifier
                        .size(iconDp)
                        .clip(CircleShape)
                )
            } else {
                // Adaptive colored initial icon
                val avatarColor = when (app.category) {
                    AppCategory.WORK -> Color(0xFFE53935)
                    AppCategory.SOCIAL -> Color(0xFF1E88E5)
                    AppCategory.MEDIA -> Color(0xFF8E24AA)
                    AppCategory.TOOLS -> Color(0xFF00897B)
                    AppCategory.ALL -> Color(0xFF546E7A)
                }

                Box(
                    modifier = Modifier
                        .size(iconDp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Dual / Cloned app badge indicator
            if (app.isCloned) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(CrimsonElectric)
                        .border(1.dp, AtlasBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (app.isWorkProfile) "W" else "2",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }

            if (app.isPinned) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(SoftSageGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PushPin,
                        contentDescription = "Pinned",
                        tint = AtlasBackground,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        if (!hideLabel) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = app.name,
                style = MaterialTheme.typography.labelSmall,
                color = TextPureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontSize = 11.sp
            )
        }
    }
}
