package com.example.ui.appearance

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BuiltInWallpaper
import com.example.ui.theme.LocalAtlasAccent
import com.example.ui.theme.TextPureWhite
import com.example.ui.viewmodel.EditCategory
import java.util.Locale

// Apple Human Interface Guidelines & visionOS Liquid Glass Palette
private val AppleLiquidGlassBackground = Color(0xF0000000)
private val AppleGlassSurface = Color(0x1F222834)
private val AppleGlassBorder = Color(0x1FFFFFFF)
private val AppleGlassBorderHighlight = Color(0x33FFFFFF)
private val AppleTextSecondary = Color(0xB3FFFFFF) // 70% opacity white as requested

/**
 * Top Bar for Live Home Screen Editor.
 * Designed like premium flagship Android launchers & iOS edit mode:
 * Left: Close button
 * Center: "Customize" capsule
 * Right: Done button
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
        // Left: Liquid Glass Cancel/Close Button
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
                .background(AppleGlassSurface)
                .border(1.dp, AppleGlassBorder, CircleShape)
                .clickable(
                    interactionSource = closeInteractionSource,
                    indication = null
                ) { onClose() }
                .testTag("editor_top_bar_close"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Cancel Customization",
                tint = TextPureWhite,
                modifier = Modifier.size(19.dp)
            )
        }

        // Center: Flagship Island Pill "Customize"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .background(AppleGlassSurface)
                .border(1.dp, AppleGlassBorder, RoundedCornerShape(22.dp))
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

        // Right: Done Button (Apple Pill style)
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
                imageVector = Icons.Rounded.Check,
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
 * Floating Liquid Glass Customization Dock.
 * Inspired by ColorOS, OneUI, Nothing OS and Apple iOS/visionOS.
 * Single unified dock on top of the live home screen.
 */
