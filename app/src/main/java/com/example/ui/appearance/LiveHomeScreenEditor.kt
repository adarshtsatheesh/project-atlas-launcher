package com.example.ui.appearance

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Grid4x4
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppIconSize
import com.example.domain.model.BuiltInWallpaper
import com.example.ui.components.LauncherPreview
import com.example.ui.theme.LocalAtlasAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.viewmodel.ClockStyle
import com.example.ui.viewmodel.EditCategory
import com.example.ui.viewmodel.LauncherUiState
import java.util.Locale

// ==========================================================================
// Design tokens — ColorOS editor: big frosted circles, full-screen pages
// ==========================================================================

private val GlassHighlight = Color(0x2EFFFFFF)
private val GlassHairline = Color(0x1AFFFFFF)
private val GlassSurface = Color(0x14FFFFFF)
private val GlassSurfaceStrong = Color(0x1FFFFFFF)
private val GlassTrack = Color(0x2EFFFFFF)
private val GlassSecondary = Color(0xC0FFFFFF)

/**
 * Live Home Screen Editor — ColorOS style.
 *
 * Editor home: the wallpaper and home content stay fully visible, a "+" pill
 * (top-left) jumps to Wallpapers, "Done" (top-right) exits, and a row of large
 * frosted circles at the bottom opens each category. Selecting a category
 * slides a full-screen page over the live wallpaper with a back arrow — every
 * control applies instantly, no Apply button anywhere.
 */
@Composable
fun LiveEditorTopBar(
    onAdd: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditorPillButton(
            label = null,
            icon = Icons.Outlined.Add,
            contentDescription = "Add wallpaper",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onAdd()
            },
            testTag = "editor_top_bar_close"
        )
        EditorPillButton(
            label = "Done",
            icon = null,
            contentDescription = "Done",
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onDone()
            },
            testTag = "editor_top_bar_done"
        )
    }
}

/** Frosted capsule button used in the editor top bar ("+" and "Done"). */
@Composable
private fun EditorPillButton(
    label: String?,
    icon: ImageVector?,
    contentDescription: String,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "pill_scale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(22.dp))
            .background(GlassSurfaceStrong)
            .border(1.dp, GlassHighlight, RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = if (label != null) 20.dp else 16.dp, vertical = 10.dp)
            .testTag(testTag)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = Color.White
            )
        }
    }
}

/**
 * Bottom customization area + full-screen category pages.
 *
 * When no category is active, only the row of big frosted circles is shown.
 * Selecting one slides a full-screen page (with back arrow) over everything.
 */
