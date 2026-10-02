package com.example.ui.appearance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAtlasAccent
import com.example.ui.theme.LocalAtlasBorderBrush
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPureWhite
import com.example.ui.theme.TextSecondary

/**
 * Categorized Navigation for Appearance Studio.
 */
enum class StudioSection(val title: String, val icon: ImageVector) {
    TYPOGRAPHY("Typography", Icons.Default.TextFields),
    THEME_ACCENT("Theme & Color", Icons.Default.ColorLens),
    ICONS_MOTION("Icons & Motion", Icons.Default.Speed),
    SURFACES("Surfaces & Radius", Icons.Default.Layers),
    WALLPAPER("Wallpaper", Icons.Default.Wallpaper),
    CLOCK_WIDGETS("Clock & Widgets", Icons.Default.Schedule),
    SYSTEM_FLOW("App Drawer", Icons.Default.Dashboard),
    NOTES("Notes Styling", Icons.Default.EditNote),
    XP_SYSTEM("XP & Leveling", Icons.Default.MilitaryTech)
}

/**
 * Appearance Studio: The ultimate personalization center for the Atlas operating system.
 * Designed with aesthetic influences from Nothing OS, Arc Browser, Linear, and Apple HIG.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppearanceStudioScreen(
    appearanceState: AppearanceState,
    onUpdateAppearance: ((AppearanceState) -> AppearanceState) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val accent = LocalAtlasAccent.current
    var selectedSection by remember { mutableStateOf(StudioSection.TYPOGRAPHY) }

    val activeFontFamily = remember(appearanceState.fontFamily) {
        TypographyManager.getFontFamily(appearanceState.fontFamily)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (appearanceState.themeMode == ThemeMode.LIGHT) Color(0xFFF8FAFC) else Color(0xFF030406))
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("appearance_studio_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // =========================================================================
            // Studio Top Header
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClose()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x18FFFFFF))
                            .border(1.dp, Color(0x24FFFFFF), CircleShape)
                            .testTag("appearance_studio_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "APPEARANCE STUDIO",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.4.sp,
                                    fontFamily = activeFontFamily
                                ),
                                color = TextPureWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(accent.copy(alpha = 0.18f))
                                    .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "OS ENGINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = activeFontFamily
                                    ),
                                    color = accent
                                )
                            }
                        }
                        Text(
                            text = "Instant OS Personalization Engine",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontFamily = activeFontFamily
                            ),
                            color = TextMuted
                        )
                    }
                }

                // Live Glowing Active Accent Indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accent)
                        .border(2.dp, accent.copy(alpha = 0.4f), CircleShape)
                )
            }

            // =========================================================================
            // Live Mini-OS Realtime Preview Monitor
            // =========================================================================
            MiniOsRealtimePreview(
                appearanceState = appearanceState,
                accent = accent,
                fontFamily = activeFontFamily
            )

            Spacer(modifier = Modifier.height(6.dp))

            // =========================================================================
            // Studio Section Tabs (Nothing OS / Arc style)
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StudioSection.entries.forEach { section ->
                    val isSelected = selectedSection == section
                    val tabBg by animateColorAsState(
                        targetValue = if (isSelected) accent.copy(alpha = 0.20f) else Color(0x0CFFFFFF),
                        label = "tab_bg"
                    )
                    val tabBorder by animateColorAsState(
                        targetValue = if (isSelected) accent.copy(alpha = 0.60f) else Color(0x18FFFFFF),
                        label = "tab_border"
                    )

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(tabBg)
                            .border(1.dp, tabBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedSection = section
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = section.icon,
                            contentDescription = null,
                            tint = if (isSelected) accent else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = section.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontFamily = activeFontFamily
                            ),
                            color = if (isSelected) TextPureWhite else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // =========================================================================
            // Dynamic Section Content
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                when (selectedSection) {
                    StudioSection.TYPOGRAPHY -> {
                        TypographySection(
                            appearanceState = appearanceState,
                            onUpdate = onUpdateAppearance
                        )
                    }
                    StudioSection.THEME_ACCENT -> {
                        ThemeAndAccentSection(
                            appearanceState = appearanceState,
                            onUpdate = onUpdateAppearance
                        )
                    }
                    StudioSection.ICONS_MOTION -> {
                        IconsAndMotionSection(
                            appearanceState = appearanceState,
                            onUpdate = onUpdateAppearance
                        )
                    }
                    StudioSection.SURFACES -> {
                        SurfacesAndBlurSection(
                            appearanceState = appearanceState,
                            onUpdate = onUpdateAppearance
                        )
                    }
                    StudioSection.WALLPAPER -> {
                        WallpaperStudioSection(
                            appearanceState = appearanceState,
                            onUpdate = onUpdateAppearance
                        )
                    }
                    StudioSection.CLOCK_WIDGETS -> {
                        ClockAndWidgetsSection(
                            appearanceState = appearanceState,
                            onUpdate = onUpdateAppearance
                        )
                    }
                    StudioSection.SYSTEM_FLOW -> {
                        AppDrawerAndSystemSection(
                            appearanceState = appearanceState,
                            onUpdate = onUpdateAppearance
                        )
                    }
                    StudioSection.NOTES -> {
                        NotesStylingSection(
                            appearanceState = appearanceState,
                            onUpdate = onUpdateAppearance
                        )
                    }
                    StudioSection.XP_SYSTEM -> {
                        XpSystemStudioSection(
                            appearanceState = appearanceState,
                            onUpdate = onUpdateAppearance
                        )
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

/**
 * Realtime OS Visualizer previewing changes as the user interacts.
 */