@Composable
fun LiveEditorBottomDock(
    activeCategory: EditCategory?,
    onSelectCategory: (EditCategory?) -> Unit,
    appearanceState: AppearanceState,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit,
    onSelectWallpaper: (BuiltInWallpaper) -> Unit,
    onSetCustomWallpaper: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAtlasAccent.current
    val fontFamily = LocalAppFontFamily.current

    // Spring physics panel expansion
    val panelHeight by animateDpAsState(
        targetValue = if (activeCategory != null) 380.dp else 84.dp,
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "liquid_glass_expansion"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Floating Rounded Liquid Glass Dock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(panelHeight)
                .clip(RoundedCornerShape(30.dp))
                .background(AppleLiquidGlassBackground)
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                AppleGlassBorderHighlight,
                                AppleGlassBorder,
                                Color(0x10FFFFFF)
                            )
                        )
                    ),
                    RoundedCornerShape(30.dp)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Grab Handle (Subtle liquid pill)
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 4.dp)
                        .width(38.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (activeCategory != null) accentColor.copy(alpha = 0.8f) else Color(0x3DFFFFFF))
                        .clickable {
                            if (activeCategory != null) {
                                onSelectCategory(null)
                            }
                        }
                )

                // Category Selector Dock (Monochrome SF Symbols inspired icons)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryDockItem(
                        title = "Wallpaper",
                        icon = Icons.Outlined.Wallpaper,
                        isSelected = activeCategory == EditCategory.WALLPAPER,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(if (activeCategory == EditCategory.WALLPAPER) null else EditCategory.WALLPAPER)
                        }
                    )
                    CategoryDockItem(
                        title = "Theme",
                        icon = Icons.Outlined.Palette,
                        isSelected = activeCategory == EditCategory.APPEARANCE,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(if (activeCategory == EditCategory.APPEARANCE) null else EditCategory.APPEARANCE)
                        }
                    )
                    CategoryDockItem(
                        title = "Font",
                        icon = Icons.Outlined.TextFields,
                        isSelected = activeCategory == EditCategory.TYPOGRAPHY,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(if (activeCategory == EditCategory.TYPOGRAPHY) null else EditCategory.TYPOGRAPHY)
                        }
                    )
                    CategoryDockItem(
                        title = "Clock",
                        icon = Icons.Outlined.Schedule,
                        isSelected = activeCategory == EditCategory.CLOCK,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(if (activeCategory == EditCategory.CLOCK) null else EditCategory.CLOCK)
                        }
                    )
                    CategoryDockItem(
                        title = "Icons",
                        icon = Icons.Outlined.Apps,
                        isSelected = activeCategory == EditCategory.ICONS,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(if (activeCategory == EditCategory.ICONS) null else EditCategory.ICONS)
                        }
                    )
                    CategoryDockItem(
                        title = "Layout",
                        icon = Icons.Outlined.Dashboard,
                        isSelected = activeCategory == EditCategory.WIDGETS,
                        accentColor = accentColor,
                        fontFamily = fontFamily,
                        onClick = {
                            onSelectCategory(if (activeCategory == EditCategory.WIDGETS) null else EditCategory.WIDGETS)
                        }
                    )
                }

                // Expanded Customization Panel
                if (activeCategory != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(top = 4.dp)
                    ) {
                        AnimatedContent(
                            targetState = activeCategory,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(180)) togetherWith
                                        fadeOut(animationSpec = tween(150))
                            },
                            label = "apple_dock_sheet_transition"
                        ) { category ->
                            when (category) {
                                EditCategory.WALLPAPER -> AppleWallpaperSheet(
                                    appearanceState = appearanceState,
                                    fontFamily = fontFamily,
                                    accentColor = accentColor,
                                    onUpdateAppearance = onUpdateAppearance,
                                    onSelectWallpaper = onSelectWallpaper,
                                    onSetCustomWallpaper = onSetCustomWallpaper
                                )
                                EditCategory.APPEARANCE -> AppleAppearanceSheet(
                                    appearanceState = appearanceState,
                                    fontFamily = fontFamily,
                                    accentColor = accentColor,
                                    onUpdateAppearance = onUpdateAppearance
                                )
                                EditCategory.TYPOGRAPHY -> AppleTypographySheet(
                                    appearanceState = appearanceState,
                                    fontFamily = fontFamily,
                                    accentColor = accentColor,
                                    onUpdateAppearance = onUpdateAppearance
                                )
                                EditCategory.CLOCK -> AppleClockSheet(
                                    appearanceState = appearanceState,
                                    fontFamily = fontFamily,
                                    accentColor = accentColor,
                                    onUpdateAppearance = onUpdateAppearance
                                )
                                EditCategory.ICONS -> AppleIconsSheet(
                                    appearanceState = appearanceState,
                                    fontFamily = fontFamily,
                                    accentColor = accentColor,
                                    onUpdateAppearance = onUpdateAppearance
                                )
                                EditCategory.WIDGETS -> AppleWidgetsSheet(
                                    appearanceState = appearanceState,
                                    fontFamily = fontFamily,
                                    accentColor = accentColor,
                                    onUpdateAppearance = onUpdateAppearance
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Category Tab inside the Liquid Glass Dock.
 * Minimum 48dp touch target, soft spring bounce on press.
 */
@Composable
private fun CategoryDockItem(
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
        targetValue = if (isPressed) 0.90f else if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "category_button_bounce"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isSelected) AppleGlassBorderHighlight
                else Color.Transparent
            )
            .border(
                if (isSelected) 1.dp else 0.dp,
                if (isSelected) AppleGlassBorderHighlight else Color.Transparent,
                RoundedCornerShape(18.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("dock_cat_$title")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) TextPureWhite else AppleTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = fontFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp
            ),
            color = if (isSelected) TextPureWhite else AppleTextSecondary
        )
    }
}

// =========================================================================
// 1. APPLE WALLPAPER SHEET
// =========================================================================

