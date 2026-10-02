package com.example.ui.appearance

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TextFields
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AppIconSize
import com.example.domain.model.BuiltInWallpaper
import com.example.ui.theme.LocalAtlasAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ClockStyle
import com.example.ui.viewmodel.EditCategory
import java.util.Locale

// ==========================================================================
// Design tokens — ColorOS style liquid glass over AMOLED
// ==========================================================================

private val SheetTopHighlight = Color(0x33FFFFFF)
private val SheetHairline = Color(0x14FFFFFF)
private val SheetSurface = Color(0x14FFFFFF)
private val SheetSurfaceStrong = Color(0x1FFFFFFF)
private val SheetTrack = Color(0x33FFFFFF)
private val SheetSecondary = Color(0xB3FFFFFF)

/**
 * Top bar of the Live Home Screen Editor.
 * Left: close · Center: "Customize" capsule · Right: Done.
 */
@Composable
fun LiveEditorTopBar(
    onClose: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAtlasAccent.current
    val fontFamily = LocalAppFontFamily.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: close
        val closeInteractionSource = remember { MutableInteractionSource() }
        val isClosePressed by closeInteractionSource.collectIsPressedAsState()
        val closeScale by animateFloatAsState(
            targetValue = if (isClosePressed) 0.92f else 1.0f,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
            label = "close_button_scale"
        )

        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = closeScale
                    scaleY = closeScale
                }
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x1F222834))
                .border(1.dp, SheetHairline, CircleShape)
                .clickable(
                    interactionSource = closeInteractionSource,
                    indication = null
                ) { onClose() }
                .testTag("editor_top_bar_close"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close",
                tint = TextPureWhite,
                modifier = Modifier.size(18.dp)
            )
        }

        // Center: Customize capsule
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0x1F222834))
                .border(1.dp, SheetHairline, RoundedCornerShape(22.dp))
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Text(
                text = "Customize",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    letterSpacing = 0.4.sp
                ),
                color = TextPureWhite
            )
        }

        // Right: Done pill
        val doneInteractionSource = remember { MutableInteractionSource() }
        val isDonePressed by doneInteractionSource.collectIsPressedAsState()
        val doneScale by animateFloatAsState(
            targetValue = if (isDonePressed) 0.92f else 1.0f,
            animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
            label = "done_button_scale"
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .graphicsLayer {
                    scaleX = doneScale
                    scaleY = doneScale
                }
                .clip(RoundedCornerShape(20.dp))
                .background(accentColor.copy(alpha = 0.25f))
                .border(1.dp, accentColor.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = doneInteractionSource,
                    indication = null
                ) { onDone() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("editor_top_bar_done")
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Done",
                tint = TextPureWhite,
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = "Done",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = TextPureWhite
            )
        }
    }
}

/**
 * Bottom customization sheet.
 *
 * ColorOS style: a compact glass sheet pinned to the bottom with a horizontally
 * scrollable category rail. Collapsed it only shows the rail; expanding a category
 * grows the sheet to a bounded share of the screen (never more than ~54%) with a
 * scrollable content area, while the live home screen stays visible above it.
 *
 * Every control here writes to real state: wallpaper + dim, theme mode/preset/accent,
 * glass opacity, typography, clock face, icon size/radius/labels/grid and widget style.
 */