@Composable
fun LiveEditorBottomDock(
    activeCategory: EditCategory?,
    onSelectCategory: (EditCategory?) -> Unit,
    onDone: () -> Unit,
    uiState: LauncherUiState,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit,
    onSelectWallpaperTarget: (com.example.util.WallpaperTarget) -> Unit,
    onUpdateWallpaperDim: (Float) -> Unit,
    onSelectClockStyle: (ClockStyle) -> Unit,
    onSelectIconSize: (AppIconSize) -> Unit,
    onToggleHideAppLabels: (Boolean) -> Unit,
    onToggleHideDockLabels: (Boolean) -> Unit,
    onSelectGridColumns: (Int) -> Unit,
    onSelectWallpaper: (BuiltInWallpaper) -> Unit,
    onSetCustomWallpaper: (String) -> Unit,
    onResetAppearance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    // Live launcher state — single source of truth for every page and preview
    val appearanceState = uiState.appearanceState
    val selectedWallpaper = uiState.selectedWallpaper
    val wallpaperDim = uiState.wallpaperDim
    val wallpaperTarget = uiState.wallpaperTarget
    val clockStyle = uiState.selectedClockStyle
    val appIconSize = uiState.appIconSize
    val hideAppLabels = uiState.hideAppLabels
    val hideDockLabels = uiState.hideDockLabels
    val drawerGridColumns = uiState.drawerGridColumns

    Box(modifier = Modifier.fillMaxSize()) {
        // ---- Full-screen category page layer ----
        AnimatedVisibility(
            visible = activeCategory != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = 0.88f, stiffness = Spring.StiffnessMediumLow)
            ) + fadeIn(tween(160)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = spring(dampingRatio = 0.88f, stiffness = Spring.StiffnessMediumLow)
            ) + fadeOut(tween(120)),
            modifier = Modifier.fillMaxSize()
        ) {
            activeCategory?.let { category ->
                CategoryPage(
                    category = category,
                    uiState = uiState,
                    appearanceState = appearanceState,
                    onUpdateAppearance = onUpdateAppearance,
                    onDone = onDone,
                    selectedWallpaper = selectedWallpaper,
                    wallpaperDim = wallpaperDim,
                    wallpaperTarget = wallpaperTarget,
                    onSelectWallpaperTarget = onSelectWallpaperTarget,
                    onUpdateWallpaperDim = onUpdateWallpaperDim,
                    clockStyle = clockStyle,
                    onSelectClockStyle = onSelectClockStyle,
                    appIconSize = appIconSize,
                    onSelectIconSize = onSelectIconSize,
                    hideAppLabels = hideAppLabels,
                    onToggleHideAppLabels = onToggleHideAppLabels,
                    hideDockLabels = hideDockLabels,
                    onToggleHideDockLabels = onToggleHideDockLabels,
                    drawerGridColumns = drawerGridColumns,
                    onSelectGridColumns = onSelectGridColumns,
                    onSelectWallpaper = onSelectWallpaper,
                    onSetCustomWallpaper = onSetCustomWallpaper,
                    onResetAppearance = onResetAppearance,
                    onBack = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSelectCategory(null)
                    }
                )
            }
        }

        // ---- Floating circle dock (editor home) ----
        AnimatedVisibility(
            visible = activeCategory == null,
            enter = fadeIn(tween(180)) + slideInVertically { it / 3 },
            exit = fadeOut(tween(120)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            DockCircleRow(
                onSelectCategory = { category ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectCategory(category)
                }
            )
        }
    }
}

/** Row of large frosted circles — Wallpapers · Theme · Icons · Clock · Layout · More. */
@Composable
private fun DockCircleRow(
    onSelectCategory: (EditCategory) -> Unit
) {
    val items = listOf(
        Triple(EditCategory.WALLPAPER, "Wallpapers", Icons.Outlined.Wallpaper),
        Triple(EditCategory.APPEARANCE, "Theme", Icons.Outlined.Palette),
        Triple(EditCategory.ICONS, "Icons", Icons.Outlined.Apps),
        Triple(EditCategory.CLOCK, "Clock", Icons.Outlined.Schedule),
        Triple(EditCategory.LAYOUT, "Layout", Icons.Outlined.Grid4x4),
        Triple(EditCategory.MORE, "More", Icons.Outlined.MoreHoriz)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        items.forEach { (category, label, icon) ->
            DockCircle(
                icon = icon,
                label = label,
                onClick = { onSelectCategory(category) }
            )
        }
    }
}

/** One big frosted circle + label beneath it, with press-bounce spring. */
@Composable
private fun DockCircle(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "dock_circle_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp),
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .testTag("dock_cat_$label")
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x2BFFFFFF), Color(0x14FFFFFF))
                    )
                )
                .border(1.dp, GlassHighlight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = Color.White,
            maxLines = 1
        )
    }
}

// ==========================================================================
// Full-screen category pages
// ==========================================================================