@Composable
private fun MiniOsRealtimePreview(
    appearanceState: AppearanceState,
    accent: Color,
    fontFamily: FontFamily
) {
    val radius = appearanceState.cardRadiusDp.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        accent.copy(alpha = 0.12f),
                        Color(0x12FFFFFF)
                    )
                )
            )
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "REALTIME OS MONITOR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = fontFamily,
                        fontSize = 9.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = accent
                )
            }

            Text(
                text = "${appearanceState.themeMode.label} • ${appearanceState.fontFamily.label}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = fontFamily,
                    fontSize = 9.5.sp
                ),
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mini OS Mock Screen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(Color(0x18FFFFFF))
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(radius))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "09:41",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily,
                        letterSpacing = (-0.5).sp
                    ),
                    color = TextPureWhite
                )
                Text(
                    text = "Friday, October 2 • Disciplined Builder",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        fontFamily = fontFamily
                    ),
                    color = accent
                )
            }

            // Mini App Icons in current style
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(accent, Color.White, accent.copy(alpha = 0.7f)).forEach { col ->
                    val shape = when (appearanceState.iconStyle) {
                        IconStylePreset.ROUNDED -> RoundedCornerShape(6.dp)
                        IconStylePreset.MATERIAL_YOU -> CircleShape
                        IconStylePreset.NOTHING_STYLE -> RoundedCornerShape(4.dp)
                        else -> RoundedCornerShape(appearanceState.smallRadiusDp.dp)
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(shape)
                            .background(col.copy(alpha = 0.25f))
                            .border(1.dp, col, shape)
                    )
                }
            }
        }
    }
}