@Composable
fun LiveEditorBottomDock(
    activeCategory: EditCategory?,
    onSelectCategory: (EditCategory?) -> Unit,
    appearanceState: AppearanceState,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit,
    selectedWallpaper: BuiltInWallpaper,
    wallpaperDim: Float,
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
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAtlasAccent.current
    val fontFamily = LocalAppFontFamily.current

    // Bounded sheet height: never more than ~54% of the screen when expanded
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val openHeight = (screenHeightDp * 0.54f).toInt().coerceIn(330, 560).dp
    val panelHeight by animateDpAsState(
        targetValue = if (activeCategory != null) openHeight else 76.dp,
        animationSpec = spring(
            dampingRatio = 0.86f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "sheet_height"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(panelHeight)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xF50A0B10),
                            Color(0xF212141B)
                        )
                    )
                )
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                SheetTopHighlight,
                                SheetHairline,
                                Color(0x0DFFFFFF)
                            )
                        )
                    ),
                    RoundedCornerShape(28.dp)
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Grab handle — tapping collapses the sheet
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 9.dp, bottom = 3.dp)
                        .width(38.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (activeCategory != null) accentColor.copy(alpha = 0.8f)
                            else Color(0x3DFFFFFF)
                        )
                        .clickable {
                            if (activeCategory != null) onSelectCategory(null)
                        }
                        .testTag("editor_sheet_handle")
                )

                // Category rail (horizontally scrollable pills, ColorOS style)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryChip(
                        title = "Wallpaper",
                        icon = Icons.Outlined.Wallpaper,
                        isSelected = activeCategory == EditCategory.WALLPAPER,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(
                                if (activeCategory == EditCategory.WALLPAPER) null else EditCategory.WALLPAPER
                            )
                        }
                    )
                    CategoryChip(
                        title = "Theme",
                        icon = Icons.Outlined.Palette,
                        isSelected = activeCategory == EditCategory.APPEARANCE,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(
                                if (activeCategory == EditCategory.APPEARANCE) null else EditCategory.APPEARANCE
                            )
                        }
                    )
                    CategoryChip(
                        title = "Font",
                        icon = Icons.Outlined.TextFields,
                        isSelected = activeCategory == EditCategory.TYPOGRAPHY,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(
                                if (activeCategory == EditCategory.TYPOGRAPHY) null else EditCategory.TYPOGRAPHY
                            )
                        }
                    )
                    CategoryChip(
                        title = "Clock",
                        icon = Icons.Outlined.Schedule,
                        isSelected = activeCategory == EditCategory.CLOCK,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(
                                if (activeCategory == EditCategory.CLOCK) null else EditCategory.CLOCK
                            )
                        }
                    )
                    CategoryChip(
                        title = "Icons",
                        icon = Icons.Outlined.Apps,
                        isSelected = activeCategory == EditCategory.ICONS,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(
                                if (activeCategory == EditCategory.ICONS) null else EditCategory.ICONS
                            )
                        }
                    )
                    CategoryChip(
                        title = "Widgets",
                        icon = Icons.Outlined.Dashboard,
                        isSelected = activeCategory == EditCategory.WIDGETS,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(
                                if (activeCategory == EditCategory.WIDGETS) null else EditCategory.WIDGETS
                            )
                        }
                    )
                }

                // Expanded content
                if (activeCategory != null) {
                    AnimatedContent(
                        targetState = activeCategory,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(170)) togetherWith
                                fadeOut(animationSpec = tween(130))
                        },
                        label = "editor_sheet_transition",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(start = 6.dp, end = 6.dp, bottom = 6.dp)
                    ) { category ->
                        when (category) {
                            EditCategory.WALLPAPER -> WallpaperSheet(
                                appearanceState = appearanceState,
                                fontFamily = fontFamily,
                                selectedWallpaper = selectedWallpaper,
                                wallpaperDim = wallpaperDim,
                                onUpdateWallpaperDim = onUpdateWallpaperDim,
                                onSelectWallpaper = onSelectWallpaper,
                                onSetCustomWallpaper = onSetCustomWallpaper,
                                onUpdateAppearance = onUpdateAppearance
                            )
                            EditCategory.APPEARANCE -> ThemeSheet(
                                appearanceState = appearanceState,
                                fontFamily = fontFamily,
                                onUpdateAppearance = onUpdateAppearance
                            )
                            EditCategory.TYPOGRAPHY -> FontSheet(
                                appearanceState = appearanceState,
                                fontFamily = fontFamily,
                                onUpdateAppearance = onUpdateAppearance
                            )
                            EditCategory.CLOCK -> ClockSheet(
                                appearanceState = appearanceState,
                                fontFamily = fontFamily,
                                clockStyle = clockStyle,
                                onSelectClockStyle = onSelectClockStyle,
                                onUpdateAppearance = onUpdateAppearance
                            )
                            EditCategory.ICONS -> IconsSheet(
                                appearanceState = appearanceState,
                                fontFamily = fontFamily,
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
                            EditCategory.WIDGETS -> LayoutSheet(
                                appearanceState = appearanceState,
                                fontFamily = fontFamily,
                                onUpdateAppearance = onUpdateAppearance
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================================================
// Shared building blocks
// ==========================================================================

/** Compact pill tab in the category rail. */
@Composable
private fun CategoryChip(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    fontFamily: FontFamily,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "category_chip_scale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) accentColor.copy(alpha = 0.20f) else SheetSurface
            )
            .border(
                1.dp,
                if (isSelected) accentColor.copy(alpha = 0.65f) else SheetHairline,
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("dock_cat_$title")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) accentColor else SheetSecondary,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = fontFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.5.sp
            ),
            color = if (isSelected) TextPureWhite else SheetSecondary
        )
    }
}