@Composable
private fun CategoryPage(
    category: EditCategory,
    uiState: LauncherUiState,
    appearanceState: AppearanceState,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit,
    onDone: () -> Unit,
    selectedWallpaper: BuiltInWallpaper,
    wallpaperDim: Float,
    wallpaperTarget: com.example.util.WallpaperTarget,
    onSelectWallpaperTarget: (com.example.util.WallpaperTarget) -> Unit,
    onUpdateWallpaperDim: (Float) -> Unit,
    clockStyle: ClockStyle,
    onSelectClockStyle: (ClockStyle) -> Unit,
    appIconSize: AppIconSize,
    onSelectIconSize: (AppIconSize) -> Unit,
    hideAppLabels: Boolean,
    onToggleHideAppLabels: (Boolean) -> Unit,
    hideDockLabels: Boolean,
    onToggleHideDockLabels: (Boolean) -> Unit,
    drawerGridColumns: Int,
    onSelectGridColumns: (Int) -> Unit,
    onSelectWallpaper: (BuiltInWallpaper) -> Unit,
    onSetCustomWallpaper: (String) -> Unit,
    onResetAppearance: () -> Unit,
    onBack: () -> Unit
) {
    // Dim scrim over the live wallpaper so content pops, wallpaper still visible
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0x59000000), Color(0xB3000000), Color(0xE6000000))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            PageHeader(
                title = when (category) {
                    EditCategory.WALLPAPER -> "Wallpapers"
                    EditCategory.APPEARANCE -> "Theme & Font"
                    EditCategory.ICONS -> "Icons"
                    EditCategory.CLOCK -> "Clock"
                    EditCategory.LAYOUT -> "Layout"
                    EditCategory.TYPOGRAPHY -> "Font"
                    EditCategory.MORE -> "More"
                },
                onBack = onBack,
                onDone = onDone
            )

            when (category) {
                EditCategory.WALLPAPER -> SplitPageLayout(
                    preview = {
                        LauncherPreview(
                            uiState = uiState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                ) {
                    WallpaperPage(
                        appearanceState = appearanceState,
                        selectedWallpaper = selectedWallpaper,
                        wallpaperDim = wallpaperDim,
                        wallpaperTarget = wallpaperTarget,
                        onSelectWallpaperTarget = onSelectWallpaperTarget,
                        onUpdateWallpaperDim = onUpdateWallpaperDim,
                        onSelectWallpaper = onSelectWallpaper,
                        onSetCustomWallpaper = onSetCustomWallpaper,
                        onUpdateAppearance = onUpdateAppearance
                    )
                }
                EditCategory.APPEARANCE, EditCategory.TYPOGRAPHY -> SplitPageLayout(
                    preview = {
                        LauncherPreview(
                            uiState = uiState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                ) {
                    ThemePage(
                        appearanceState = appearanceState,
                        onUpdateAppearance = onUpdateAppearance
                    )
                }
                EditCategory.ICONS -> SplitPageLayout(
                    preview = {
                        LauncherPreview(
                            uiState = uiState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                ) {
                    IconsPage(
                        appearanceState = appearanceState,
                        appIconSize = appIconSize,
                        onSelectIconSize = onSelectIconSize,
                        hideAppLabels = hideAppLabels,
                        onToggleHideAppLabels = onToggleHideAppLabels,
                        hideDockLabels = hideDockLabels,
                        onToggleHideDockLabels = onToggleHideDockLabels,
                        drawerGridColumns = drawerGridColumns,
                        onSelectGridColumns = onSelectGridColumns,
                        onUpdateAppearance = onUpdateAppearance
                    )
                }
                EditCategory.CLOCK -> SplitPageLayout(
                    preview = {
                        LauncherPreview(
                            uiState = uiState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                ) {
                    ClockPage(
                        appearanceState = appearanceState,
                        clockStyle = clockStyle,
                        onSelectClockStyle = onSelectClockStyle,
                        onUpdateAppearance = onUpdateAppearance
                    )
                }
                EditCategory.LAYOUT -> SplitPageLayout(
                    preview = {
                        LauncherPreview(
                            uiState = uiState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                ) {
                    LayoutPage(
                        appearanceState = appearanceState,
                        onUpdateAppearance = onUpdateAppearance
                    )
                }
                EditCategory.MORE -> SplitPageLayout(
                    preview = {
                        LauncherPreview(
                            uiState = uiState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                ) {
                    MorePage(
                        onResetAppearance = onResetAppearance
                    )
                }
            }
        }
    }
}

/** Back arrow + page title, ColorOS style. */
@Composable
private fun PageHeader(
    title: String,
    onBack: () -> Unit,
    onDone: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "back_scale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .size(42.dp)
                .clip(CircleShape)
                .background(GlassSurface)
                .border(1.dp, GlassHairline, CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onBack() }
                .testTag("editor_page_back"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(21.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp
            ),
            color = Color.White
        )
        if (onDone != null) {
            Spacer(modifier = Modifier.weight(1f))
            EditorPillButton(
                label = "Done",
                icon = null,
                contentDescription = "Done",
                onClick = onDone,
                testTag = "editor_page_done"
            )
        }
    }
}

/** Scrollable page body with standard horizontal padding. */
@Composable
private fun PageContent(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

/** Uppercase section label. */
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(Locale.US),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp,
            letterSpacing = 1.2.sp
        ),
        color = GlassSecondary,
        modifier = Modifier.padding(start = 6.dp, top = 8.dp)
    )
}

/** Frosted glass card grouping controls. */
@Composable
private fun EditorCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassSurface)
            .border(1.dp, GlassHairline, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

/** Label + live value + slider row. */
@Composable
private fun SliderRow(
    title: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    accentColor: Color,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = GlassSecondary
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = accentColor,
                inactiveTrackColor = GlassTrack
            )
        )
    }
}

/** Title + subtitle + switch row. */
@Composable
private fun ToggleRow(
    title: String,
    subtitle: String?,
    checked: Boolean,
    accentColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 14.5.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = GlassSecondary,
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = Color(0xFFB6B9C2),
                uncheckedTrackColor = GlassTrack,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

/** Horizontally scrollable pill selector. */
@Composable
private fun <T> PillRow(
    options: List<Pair<T, String>>,
    selected: T,
    accentColor: Color,
    fontFamily: FontFamily,
    onSelect: (T) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (value, label) ->
            val isSelected = value == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) accentColor.copy(alpha = 0.26f) else GlassSurface
                    )
                    .border(
                        1.dp,
                        if (isSelected) accentColor.copy(alpha = 0.8f) else GlassHairline,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelect(value) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = fontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    ),
                    color = if (isSelected) Color.White else GlassSecondary
                )
            }
        }
    }
}