// =============================================================================
// 1. TYPOGRAPHY SECTION
// =============================================================================
@Composable
private fun TypographySection(
    appearanceState: AppearanceState,
    onUpdate: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val accent = LocalAtlasAccent.current
    val currentFontFamily = remember(appearanceState.fontFamily) {
        TypographyManager.getFontFamily(appearanceState.fontFamily)
    }

    Text(
        text = "ACTIVE TYPEFACE SHOWCASE",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    // List of All 10 Fonts with full Alphabet, Numbers, and Pangram previews
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        AppFontFamily.entries.forEach { font ->
            val isSelected = appearanceState.fontFamily == font
            val fontItemFamily = remember(font) { TypographyManager.getFontFamily(font) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.16f) else Color(0x0CFFFFFF))
                    .border(
                        1.5.dp,
                        if (isSelected) accent else Color(0x1CFFFFFF),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onUpdate { it.copy(fontFamily = font) }
                    }
                    .padding(18.dp)
            ) {
                // Header row of font card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = font.label,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = fontItemFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPureWhite
                        )
                        if (font.isDefault) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x28FFFFFF))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "DEFAULT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        fontFamily = fontItemFamily
                                    ),
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(accent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Black,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                Text(
                    text = font.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = fontItemFamily,
                        fontSize = 11.5.sp
                    ),
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                // MANDATED SPECIFIC PREVIEWS:
                // Aa
                Text(
                    text = "Aa",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = fontItemFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isSelected) accent else TextPureWhite
                )

                Spacer(modifier = Modifier.height(4.dp))

                // ABCDEFGHIJKLMNOPQRSTUVWXYZ
                Text(
                    text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = fontItemFamily,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    ),
                    color = TextPureWhite
                )

                Spacer(modifier = Modifier.height(3.dp))

                // 0123456789
                Text(
                    text = "0123456789",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = fontItemFamily,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.5.sp
                    ),
                    color = if (isSelected) accent else TextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // The quick brown fox jumps over the lazy dog.
                Text(
                    text = "The quick brown fox jumps over the lazy dog.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = fontItemFamily,
                        fontSize = 12.sp
                    ),
                    color = TextSecondary
                )
            }
        }
    }
}

// =============================================================================
// 2. THEME & ACCENT COLOR SECTION
// =============================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeAndAccentSection(
    appearanceState: AppearanceState,
    onUpdate: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val accent = LocalAtlasAccent.current
    val currentFontFamily = remember(appearanceState.fontFamily) {
        TypographyManager.getFontFamily(appearanceState.fontFamily)
    }

    Text(
        text = "THEME ARCHITECTURE",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ThemeMode.entries.forEach { mode ->
            val isSelected = appearanceState.themeMode == mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.16f) else Color(0x0AFFFFFF))
                    .border(
                        1.dp,
                        if (isSelected) accent.copy(alpha = 0.60f) else Color(0x18FFFFFF),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onUpdate { it.copy(themeMode = mode) }
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            when (mode) {
                                ThemeMode.AMOLED_BLACK -> Color.Black
                                ThemeMode.DARK -> Color(0xFF10131A)
                                ThemeMode.LIGHT -> Color(0xFFF1F5F9)
                                ThemeMode.SYSTEM -> Color(0xFF334155)
                                ThemeMode.CUSTOM -> accent.copy(alpha = 0.3f)
                            }
                        )
                        .border(1.dp, if (isSelected) accent else Color(0x28FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(accent))
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mode.label,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = currentFontFamily
                        ),
                        color = TextPureWhite
                    )
                    Text(
                        text = mode.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontFamily = currentFontFamily
                        ),
                        color = TextMuted
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(26.dp))

    // --- ACCENT COLOR PRESETS ---
    Text(
        text = "ACCENT COLOR",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Updates live across buttons, progress bars, icons, highlights & XP",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontFamily = currentFontFamily),
        color = TextMuted
    )

    Spacer(modifier = Modifier.height(14.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AccentColorPreset.entries.forEach { preset ->
            val isSelected = appearanceState.accentPreset == preset
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) preset.color.copy(alpha = 0.22f) else Color(0x0EFFFFFF))
                    .border(
                        1.5.dp,
                        if (isSelected) preset.color else Color(0x18FFFFFF),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onUpdate { it.copy(accentPreset = preset) }
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(preset.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = preset.label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = currentFontFamily
                    ),
                    color = if (isSelected) TextPureWhite else TextSecondary
                )
            }
        }
    }

    // --- CUSTOM COLOR PICKER ---
    Spacer(modifier = Modifier.height(18.dp))
    var customHexInput by remember(appearanceState.customAccentHex) {
        mutableStateOf(appearanceState.customAccentHex)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x0CFFFFFF))
            .border(1.dp, Color(0x1EFFFFFF), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "CUSTOM COLOR MATRIX",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = currentFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customHexInput,
                onValueChange = { input ->
                    customHexInput = input
                    try {
                        val parsed = if (input.startsWith("#")) input else "#$input"
                        if (parsed.length == 7) {
                            android.graphics.Color.parseColor(parsed)
                            onUpdate {
                                it.copy(
                                    accentPreset = AccentColorPreset.CUSTOM,
                                    customAccentHex = parsed
                                )
                            }
                        }
                    } catch (e: Exception) {
                        // ignore invalid hex while typing
                    }
                },
                label = { Text("Hex Code", color = TextMuted, fontFamily = currentFontFamily) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accent,
                    unfocusedBorderColor = Color(0x28FFFFFF),
                    focusedTextColor = TextPureWhite,
                    unfocusedTextColor = TextPureWhite
                )
            )
            Spacer(modifier = Modifier.width(14.dp))
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(appearanceState.activeAccentColor)
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            )
        }
    }
}

