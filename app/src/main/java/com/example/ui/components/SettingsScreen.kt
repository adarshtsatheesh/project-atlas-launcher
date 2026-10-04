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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.viewmodel.CoachTone
import com.example.ui.viewmodel.FocusPreset
import com.example.util.LockScreenManager

@Composable
fun SettingsScreen(
    profileName: String = "Alex",
    colorPalette: ColorPalette = ColorPalette.OBSIDIAN_CRIMSON,
    aiNudgesEnabled: Boolean = true,
    ambientSoundEnabled: Boolean = true,
    exitFrictionEnabled: Boolean = true,
    coachTone: CoachTone = CoachTone.BALANCED,
    defaultFocusPreset: FocusPreset = FocusPreset.POMODORO_25,
    profilePhotoPath: String? = null,
    onUploadProfilePhotoUri: (Uri) -> Unit = {},
    onRemoveProfilePhoto: () -> Unit = {},
    onUpdateProfileName: (String) -> Unit = {},
    onSelectColorPalette: (ColorPalette) -> Unit = {},
    onToggleAiNudges: (Boolean) -> Unit = {},
    onToggleAmbientSound: (Boolean) -> Unit = {},
    onToggleExitFriction: (Boolean) -> Unit = {},
    onCycleCoachTone: () -> Unit = {},
    onCycleDefaultFocusPreset: () -> Unit = {},
    onOpenAiCoach: () -> Unit = {},
    onOpenAppearanceStudio: () -> Unit = {},
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showNameDialog by remember { mutableStateOf(false) }
    var tempName by remember(profileName) { mutableStateOf(profileName) }

    var showPaletteDialog by remember { mutableStateOf(false) }

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
                containerColor = Color(0xF5101218),
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
                containerColor = Color(0xF5101218),
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