@Composable
private fun SelectedBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(7.dp)
            .size(19.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Selected",
            tint = Color.Black,
            modifier = Modifier.size(12.dp)
        )
    }
}

private fun parseHexColor(hex: String, fallback: Color): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    fallback
}

private fun colorToHex(color: Color): String =
    String.format(Locale.US, "#%06X", 0xFFFFFF and color.value.toInt())

private fun Color.luminanceCompat(): Float =
    (0.299f * red + 0.587f * green + 0.114f * blue)

// ==========================================================================
// WALLPAPERS PAGE — Album card + built-in grid + apply-to + atmosphere
// ==========================================================================

@Composable
private fun WallpaperPage(
    appearanceState: AppearanceState,
    selectedWallpaper: BuiltInWallpaper,
    wallpaperDim: Float,
    wallpaperTarget: com.example.util.WallpaperTarget,
    onSelectWallpaperTarget: (com.example.util.WallpaperTarget) -> Unit,
    onUpdateWallpaperDim: (Float) -> Unit,
    onSelectWallpaper: (BuiltInWallpaper) -> Unit,
    onSetCustomWallpaper: (String) -> Unit,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accentColor = LocalAtlasAccent.current
    val fontFamily = LocalAppFontFamily.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onSetCustomWallpaper(uri.toString())
            onSelectWallpaper(BuiltInWallpaper.CUSTOM_GALLERY)
        }
    }

    PageContent {
        SectionLabel("Album")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF3D5AFE).copy(alpha = 0.75f), Color(0xFF7C4DFF).copy(alpha = 0.55f))
                    )
                )
                .border(1.dp, GlassHighlight, RoundedCornerShape(22.dp))
                .clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AddPhotoAlternate,
                contentDescription = "Photos",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Photos",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "Pick any picture from your gallery",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            if (selectedWallpaper == BuiltInWallpaper.CUSTOM_GALLERY) {
                Spacer(modifier = Modifier.weight(1f))
                SelectedBadge(Modifier.padding(0.dp))
            }
        }

        SectionLabel("Built-in")
        BuiltInWallpaper.entries
            .filter { it != BuiltInWallpaper.CUSTOM_GALLERY }
            .chunked(3)
            .forEach { rowWallpapers ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowWallpapers.forEach { wp ->
                        val isSelected = selectedWallpaper == wp
                        val accent = parseHexColor(wp.accentHex, accentColor)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(150.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            accent.copy(alpha = 0.7f),
                                            Color(0xFF0C0D12),
                                            Color.Black
                                        )
                                    )
                                )
                                .border(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) Color.White else GlassHairline,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    onSelectWallpaper(wp)
                                    onUpdateAppearance { it.copy(wallpaperSelectionId = wp.id) }
                                }
                                .testTag("wallpaper_card_${wp.name}"),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Text(
                                text = wp.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                ),
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(0.8f)
                            )
                            if (isSelected) {
                                SelectedBadge(Modifier.align(Alignment.TopEnd))
                            }
                        }
                    }
                    // Fill remaining slots so rows stay even
                    repeat(3 - rowWallpapers.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

        SectionLabel("Apply to")
        EditorCard {
            PillRow(
                options = listOf(
                    com.example.util.WallpaperTarget.HOME to "Home only",
                    com.example.util.WallpaperTarget.LOCK to "Lock screen",
                    com.example.util.WallpaperTarget.BOTH to "Both"
                ),
                selected = wallpaperTarget,
                accentColor = accentColor,
                fontFamily = fontFamily
            ) { target -> onSelectWallpaperTarget(target) }
            Text(
                text = "Home only keeps your lock screen untouched; Both mirrors the wallpaper everywhere.",
                style = MaterialTheme.typography.bodySmall,
                color = GlassSecondary,
                fontSize = 11.5.sp,
                lineHeight = 15.sp
            )
        }

        SectionLabel("Atmosphere")
        EditorCard {
            SliderRow(
                title = "Background dim",
                valueText = "${(wallpaperDim * 100).toInt()}%",
                value = wallpaperDim,
                range = 0.10f..0.85f,
                steps = 7,
                accentColor = accentColor,
                onValueChange = onUpdateWallpaperDim
            )
            Text(
                text = "Darkens the wallpaper so the clock, tasks and icons stay razor sharp over any photo.",
                style = MaterialTheme.typography.bodySmall,
                color = GlassSecondary,
                fontSize = 11.5.sp,
                lineHeight = 15.sp
            )
        }
    }
}