// =============================================================================
// 3. ICONS & MOTION SECTION
// =============================================================================
@Composable
private fun IconsAndMotionSection(
    appearanceState: AppearanceState,
    onUpdate: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val accent = LocalAtlasAccent.current
    val currentFontFamily = remember(appearanceState.fontFamily) {
        TypographyManager.getFontFamily(appearanceState.fontFamily)
    }

    Text(
        text = "ICON ARCHITECTURE",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        IconStylePreset.entries.forEach { style ->
            val isSelected = appearanceState.iconStyle == style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.16f) else Color(0x0AFFFFFF))
                    .border(
                        1.dp,
                        if (isSelected) accent.copy(alpha = 0.60f) else Color(0x18FFFFFF),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onUpdate { it.copy(iconStyle = style) }
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = style.label,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = currentFontFamily
                        ),
                        color = TextPureWhite
                    )
                    Text(
                        text = style.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontFamily = currentFontFamily
                        ),
                        color = TextMuted
                    )
                }
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Text(
        text = "MOTION ENGINE PRESETS",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AnimationPreset.entries.forEach { anim ->
            val isSelected = appearanceState.animationPreset == anim
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.16f) else Color(0x0AFFFFFF))
                    .border(
                        1.dp,
                        if (isSelected) accent.copy(alpha = 0.60f) else Color(0x18FFFFFF),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onUpdate { it.copy(animationPreset = anim) }
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = anim.label,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = currentFontFamily
                        ),
                        color = TextPureWhite
                    )
                    Text(
                        text = anim.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontFamily = currentFontFamily
                        ),
                        color = TextMuted
                    )
                }
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "DYNAMIC PHYSICS & EFFECTS",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x0CFFFFFF))
            .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MotionSwitchRow("Reduce Motion", "Limits animation travel for accessibility", appearanceState.reduceMotion, currentFontFamily) {
            onUpdate { it.copy(reduceMotion = !it.reduceMotion) }
        }
        MotionSwitchRow("Enable Physics", "Applies natural velocity inertia", appearanceState.enablePhysics, currentFontFamily) {
            onUpdate { it.copy(enablePhysics = !it.enablePhysics) }
        }
        MotionSwitchRow("Enable Bounce", "Playful spring overshoot on sheet triggers", appearanceState.enableBounce, currentFontFamily) {
            onUpdate { it.copy(enableBounce = !it.enableBounce) }
        }
        MotionSwitchRow("Enable Blur", "Volumetric backdrop blur rendering", appearanceState.enableBlur, currentFontFamily) {
            onUpdate { it.copy(enableBlur = !it.enableBlur) }
        }
        MotionSwitchRow("Enable Glow", "Specular luminous accent edges", appearanceState.enableGlow, currentFontFamily) {
            onUpdate { it.copy(enableGlow = !it.enableGlow) }
        }
    }
}

@Composable
private fun MotionSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    fontFamily: FontFamily,
    onToggle: () -> Unit
) {
    val accent = LocalAtlasAccent.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, fontFamily = fontFamily),
                color = TextPureWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontFamily = fontFamily),
                color = TextMuted
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accent,
                uncheckedTrackColor = Color(0x28FFFFFF)
            )
        )
    }
}

