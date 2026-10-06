package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppCategory
import com.example.domain.model.AppIconSize
import com.example.domain.model.AppItem
import com.example.domain.model.BuiltInWallpaper
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassBorderWhite
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LauncherUiState
import kotlinx.coroutines.launch
import kotlin.math.abs

/** Actions offered by the app drawer's long-press menu. */
enum class AppDrawerAction { PIN, APP_INFO, UNINSTALL, SHARE, EDIT }

/** Extra forgiveness (px) around the home dock when a drag is released. */
private const val DOCK_SLOP_PX = 26f

/** Short toast helper so gesture feedback can never crash a drag. */
private fun toast(context: android.content.Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun AppDrawerSheet(
    apps: List<AppItem>,
    pinnedApps: List<AppItem>,
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
    onPinApp: (AppItem, Int?) -> Unit,
    onRemovePinnedApp: (AppItem) -> Unit,
    /**
     * Screen (root) bounds of the REAL home dock, reported by the home canvas.
     * An app dragged out of the drawer is dropped straight onto that area.
     */
    homeDockBounds: Rect = Rect.Zero,
    onOpenAppInfo: (AppItem) -> Unit,
    onUninstallApp: (AppItem) -> Unit,
    onShareApp: (AppItem) -> Unit,
    onSaveAppEdit: (AppItem, String?, Int?, String?) -> Unit,
    onResetAppEdit: (AppItem) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerAccent = CrimsonElectric
    val context = androidx.compose.ui.platform.LocalContext.current

    // The drawer deliberately has NO dock of its own. Dragging an app out of
    // the drawer down onto the REAL home dock (behind this sheet) pins it:
    // while the finger hovers that area the whole drawer melts away so the home
    // screen and its dock show through as the drop target.
    var rootOrigin by remember { mutableStateOf(Offset.Zero) }
    var editingApp by remember { mutableStateOf<AppItem?>(null) }
    var draggedApp by remember { mutableStateOf<AppItem?>(null) }
    var dragRootPos by remember { mutableStateOf(Offset.Zero) }
    var overDock by remember { mutableStateOf(false) }

    fun landsOnDock(rootPos: Offset): Boolean =
        homeDockBounds.width > 0f && homeDockBounds.inflate(DOCK_SLOP_PX).contains(rootPos)

    val beginDrag: (AppItem, Offset) -> Unit = { app, rootPos ->
        draggedApp = app
        dragRootPos = rootPos
        overDock = landsOnDock(rootPos)
    }

    val updateDrag: (Offset) -> Unit = { rootPos ->
        if (draggedApp != null) {
            dragRootPos = rootPos
            overDock = landsOnDock(rootPos)
        }
    }

    // Release decides the drop: on the home dock → pin it, anywhere else →
    // cancel. Nothing is ever removed or reordered by accident.
    val dropDrag: (Offset) -> Unit = { finalPos ->
        val released = draggedApp
        val landed = landsOnDock(finalPos)
        draggedApp = null
        overDock = false
        if (released != null && landed) {
            when {
                released.isPinned -> toast(
                    context,
                    "${released.displayName} is already in your dock"
                )

                pinnedApps.size >= LauncherUiState.MAX_DOCK_APPS -> toast(
                    context,
                    "Dock is full — long-press a dock icon to free a slot"
                )

                else -> {
                    toast(context, "${released.displayName} pinned to dock")
                    onPinApp(released, null)
                    // A successful pin is the end of this task: hand the user
                    // straight back to the home screen with the icon in place.
                    onClose()
                }
            }
        }
    }

    // 1 → drawer fully visible, ~0 → home screen showing through the drag.
    val revealAlpha by animateFloatAsState(
        targetValue = if (draggedApp != null && overDock) 0.06f else 1f,
        animationSpec = tween(durationMillis = 140),
        label = "drawer_reveal"
    )

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
            .onGloballyPositioned { rootOrigin = it.positionInRoot() }
            .testTag("app_drawer_sheet")
    ) {
        // Everything below fades away while a drag hovers the home dock.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = revealAlpha }
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
                            imageVector = Icons.Outlined.Search,
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
                        imageVector = Icons.Filled.Close,
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
                    text = "Hold for options • drag down to your dock",
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
                    val firstChar = app.displayName.trim().firstOrNull()?.uppercaseChar() ?: '#'
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
                                    onAction = { action ->
                                        when (action) {
                                            AppDrawerAction.PIN -> {
                                                if (app.isPinned) {
                                                    onRemovePinnedApp(app)
                                                } else {
                                                    onPinApp(app, null)
                                                    // Leave the drawer and show the new dock.
                                                    onClose()
                                                }
                                            }

                                            AppDrawerAction.APP_INFO -> onOpenAppInfo(app)
                                            AppDrawerAction.UNINSTALL -> onUninstallApp(app)
                                            AppDrawerAction.SHARE -> onShareApp(app)
                                            AppDrawerAction.EDIT -> editingApp = app
                                        }
                                    },
                                    onDragStart = { pos -> beginDrag(app, pos) },
                                    onDragMove = updateDrag,
                                    onDragEnd = dropDrag
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
                                        onAction = { action ->
                                            when (action) {
                                                AppDrawerAction.PIN -> {
                                                    if (app.isPinned) {
                                                        onRemovePinnedApp(app)
                                                    } else {
                                                        onPinApp(app, null)
                                                        // Leave the drawer and show the new dock.
                                                        onClose()
                                                    }
                                                }

                                                AppDrawerAction.APP_INFO -> onOpenAppInfo(app)
                                                AppDrawerAction.UNINSTALL -> onUninstallApp(app)
                                                AppDrawerAction.SHARE -> onShareApp(app)
                                                AppDrawerAction.EDIT -> editingApp = app
                                            }
                                        },
                                        onDragStart = { pos -> beginDrag(app, pos) },
                                        onDragMove = updateDrag,
                                        onDragEnd = dropDrag
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
        } // ← end of the fading drawer content (reveal wrapper)

        // Drop-zone outline drawn over the revealed home dock while dragging.
        if (draggedApp != null && overDock && homeDockBounds.width > 0f) {
            val zone = homeDockBounds.translate(-rootOrigin.x, -rootOrigin.y).inflate(10f)
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = drawerAccent.copy(alpha = 0.18f),
                    topLeft = Offset(zone.left, zone.top),
                    size = Size(zone.width, zone.height),
                    cornerRadius = CornerRadius(34f, 34f)
                )
                drawRoundRect(
                    color = drawerAccent.copy(alpha = 0.95f),
                    topLeft = Offset(zone.left, zone.top),
                    size = Size(zone.width, zone.height),
                    cornerRadius = CornerRadius(34f, 34f),
                    style = Stroke(width = 5f)
                )
            }
        }

        // Dragged icon floating under the finger, above everything else
        DragGhostLayer(
            app = draggedApp,
            rootPos = dragRootPos,
            rootOrigin = rootOrigin,
            iconSize = iconSize
        )

        // Edit popup (rename + icon override with Save / Reset)
        editingApp?.let { app ->
            AppEditDialog(
                app = app,
                onDismiss = { editingApp = null },
                onSave = { label, presetId, iconUri ->
                    onSaveAppEdit(app, label, presetId, iconUri)
                    editingApp = null
                },
                onReset = { onResetAppEdit(app) }
            )
        }
    }
}