// ==========================================================================
// SPLIT PAGE LAYOUT — live miniature preview (top ~2/3) + controls (bottom ~1/3)
// ==========================================================================

/**
 * ColorOS-style split editor page: the top ~55% shows the shared live phone
 * preview, the bottom ~45% is a frosted panel holding the page's controls
 * (each page scrolls internally via PageContent).
 */
@Composable
private fun SplitPageLayout(
    preview: @Composable () -> Unit,
    pageContent: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.55f)
                .padding(start = 14.dp, end = 14.dp, top = 2.dp, bottom = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF12141D), Color(0xFF05060A))
                        )
                    )
                    .border(1.dp, GlassHairline, RoundedCornerShape(26.dp))
            ) {
                preview()
                PreviewCaption(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f)
                .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1B1E2A), Color(0xFF0C0D13))
                    )
                )
                .border(
                    1.dp,
                    GlassHairline,
                    RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
                )
        ) {
            pageContent()
        }
    }
}

/** Small "Live preview" pill shown at the bottom of the preview pane. */
@Composable
private fun PreviewCaption(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x73000000))
            .border(1.dp, GlassHairline, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "LIVE PREVIEW",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            ),
            color = GlassSecondary
        )
    }
}

// ==========================================================================
// THEME & FONT PAGE — mode, presets, accent swatches, glass, typography
// ==========================================================================