@Composable
private fun AppleWallpaperSheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
    accentColor: Color,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit,
    onSelectWallpaper: (BuiltInWallpaper) -> Unit,
    onSetCustomWallpaper: (String) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onSetCustomWallpaper(uri.toString())
            onSelectWallpaper(BuiltInWallpaper.CUSTOM_GALLERY)
        }
    }

    var selectedFilter by remember { mutableStateOf("All") }
    val filterTabs = listOf("All", "AMOLED", "Gradient", "Minimal", "AI Cosmic")

    data class WpItem(val wallpaper: BuiltInWallpaper, val color: Color, val tag: String)

    val allWallpapers = listOf(
        WpItem(BuiltInWallpaper.OBSIDIAN_VOID, Color(0xFF007AFF), "AMOLED"),
        WpItem(BuiltInWallpaper.STEALTH_OLED, Color(0xFF64748B), "AMOLED"),
        WpItem(BuiltInWallpaper.CYBER_EMERALD, Color(0xFF30D158), "Gradient"),
        WpItem(BuiltInWallpaper.ELECTRIC_AZURE, Color(0xFF0A84FF), "Gradient"),
        WpItem(BuiltInWallpaper.AMBER_ECLIPSE, Color(0xFFFF9500), "Gradient"),
        WpItem(BuiltInWallpaper.VIOLET_TWILIGHT, Color(0xFFAF52DE), "AI Cosmic")
    )

    val filteredList = if (selectedFilter == "All") allWallpapers else allWallpapers.filter { it.tag == selectedFilter }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Filter tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(filterTabs) { tab ->
                val isTabSelected = selectedFilter == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isTabSelected) accentColor.copy(alpha = 0.3f) else AppleGlassSurface)
                        .border(
                            1.dp,
                            if (isTabSelected) accentColor else AppleGlassBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedFilter = tab }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.5.sp
                        ),
                        color = if (isTabSelected) TextPureWhite else AppleTextSecondary
                    )
                }
            }
        }

        // Floating Wallpaper Cards Carousel
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            // Photo Picker Card
            item {
                Box(
                    modifier = Modifier
                        .size(width = 96.dp, height = 130.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(AppleGlassSurface)
                        .border(1.dp, AppleGlassBorder, RoundedCornerShape(20.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
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
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = "Photos",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            ),
                            color = TextPureWhite
                        )
                    }
                }
            }

            // Built-in Wallpapers
            items(filteredList) { item ->
                val wp = item.wallpaper
                val thumbAccent = item.color
                val isSelected = appearanceState.wallpaperSelectionId == wp.id ||
                        appearanceState.themeColorPreset.id == wp.id

                Box(
                    modifier = Modifier
                        .size(width = 96.dp, height = 130.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    thumbAccent.copy(alpha = 0.40f),
                                    Color(0xFF10121A),
                                    Color.Black
                                )
                            )
                        )
                        .border(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) Color.White else AppleGlassBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            onSelectWallpaper(wp)
                            onUpdateAppearance { it.copy(wallpaperSelectionId = wp.id) }
                        }
                        .padding(10.dp)
                ) {
                    Text(
                        text = wp.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp
                        ),
                        color = TextPureWhite,
                        modifier = Modifier.align(Alignment.BottomStart),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }

        // Live Atmosphere Dim Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Atmospheric Dim",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Text(
                text = "${appearanceState.wallpaperBlurPercent}%",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPureWhite
            )
        }
        Slider(
            value = appearanceState.wallpaperBlurPercent.toFloat(),
            onValueChange = { percent ->
                onUpdateAppearance { it.copy(wallpaperBlurPercent = percent.toInt()) }
            },
            valueRange = 0f..80f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = AppleGlassBorder
            )
        )
    }
}

// =========================================================================
// 2. APPLE APPEARANCE / THEME SHEET
// =========================================================================

