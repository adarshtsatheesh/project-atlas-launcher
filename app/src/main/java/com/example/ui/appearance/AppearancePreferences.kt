package com.example.ui.appearance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.R

/**
 * Available Typography Typefaces.
 * All fonts are downloaded and bundled locally as static .ttf resources in res/font/,
 * or mapped to clean system/modern typography fallbacks.
 */
enum class AppFontFamily(
    val id: String,
    val label: String,
    val fontResId: Int,
    val subtitle: String,
    val isDefault: Boolean = false
) {
    SPACE_GROTESK(
        id = "space_grotesk",
        label = "Space Grotesk",
        fontResId = R.font.space_grotesk,
        subtitle = "Geometric monospace-infused modern sans",
        isDefault = true
    ),
    SATOSHI(
        id = "satoshi",
        label = "Satoshi",
        fontResId = R.font.plus_jakarta_sans,
        subtitle = "Clean geometric modernist sans"
    ),
    INTER(
        id = "inter",
        label = "Inter",
        fontResId = R.font.inter,
        subtitle = "World-class digital interface typography"
    ),
    GEIST(
        id = "geist",
        label = "Geist",
        fontResId = R.font.geist,
        subtitle = "Engineered for developers, ultra-minimal & precise"
    ),
    SF_PRO(
        id = "sf_pro",
        label = "SF Pro",
        fontResId = R.font.inter,
        subtitle = "Iconic Apple system design typography"
    ),
    JETBRAINS_MONO(
        id = "jetbrains_mono",
        label = "JetBrains Mono",
        fontResId = R.font.jetbrains_mono,
        subtitle = "Crafted for developer focus, code & execution"
    ),
    POPPINS(
        id = "poppins",
        label = "Poppins",
        fontResId = R.font.poppins,
        subtitle = "Geometric friendly rounded sans"
    ),
    MANROPE(
        id = "manrope",
        label = "Manrope",
        fontResId = R.font.manrope,
        subtitle = "Modern semi-rounded geometric sans"
    ),
    PLUS_JAKARTA_SANS(
        id = "plus_jakarta_sans",
        label = "Plus Jakarta Sans",
        fontResId = R.font.plus_jakarta_sans,
        subtitle = "Refined contemporary neo-grotesque sans"
    ),
    OUTFIT(
        id = "outfit",
        label = "Outfit",
        fontResId = R.font.outfit,
        subtitle = "Clean, friendly & contemporary geometric typeface"
    ),
    SORA(
        id = "sora",
        label = "Sora",
        fontResId = R.font.sora,
        subtitle = "Futuristic aesthetic with architectural cuts"
    ),
    IBM_PLEX_SANS(
        id = "ibm_plex_sans",
        label = "IBM Plex Sans",
        fontResId = R.font.ibm_plex_sans,
        subtitle = "Industrial design heritage, neutral & razor-sharp"
    ),
    GENERAL_SANS(
        id = "general_sans",
        label = "General Sans",
        fontResId = R.font.plus_jakarta_sans,
        subtitle = "Contemporary grotesque with refined balance"
    ),
    CLASH_GROTESK(
        id = "clash_grotesk",
        label = "Clash Grotesk",
        fontResId = R.font.space_grotesk,
        subtitle = "Bold architectural contrast display typeface"
    ),
    DM_SANS(
        id = "dm_sans",
        label = "DM Sans",
        fontResId = R.font.dm_sans,
        subtitle = "Low-contrast geometric sans optimized for screens"
    )
}

/**
 * Font Scale Options.
 */
enum class FontSizeScale(val label: String, val scale: Float) {
    SMALL("Small", 0.88f),
    DEFAULT("Default", 1.0f),
    LARGE("Large", 1.14f),
    EXTRA_LARGE("Extra Large", 1.28f)
}

/**
 * Line Height Options.
 */
enum class LineHeightOption(val label: String, val multiplier: Float) {
    TIGHT("Tight", 0.92f),
    NORMAL("Normal", 1.0f),
    RELAXED("Relaxed", 1.20f)
}

/**
 * Font Boldness Options.
 */