@Composable
private fun ThemePage(
    appearanceState: AppearanceState,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accentColor = LocalAtlasAccent.current
    val fontFamily = LocalAppFontFamily.current

    val accentSwatches = listOf(
        Color(0xFFFF2A4D) to "Crimson",
        Color(0xFF00C8FF) to "Azure",
        Color(0xFF10B981) to "Emerald",
        Color(0xFFF59E0B) to "Amber",
        Color(0xFFA855F7) to "Violet",
        Color(0xFF00E5FF) to "Cyan",
        Color(0xFFFF9500) to "Orange",
        Color(0xFF34C759) to "Green",
        Color(0xFFF2F2F7) to "Titanium",
        Color(0xFF5856D6) to "Indigo"
    )

    PageContent {
        SectionLabel("Appearance mode")
        EditorCard {
            PillRow(
                options = ThemeMode.entries.map { it to it.label },
                selected = appearanceState.themeMode,
                accentColor = accentColor,
                fontFamily = fontFamily
            ) { mode ->
                onUpdateAppearance { it.copy(themeMode = mode) }
            }
        }

        SectionLabel("Color presets")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(ThemeColorPreset.entries) { preset ->
                val isSelected = appearanceState.themeColorPreset == preset
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.2f) else GlassSurface
                        )
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) accentColor.copy(alpha = 0.85f) else GlassHairline,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            onUpdateAppearance {
                                it.copy(
                                    themeColorPreset = preset,
                                    customAccentHex = preset.hexCode,
                                    accentPreset = AccentColorPreset.CUSTOM
                                )
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(preset.primaryColor)
                            .border(1.dp, GlassHighlight, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = preset.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) Color.White else GlassSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        SectionLabel("Accent color")
        EditorCard {
            accentSwatches.chunked(5).forEach { rowColors ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowColors.forEach { (color, name) ->
                        val hex = colorToHex(color)
                        val isSelected =
                            appearanceState.customAccentHex.equals(hex, ignoreCase = true) &&
                                appearanceState.accentPreset == AccentColorPreset.CUSTOM
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    if (isSelected) 2.5.dp else 1.dp,
                                    if (isSelected) Color.White else GlassHairline,
                                    CircleShape
                                )
                                .clickable {
                                    onUpdateAppearance {
                                        it.copy(
                                            customAccentHex = hex,
                                            accentPreset = AccentColorPreset.CUSTOM
                                        )
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = name,
                                    tint = if (color.luminanceCompat() > 0.6f) Color.Black else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        SectionLabel("Typeface")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(AppFontFamily.entries) { font ->
                val isSelected = appearanceState.fontFamily == font
                val previewFamily = TypographyManager.getFontFamily(font)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.2f) else GlassSurface
                        )
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) accentColor.copy(alpha = 0.85f) else GlassHairline,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(fontFamily = font) }
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Aa",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = previewFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp
                        ),
                        color = if (isSelected) Color.White else GlassSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = font.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.5.sp
                        ),
                        color = if (isSelected) Color.White else GlassSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        SectionLabel("Text")
        EditorCard {
            PillRow(
                options = FontSizeScale.entries.map { it to it.label },
                selected = appearanceState.fontSizeScale,
                accentColor = accentColor,
                fontFamily = fontFamily
            ) { scale ->
                onUpdateAppearance { it.copy(fontSizeScale = scale) }
            }
            PillRow(
                options = FontBoldnessOption.entries.map { it to it.label },
                selected = appearanceState.fontBoldness,
                accentColor = accentColor,
                fontFamily = fontFamily
            ) { weight ->
                onUpdateAppearance { it.copy(fontBoldness = weight) }
            }
            PillRow(
                options = LineHeightOption.entries.map { it to it.label },
                selected = LineHeightOption.entries.firstOrNull {
                    it.multiplier == appearanceState.fontLineHeightMultiplier
                } ?: LineHeightOption.NORMAL,
                accentColor = accentColor,
                fontFamily = fontFamily
            ) { option ->
                onUpdateAppearance { it.copy(fontLineHeightMultiplier = option.multiplier) }
            }
        }

        SectionLabel("Glass surface")
        EditorCard {
            SliderRow(
                title = "Card opacity",
                valueText = "${appearanceState.cardTransparency}%",
                value = appearanceState.cardTransparency.toFloat(),
                range = 5f..40f,
                steps = 7,
                accentColor = accentColor
            ) { value ->
                onUpdateAppearance { it.copy(cardTransparency = value.toInt()) }
            }
            Text(
                text = "Controls how solid every glass card, clock pill and dock looks over the wallpaper.",
                style = MaterialTheme.typography.bodySmall,
                color = GlassSecondary,
                fontSize = 11.5.sp,
                lineHeight = 15.sp
            )
        }

        SectionLabel("Icon labels")
        EditorCard {
            PillRow(
                options = IconLabelSize.entries.map { it to it.label },
                selected = appearanceState.iconLabelSize,
                accentColor = accentColor,
                fontFamily = fontFamily
            ) { size ->
                onUpdateAppearance { it.copy(iconLabelSize = size) }
            }
        }
    }
}

// ==========================================================================
// ICONS PAGE — live preview + size/style/labels, ColorOS style
// ==========================================================================

