package com.example.ui.appearance

import androidx.compose.ui.graphics.Color
import com.example.R

/**
 * Available Typography Typefaces.
 * All fonts are downloaded and bundled locally as static .ttf resources in res/font/.
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
    GEIST(
        id = "geist",
        label = "Geist",
        fontResId = R.font.geist,
        subtitle = "Engineered for developers, ultra-minimal & precise"
    ),
    INTER(
        id = "inter",
        label = "Inter",
        fontResId = R.font.inter,
        subtitle = "World-class digital interface typography"
    ),
    MANROPE(
        id = "manrope",
        label = "Manrope",
        fontResId = R.font.manrope,
        subtitle = "Modern semi-rounded geometric sans"
    ),
    SORA(
        id = "sora",
        label = "Sora",
        fontResId = R.font.sora,
        subtitle = "Futuristic aesthetic with distinctive architectural cuts"
    ),
    OUTFIT(
        id = "outfit",
        label = "Outfit",
        fontResId = R.font.outfit,
        subtitle = "Clean, friendly & contemporary geometric typeface"
    ),
    IBM_PLEX_SANS(
        id = "ibm_plex_sans",
        label = "IBM Plex Sans",
        fontResId = R.font.ibm_plex_sans,
        subtitle = "Industrial design heritage, neutral & razor-sharp"
    ),
    PLUS_JAKARTA_SANS(
        id = "plus_jakarta_sans",
        label = "Plus Jakarta Sans",
        fontResId = R.font.plus_jakarta_sans,
        subtitle = "Refined contemporary neo-grotesque sans"
    ),
    DM_SANS(
        id = "dm_sans",
        label = "DM Sans",
        fontResId = R.font.dm_sans,
        subtitle = "Low-contrast geometric sans optimized for screens"
    ),
    JETBRAINS_MONO(
        id = "jetbrains_mono",
        label = "JetBrains Mono",
        fontResId = R.font.jetbrains_mono,
        subtitle = "Crafted for developer focus, code & execution"
    )
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
 * Accent Color Presets.
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
 * Icon Visual Style Presets.
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
 * Animation & Motion Speed Presets.
 */
enum class AnimationPreset(val label: String, val description: String, val speedMultiplier: Float) {
    MINIMAL("Minimal", "Subtle, zero overshoot, instantaneous feel", 0.5f),
    BALANCED("Balanced", "Smooth natural Apple/Nothing OS spring damping", 1.0f),
    DYNAMIC("Dynamic", "Energetic spring physics with responsive travel", 1.2f),
    FAST("120Hz Hyperfast", "Snappy micro-durations for max efficiency", 0.3f),
    SLOW("Cinematic", "Deliberate, luxurious slow-motion deceleration", 1.8f)
}

/**
 * Interface Spacing & Density.
 */
enum class InterfaceDensity(val label: String, val paddingMultiplier: Float, val description: String) {
    COMPACT("Compact", 0.8f, "Higher information density, reduced padding"),
    COMFORTABLE("Comfortable", 1.0f, "Balanced spacing for effortless daily flow"),
    SPACIOUS("Spacious", 1.25f, "Generous breathing room, relaxed margins")
}

/**
 * Wallpaper Visual Presets.
 */
enum class WallpaperStylePreset(val label: String, val description: String, val isFuture: Boolean = false) {
    STATIC_WALLPAPER("Static Wallpaper", "Crisp minimalist photographic backdrop"),
    GRADIENT("Dynamic Gradient", "Deep obsidian chromatic gradient mesh"),
    BLURRED_WALLPAPER("Blurred Wallpaper", "Soft volumetric frosted ambient tone"),
    LIVE_WALLPAPER("Live Wallpaper", "Breathing organic pulse & particle physics"),
    VIDEO_WALLPAPER("Video Wallpaper", "High frame-rate looping atmosphere", isFuture = true)
}

/**
 * Clock Styles.
 */
enum class ClockStylePreset(val id: String, val label: String, val description: String) {
    MINIMAL("minimal", "Minimal", "Ultra-clean single line time & date"),
    NOTHING_INSPIRED("nothing", "Nothing Inspired", "Signature dot-matrix layout with telemetry"),
    APPLE_STYLE("apple", "Apple Style", "Bold elegant typography with dynamic date capsule"),
    INDUSTRIAL("industrial", "Industrial", "Technical monospace time with ISO timestamp"),
    DIGITAL_MONO("digital_mono", "Digital Mono", "Retro-modern terminal time readout"),
    MODERN("neo_glass", "Modern Horizon", "Neo-glass dual-tone with glowing minutes"),
    LARGE_CLOCK("large", "Large Hero Clock", "Oversized typography filling the header")
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
 * Status Bar Appearance Mode.
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