@Composable
private fun AppleAppearanceSheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
    accentColor: Color,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val presets = listOf(
        ThemeColorPreset.AMOLED_BLACK to "AMOLED Black",
        ThemeColorPreset.STEALTHICS_CYAN to "Stealthics Cyan",
        ThemeColorPreset.PURE_WHITE to "Pure White",
        ThemeColorPreset.NEON_BLUE to "Neon Blue",
        ThemeColorPreset.CRIMSON_RED to "Crimson Red",
        ThemeColorPreset.FOREST_GREEN to "Forest Green",
        ThemeColorPreset.MINIMAL_GRAY to "Minimal Gray"
    )

    // Apple System Palette Swatches
    val appleSwatches = listOf(
        Color(0xFF007AFF) to "System Blue",
        Color(0xFF00E5FF) to "Stealthics Cyan",
        Color(0xFFAF52DE) to "Purple",
        Color(0xFFFF2D55) to "Pink",
        Color(0xFFFF9500) to "Orange",
        Color(0xFFFFCC00) to "Yellow",
        Color(0xFF34C759) to "Green",
        Color(0xFF5AC8FA) to "Teal",
        Color(0xFF5856D6) to "Indigo",
        Color(0xFFF2F2F7) to "Titanium"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Preset Themes",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = AppleTextSecondary
        )

        // Floating Glass Cards for Appearance Presets
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(presets) { (preset, label) ->
                val isSelected = appearanceState.themeColorPreset == preset
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) AppleGlassBorderHighlight else AppleGlassSurface)
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) Color.White else AppleGlassBorder,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            onUpdateAppearance {
                                it.copy(
                                    themeColorPreset = preset,
                                    customAccentHex = preset.hexCode
                                )
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(preset.primaryColor)
                            .border(1.dp, AppleGlassBorder, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) TextPureWhite else AppleTextSecondary
                    )
                }
            }
        }

        Text(
            text = "Accent Color",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = AppleTextSecondary
        )

        // Apple System Palette Swatches
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            appleSwatches.forEach { (color, _) ->
                val hexStr = String.format("#%06X", 0xFFFFFF and color.value.toInt())
                val isSelected = appearanceState.customAccentHex.equals(hexStr, ignoreCase = true) ||
                        accentColor == color

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            if (isSelected) 2.5.dp else 0.dp,
                            if (isSelected) Color.White else Color.Transparent,
                            CircleShape
                        )
                        .clickable {
                            onUpdateAppearance {
                                it.copy(
                                    customAccentHex = hexStr,
                                    accentPreset = AccentColorPreset.CUSTOM
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = if (color == Color(0xFFF2F2F7)) Color.Black else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Ultra Translucent Glass Slider (Opacity around 12%)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Glass Surface Opacity",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Text(
                text = "${appearanceState.themeTransparencyPercent}%",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPureWhite
            )
        }
        Slider(
            value = appearanceState.themeTransparencyPercent.toFloat(),
            onValueChange = { trans ->
                onUpdateAppearance { it.copy(themeTransparencyPercent = trans.toInt()) }
            },
            valueRange = 5f..35f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = AppleGlassBorder
            )
        )
    }
}

// =========================================================================
// 3. APPLE TYPOGRAPHY / FONT SHEET
// =========================================================================