// =============================================================================
// 4. SURFACES & CORNER RADIUS SECTION
// =============================================================================
@Composable
private fun SurfacesAndBlurSection(
    appearanceState: AppearanceState,
    onUpdate: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accent = LocalAtlasAccent.current
    val currentFontFamily = remember(appearanceState.fontFamily) {
        TypographyManager.getFontFamily(appearanceState.fontFamily)
    }

    // --- CORNER RADIUS SLIDER ---
    Text(
        text = "CORNER GEOMETRY (0% to 100%)",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Current: ${appearanceState.cardRadiusDp}dp (${(appearanceState.cornerRadiusFactor * 100).toInt()}%)",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, fontFamily = currentFontFamily),
        color = accent
    )

    Spacer(modifier = Modifier.height(10.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(appearanceState.cardRadiusDp.dp))
            .background(Color(0x18FFFFFF))
            .border(1.5.dp, accent, RoundedCornerShape(appearanceState.cardRadiusDp.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Adaptive Surface Geometry • ${appearanceState.cardRadiusDp}dp",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = currentFontFamily
            ),
            color = TextPureWhite
        )
    }

    Slider(
        value = appearanceState.cornerRadiusFactor,
        onValueChange = { factor ->
            onUpdate { it.copy(cornerRadiusFactor = factor) }
        },
        valueRange = 0f..1f,
        steps = 3, // 0%, 25%, 50%, 75%, 100%
        colors = SliderDefaults.colors(
            thumbColor = accent,
            activeTrackColor = accent,
            inactiveTrackColor = Color(0x28FFFFFF)
        )
    )

    Spacer(modifier = Modifier.height(24.dp))

    // --- INTERFACE DENSITY ---
    Text(
        text = "INTERFACE DENSITY",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InterfaceDensity.entries.forEach { density ->
            val isSelected = appearanceState.density == density
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.22f) else Color(0x0CFFFFFF))
                    .border(
                        1.dp,
                        if (isSelected) accent else Color(0x18FFFFFF),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        onUpdate { it.copy(density = density) }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = density.label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = currentFontFamily
                    ),
                    color = if (isSelected) TextPureWhite else TextSecondary
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    // --- WALLPAPER BLUR & TRANSPARENCY ---
    Text(
        text = "WALLPAPER BLUR (0-100%)",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Intensity: ${appearanceState.wallpaperBlurPercent}%",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, fontFamily = currentFontFamily),
        color = accent
    )

    Slider(
        value = appearanceState.wallpaperBlurPercent.toFloat(),
        onValueChange = { onUpdate { s -> s.copy(wallpaperBlurPercent = it.toInt()) } },
        valueRange = 0f..100f,
        colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "TRANSPARENCY CONTROLS",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x0CFFFFFF))
            .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TransparencySliderRow("Cards Transparency", appearanceState.cardTransparency, currentFontFamily) { v ->
            onUpdate { it.copy(cardTransparency = v) }
        }
        TransparencySliderRow("Dock Transparency", appearanceState.dockTransparency, currentFontFamily) { v ->
            onUpdate { it.copy(dockTransparency = v) }
        }
        TransparencySliderRow("Widgets Transparency", appearanceState.widgetTransparency, currentFontFamily) { v ->
            onUpdate { it.copy(widgetTransparency = v) }
        }
        TransparencySliderRow("Notes Transparency", appearanceState.notesTransparency, currentFontFamily) { v ->
            onUpdate { it.copy(notesTransparency = v) }
        }
        TransparencySliderRow("Search Transparency", appearanceState.searchTransparency, currentFontFamily) { v ->
            onUpdate { it.copy(searchTransparency = v) }
        }
    }
}

@Composable
private fun TransparencySliderRow(
    label: String,
    value: Int,
    fontFamily: FontFamily,
    onValueChange: (Int) -> Unit
) {
    val accent = LocalAtlasAccent.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium.copy(fontFamily = fontFamily), color = TextPureWhite)
            Text(text = "$value%", style = MaterialTheme.typography.labelSmall.copy(fontFamily = fontFamily), color = accent)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent)
        )
    }
}