enum class FontBoldnessOption(val label: String, val weight: FontWeight) {
    NORMAL("Regular", FontWeight.Normal),
    MEDIUM("Medium", FontWeight.Medium),
    SEMIBOLD("SemiBold", FontWeight.SemiBold),
    BOLD("Bold", FontWeight.Bold),
    EXTRA_BOLD("ExtraBold", FontWeight.ExtraBold)
}

/**
 * Top-level Theme Modes.
 */
enum class ThemeMode(val label: String, val description: String) {
    AMOLED_BLACK("AMOLED Black", "True #000000 pure void for maximum battery & contrast"),
    DARK("Dark Obsidian", "Deep charcoal slate for smooth nocturnal comfort"),
    LIGHT("Porcelain Light", "Crisp minimalist bright aesthetic with high legibility"),
    SYSTEM("System Match", "Automatically sync with device Android theme"),
    CUSTOM("Custom Matrix", "User-selected background tone and surface saturation")
}

/**
 * Theme Color Presets with complete palette sets.
 */
enum class ThemeColorPreset(
    val id: String,
    val label: String,
    val hexCode: String,
    val primaryColor: Color,
    val backgroundColor: Color,
    val cardColor: Color,
    val glowColor: Color,
    val borderColor: Color
) {
    AMOLED_BLACK(
        id = "amoled_black",
        label = "AMOLED Black",
        hexCode = "#FF2A4D",
        primaryColor = Color(0xFFFF2A4D),
        backgroundColor = Color(0xFF000000),
        cardColor = Color(0xFF0D0E12),
        glowColor = Color(0xFFFF2A4D),
        borderColor = Color(0x33FF2A4D)
    ),
    STEALTHICS_CYAN(
        id = "stealthics_cyan",
        label = "Stealthics Cyan",
        hexCode = "#00F0FF",
        primaryColor = Color(0xFF00F0FF),
        backgroundColor = Color(0xFF030A10),
        cardColor = Color(0xFF071822),
        glowColor = Color(0xFF00F0FF),
        borderColor = Color(0x3300F0FF)
    ),
    PURE_WHITE(
        id = "pure_white",
        label = "Pure White",
        hexCode = "#FFFFFF",
        primaryColor = Color(0xFFFFFFFF),
        backgroundColor = Color(0xFF050505),
        cardColor = Color(0xFF141414),
        glowColor = Color(0xFFFFFFFF),
        borderColor = Color(0x40FFFFFF)
    ),
    NEON_BLUE(
        id = "neon_blue",
        label = "Neon Blue",
        hexCode = "#0088FF",
        primaryColor = Color(0xFF0088FF),
        backgroundColor = Color(0xFF020914),
        cardColor = Color(0xFF0A1828),
        glowColor = Color(0xFF0088FF),
        borderColor = Color(0x330088FF)
    ),
    CRIMSON_RED(
        id = "crimson_red",
        label = "Crimson Red",
        hexCode = "#FF2A4D",
        primaryColor = Color(0xFFFF2A4D),
        backgroundColor = Color(0xFF0F0205),
        cardColor = Color(0xFF200910),
        glowColor = Color(0xFFFF4D6D),
        borderColor = Color(0x33FF2A4D)
    ),
    FOREST_GREEN(
        id = "forest_green",
        label = "Forest Green",
        hexCode = "#10B981",
        primaryColor = Color(0xFF10B981),
        backgroundColor = Color(0xFF020E08),
        cardColor = Color(0xFF082015),
        glowColor = Color(0xFF34D399),
        borderColor = Color(0x3310B981)
    ),
    MINIMAL_GRAY(
        id = "minimal_gray",
        label = "Minimal Gray",
        hexCode = "#94A3B8",
        primaryColor = Color(0xFF94A3B8),
        backgroundColor = Color(0xFF090A0D),
        cardColor = Color(0xFF14171E),
        glowColor = Color(0xFFCBD5E1),
        borderColor = Color(0x2894A3B8)
    ),
    ARCTIC_WHITE(
        id = "arctic_white",
        label = "Arctic White",
        hexCode = "#0284C7",
        primaryColor = Color(0xFF0284C7),
        backgroundColor = Color(0xFFF8FAFC),
        cardColor = Color(0xFFFFFFFF),
        glowColor = Color(0xFF38BDF8),
        borderColor = Color(0x220284C7)
    ),
    OCEAN_BLUE(
        id = "ocean_blue",
        label = "Ocean Blue",
        hexCode = "#00C8FF",
        primaryColor = Color(0xFF00C8FF),
        backgroundColor = Color(0xFF030914),
        cardColor = Color(0xFF0A1728),
        glowColor = Color(0xFF00C8FF),
        borderColor = Color(0x3300C8FF)
    ),
    SUNSET_ORANGE(
        id = "sunset_orange",
        label = "Sunset Orange",
        hexCode = "#F97316",
        primaryColor = Color(0xFFF97316),
        backgroundColor = Color(0xFF100702),
        cardColor = Color(0xFF221107),
        glowColor = Color(0xFFFB923C),
        borderColor = Color(0x33F97316)
    ),
    ROYAL_PURPLE(
        id = "royal_purple",
        label = "Royal Purple",
        hexCode = "#A855F7",
        primaryColor = Color(0xFFA855F7),
        backgroundColor = Color(0xFF0B0414),
        cardColor = Color(0xFF1A0B2C),
        glowColor = Color(0xFFC084FC),
        borderColor = Color(0x33A855F7)
    ),
    MIDNIGHT_GOLD(
        id = "midnight_gold",
        label = "Midnight Gold",
        hexCode = "#F59E0B",
        primaryColor = Color(0xFFF59E0B),
        backgroundColor = Color(0xFF0C0902),
        cardColor = Color(0xFF201806),
        glowColor = Color(0xFFFBBF24),
        borderColor = Color(0x33F59E0B)
    ),
    NOTHING_WHITE(
        id = "nothing_white",
        label = "Nothing White",
        hexCode = "#FFFFFF",
        primaryColor = Color(0xFFE2E8F0),
        backgroundColor = Color(0xFF000000),
        cardColor = Color(0xFF111111),
        glowColor = Color(0xFFFFFFFF),
        borderColor = Color(0x40FFFFFF)
    )
}