    // 2. Theme
    val themeMode: ThemeMode = ThemeMode.AMOLED_BLACK,

    // 3. Accent Color
    val accentPreset: AccentColorPreset = AccentColorPreset.RED,
    val customAccentHex: String = "#FF2A4D",

    // 4. Icon Style
    val iconStyle: IconStylePreset = IconStylePreset.ROUNDED,

    // 5. Animation Style
    val animationPreset: AnimationPreset = AnimationPreset.BALANCED,
    val reduceMotion: Boolean = false,
    val enablePhysics: Boolean = true,
    val enableBounce: Boolean = true,
    val enableBlur: Boolean = true,
    val enableGlow: Boolean = true,

    // 6. Corner Radius (0f to 1f, representing 0% to 100%)
    val cornerRadiusFactor: Float = 0.50f, // 0 = 0dp, 1.0 = 28dp

    // 7. Interface Density
    val density: InterfaceDensity = InterfaceDensity.COMFORTABLE,

    // 8. Blur
    val wallpaperBlurPercent: Int = 30, // 0 to 100

    // 9. Transparency (0 to 100)
    val cardTransparency: Int = 15,
    val dockTransparency: Int = 20,
    val widgetTransparency: Int = 10,
    val notesTransparency: Int = 15,
    val searchTransparency: Int = 20,

    // 10. Wallpaper Style
    val wallpaperStyle: WallpaperStylePreset = WallpaperStylePreset.GRADIENT,

    // 11. Clock Style
    val clockStylePreset: ClockStylePreset = ClockStylePreset.MODERN,

    // 12. Widget Style
    val widgetStyle: WidgetStyle = WidgetStyle.GLASS,

    // 13. Status Bar
    val statusBarMode: StatusBarMode = StatusBarMode.DEFAULT,

    // 14. Navigation
    val gestureSensitivity: GestureSensitivity = GestureSensitivity.MEDIUM,
    val dockSize: DockSize = DockSize.NORMAL,
    val dockBlur: Boolean = true,
    val dockOpacity: Float = 0.85f,

    // 15. App Drawer
    val drawerGridCols: Int = 4,
    val drawerAlphabetScroller: Boolean = true,
    val drawerScrollbarWidth: Int = 4, // 2, 4, 6 dp
    val drawerScrollbarAnimation: String = "fade", // fade, persistent, elastic
    val drawerSearchPosition: String = "top", // top, bottom
    val drawerSearchStyle: String = "glass", // glass, minimal, outlined
    val drawerAppLabels: Boolean = true,
    val drawerSpacing: String = "balanced", // tight, balanced, relaxed
    val drawerFolderStyle: String = "grid", // grid, stack, floating

    // 16. Notes
    val notesPaperStyle: NotesPaperStyle = NotesPaperStyle.PLAIN,
    val notesInkColorHex: String = "#FF2A4D",
    val notesFontFamily: AppFontFamily = AppFontFamily.SPACE_GROTESK,
    val notesSpacing: NotesSpacing = NotesSpacing.NORMAL,

    // 17. XP System
    val xpSystemStyle: XpSystemStyle = XpSystemStyle.GRADIENT_GLOW,
    val xpBarGlow: Boolean = true,
    val xpLevelBadgeShape: LevelBadgeShape = LevelBadgeShape.HEX,
    val streakCardStyle: StreakCardStyle = StreakCardStyle.FLAME_GLOW,
    val xpAnimationsEnabled: Boolean = true
) {
    /**
     * Active Accent Color derived from preset or custom hex.
     */
    val activeAccentColor: Color
        get() {
            if (accentPreset == AccentColorPreset.CUSTOM) {
                return try {
                    Color(android.graphics.Color.parseColor(customAccentHex))
                } catch (e: Exception) {
                    Color(0xFFFF2A4D)
                }
            }
            return accentPreset.color
        }

    /**
     * Computed card corner radius in dp from the factor.
     */
    val cardRadiusDp: Int
        get() = (cornerRadiusFactor * 28f).toInt().coerceIn(0, 32)

    val buttonRadiusDp: Int
        get() = (cornerRadiusFactor * 22f).toInt().coerceIn(0, 26)

    val smallRadiusDp: Int
        get() = (cornerRadiusFactor * 12f).toInt().coerceIn(0, 16)
}
