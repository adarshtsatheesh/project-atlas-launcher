package com.example.domain.model

enum class AppIconSize(val label: String, val dpSize: Int) {
    COMPACT("Compact", 44),
    DEFAULT("Standard", 52),
    LARGE("Comfortable", 60),
    EXTRA_LARGE("Maximum", 68)
}

enum class ColorPalette(
    val label: String,
    val hexCode: String,
    val description: String
) {
    OBSIDIAN_CRIMSON("Obsidian Crimson", "#FF2A4D", "Signature glowing ruby & neon red accents"),
    CYBER_EMERALD("Cyber Emerald", "#10B981", "Bioluminescent emerald & fresh jade accents"),
    ELECTRIC_AZURE("Electric Azure", "#0284C7", "High-voltage sapphire & electric cyan accents"),
    AMBER_GOLD("Amber Solar", "#F59E0B", "Warm molten solar & golden amber accents"),
    NEON_VIOLET("Neon Violet", "#A855F7", "Cosmic ultraviolet & magenta twilight accents"),
    MINIMAL_MONOCHROME("Stealth Platinum", "#E2E8F0", "Pure architectural titanium monochrome accents")
}