/**
 * Legacy Accent Color Presets for backward compatibility.
 */
enum class AccentColorPreset(
    val label: String,
    val hexCode: String,
    val color: Color
) {
    BLUE("Electric Blue", "#00C8FF", Color(0xFF00C8FF)),
    GREEN("Cyber Emerald", "#10B981", Color(0xFF10B981)),
    PURPLE("Neon Violet", "#A855F7", Color(0xFFA855F7)),
    ORANGE("Amber Solar", "#F59E0B", Color(0xFFF59E0B)),
    RED("Obsidian Crimson", "#FF2A4D", Color(0xFFFF2A4D)),
    WHITE("Pure Titanium", "#F8FAFC", Color(0xFFF8FAFC)),
    GRAY("Stealth Slate", "#94A3B8", Color(0xFF94A3B8)),
    CUSTOM("Custom Hex", "#00FFCC", Color(0xFF00FFCC))
}

/**
 * Icon Shape Customization.
 */
enum class IconShapePreset(val id: String, val label: String) {
    CIRCLE("circle", "Circle"),
    ROUNDED_SQUARE("rounded_square", "Rounded Square"),
    PEBBLE("pebble", "Pebble"),
    SUPERELLIPSE("superellipse", "Superellipse"),
    SQUIRCLE("squircle", "Squircle"),
    SHARP("sharp", "Sharp")
}

/**
 * Icon Label Size.
 */
enum class IconLabelSize(val label: String, val spSize: Float) {
    SMALL("Small", 10.5f),
    NORMAL("Normal", 12f),
    LARGE("Large", 13.5f)
}

/**
 * Notification Dot Style.
 */
enum class NotificationDotStyle(val label: String) {
    ACCENT_GLOW("Accent Glow Dot"),
    PILL_BADGE("Pill Badge"),
    NUMBER_COUNT("Number Count"),
    NONE("Off")
}