/** Uppercase section label above a group of controls. */
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(Locale.US),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 1.1.sp
        ),
        color = TextMuted,
        modifier = Modifier.padding(start = 6.dp, top = 6.dp)
    )
}

/** Translucent rounded card that groups controls. */
@Composable
private fun EditorCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SheetSurface)
            .border(1.dp, SheetHairline, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
                style = MaterialTheme.typography.bodySmall,
                color = SheetSecondary
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPureWhite
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
                inactiveTrackColor = SheetTrack
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
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPureWhite,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp
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
                uncheckedTrackColor = SheetTrack,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

/**
 * Horizontally scrollable pill selector (ColorOS style option row).
 */
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
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) accentColor.copy(alpha = 0.20f) else SheetSurface
                    )
                    .border(
                        1.dp,
                        if (isSelected) accentColor.copy(alpha = 0.7f) else SheetHairline,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelect(value) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = fontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.5.sp
                    ),
                    color = if (isSelected) TextPureWhite else SheetSecondary
                )
            }
        }
    }
}

/** Scrollable content column used by every sheet. */
@Composable
private fun SheetContent(
    fontFamily: FontFamily,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

private fun parseHexColor(hex: String, fallback: Color): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    fallback
}

private fun colorToHex(color: Color): String =
    String.format(Locale.US, "#%06X", 0xFFFFFF and color.value.toInt())

// ==========================================================================
// 1. WALLPAPER
// ==========================================================================

@Composable
private fun WallpaperSheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
    selectedWallpaper: BuiltInWallpaper,
    wallpaperDim: Float,
    onUpdateWallpaperDim: (Float) -> Unit,
    onSelectWallpaper: (BuiltInWallpaper) -> Unit,
    onSetCustomWallpaper: (String) -> Unit,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accentColor = LocalAtlasAccent.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onSetCustomWallpaper(uri.toString())
            onSelectWallpaper(BuiltInWallpaper.CUSTOM_GALLERY)
        }
    }

    SheetContent(fontFamily = fontFamily) {
        SectionLabel("Wallpapers")

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            // Custom photo card
            item {
                val isCustomSelected = selectedWallpaper == BuiltInWallpaper.CUSTOM_GALLERY
                Box(
                    modifier = Modifier
                        .size(width = 92.dp, height = 124.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SheetSurface)
                        .border(
                            if (isCustomSelected) 2.dp else 1.dp,
                            if (isCustomSelected) Color.White else SheetHairline,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddPhotoAlternate,
                            contentDescription = "Photos",
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Photos",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = TextPureWhite
                        )
                    }
                    if (isCustomSelected) {
                        SelectedBadge(Modifier.align(Alignment.TopEnd))
                    }
                }
            }

            items(BuiltInWallpaper.entries.filter { it != BuiltInWallpaper.CUSTOM_GALLERY }) { wp ->
                val isSelected = selectedWallpaper == wp
                val accent = parseHexColor(wp.accentHex, accentColor)
                Box(
                    modifier = Modifier
                        .size(width = 92.dp, height = 124.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    accent.copy(alpha = 0.55f),
                                    Color(0xFF0C0D12),
                                    Color.Black
                                )
                            )
                        )
                        .border(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) Color.White else SheetHairline,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            onSelectWallpaper(wp)
                            onUpdateAppearance { it.copy(wallpaperSelectionId = wp.id) }
                        }
                        .padding(9.dp)
                ) {
                    Text(
                        text = wp.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        ),
                        color = TextPureWhite,
                        modifier = Modifier.align(Alignment.BottomStart),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (isSelected) {
                        SelectedBadge(Modifier.align(Alignment.TopEnd))
                    }
                }
            }
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
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun SelectedBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(6.dp)
            .size(18.dp)
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