@Composable
private fun IconsPage(
    appearanceState: AppearanceState,
    appIconSize: AppIconSize,
    onSelectIconSize: (AppIconSize) -> Unit,
    hideAppLabels: Boolean,
    onToggleHideAppLabels: (Boolean) -> Unit,
    hideDockLabels: Boolean,
    onToggleHideDockLabels: (Boolean) -> Unit,
    drawerGridColumns: Int,
    onSelectGridColumns: (Int) -> Unit,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accentColor = LocalAtlasAccent.current
    val fontFamily = LocalAppFontFamily.current
    PageContent {
        SectionLabel("Icon size")
        EditorCard {
            PillRow(
                options = AppIconSize.entries.map { it to "${it.label} · ${it.dpSize}dp" },
                selected = appIconSize,
                accentColor = accentColor,
                fontFamily = fontFamily
            ) { size ->
                onSelectIconSize(size)
            }
        }

        SectionLabel("Corner radius")
        EditorCard {
            SliderRow(
                title = "Icon corners",
                valueText = "${appearanceState.iconCornerRadiusPercent}%",
                value = appearanceState.iconCornerRadiusPercent.toFloat(),
                range = 3f..50f,
                steps = 47,
                accentColor = accentColor
            ) { value ->
                onUpdateAppearance { it.copy(iconCornerRadiusPercent = value.toInt()) }
            }
        }

        SectionLabel("Labels")
        EditorCard {
            ToggleRow(
                title = "Show app names in drawer",
                subtitle = if (hideAppLabels) "Icons only — ultra clean" else "Names shown under icons",
                checked = !hideAppLabels,
                accentColor = accentColor,
                onCheckedChange = { enabled -> onToggleHideAppLabels(!enabled) }
            )
            ToggleRow(
                title = "Show names in dock",
                subtitle = if (hideDockLabels) "Dock shows icons only" else "Names shown under dock icons",
                checked = !hideDockLabels,
                accentColor = accentColor,
                onCheckedChange = { enabled -> onToggleHideDockLabels(!enabled) }
            )
        }

        SectionLabel("App drawer grid")
        EditorCard {
            PillRow(
                options = listOf(
                    3 to "3 columns",
                    4 to "4 columns",
                    5 to "5 columns"
                ),
                selected = drawerGridColumns,
                accentColor = accentColor,
                fontFamily = fontFamily
            ) { columns ->
                onSelectGridColumns(columns)
            }
        }
    }
}

// ==========================================================================
// CLOCK PAGE — face cards, digital layout, options, date format
// ==========================================================================

@Composable
private fun ClockPage(
    appearanceState: AppearanceState,
    clockStyle: ClockStyle,
    onSelectClockStyle: (ClockStyle) -> Unit,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accentColor = LocalAtlasAccent.current
    val fontFamily = LocalAppFontFamily.current

    val faces = listOf(
        ClockStylePreset.MODERN to "Neo Glass",
        ClockStylePreset.LARGE_MINIMAL to "Hero",
        ClockStylePreset.MINIMAL to "Minimal",
        ClockStylePreset.NOTHING_STYLE to "Nothing",
        ClockStylePreset.PIXEL_STYLE to "Pixel",
        ClockStylePreset.HYPEROS to "HyperOS",
        ClockStylePreset.STEALTHICS to "Stealthics",
        ClockStylePreset.CYBER to "Cyber",
        ClockStylePreset.DIGITAL to "Digital",
        ClockStylePreset.DIGITAL_THIN to "Thin",
        ClockStylePreset.OUTLINE to "Outline",
        ClockStylePreset.GLASS to "Glass",
        ClockStylePreset.SAMSUNG_STYLE to "Stacked",
        ClockStylePreset.MONOSPACE to "Mono"
    )

    val dateFormats = listOf(
        "EEE, MMM d" to "Fri, Oct 3",
        "EEEE" to "Friday",
        "MMM d, yyyy" to "Oct 3, 2026"
    )

    PageContent {
        SectionLabel("Clock face")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(faces) { (preset, label) ->
                val isSelected = appearanceState.clockStylePreset == preset
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.2f) else GlassSurface
                        )
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) accentColor.copy(alpha = 0.85f) else GlassHairline,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(clockStylePreset = preset) }
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "09:41",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 18.sp
                        ),
                        color = if (isSelected) accentColor else Color.White
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.5.sp
                        ),
                        color = if (isSelected) Color.White else GlassSecondary
                    )
                }
            }
        }

        SectionLabel("Digital layout")
        EditorCard {
            PillRow(
                options = listOf(
                    ClockStyle.NEO_GLASS to "Neo Glass",
                    ClockStyle.STACKED_DIGITAL to "Stacked",
                    ClockStyle.CHRONOGRAPH to "Chrono"
                ),
                selected = clockStyle,
                accentColor = accentColor,
                fontFamily = fontFamily
            ) { style ->
                onSelectClockStyle(style)
            }
        }

        SectionLabel("Options")
        EditorCard {
            ToggleRow(
                title = "24-hour time",
                subtitle = if (appearanceState.clockIs24Hour) "Showing 14:30" else "Showing 2:30 PM",
                checked = appearanceState.clockIs24Hour,
                accentColor = accentColor,
                onCheckedChange = { enabled ->
                    onUpdateAppearance { it.copy(clockIs24Hour = enabled) }
                }
            )
            ToggleRow(
                title = "Show seconds",
                subtitle = "Live seconds sweep on the clock",
                checked = appearanceState.clockShowSeconds,
                accentColor = accentColor,
                onCheckedChange = { enabled ->
                    onUpdateAppearance { it.copy(clockShowSeconds = enabled) }
                }
            )
            ToggleRow(
                title = "Weather chip",
                subtitle = "Show weather next to the clock",
                checked = appearanceState.clockShowWeather,
                accentColor = accentColor,
                onCheckedChange = { enabled ->
                    onUpdateAppearance { it.copy(clockShowWeather = enabled) }
                }
            )
        }

        SectionLabel("Date format")
        EditorCard {
            PillRow(
                options = dateFormats,
                selected = appearanceState.clockDateFormat,
                accentColor = accentColor,
                fontFamily = fontFamily
            ) { pattern ->
                onUpdateAppearance { it.copy(clockDateFormat = pattern) }
            }
        }
    }
}