/**
 * Clock Styles.
 */
enum class ClockStylePreset(val id: String, val label: String, val description: String) {
    MINIMAL("minimal", "Minimal", "Clean minimalist single-line time with pill date"),
    NOTHING_STYLE("nothing_style", "Nothing Style", "Signature dot-matrix layout with telemetry"),
    PIXEL_STYLE("pixel_style", "Pixel Style", "Modern Android Material clock with date pill"),
    HYPEROS("hyperos", "HyperOS", "Bold geometric futuristic display"),
    STEALTHICS("stealthics", "Stealthics", "Neo-glass telemetry HUD with brackets"),
    CYBER("cyber", "Cyber", "Cyberpunk monospace dual-hex time readout"),
    DIGITAL("digital", "Digital", "Solid high-contrast athletic digital clock"),
    OUTLINE("outline", "Outline", "Architectural hollow typography clock"),
    GLASS("glass", "Glass", "Frosted glassmorphism card clock with ambient glow"),
    LARGE_MINIMAL("large_minimal", "Large Minimal", "Ultra-clean oversized dual-line clock"),
    SAMSUNG_STYLE("samsung_style", "Samsung Style", "Stacked bold hour and light minute numbers"),
    MONOSPACE("monospace", "Monospace", "Technical monospace time with ISO timestamp"),
    DIGITAL_THIN("digital_thin", "Digital Thin", "Ultra-thin architectural digital readout"),
    DIGITAL_BOLD("digital_bold", "Digital Bold", "Solid high-contrast athletic digital clock"),
    // Aliases for legacy compatibility
    NOTHING_INSPIRED("nothing", "Nothing Inspired", "Dot matrix display"),
    APPLE_STYLE("apple", "Apple Style", "Bold elegant typography"),
    INDUSTRIAL("industrial", "Industrial", "Technical monospace"),
    DIGITAL_MONO("digital_mono", "Digital Mono", "Terminal time readout"),
    MODERN("neo_glass", "Modern Horizon", "Neo-glass dual-tone"),
    LARGE_CLOCK("large", "Large Hero Clock", "Hero oversized typography")
}

/**
 * Clock Position on Home Screen.
 */
enum class ClockPosition(val label: String) {
    TOP("Top Header"),
    CENTER("Center Stage"),
    COMPACT("Compact Bar")
}

/**
 * Clock Size.
 */
enum class ClockSize(val label: String, val scale: Float) {
    COMPACT("Compact", 0.85f),
    NORMAL("Normal", 1.0f),
    LARGE("Large", 1.25f),
    HERO("Hero Oversized", 1.55f)
}

/**
 * Wallpaper Category.
 */
enum class WallpaperCategory(val label: String) {
    RECENT("Recent"),
    GALLERY("Gallery"),
    AI_WALLPAPERS("AI Wallpapers"),
    SOLID_COLORS("Solid Colors"),
    GRADIENT("Gradient"),
    BLURRED("Blurred"),
    ANIMATED("Live Animated")
}

/**
 * Icon Visual Style Presets (Legacy).
 */
enum class IconStylePreset(val label: String, val description: String) {
    MATERIAL_YOU("Material You", "Dynamic dual-tone adaptive palette"),
    ROUNDED("Rounded Squircle", "Refined 24% curved corners with soft depth"),
    FILLED("High Contrast Filled", "Bold solid containers with accent glyphs"),
    OUTLINED("Technical Outlined", "Minimalist 1.2dp geometric wireframe strokes"),
    MINIMAL("Pure Monochrome", "No background containers, clean silhouette"),
    NOTHING_STYLE("Nothing OS Style", "Signature monochrome high-tech dot matrix"),
    CUSTOM_PACK("Adaptive Mask", "Uniform system geometry mask")
}

/**
 * Animation Presets.
 */