// ==========================================================================
// 2. THEME
// ==========================================================================

@Composable
private fun ThemeSheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accentColor = LocalAtlasAccent.current

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

    SheetContent(fontFamily = fontFamily) {
        SectionLabel("Appearance mode")
        PillRow(
            options = ThemeMode.entries.map { it to it.label },
            selected = appearanceState.themeMode,
            accentColor = accentColor,
            fontFamily = fontFamily
        ) { mode ->
            onUpdateAppearance { it.copy(themeMode = mode) }
        }

        SectionLabel("Color presets")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(ThemeColorPreset.entries) { preset ->
                val isSelected = appearanceState.themeColorPreset == preset
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.16f) else SheetSurface
                        )
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) accentColor.copy(alpha = 0.8f) else SheetHairline,
                            RoundedCornerShape(16.dp)
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
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(preset.primaryColor)
                            .border(1.dp, SheetHairline, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = preset.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.5.sp
                        ),
                        color = if (isSelected) TextPureWhite else SheetSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        SectionLabel("Accent color")
        accentSwatches.chunked(5).forEach { rowColors ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowColors.forEach { (color, name) ->
                    val hex = colorToHex(color)
                    val isSelected =
                        appearanceState.customAccentHex.equals(hex, ignoreCase = true) &&
                            appearanceState.accentPreset == AccentColorPreset.CUSTOM
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                if (isSelected) 2.5.dp else 1.dp,
                                if (isSelected) Color.White else SheetHairline,
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
                                modifier = Modifier.size(15.dp)
                            )
                        } else {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.Black.copy(alpha = 0.45f),
                                maxLines = 1
                            )
                        }
                    }
                }
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
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

private fun Color.luminanceCompat(): Float =
    (0.299f * red + 0.587f * green + 0.114f * blue)

// ==========================================================================
// 3. FONT
// ==========================================================================

@Composable
private fun FontSheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accentColor = LocalAtlasAccent.current

    SheetContent(fontFamily = fontFamily) {
        SectionLabel("Typeface")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(AppFontFamily.entries) { font ->
                val isSelected = appearanceState.fontFamily == font
                val previewFamily = TypographyManager.getFontFamily(font)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.16f) else SheetSurface
                        )
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) accentColor.copy(alpha = 0.8f) else SheetHairline,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(fontFamily = font) }
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Aa",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = previewFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp
                        ),
                        color = if (isSelected) TextPureWhite else SheetSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = font.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp
                        ),
                        color = if (isSelected) TextPureWhite else SheetSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        SectionLabel("Text size")
        PillRow(
            options = FontSizeScale.entries.map { it to it.label },
            selected = appearanceState.fontSizeScale,
            accentColor = accentColor,
            fontFamily = fontFamily
        ) { scale ->
            onUpdateAppearance { it.copy(fontSizeScale = scale) }
        }

        SectionLabel("Weight")
        PillRow(
            options = FontBoldnessOption.entries.map { it to it.label },
            selected = appearanceState.fontBoldness,
            accentColor = accentColor,
            fontFamily = fontFamily
        ) { weight ->
            onUpdateAppearance { it.copy(fontBoldness = weight) }
        }

        SectionLabel("Line height")
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

        SectionLabel("Preview")
        EditorCard {
            Text(
                text = "Energy flows where attention goes.",
                style = MaterialTheme.typography.titleMedium,
                color = TextPureWhite,
                fontSize = 16.sp
            )
            Text(
                text = "Build the habit before you build the ambition.",
                style = MaterialTheme.typography.bodyMedium,
                color = SheetSecondary,
                fontSize = 13.sp
            )
        }
    }
}