@Composable
private fun AppleTypographySheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
    accentColor: Color,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val fonts = listOf(
        AppFontFamily.SF_PRO to "SF Pro Display",
        AppFontFamily.INTER to "Inter",
        AppFontFamily.GEIST to "Geist",
        AppFontFamily.SPACE_GROTESK to "Space Grotesk",
        AppFontFamily.SATOSHI to "Satoshi",
        AppFontFamily.GENERAL_SANS to "General Sans",
        AppFontFamily.MANROPE to "Manrope",
        AppFontFamily.CLASH_GROTESK to "Clash Grotesk",
        AppFontFamily.PLUS_JAKARTA_SANS to "Plus Jakarta Sans",
        AppFontFamily.IBM_PLEX_SANS to "IBM Plex Sans"
    )

    val fontSizes = listOf(
        FontSizeScale.SMALL to "Small",
        FontSizeScale.DEFAULT to "Normal",
        FontSizeScale.LARGE to "Large",
        FontSizeScale.EXTRA_LARGE to "Extra Large"
    )

    val fontWeights = listOf(
        FontBoldnessOption.NORMAL to "Light",
        FontBoldnessOption.MEDIUM to "Regular",
        FontBoldnessOption.SEMIBOLD to "Medium",
        FontBoldnessOption.BOLD to "Bold"
    )

    val lineHeights = listOf(
        LineHeightOption.TIGHT to "Tight",
        LineHeightOption.NORMAL to "Normal",
        LineHeightOption.RELAXED to "Relaxed"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Typeface",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = AppleTextSecondary
        )

        // Large Apple-like Font Cards Carousel
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(fonts) { (fontItem, label) ->
                val isSelected = appearanceState.fontFamily == fontItem
                val itemFont = TypographyManager.getFontFamily(fontItem)

                Box(
                    modifier = Modifier
                        .size(width = 230.dp, height = 158.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (isSelected) AppleGlassBorderHighlight else AppleGlassSurface)
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) Color.White else AppleGlassBorder,
                            RoundedCornerShape(22.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(fontFamily = fontItem) }
                        }
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = itemFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = TextPureWhite
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        // Rich Alphabet & Number Preview
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = itemFont,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = AppleTextSecondary,
                                maxLines = 1
                            )
                            Text(
                                text = "1234567890",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = itemFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = accentColor,
                                maxLines = 1
                            )
                            Text(
                                text = "09:41 • Wednesday, Oct 2",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = itemFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = TextPureWhite,
                                maxLines = 1
                            )
                            Text(
                                text = "Design visionOS spatial canvas",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = itemFont,
                                    fontSize = 9.5.sp
                                ),
                                color = AppleTextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Font Size Chips
        Text(
            text = "Font Size",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = AppleTextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fontSizes.forEach { (scale, label) ->
                val isSelected = appearanceState.fontSizeScale == scale
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AppleGlassBorderHighlight else AppleGlassSurface)
                        .border(
                            if (isSelected) 1.dp else 0.dp,
                            if (isSelected) Color.White else Color.Transparent,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(fontSizeScale = scale) }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) TextPureWhite else AppleTextSecondary
                    )
                }
            }
        }

        // Font Weight Chips
        Text(
            text = "Weight",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = AppleTextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fontWeights.forEach { (boldness, label) ->
                val isSelected = appearanceState.fontBoldness == boldness
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AppleGlassBorderHighlight else AppleGlassSurface)
                        .border(
                            if (isSelected) 1.dp else 0.dp,
                            if (isSelected) Color.White else Color.Transparent,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(fontBoldness = boldness) }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) TextPureWhite else AppleTextSecondary
                    )
                }
            }
        }

        // Line Height Chips
        Text(
            text = "Line Height",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = AppleTextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            lineHeights.forEach { (lh, label) ->
                val isSelected = (appearanceState.fontLineHeightMultiplier == lh.multiplier)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AppleGlassBorderHighlight else AppleGlassSurface)
                        .border(
                            if (isSelected) 1.dp else 0.dp,
                            if (isSelected) Color.White else Color.Transparent,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(fontLineHeightMultiplier = lh.multiplier) }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) TextPureWhite else AppleTextSecondary
                    )
                }
            }
        }

        // Letter Spacing Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Letter Spacing",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Text(
                text = String.format(Locale.US, "%.1f sp", appearanceState.fontLetterSpacingSp),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPureWhite
            )
        }
        Slider(
            value = appearanceState.fontLetterSpacingSp,
            onValueChange = { sp ->
                onUpdateAppearance { it.copy(fontLetterSpacingSp = sp) }
            },
            valueRange = -1.0f..3.0f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = AppleGlassBorder
            )
        )
    }
}

// =========================================================================
// 4. APPLE CLOCK SHEET
// =========================================================================