enum class AnimationPreset(val label: String, val description: String, val speedMultiplier: Float) {
    MINIMAL("Minimal", "Subtle, zero overshoot, instantaneous feel", 0.5f),
    BALANCED("Balanced", "Smooth natural Apple/Nothing OS spring damping", 1.0f),
    DYNAMIC("Dynamic", "Energetic spring physics with responsive travel", 1.2f),
    FAST("120Hz Hyperfast", "Snappy micro-durations for max efficiency", 0.3f),
    SLOW("Cinematic", "Deliberate, luxurious slow-motion deceleration", 1.8f)
}

/**
 * Interface Density.
 */
enum class InterfaceDensity(val label: String, val paddingMultiplier: Float, val description: String) {
    COMPACT("Compact", 0.8f, "Higher information density, reduced padding"),
    COMFORTABLE("Comfortable", 1.0f, "Balanced spacing for effortless daily flow"),
    SPACIOUS("Spacious", 1.25f, "Generous breathing room, relaxed margins")
}

/**
 * Wallpaper Visual Presets (Legacy).
 */
enum class WallpaperStylePreset(val label: String, val description: String, val isFuture: Boolean = false) {
    STATIC_WALLPAPER("Static Wallpaper", "Crisp minimalist photographic backdrop"),
    GRADIENT("Dynamic Gradient", "Deep obsidian chromatic gradient mesh"),
    BLURRED_WALLPAPER("Blurred Wallpaper", "Soft volumetric frosted ambient tone"),
    LIVE_WALLPAPER("Live Wallpaper", "Breathing organic pulse & particle physics"),
    VIDEO_WALLPAPER("Video Wallpaper", "High frame-rate looping atmosphere", isFuture = true)
}

/**
 * Widget Surface Style.
 */
enum class WidgetStyle(val label: String, val description: String) {
    MINIMAL("Minimal", "No border or background, pure typography"),
    GLASS("Frosted Glass", "Translucent glassmorphism with subtle specular border"),
    OUTLINED("Outlined", "Crisp hairline stroke with transparent interior"),
    SOLID("Solid Opaque", "Deep matte elevated background"),
    MODERN("Modern Dual-Tone", "Gradient surface with accent edge"),
    ADAPTIVE("Adaptive", "Matches active wallpaper brightness and tone")
}

/**
 * Status Bar Mode.
 */
enum class StatusBarMode(val label: String, val description: String) {
    DEFAULT("Default", "Standard system status bar icons"),
    MINIMAL("Minimal", "Only clock and battery indicator"),
    TRANSPARENT("Transparent", "Completely clear scrim overlay"),
    AUTO_HIDE("Auto Hide", "Hidden until swiped from top edge"),
    ALWAYS_VISIBLE("Always Visible", "Permanent system bar prominence")
}

/**
 * Navigation & Gesture Sensitivity.
 */
enum class GestureSensitivity(val label: String, val multiplier: Float) {
    LOW("Low Sensitivity", 0.75f),
    MEDIUM("Standard", 1.0f),
    HIGH("High Sensitivity", 1.35f)
}

/**
 * Quick Dock Size.
 */
enum class DockSize(val label: String, val dpHeight: Int) {
    SMALL("Small (44dp)", 44),
    NORMAL("Standard (52dp)", 52),
    LARGE("Large (60dp)", 60)
}

/**
 * Notes Paper Visual Style.
 */
enum class NotesPaperStyle(val label: String, val description: String) {
    PLAIN("Plain Void", "Pure clean surface without lines"),
    NOTEBOOK("Ruled Notebook", "Subtle horizontal ledger guide lines"),
    GRID("Engineering Grid", "Technical dot/grid matrix"),
    DARK("Carbon Dark", "AMOLED charcoal background")
}

/**
 * Notes Spacing.
 */
enum class NotesSpacing(val label: String, val lineSpacingDp: Int) {
    COMPACT("Compact", 4),
    NORMAL("Normal", 8),
    SPACIOUS("Spacious", 14)
}

/**
 * XP & Level System Visual Style.
 */
enum class XpSystemStyle(val label: String, val description: String) {
    GRADIENT_GLOW("Gradient Glow", "Radiant animated beam progress"),
    MINIMAL_SOLID("Minimal Solid", "Discrete monochrome progress track"),
    SEGMENTED("Segmented Blocks", "RPG-style segmented milestone bars")
}

