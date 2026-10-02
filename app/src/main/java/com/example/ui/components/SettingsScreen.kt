package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.AppIconSize
import com.example.domain.model.BuiltInWallpaper
import com.example.domain.model.ColorPalette
import java.io.File
import com.example.ui.theme.AtlasBackground
import com.example.ui.theme.AtlasBorder
import com.example.ui.theme.AtlasCard
import com.example.ui.theme.CrimsonElectric
import com.example.ui.theme.GlassBorderBrush
import com.example.ui.theme.GlassCardBackgroundBrush
import com.example.ui.theme.SoftSageGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary
import com.example.BuildConfig
import com.example.ui.viewmodel.ClockStyle
import com.example.ui.viewmodel.CoachTone
import com.example.ui.viewmodel.FocusPreset
import com.example.util.LockScreenManager

@Composable
fun SettingsScreen(
    clockStyle: ClockStyle = ClockStyle.NEO_GLASS,
    profileName: String = "Alex",
    colorPalette: ColorPalette = ColorPalette.OBSIDIAN_CRIMSON,
    appIconSize: AppIconSize = AppIconSize.DEFAULT,
    hideAppLabels: Boolean = false,
    hideDockLabels: Boolean = true,
    drawerGridColumns: Int = 4,
    aiNudgesEnabled: Boolean = true,
    ambientSoundEnabled: Boolean = true,
    exitFrictionEnabled: Boolean = true,
    coachTone: CoachTone = CoachTone.BALANCED,
    defaultFocusPreset: FocusPreset = FocusPreset.POMODORO_25,
    selectedWallpaper: BuiltInWallpaper = BuiltInWallpaper.OBSIDIAN_VOID,
    customWallpaperPath: String? = null,
    wallpaperDim: Float = 0.35f,
    profilePhotoPath: String? = null,
    onUploadProfilePhotoUri: (Uri) -> Unit = {},
    onRemoveProfilePhoto: () -> Unit = {},
    onSelectClockStyle: (ClockStyle) -> Unit = {},
    onToggleClockStyle: () -> Unit = {},
    onUpdateProfileName: (String) -> Unit = {},
    onSelectColorPalette: (ColorPalette) -> Unit = {},
    onSelectIconSize: (AppIconSize) -> Unit = {},
    onToggleHideAppLabels: (Boolean) -> Unit = {},
    onToggleHideDockLabels: (Boolean) -> Unit = {},
    onSelectGridColumns: (Int) -> Unit = {},
    onToggleAiNudges: (Boolean) -> Unit = {},
    onToggleAmbientSound: (Boolean) -> Unit = {},
    onToggleExitFriction: (Boolean) -> Unit = {},
    onCycleCoachTone: () -> Unit = {},
    onCycleDefaultFocusPreset: () -> Unit = {},
    onOpenAiCoach: () -> Unit = {},
    onSelectWallpaper: (BuiltInWallpaper) -> Unit = {},
    onPickCustomWallpaperUri: (Uri) -> Unit = {},
    onUpdateWallpaperDim: (Float) -> Unit = {},
    onClearCustomWallpaper: () -> Unit = {},
    onOpenAppearanceStudio: () -> Unit = {},
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showNameDialog by remember { mutableStateOf(false) }
    var tempName by remember(profileName) { mutableStateOf(profileName) }

    var showPaletteDialog by remember { mutableStateOf(false) }
    var showIconSizeDialog by remember { mutableStateOf(false) }
    var showGridDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onPickCustomWallpaperUri(uri)
        }
    }

    val profilePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onUploadProfilePhotoUri(uri)
        }
    }

    val context = LocalContext.current
    val isLockConfigured = LockScreenManager.isLockPermissionGranted(context)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtlasBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Back Arrow & "Settings"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AtlasCard)
                        .border(1.dp, AtlasBorder, CircleShape)
                        .testTag("close_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPureWhite,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =========================================================================
            // HERO: APPEARANCE STUDIO
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(
                                com.example.ui.theme.LocalAtlasAccent.current.copy(alpha = 0.22f),
                                Color(0x18FFFFFF)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        com.example.ui.theme.LocalAtlasAccent.current.copy(alpha = 0.65f),
                        RoundedCornerShape(22.dp)
                    )
                    .clickable { onOpenAppearanceStudio() }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(com.example.ui.theme.LocalAtlasAccent.current.copy(alpha = 0.25f))
                        .border(1.dp, com.example.ui.theme.LocalAtlasAccent.current, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = com.example.ui.theme.LocalAtlasAccent.current,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Appearance Studio",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPureWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(com.example.ui.theme.LocalAtlasAccent.current.copy(alpha = 0.2f))
                                .border(0.5.dp, com.example.ui.theme.LocalAtlasAccent.current.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "STUDIO",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.ExtraBold),
                                color = com.example.ui.theme.LocalAtlasAccent.current
                            )
                        }
                    }
                    Text(
                        text = "Personalize typography, themes, motion physics, glass blur & OS geometry",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = TextMuted
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open",
                    tint = com.example.ui.theme.LocalAtlasAccent.current,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // =========================================================================
            // Section 1: Home Clock Style
            // =========================================================================
            SettingsSectionHeader(title = "HOME CLOCK STYLE")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassCardBackgroundBrush)
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(20.dp))
                    .padding(vertical = 4.dp)
            ) {
                ClockStyleSelectionItem(
                    title = "Neo-Glass Horizon",
                    subtitle = "Dual-tone typography with pure white hours, glowing electric crimson minutes, pulsing colon & live seconds badge",
                    previewBadge = "09:41 :00 AM",
                    isSelected = clockStyle == ClockStyle.NEO_GLASS,
                    testTagKey = "clock_style_neo_glass",
                    onClick = { onSelectClockStyle(ClockStyle.NEO_GLASS) }
                )
                SettingsDivider()
                ClockStyleSelectionItem(
                    title = "Stacked Digital",
                    subtitle = "Cyberpunk vertical numerals (hours above, minutes below) with high-contrast minimalist lockscreen style",
                    previewBadge = "09 / 41",
                    isSelected = clockStyle == ClockStyle.STACKED_DIGITAL,
                    testTagKey = "clock_style_stacked_digital",
                    onClick = { onSelectClockStyle(ClockStyle.STACKED_DIGITAL) }
                )
                SettingsDivider()
                ClockStyleSelectionItem(
                    title = "Precision Chronograph",
                    subtitle = "Minimalist frosted glass analog face with illuminated crimson second sweep and clean cardinal tick markers",
                    previewBadge = "Analog • 09:41",
                    isSelected = clockStyle == ClockStyle.CHRONOGRAPH,
                    testTagKey = "clock_style_chronograph",
                    onClick = { onSelectClockStyle(ClockStyle.CHRONOGRAPH) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================================================
            // Section 2: Wallpaper & Atmosphere
            // =========================================================================
            SettingsSectionHeader(title = "WALLPAPER & ATMOSPHERE")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassCardBackgroundBrush)
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                // Active Wallpaper Showcase Card
                ActiveWallpaperShowcase(
                    selectedWallpaper = selectedWallpaper,
                    customWallpaperPath = customWallpaperPath,
                    wallpaperDim = wallpaperDim,
                    onOpenGalleryPicker = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Wallpaper Horizontal Carousel
                Text(
                    text = "SELECT THEME OR CUSTOM PHOTO",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BuiltInWallpaper.entries.forEach { wallpaperOption ->
                        val isSelected = selectedWallpaper == wallpaperOption
                        WallpaperThumbnailCard(
                            wallpaper = wallpaperOption,
                            isSelected = isSelected,
                            customImagePath = if (wallpaperOption == BuiltInWallpaper.CUSTOM_GALLERY) customWallpaperPath else null,
                            onClick = {
                                if (wallpaperOption == BuiltInWallpaper.CUSTOM_GALLERY && customWallpaperPath.isNullOrEmpty()) {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else {
                                    onSelectWallpaper(wallpaperOption)
                                }
                            }
                        )
                    }
                }

                // If Custom Gallery Wallpaper is active, show Dimming/Legibility controls
                if (selectedWallpaper == BuiltInWallpaper.CUSTOM_GALLERY) {
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = CrimsonElectric,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Photo Dim & Text Legibility",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPureWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${(wallpaperDim * 100).toInt()}% Dim",
                            style = MaterialTheme.typography.labelSmall,
                            color = CrimsonElectric,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Adjust background dimming to keep home clock, tasks, and app icons razor sharp.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = wallpaperDim,
                        onValueChange = onUpdateWallpaperDim,
                        valueRange = 0.10f..0.80f,
                        steps = 6,
                        colors = SliderDefaults.colors(
                            thumbColor = CrimsonElectric,
                            activeTrackColor = CrimsonElectric,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AtlasBorder)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = CrimsonElectric,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change Photo", color = TextPureWhite, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onClearCustomWallpaper,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AtlasBorder)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                SettingsDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // =====================================================================
                // Theme Option: Matching Color Palettes (Under Wallpaper Option)
                // =====================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = CrimsonElectric,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LAUNCHER THEME ACCENT",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 10.sp,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = colorPalette.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPureWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Active accent color preview badge
                    val parsedCurrentAccent = remember(colorPalette.hexCode) {
                        try {
                            Color(android.graphics.Color.parseColor(colorPalette.hexCode))
                        } catch (e: Exception) {
                            Color(0xFFFF2A4D)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(parsedCurrentAccent)
                            .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal selector of matching themes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ColorPalette.entries.forEach { paletteOption ->
                        val isSelected = colorPalette == paletteOption
                        val themeColor = remember(paletteOption.hexCode) {
                            try {
                                Color(android.graphics.Color.parseColor(paletteOption.hexCode))
                            } catch (e: Exception) {
                                Color(0xFFFF2A4D)
                            }
                        }

                        ThemeOptionPill(
                            palette = paletteOption,
                            themeColor = themeColor,
                            isSelected = isSelected,
                            onClick = { onSelectColorPalette(paletteOption) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================================================
            // Section 3: App & Icon Customization (Icon size, Name hider, Grid layout)
            // =========================================================================
            SettingsSectionHeader(title = "APP & ICON APPEARANCE")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassCardBackgroundBrush)
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(20.dp))
            ) {
                // Icon Size Selector
                SettingsNavigationRow(
                    title = "App Icon Size",
                    subtitle = "${appIconSize.label} (${appIconSize.dpSize} dp)",
                    leadingIcon = Icons.Default.PhotoSizeSelectLarge,
                    testTagKey = "settings_icon_size_row",
                    onClick = { showIconSizeDialog = true }
                )
                SettingsDivider()

                // Hide App Drawer Labels
                SettingsSwitchRow(
                    title = "Hide Drawer App Names",
                    subtitle = if (hideAppLabels) "Clean aesthetic: App names hidden in drawer" else "Show text labels under icons",
                    checked = hideAppLabels,
                    testTagKey = "settings_hide_drawer_labels_switch",
                    onCheckedChange = { onToggleHideAppLabels(it) }
                )
                SettingsDivider()

                // Hide Dock Labels
                SettingsSwitchRow(
                    title = "Hide Dock App Names",
                    subtitle = if (hideDockLabels) "Ultra-minimal dock without text labels" else "Show text labels under dock icons",
                    checked = hideDockLabels,
                    testTagKey = "settings_hide_dock_labels_switch",
                    onCheckedChange = { onToggleHideDockLabels(it) }
                )
                SettingsDivider()

                // Drawer Grid Columns
                SettingsNavigationRow(
                    title = "Drawer Grid Columns",
                    subtitle = "$drawerGridColumns columns per row",
                    leadingIcon = Icons.Default.GridOn,
                    testTagKey = "settings_grid_columns_row",
                    onClick = { showGridDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================================================
            // Section 3: Personalization & Identity
            // =========================================================================
            SettingsSectionHeader(title = "PERSONALIZATION & IDENTITY")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassCardBackgroundBrush)
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(20.dp))
            ) {
                // Profile Name changer
                SettingsNavigationRow(
                    title = "Profile Name",
                    subtitle = profileName,
                    leadingIcon = Icons.Default.Edit,
                    testTagKey = "settings_profile_name_row",
                    onClick = {
                        tempName = profileName
                        showNameDialog = true
                    }
                )
                SettingsDivider()

                // Profile Photo
                val hasPhoto = !profilePhotoPath.isNullOrEmpty() && java.io.File(profilePhotoPath).exists()
                SettingsNavigationRow(
                    title = "Profile Photo",
                    subtitle = if (hasPhoto) "Custom photo set • Tap to change" else "No photo set • Tap to add",
                    leadingIcon = Icons.Default.CameraAlt,
                    testTagKey = "settings_profile_photo_row",
                    onClick = {
                        profilePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
                if (hasPhoto) {
                    SettingsDivider()
                    SettingsNavigationRow(
                        title = "Remove Profile Photo",
                        subtitle = "Revert to default outline placeholder",
                        leadingIcon = Icons.Default.DeleteOutline,
                        testTagKey = "settings_remove_photo_row",
                        onClick = { onRemoveProfilePhoto() }
                    )
                }
                SettingsDivider()

                // Color Language Palette
                SettingsNavigationRow(
                    title = "Color Accent",
                    subtitle = colorPalette.label,
                    leadingIcon = Icons.Default.Palette,
                    accentHex = colorPalette.hexCode,
                    testTagKey = "settings_color_palette_row",
                    onClick = { showPaletteDialog = true }
                )
                SettingsDivider()

                // Double Tap Screen Lock
                SettingsNavigationRow(
                    title = "Double-Tap System Lock",
                    subtitle = if (isLockConfigured) "Enabled (Turns off & locks mobile screen)" else "Tap to grant Accessibility Service permission",
                    onClick = { LockScreenManager.openAccessibilitySettings(context) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================================================
            // Section 4: AI Assistant
            // =========================================================================
            SettingsSectionHeader(title = "AI ASSISTANT")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassCardBackgroundBrush)
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(20.dp))
            ) {
                SettingsSwitchRow(
                    title = "Proactive Micro-Step Breakdown",
                    subtitle = "Asks Gemini for 3 first steps when you set a mission",
                    checked = aiNudgesEnabled,
                    testTagKey = "settings_ai_nudges_switch",
                    onCheckedChange = { onToggleAiNudges(it) }
                )
                SettingsDivider()
                SettingsNavigationRow(
                    title = "Coaching Sensitivity",
                    subtitle = "${coachTone.label} • tap to change",
                    testTagKey = "settings_coach_tone_row",
                    onClick = { onCycleCoachTone() }
                )
                SettingsDivider()
                SettingsNavigationRow(
                    title = "Open AI Coach",
                    subtitle = "Ask Atlas to break, plan or refocus",
                    testTagKey = "settings_open_ai_coach",
                    onClick = { onOpenAiCoach() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================================================
            // Section 5: Focus Mode
            // =========================================================================
            SettingsSectionHeader(title = "FOCUS MODE")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassCardBackgroundBrush)
                    .border(1.dp, GlassBorderBrush, RoundedCornerShape(20.dp))
            ) {
                SettingsNavigationRow(
                    title = "Default Focus Sprint",
                    subtitle = "${defaultFocusPreset.minutes} minutes • ${defaultFocusPreset.label} • tap to change",
                    testTagKey = "settings_default_focus_row",
                    onClick = { onCycleDefaultFocusPreset() }
                )
                SettingsDivider()
                SettingsSwitchRow(
                    title = "Ambient Lo-fi Audio",
                    subtitle = "Calming procedural soundscapes played during focus",
                    checked = ambientSoundEnabled,
                    testTagKey = "settings_ambient_sound_switch",
                    onCheckedChange = { onToggleAmbientSound(it) }
                )
                SettingsDivider()
                SettingsSwitchRow(
                    title = "Mindful Exit Friction",
                    subtitle = "Asks you to take a breath before quitting focus sessions",
                    checked = exitFrictionEnabled,
                    testTagKey = "settings_exit_friction_switch",
                    onCheckedChange = { onToggleExitFriction(it) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================================================
            // Section 6: About Project Atlas
            // =========================================================================
            SettingsSectionHeader(title = "ABOUT ATLAS")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(AtlasCard)
                    .border(1.dp, AtlasBorder, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text = "Project Atlas",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPureWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "“Bridge the gap between planning and doing.” Built with Nothing OS aesthetics, Apple Human Interface precision, and intentional behavioral psychology.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // =========================================================================
        // Dialog 1: Profile Name Editor
        // =========================================================================
        if (showNameDialog) {
            AlertDialog(
                onDismissRequest = { showNameDialog = false },
                title = {
                    Text(
                        text = "Customize Profile Name",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPureWhite
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Set the personalized name shown across the launcher and ambient displays.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPureWhite,
                                unfocusedTextColor = TextPureWhite,
                                focusedBorderColor = CrimsonElectric,
                                unfocusedBorderColor = AtlasBorder,
                                cursorColor = CrimsonElectric
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_name_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                onUpdateProfileName(tempName.trim())
                            }
                            showNameDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonElectric),
                        modifier = Modifier.testTag("save_profile_name_button")
                    ) {
                        Text("Save", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNameDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = AtlasBackground,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // =========================================================================
        // Dialog 2: App Icon Size Selector
        // =========================================================================
        if (showIconSizeDialog) {
            AlertDialog(
                onDismissRequest = { showIconSizeDialog = false },
                title = {
                    Text(
                        text = "Select App Icon Size",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPureWhite
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppIconSize.entries.forEach { sizeOption ->
                            val isSelected = appIconSize == sizeOption
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) CrimsonElectric.copy(alpha = 0.15f) else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isSelected) CrimsonElectric.copy(alpha = 0.5f) else AtlasBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        onSelectIconSize(sizeOption)
                                        showIconSizeDialog = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = sizeOption.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) TextPureWhite else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                    Text(
                                        text = "${sizeOption.dpSize} dp diameter",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = CrimsonElectric,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showIconSizeDialog = false }) {
                        Text("Done", color = CrimsonElectric)
                    }
                },
                containerColor = AtlasBackground,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // =========================================================================
        // Dialog 3: Color Palette Picker
        // =========================================================================
        if (showPaletteDialog) {
            AlertDialog(
                onDismissRequest = { showPaletteDialog = false },
                title = {
                    Text(
                        text = "Color Language",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPureWhite
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ColorPalette.entries.forEach { palette ->
                            val isSelected = colorPalette == palette
                            val fallbackAccent = CrimsonElectric
                            val accentColor = remember(palette.hexCode, fallbackAccent) {
                                try {
                                    Color(android.graphics.Color.parseColor(palette.hexCode))
                                } catch (e: Exception) {
                                    fallbackAccent
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isSelected) accentColor.copy(alpha = 0.6f) else AtlasBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        onSelectColorPalette(palette)
                                        showPaletteDialog = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(accentColor)
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = palette.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isSelected) TextPureWhite else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPaletteDialog = false }) {
                        Text("Done", color = CrimsonElectric)
                    }
                },
                containerColor = AtlasBackground,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // =========================================================================
        // Dialog 4: Drawer Grid Columns Picker
        // =========================================================================
        if (showGridDialog) {
            AlertDialog(
                onDismissRequest = { showGridDialog = false },
                title = {
                    Text(
                        text = "App Drawer Columns",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPureWhite
                    )
                },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(3, 4, 5).forEach { cols ->
                            val isSelected = drawerGridColumns == cols
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) CrimsonElectric.copy(alpha = 0.2f) else AtlasCard)
                                    .border(
                                        1.5.dp,
                                        if (isSelected) CrimsonElectric else AtlasBorder,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        onSelectGridColumns(cols)
                                        showGridDialog = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$cols",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = if (isSelected) CrimsonElectric else TextPureWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "cols",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showGridDialog = false }) {
                        Text("Close", color = CrimsonElectric)
                    }
                },
                containerColor = AtlasBackground,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsNavigationRow(
    title: String,
    subtitle: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    accentHex: String? = null,
    testTagKey: String = "",
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .then(if (testTagKey.isNotEmpty()) Modifier.testTag(testTagKey) else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x14FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = CrimsonElectric,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
            } else if (accentHex != null) {
                val fallbackAccent = CrimsonElectric
                val parsedColor = remember(accentHex, fallbackAccent) {
                    try {
                        Color(android.graphics.Color.parseColor(accentHex))
                    } catch (e: Exception) {
                        fallbackAccent
                    }
                }
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(parsedColor)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                )
                Spacer(modifier = Modifier.width(14.dp))
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPureWhite
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    testTagKey: String = "",
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .then(if (testTagKey.isNotEmpty()) Modifier.testTag(testTagKey) else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPureWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AtlasBackground,
                checkedTrackColor = CrimsonElectric,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = AtlasCard
            )
        )
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AtlasBorder)
    )
}

@Composable
private fun ClockStyleSelectionItem(
    title: String,
    subtitle: String,
    previewBadge: String,
    isSelected: Boolean,
    testTagKey: String = "",
    onClick: () -> Unit
) {
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) CrimsonElectric.copy(alpha = 0.08f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "clock_item_bg"
    )
    val dotScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "clock_item_dot_scale"
    )
    val badgeBgColor by animateColorAsState(
        targetValue = if (isSelected) CrimsonElectric.copy(alpha = 0.20f) else Color(0x1AFFFFFF),
        animationSpec = tween(durationMillis = 180),
        label = "clock_item_badge_bg"
    )
    val badgeBorderColor by animateColorAsState(
        targetValue = if (isSelected) CrimsonElectric.copy(alpha = 0.50f) else Color(0x1AFFFFFF),
        animationSpec = tween(durationMillis = 180),
        label = "clock_item_badge_border"
    )
    val badgeTextColor by animateColorAsState(
        targetValue = if (isSelected) CrimsonElectric else TextMuted,
        animationSpec = tween(durationMillis = 180),
        label = "clock_item_badge_text"
    )
    val radioBorderColor by animateColorAsState(
        targetValue = if (isSelected) CrimsonElectric else Color(0x33FFFFFF),
        animationSpec = tween(durationMillis = 180),
        label = "clock_item_radio_border"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(animatedBgColor)
            .selectable(
                selected = isSelected,
                role = Role.RadioButton,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .then(if (testTagKey.isNotEmpty()) Modifier.testTag(testTagKey) else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPureWhite,
                    fontWeight = FontWeight.SemiBold
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeBgColor)
                        .border(
                            1.dp,
                            badgeBorderColor,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = previewBadge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = badgeTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Radio / Selection Indicator with glowing crimson dot
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .border(
                    1.5.dp,
                    radioBorderColor,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (dotScale > 0.01f) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .graphicsLayer {
                            scaleX = dotScale
                            scaleY = dotScale
                        }
                        .clip(CircleShape)
                        .background(CrimsonElectric)
                )
            }
        }
    }
}

@Composable
private fun ActiveWallpaperShowcase(
    selectedWallpaper: BuiltInWallpaper,
    customWallpaperPath: String?,
    wallpaperDim: Float,
    onOpenGalleryPicker: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x1A000000))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mini Phone Screen Frame with Live Wallpaper
        Box(
            modifier = Modifier
                .size(width = 68.dp, height = 110.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.5.dp, CrimsonElectric.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (selectedWallpaper == BuiltInWallpaper.CUSTOM_GALLERY && !customWallpaperPath.isNullOrEmpty() && File(customWallpaperPath).exists()) {
                AsyncImage(
                    model = File(customWallpaperPath),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = wallpaperDim))
                )
            } else {
                WallpaperCanvas(wallpaper = selectedWallpaper)
            }

            // Mock Launcher Elements on Mini Preview (Clock + Dock)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "09:41",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = Color.White
                )

                // Mock dock dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.7f))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Wallpaper metadata and quick actions
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CrimsonElectric.copy(alpha = 0.2f))
                        .border(1.dp, CrimsonElectric.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = selectedWallpaper.badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = CrimsonElectric
                    )
                }

                Text(
                    text = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SoftSageGreen
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = selectedWallpaper.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPureWhite
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = selectedWallpaper.description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                color = TextSecondary
            )

            if (selectedWallpaper == BuiltInWallpaper.CUSTOM_GALLERY) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onOpenGalleryPicker,
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonElectric),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pick New", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun WallpaperThumbnailCard(
    wallpaper: BuiltInWallpaper,
    isSelected: Boolean,
    customImagePath: String? = null,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) CrimsonElectric else AtlasBorder,
        animationSpec = tween(180),
        label = "thumb_border"
    )

    Column(
        modifier = Modifier
            .width(96.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
            .background(AtlasCard),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Thumbnail Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (wallpaper == BuiltInWallpaper.CUSTOM_GALLERY) {
                if (!customImagePath.isNullOrEmpty() && File(customImagePath).exists()) {
                    AsyncImage(
                        model = File(customImagePath),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                } else {
                    // Empty Gallery Placeholder
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF))
                                .border(1.dp, Color(0x33FFFFFF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = CrimsonElectric,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Add Photo",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = TextPureWhite
                        )
                    }
                }
            } else {
                WallpaperCanvas(wallpaper = wallpaper)
            }

            // Selected Checkmark Badge
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(CrimsonElectric),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // Title Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isSelected) CrimsonElectric.copy(alpha = 0.12f) else Color.Transparent)
                .padding(vertical = 6.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = wallpaper.title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) TextPureWhite else TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ThemeOptionPill(
    palette: ColorPalette,
    themeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) themeColor else AtlasBorder,
        animationSpec = tween(180),
        label = "pill_border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) themeColor.copy(alpha = 0.18f) else AtlasCard,
        animationSpec = tween(180),
        label = "pill_bg"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(themeColor)
                .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
        )

        Text(
            text = palette.label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = if (isSelected) TextPureWhite else TextSecondary
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = themeColor,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}