// =============================================================================
// 5. WALLPAPER SECTION
// =============================================================================
@Composable
private fun WallpaperStudioSection(
    appearanceState: AppearanceState,
    onUpdate: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accent = LocalAtlasAccent.current
    val currentFontFamily = remember(appearanceState.fontFamily) {
        TypographyManager.getFontFamily(appearanceState.fontFamily)
    }

    Text(
        text = "WALLPAPER ENGINE",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        WallpaperStylePreset.entries.forEach { wp ->
            val isSelected = appearanceState.wallpaperStyle == wp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.16f) else Color(0x0AFFFFFF))
                    .border(
                        1.dp,
                        if (isSelected) accent.copy(alpha = 0.60f) else Color(0x18FFFFFF),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onUpdate { it.copy(wallpaperStyle = wp) } }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = wp.label,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = currentFontFamily
                            ),
                            color = TextPureWhite
                        )
                        if (wp.isFuture) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(accent.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "FUTURE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = currentFontFamily),
                                    color = accent
                                )
                            }
                        }
                    }
                    Text(
                        text = wp.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontFamily = currentFontFamily
                        ),
                        color = TextMuted
                    )
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// =============================================================================
// 6. CLOCK & WIDGETS SECTION
// =============================================================================
@Composable
private fun ClockAndWidgetsSection(
    appearanceState: AppearanceState,
    onUpdate: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accent = LocalAtlasAccent.current
    val currentFontFamily = remember(appearanceState.fontFamily) {
        TypographyManager.getFontFamily(appearanceState.fontFamily)
    }

    Text(
        text = "CLOCK STYLES (7 ARCHITECTURES)",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ClockStylePreset.entries.forEach { clk ->
            val isSelected = appearanceState.clockStylePreset == clk
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.16f) else Color(0x0AFFFFFF))
                    .border(
                        1.dp,
                        if (isSelected) accent.copy(alpha = 0.5f) else Color(0x18FFFFFF),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onUpdate { it.copy(clockStylePreset = clk) } }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = clk.label,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = currentFontFamily),
                        color = TextPureWhite
                    )
                    Text(
                        text = clk.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontFamily = currentFontFamily),
                        color = TextMuted
                    )
                }
                if (isSelected) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(26.dp))

    Text(
        text = "WIDGET STYLE",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        WidgetStyle.entries.forEach { style ->
            val isSelected = appearanceState.widgetStyle == style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.16f) else Color(0x0AFFFFFF))
                    .border(1.dp, if (isSelected) accent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                    .clickable { onUpdate { it.copy(widgetStyle = style) } }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = style.label,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = currentFontFamily),
                        color = TextPureWhite
                    )
                    Text(
                        text = style.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontFamily = currentFontFamily),
                        color = TextMuted
                    )
                }
                if (isSelected) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(26.dp))

    Text(
        text = "STATUS BAR MODE",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StatusBarMode.entries.forEach { mode ->
            val isSelected = appearanceState.statusBarMode == mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.16f) else Color(0x0AFFFFFF))
                    .border(1.dp, if (isSelected) accent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                    .clickable { onUpdate { it.copy(statusBarMode = mode) } }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mode.label,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = currentFontFamily),
                        color = TextPureWhite
                    )
                    Text(
                        text = mode.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontFamily = currentFontFamily),
                        color = TextMuted
                    )
                }
                if (isSelected) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// =============================================================================
// 7. APP DRAWER & SYSTEM FLOW
// =============================================================================
@Composable
private fun AppDrawerAndSystemSection(
    appearanceState: AppearanceState,
    onUpdate: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accent = LocalAtlasAccent.current
    val currentFontFamily = remember(appearanceState.fontFamily) {
        TypographyManager.getFontFamily(appearanceState.fontFamily)
    }

    Text(
        text = "DRAWER GRID COLUMNS",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        listOf(3, 4, 5).forEach { cols ->
            val isSelected = appearanceState.drawerGridCols == cols
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.22f) else Color(0x0CFFFFFF))
                    .border(1.dp, if (isSelected) accent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                    .clickable { onUpdate { it.copy(drawerGridCols = cols) } }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$cols Columns",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = currentFontFamily),
                    color = if (isSelected) TextPureWhite else TextSecondary
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x0CFFFFFF))
            .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MotionSwitchRow("Alphabet Fast Scroller", "Index scroller on drawer edge", appearanceState.drawerAlphabetScroller, currentFontFamily) {
            onUpdate { it.copy(drawerAlphabetScroller = !it.drawerAlphabetScroller) }
        }
        MotionSwitchRow("App Labels", "Show application titles below icons", appearanceState.drawerAppLabels, currentFontFamily) {
            onUpdate { it.copy(drawerAppLabels = !it.drawerAppLabels) }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "SEARCH BAR POSITION",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        listOf("top" to "Top of Drawer", "bottom" to "Bottom Docked").forEach { (pos, label) ->
            val isSelected = appearanceState.drawerSearchPosition == pos
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.22f) else Color(0x0CFFFFFF))
                    .border(1.dp, if (isSelected) accent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                    .clickable { onUpdate { it.copy(drawerSearchPosition = pos) } }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontFamily = currentFontFamily),
                    color = if (isSelected) TextPureWhite else TextSecondary
                )
            }
        }
    }
}