/**
 * Level Badge Shape.
 */
enum class LevelBadgeShape(val label: String) {
    HEX("Hexagon"),
    SHIELD("Shield"),
    CIRCLE("Circle"),
    DOT("Minimal Dot")
}

/**
 * Streak Card Style.
 */
enum class StreakCardStyle(val label: String) {
    FLAME_GLOW("Flame Glow"),
    CLEAN_COUNTER("Clean Counter"),
    METRIC_MINIMAL("Metric Minimal")
}

/**
 * Complete immutable Appearance State.
 */
data class AppearanceState(
    // 1. Typography
    val fontFamily: AppFontFamily = AppFontFamily.SPACE_GROTESK,
    val titleFont: AppFontFamily = AppFontFamily.SPACE_GROTESK,
    val bodyFont: AppFontFamily = AppFontFamily.SPACE_GROTESK,
    val clockFont: AppFontFamily = AppFontFamily.SPACE_GROTESK,
    val fontSizeScale: FontSizeScale = FontSizeScale.DEFAULT,
    val fontLetterSpacingSp: Float = 0f,
    val fontLineHeightMultiplier: Float = 1.0f,
    val fontBoldness: FontBoldnessOption = FontBoldnessOption.NORMAL,

    // 2. Theme & Colors
    val themeMode: ThemeMode = ThemeMode.AMOLED_BLACK,
    val themeColorPreset: ThemeColorPreset = ThemeColorPreset.AMOLED_BLACK,
    val accentPreset: AccentColorPreset = AccentColorPreset.RED,
    val customAccentHex: String = "#FF2A4D",
    val customCardColorHex: String = "#0D0E12",
    val customGlowColorHex: String = "#FF2A4D",
    val customBorderColorHex: String = "#33FF2A4D",
    val themeTransparencyPercent: Int = 15,
    val blurAmountPercent: Int = 30,

    // 3. Icon Customization
    val iconShape: IconShapePreset = IconShapePreset.ROUNDED_SQUARE,
    val iconCornerRadiusPercent: Int = 24,
    val iconSizeDp: Int = 52,
    val iconShowLabels: Boolean = true,
    val iconLabelSize: IconLabelSize = IconLabelSize.NORMAL,
    val iconShadow: Boolean = true,
    val iconOutline: Boolean = false,
    val iconSpacingDp: Int = 12,
    val dockIconSizeDp: Int = 50,
    val dockTransparencyPercent: Int = 20,
    val dockBlurEnabled: Boolean = true,
    val dockRadiusPercent: Int = 50,
    val notificationDotStyle: NotificationDotStyle = NotificationDotStyle.ACCENT_GLOW,
    val iconStyle: IconStylePreset = IconStylePreset.ROUNDED,

    // 4. Clock Customization
    val clockStylePreset: ClockStylePreset = ClockStylePreset.LARGE_MINIMAL,
    val clockIs24Hour: Boolean = false,
    val clockShowSeconds: Boolean = false,
    val clockDateFormat: String = "EEE, MMM d",
    val clockShowWeather: Boolean = true,
    val clockShowGreeting: Boolean = true,
    val clockPosition: ClockPosition = ClockPosition.TOP,
    val clockSize: ClockSize = ClockSize.LARGE,

    // 5. Wallpaper
    val wallpaperCategory: WallpaperCategory = WallpaperCategory.GRADIENT,
    val wallpaperSelectionId: String = "obsidian_crimson",
    val wallpaperStyle: WallpaperStylePreset = WallpaperStylePreset.GRADIENT,
    val wallpaperBlurPercent: Int = 30,

    // 6. Animation Style
    val animationPreset: AnimationPreset = AnimationPreset.BALANCED,
    val reduceMotion: Boolean = false,
    val enablePhysics: Boolean = true,
    val enableBounce: Boolean = true,
    val enableBlur: Boolean = true,
    val enableGlow: Boolean = true,

    // 7. Corner Radius, Density & Layout
    val cornerRadiusFactor: Float = 0.50f, // 0 = 0dp, 1.0 = 28dp
    val density: InterfaceDensity = InterfaceDensity.COMFORTABLE,
    val layoutRows: Int = 5,
    val layoutColumns: Int = 4,
    val layoutShowSearchBar: Boolean = false,
    val layoutHideStatusBar: Boolean = false,
    val layoutHideDock: Boolean = false,
    val layoutWidgetPaddingDp: Int = 16,
    val layoutGridDensity: InterfaceDensity = InterfaceDensity.COMFORTABLE,

    // 8. Legacy Transparency Values (0 to 100)
    val cardTransparency: Int = 15,
    val dockTransparency: Int = 20,
    val widgetTransparency: Int = 10,
    val notesTransparency: Int = 15,
    val searchTransparency: Int = 20,

    // 9. Legacy Widgets, Navigation, Notes & XP
    val widgetStyle: WidgetStyle = WidgetStyle.GLASS,
    val widgetCornerRadiusDp: Int = 22,
    val statusBarMode: StatusBarMode = StatusBarMode.DEFAULT,
    val gestureSensitivity: GestureSensitivity = GestureSensitivity.MEDIUM,
    val dockSize: DockSize = DockSize.NORMAL,
    val dockBlur: Boolean = true,
    val dockOpacity: Float = 0.85f,
    val drawerGridCols: Int = 4,
    val drawerAlphabetScroller: Boolean = true,
    val drawerScrollbarWidth: Int = 4,
    val drawerScrollbarAnimation: String = "fade",
    val drawerSearchPosition: String = "top",
    val drawerSearchStyle: String = "glass",
    val drawerAppLabels: Boolean = true,
    val drawerSpacing: String = "balanced",
    val drawerFolderStyle: String = "grid",
    val notesPaperStyle: NotesPaperStyle = NotesPaperStyle.PLAIN,
    val notesInkColorHex: String = "#FF2A4D",
    val notesFontFamily: AppFontFamily = AppFontFamily.SPACE_GROTESK,
    val notesSpacing: NotesSpacing = NotesSpacing.NORMAL,
    val xpSystemStyle: XpSystemStyle = XpSystemStyle.GRADIENT_GLOW,
    val xpBarGlow: Boolean = true,
    val xpLevelBadgeShape: LevelBadgeShape = LevelBadgeShape.HEX,
    val streakCardStyle: StreakCardStyle = StreakCardStyle.FLAME_GLOW,
    val xpAnimationsEnabled: Boolean = true
) {
    /**
     * Active Accent Color derived from active preset, themeColorPreset, or custom hex.
     */
    val activeAccentColor: Color
        get() {
            if (accentPreset == AccentColorPreset.CUSTOM) {
                return try {
                    Color(android.graphics.Color.parseColor(customAccentHex))
                } catch (e: Exception) {
                    themeColorPreset.primaryColor
                }
            }
            return themeColorPreset.primaryColor
        }

    val activeCardColor: Color
        get() {
            return try {
                Color(android.graphics.Color.parseColor(customCardColorHex))
            } catch (e: Exception) {
                themeColorPreset.cardColor
            }
        }

    val activeGlowColor: Color
        get() {
            return try {
                Color(android.graphics.Color.parseColor(customGlowColorHex))
            } catch (e: Exception) {
                themeColorPreset.glowColor
            }
        }

    val activeBorderColor: Color
        get() {
            return try {
                Color(android.graphics.Color.parseColor(customBorderColorHex))
            } catch (e: Exception) {
                themeColorPreset.borderColor
            }
        }

    /**
     * Computed card corner radius in dp from the factor or iconCornerRadiusPercent.
     */
    val cardRadiusDp: Int
        get() = ((iconCornerRadiusPercent / 100f) * 28f).toInt().coerceIn(0, 32)

    val buttonRadiusDp: Int
        get() = ((iconCornerRadiusPercent / 100f) * 22f).toInt().coerceIn(0, 26)

    val smallRadiusDp: Int
        get() = ((iconCornerRadiusPercent / 100f) * 12f).toInt().coerceIn(0, 16)
}