// ==========================================================================
// 4. CLOCK
// ==========================================================================

@Composable
private fun ClockSheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
    clockStyle: ClockStyle,
    onSelectClockStyle: (ClockStyle) -> Unit,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accentColor = LocalAtlasAccent.current

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

    SheetContent(fontFamily = fontFamily) {
        SectionLabel("Clock face")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(faces) { (preset, label) ->
                val isSelected = appearanceState.clockStylePreset == preset
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) accentColor.copy(alpha = 0.16f) else SheetSurface
                        )
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) accentColor.copy(alpha = 0.8f) else SheetHairline,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(clockStylePreset = preset) }
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "09:41",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 17.sp
                        ),
                        color = if (isSelected) accentColor else TextPureWhite
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp
                        ),
                        color = if (isSelected) TextPureWhite else SheetSecondary
                    )
                }
            }
        }

        SectionLabel("Digital layout")
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
        }

        SectionLabel("Date format")
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

// ==========================================================================
// 5. ICONS
// ==========================================================================

@Composable
private fun IconsSheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
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

    SheetContent(fontFamily = fontFamily) {
        SectionLabel("Icon size")
        PillRow(
            options = AppIconSize.entries.map { it to "${it.label} · ${it.dpSize}dp" },
            selected = appIconSize,
            accentColor = accentColor,
            fontFamily = fontFamily
        ) { size ->
            onSelectIconSize(size)
        }

        SectionLabel("Corner radius")
        EditorCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val previewSize = (34 + index * 8).dp
                    Box(
                        modifier = Modifier
                            .size(previewSize)
                            .clip(
                                RoundedCornerShape(
                                    (appearanceState.iconCornerRadiusPercent * 0.3f)
                                        .coerceIn(3f, 30f).dp
                                )
                            )
                            .background(accentColor.copy(alpha = 0.35f))
                            .border(1.dp, accentColor.copy(alpha = 0.7f), RoundedCornerShape(
                                (appearanceState.iconCornerRadiusPercent * 0.3f)
                                    .coerceIn(3f, 30f).dp
                            ))
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${appearanceState.iconCornerRadiusPercent}%",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPureWhite
                )
            }
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
                title = "Hide app names in drawer",
                subtitle = if (hideAppLabels) "Icons only — ultra clean" else "Names shown under icons",
                checked = hideAppLabels,
                accentColor = accentColor,
                onCheckedChange = onToggleHideAppLabels
            )
            ToggleRow(
                title = "Hide names in dock",
                subtitle = if (hideDockLabels) "Dock shows icons only" else "Names shown under dock icons",
                checked = hideDockLabels,
                accentColor = accentColor,
                onCheckedChange = onToggleHideDockLabels
            )
        }

        SectionLabel("App drawer grid")
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

// ==========================================================================
// 6. WIDGETS / LAYOUT
// ==========================================================================

@Composable
private fun LayoutSheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accentColor = LocalAtlasAccent.current

    SheetContent(fontFamily = fontFamily) {
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

        SectionLabel("Mission widget style")
        PillRow(
            options = WidgetStyle.entries.map { it to it.label },
            selected = appearanceState.widgetStyle,
            accentColor = accentColor,
            fontFamily = fontFamily
        ) { style ->
            onUpdateAppearance { it.copy(widgetStyle = style) }
        }

        SectionLabel("Widget surface")
        EditorCard {
            SliderRow(
                title = "Transparency",
                valueText = "${appearanceState.widgetTransparency}%",
                value = appearanceState.widgetTransparency.toFloat(),
                range = 0f..80f,
                steps = 8,
                accentColor = accentColor
            ) { value ->
                onUpdateAppearance { it.copy(widgetTransparency = value.toInt()) }
            }
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

        SectionLabel("Preview hint")
        Text(
            text = "Every control applies live to the home screen above — no save button needed.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
    }
}