// =============================================================================
// 8. NOTES STYLING SECTION
// =============================================================================
@Composable
private fun NotesStylingSection(
    appearanceState: AppearanceState,
    onUpdate: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accent = LocalAtlasAccent.current
    val currentFontFamily = remember(appearanceState.fontFamily) {
        TypographyManager.getFontFamily(appearanceState.fontFamily)
    }

    Text(
        text = "PAPER TEXTURE",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NotesPaperStyle.entries.forEach { paper ->
            val isSelected = appearanceState.notesPaperStyle == paper
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.22f) else Color(0x0CFFFFFF))
                    .border(1.dp, if (isSelected) accent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                    .clickable { onUpdate { it.copy(notesPaperStyle = paper) } }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = paper.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = currentFontFamily
                    ),
                    color = if (isSelected) TextPureWhite else TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "LINE SPACING",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NotesSpacing.entries.forEach { spacing ->
            val isSelected = appearanceState.notesSpacing == spacing
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.22f) else Color(0x0CFFFFFF))
                    .border(1.dp, if (isSelected) accent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                    .clickable { onUpdate { it.copy(notesSpacing = spacing) } }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = spacing.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = currentFontFamily
                    ),
                    color = if (isSelected) TextPureWhite else TextSecondary
                )
            }
        }
    }
}

// =============================================================================
// 9. XP & LEVELING SYSTEM SECTION
// =============================================================================
@Composable
private fun XpSystemStudioSection(
    appearanceState: AppearanceState,
    onUpdate: ((AppearanceState) -> AppearanceState) -> Unit
) {
    val accent = LocalAtlasAccent.current
    val currentFontFamily = remember(appearanceState.fontFamily) {
        TypographyManager.getFontFamily(appearanceState.fontFamily)
    }

    Text(
        text = "XP PROGRESS BAR STYLE",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        XpSystemStyle.entries.forEach { xpStyle ->
            val isSelected = appearanceState.xpSystemStyle == xpStyle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.16f) else Color(0x0AFFFFFF))
                    .border(1.dp, if (isSelected) accent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                    .clickable { onUpdate { it.copy(xpSystemStyle = xpStyle) } }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = xpStyle.label,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = currentFontFamily),
                        color = TextPureWhite
                    )
                    Text(
                        text = xpStyle.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, fontFamily = currentFontFamily),
                        color = TextMuted
                    )
                }
                if (isSelected) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "LEVEL BADGE SHAPE",
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = currentFontFamily,
            letterSpacing = 1.4.sp,
            fontWeight = FontWeight.Bold
        ),
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LevelBadgeShape.entries.forEach { badge ->
            val isSelected = appearanceState.xpLevelBadgeShape == badge
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) accent.copy(alpha = 0.22f) else Color(0x0CFFFFFF))
                    .border(1.dp, if (isSelected) accent else Color(0x18FFFFFF), RoundedCornerShape(14.dp))
                    .clickable { onUpdate { it.copy(xpLevelBadgeShape = badge) } }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = currentFontFamily
                    ),
                    color = if (isSelected) TextPureWhite else TextSecondary
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x0CFFFFFF))
            .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        MotionSwitchRow("Bar Ambient Glow", "Luminous aura around active level progression", appearanceState.xpBarGlow, currentFontFamily) {
            onUpdate { it.copy(xpBarGlow = !it.xpBarGlow) }
        }
    }
}