// ==========================================================================
// LAYOUT PAGE — home screen toggles + widget margins
// ==========================================================================

@Composable
private fun LayoutPage(
    appearanceState: AppearanceState,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accentColor = LocalAtlasAccent.current

    PageContent {
        SectionLabel("Home screen")
        EditorCard {
            ToggleRow(
                title = "Hide dock",
                subtitle = if (appearanceState.layoutHideDock) "Dock hidden on home" else "Dock visible on home",
                checked = appearanceState.layoutHideDock,
                accentColor = accentColor,
                onCheckedChange = { enabled ->
                    onUpdateAppearance { it.copy(layoutHideDock = enabled) }
                }
            )
            ToggleRow(
                title = "Hide status bar",
                subtitle = if (appearanceState.layoutHideStatusBar) "Immersive full-bleed home" else "System status bar shown",
                checked = appearanceState.layoutHideStatusBar,
                accentColor = accentColor,
                onCheckedChange = { enabled ->
                    onUpdateAppearance { it.copy(layoutHideStatusBar = enabled) }
                }
            )
        }

        SectionLabel("Widget margins")
        EditorCard {
            SliderRow(
                title = "Side margins",
                valueText = "${appearanceState.layoutWidgetPaddingDp} dp",
                value = appearanceState.layoutWidgetPaddingDp.toFloat(),
                range = 8f..32f,
                steps = 24,
                accentColor = accentColor
            ) { value ->
                onUpdateAppearance { it.copy(layoutWidgetPaddingDp = value.toInt()) }
            }
        }
    }
}

// ==========================================================================
// MORE PAGE — reset + guidance
// ==========================================================================

@Composable
private fun MorePage(
    onResetAppearance: () -> Unit
) {
    val accentColor = LocalAtlasAccent.current
    val fontFamily = LocalAppFontFamily.current

    PageContent {
        SectionLabel("Reset")
        EditorCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = 0.10f))
                    .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .clickable { onResetAppearance() }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("editor_reset_appearance")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Reset",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Reset all appearance",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Return every style to the default look",
                        style = MaterialTheme.typography.bodySmall,
                        color = GlassSecondary,
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        SectionLabel("About live editing")
        EditorCard {
            Text(
                text = "Changes apply instantly and are saved automatically. Long-press an empty spot on the home screen any time to reopen this editor, and double-tap a blank area to lock the phone.",
                style = MaterialTheme.typography.bodySmall,
                color = GlassSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}