@Composable
private fun AppleClockSheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
    accentColor: Color,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val clockPresets = listOf(
        ClockStylePreset.MINIMAL to "Minimal",
        ClockStylePreset.NOTHING_STYLE to "Nothing Style",
        ClockStylePreset.APPLE_STYLE to "Apple Style",
        ClockStylePreset.HYPEROS to "HyperOS",
        ClockStylePreset.PIXEL_STYLE to "Pixel",
        ClockStylePreset.STEALTHICS to "Stealthics",
        ClockStylePreset.CYBER to "Cyber",
        ClockStylePreset.DIGITAL to "Digital",
        ClockStylePreset.OUTLINE to "Outline",
        ClockStylePreset.GLASS to "Glass"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Clock Layouts",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = AppleTextSecondary
        )

        // Large Live Clock Previews Carousel
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(clockPresets) { (preset, label) ->
                val isSelected = appearanceState.clockStylePreset == preset
                Box(
                    modifier = Modifier
                        .size(width = 156.dp, height = 108.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (isSelected) AppleGlassBorderHighlight else AppleGlassSurface)
                        .border(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) Color.White else AppleGlassBorder,
                            RoundedCornerShape(22.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(clockStylePreset = preset) }
                        }
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                color = TextPureWhite
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }

                        // Live Clock Graphic
                        when (preset) {
                            ClockStylePreset.APPLE_STYLE -> {
                                Text(
                                    text = "09:41",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 24.sp,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = TextPureWhite
                                )
                            }
                            ClockStylePreset.NOTHING_STYLE -> {
                                Text(
                                    text = "09:41",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        letterSpacing = 1.sp
                                    ),
                                    color = TextPureWhite
                                )
                            }
                            ClockStylePreset.PIXEL_STYLE -> {
                                Column {
                                    Text(
                                        text = "09",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                                        color = TextPureWhite
                                    )
                                    Text(
                                        text = "41",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                                        color = accentColor
                                    )
                                }
                            }
                            ClockStylePreset.HYPEROS -> {
                                Row {
                                    Text(
                                        text = "09",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, fontSize = 18.sp),
                                        color = TextPureWhite
                                    )
                                    Text(
                                        text = "/",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Thin, fontSize = 16.sp),
                                        color = accentColor
                                    )
                                    Text(
                                        text = "41",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                        color = accentColor
                                    )
                                }
                            }
                            else -> {
                                Text(
                                    text = "09:41",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 24.sp
                                    ),
                                    color = TextPureWhite
                                )
                            }
                        }

                        Text(
                            text = "Wednesday, Oct 2",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = fontFamily,
                                fontSize = 9.5.sp
                            ),
                            color = AppleTextSecondary
                        )
                    }
                }
            }
        }

        // 24-Hour Format Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "24-Hour Time Format",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Switch(
                checked = appearanceState.clockIs24Hour,
                onCheckedChange = { checked ->
                    onUpdateAppearance { it.copy(clockIs24Hour = checked) }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor
                )
            )
        }

        // Seconds Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Show Realtime Seconds",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Switch(
                checked = appearanceState.clockShowSeconds,
                onCheckedChange = { checked ->
                    onUpdateAppearance { it.copy(clockShowSeconds = checked) }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor
                )
            )
        }
    }
}

// =========================================================================
// 5. APPLE ICONS SHEET
// =========================================================================