// ==========================================================================
// DRAG GHOST
// ==========================================================================

@Composable
private fun DragGhostLayer(
    app: AppItem?,
    rootPos: Offset,
    rootOrigin: Offset,
    iconSize: AppIconSize
) {
    if (app == null) return
    val density = LocalDensity.current
    val iconDp = iconSize.dpSize.dp
    val ghostSize = iconDp + 10.dp
    val half = with(density) { ghostSize.toPx() / 2f }

    Box(
        modifier = Modifier
            .graphicsLayer {
                translationX = rootPos.x - rootOrigin.x - half
                translationY = rootPos.y - rootOrigin.y - half
                scaleX = 1.12f
                scaleY = 1.12f
                alpha = 0.98f
            }
            .size(ghostSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0x55000000))
        )
        if (!AtlasAppIcon(app, iconDp, RoundedCornerShape(18.dp))) {
            Box(
                modifier = Modifier
                    .size(iconDp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF455A64)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.displayName.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================================================
// APP GRID CELL
// ==========================================================================

@Composable
private fun AppGridCell(
    app: AppItem,
    iconSize: AppIconSize = AppIconSize.DEFAULT,
    hideLabel: Boolean = false,
    onLaunch: () -> Unit,
    onAction: (AppDrawerAction) -> Unit,
    onDragStart: (Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: (Offset) -> Unit
) {
    val boxSize = (iconSize.dpSize + 6).dp
    val iconDp = iconSize.dpSize.dp
    val haptics = LocalHapticFeedback.current
    val labelFontSize = com.example.ui.appearance.LocalAppearanceState.current
        .iconLabelSize.spSize.sp

    var menuOpen by remember(app.uniqueKey) { mutableStateOf(false) }
    var coordinates by remember(app.uniqueKey) { mutableStateOf<LayoutCoordinates?>(null) }

    // The gesture coroutine is long-lived, so always call the LATEST drag
    // callbacks (they close over the current pinned list / dock metrics).
    val currentDragStart by rememberUpdatedState(onDragStart)
    val currentDragMove by rememberUpdatedState(onDragMove)
    val currentDragEnd by rememberUpdatedState(onDragEnd)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .onGloballyPositioned { coordinates = it }
            .clip(RoundedCornerShape(16.dp))
            .longPressDragOrTap(
                gestureKey = "cell_${app.uniqueKey}",
                haptics = haptics,
                onTap = onLaunch,
                onLongPressRelease = { menuOpen = true },
                onDragStart = { local ->
                    currentDragStart(coordinates?.localToRoot(local) ?: local)
                },
                onDragMove = { local ->
                    currentDragMove(coordinates?.localToRoot(local) ?: local)
                },
                onDragEnd = { local ->
                    currentDragEnd(coordinates?.localToRoot(local) ?: local)
                }
            )
            .padding(vertical = 4.dp)
            .testTag("app_cell_${app.uniqueKey}")
    ) {
        Box(
            modifier = Modifier.size(boxSize),
            contentAlignment = Alignment.Center
        ) {
            if (!AtlasAppIcon(app, iconDp, CircleShape)) {
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
                        text = app.displayName.take(1).uppercase(),
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
                        .background(com.example.ui.theme.SoftSageGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PushPin,
                        contentDescription = "In dock",
                        tint = AtlasBackground,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        if (!hideLabel) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = app.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = TextPureWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontSize = labelFontSize
            )
        }
    }

    // Long-press context menu: the real OS actions plus the launcher's Edit.
    DropdownMenu(
        expanded = menuOpen,
        onDismissRequest = { menuOpen = false },
        modifier = Modifier
            .border(1.dp, GlassBorderWhite, RoundedCornerShape(18.dp))
            .testTag("app_menu_${app.uniqueKey}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!AtlasAppIcon(app, 30.dp, RoundedCornerShape(9.dp))) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(Color(0xFF455A64)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.displayName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = app.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPureWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "App options",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }

        HorizontalDivider(color = GlassBorderSubtle)

        DropdownMenuItem(
            text = {
                Text(
                    text = if (app.isPinned) "Remove from dock" else "Pin to dock",
                    color = if (app.isPinned) Color(0xFFFF6B6B) else TextPureWhite,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.PushPin,
                    contentDescription = null,
                    tint = if (app.isPinned) Color(0xFFFF6B6B) else CrimsonElectric,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                menuOpen = false
                onAction(AppDrawerAction.PIN)
            }
        )

        DropdownMenuItem(
            text = { Text("App info", color = TextPureWhite, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                menuOpen = false
                onAction(AppDrawerAction.APP_INFO)
            }
        )

        DropdownMenuItem(
            text = { Text("Uninstall", color = Color(0xFFFF6B6B), fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                menuOpen = false
                onAction(AppDrawerAction.UNINSTALL)
            }
        )

        DropdownMenuItem(
            text = { Text("Share", color = TextPureWhite, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                menuOpen = false
                onAction(AppDrawerAction.SHARE)
            }
        )

        DropdownMenuItem(
            text = { Text("Edit", color = TextPureWhite, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                menuOpen = false
                onAction(AppDrawerAction.EDIT)
            }
        )
    }
}

// ==========================================================================
// GESTURE: tap to open · hold to see options · hold + move to drag
// ==========================================================================

private const val DRAG_SLOP_DP = 12f

/**
 * One gesture, three outcomes — exactly how real launchers behave:
 *  - quick tap            → [onTap]
 *  - hold and release     → [onLongPressRelease] (context menu)
 *  - hold and move        → [onDragStart]/[onDragMove]/[onDragEnd] with root coordinates
 *
 * The scroll container keeps priority: if the parent consumes the gesture (a
 * flick), nothing fires. Once the drag starts, every move is consumed so the
 * grid never scrolls under the finger.
 */
private fun Modifier.longPressDragOrTap(
    gestureKey: Any,
    haptics: HapticFeedback,
    onTap: () -> Unit,
    onLongPressRelease: () -> Unit,
    onDragStart: (Offset) -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: (Offset) -> Unit
): Modifier = this.pointerInput(gestureKey) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)

        var longPressed = false
        val upOrCancel: PointerInputChange? = try {
            withTimeout(viewConfiguration.longPressTimeoutMillis) {
                waitForUpOrCancellation()
            }
        } catch (e: PointerEventTimeoutCancellationException) {
            longPressed = true
            null
        }

        if (!longPressed) {
            if (upOrCancel != null) {
                upOrCancel.consume()
                onTap()
            }
            return@awaitEachGesture
        }

        // Long press reached: the finger decides between menu and drag.
        val slopPx = DRAG_SLOP_DP * density
        var dragging = false
        var cancelled = false
        var travelled = 0f
        var lastPosition = down.position
        var lastRoot = down.position

        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == down.id } ?: break
            if (change.isConsumed) {
                cancelled = true
                break
            }
            if (!change.pressed) break

            if (!dragging) {
                travelled += (change.position - lastPosition).getDistance()
                lastPosition = change.position
                if (travelled > slopPx) {
                    dragging = true
                    lastRoot = change.position
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDragStart(change.position)
                }
            } else {
                change.consume()
                lastRoot = change.position
                onDragMove(change.position)
            }
        }

        when {
            dragging -> {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onDragEnd(lastRoot)
            }

            !cancelled -> onLongPressRelease()
        }
    }
}