@Composable
private fun AppleIconsSheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
    accentColor: Color,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Corner Radius Slider with live shape preview chip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Corner Radius",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Miniature live icon shape preview
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape((appearanceState.iconCornerRadiusPercent * 0.26f).dp.coerceAtLeast(3.dp)))
                        .background(Color.White)
                )
                Text(
                    text = "${appearanceState.iconCornerRadiusPercent}%",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPureWhite
                )
            }
        }
        Slider(
            value = appearanceState.iconCornerRadiusPercent.toFloat(),
            onValueChange = { radius ->
                onUpdateAppearance { it.copy(iconCornerRadiusPercent = radius.toInt()) }
            },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = AppleGlassBorder
            )
        )

        // Icon Size Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Icon Size",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Text(
                text = "${appearanceState.iconSizeDp} dp",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPureWhite
            )
        }
        Slider(
            value = appearanceState.iconSizeDp.toFloat(),
            onValueChange = { size ->
                onUpdateAppearance { it.copy(iconSizeDp = size.toInt(), dockIconSizeDp = size.toInt()) }
            },
            valueRange = 42f..68f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = AppleGlassBorder
            )
        )

        // Icon Labels Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Show Icon Labels",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Switch(
                checked = appearanceState.iconShowLabels,
                onCheckedChange = { show ->
                    onUpdateAppearance { it.copy(iconShowLabels = show) }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor
                )
            )
        }

        // Label Size Selector
        val labelSizes = listOf(
            IconLabelSize.SMALL to "Small",
            IconLabelSize.NORMAL to "Normal",
            IconLabelSize.LARGE to "Large"
        )
        Text(
            text = "Label Size",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = AppleTextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            labelSizes.forEach { (size, label) ->
                val isSelected = appearanceState.iconLabelSize == size
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AppleGlassBorderHighlight else AppleGlassSurface)
                        .border(
                            if (isSelected) 1.dp else 0.dp,
                            if (isSelected) Color.White else Color.Transparent,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(iconLabelSize = size) }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) TextPureWhite else AppleTextSecondary
                    )
                }
            }
        }

        // Icon Shadow Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Icon Shadow",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Switch(
                checked = appearanceState.iconShadow,
                onCheckedChange = { shadow ->
                    onUpdateAppearance { it.copy(iconShadow = shadow) }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor
                )
            )
        }

        // Icon Outline Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Icon Outline",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Switch(
                checked = appearanceState.iconOutline,
                onCheckedChange = { outline ->
                    onUpdateAppearance { it.copy(iconOutline = outline) }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor
                )
            )
        }

        // Padding / Spacing Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Icon Spacing",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Text(
                text = "${appearanceState.iconSpacingDp} dp",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPureWhite
            )
        }
        Slider(
            value = appearanceState.iconSpacingDp.toFloat(),
            onValueChange = { pad ->
                onUpdateAppearance { it.copy(iconSpacingDp = pad.toInt()) }
            },
            valueRange = 4f..24f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = AppleGlassBorder
            )
        )
    }
}

// =========================================================================
// 6. APPLE LAYOUT / WIDGETS SHEET
// =========================================================================

@Composable
private fun AppleWidgetsSheet(
    appearanceState: AppearanceState,
    fontFamily: FontFamily,
    accentColor: Color,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val rowsList = listOf(4, 5, 6, 7)
    val colsList = listOf(3, 4, 5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Grid Rows Selector
        Text(
            text = "Grid Rows",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = AppleTextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rowsList.forEach { r ->
                val isSelected = appearanceState.layoutRows == r
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AppleGlassBorderHighlight else AppleGlassSurface)
                        .border(
                            if (isSelected) 1.dp else 0.dp,
                            if (isSelected) Color.White else Color.Transparent,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(layoutRows = r) }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$r Rows",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) TextPureWhite else AppleTextSecondary
                    )
                }
            }
        }

        // Grid Columns Selector
        Text(
            text = "Grid Columns",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = fontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = AppleTextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colsList.forEach { c ->
                val isSelected = appearanceState.layoutColumns == c
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) AppleGlassBorderHighlight else AppleGlassSurface)
                        .border(
                            if (isSelected) 1.dp else 0.dp,
                            if (isSelected) Color.White else Color.Transparent,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            onUpdateAppearance { it.copy(layoutColumns = c) }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$c Cols",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = fontFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) TextPureWhite else AppleTextSecondary
                    )
                }
            }
        }

        // Widget Padding Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Widget Padding",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Text(
                text = "${appearanceState.layoutWidgetPaddingDp} dp",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = TextPureWhite
            )
        }
        Slider(
            value = appearanceState.layoutWidgetPaddingDp.toFloat(),
            onValueChange = { pad ->
                onUpdateAppearance { it.copy(layoutWidgetPaddingDp = pad.toInt()) }
            },
            valueRange = 8f..28f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = AppleGlassBorder
            )
        )

        // Hide Status Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hide Status Bar",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Switch(
                checked = appearanceState.layoutHideStatusBar,
                onCheckedChange = { hide ->
                    onUpdateAppearance { it.copy(layoutHideStatusBar = hide) }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor
                )
            )
        }

        // Hide Dock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hide Dock",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = fontFamily),
                color = AppleTextSecondary
            )
            Switch(
                checked = appearanceState.layoutHideDock,
                onCheckedChange = { hide ->
                    onUpdateAppearance { it.copy(layoutHideDock = hide) }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accentColor
                )
            )
        }
    }
}
