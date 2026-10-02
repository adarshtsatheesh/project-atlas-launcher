package com.example.domain.model

enum class BuiltInWallpaper(
    val id: String,
    val title: String,
    val description: String,
    val accentHex: String,
    val badge: String
) {
    OBSIDIAN_VOID(
        id = "obsidian_void",
        title = "Obsidian Horizon",
        description = "Signature deep void with radiant crimson orbs & crystalline refraction ridges",
        accentHex = "#FF2A4D",
        badge = "Original"
    ),
    CYBER_EMERALD(
        id = "cyber_emerald",
        title = "Cyber Emerald",
        description = "Bioluminescent emerald aurora waves across charcoal abyss",
        accentHex = "#10B981",
        badge = "Aurora"
    ),
    ELECTRIC_AZURE(
        id = "electric_azure",
        title = "Electric Azure",
        description = "Deep oceanic midnight with radiant cyan and sapphire nebulae",
        accentHex = "#0284C7",
        badge = "Nebula"
    ),
    AMBER_ECLIPSE(
        id = "amber_eclipse",
        title = "Amber Eclipse",
        description = "Warm golden eclipse halo and molten solar dusk refraction",
        accentHex = "#F59E0B",
        badge = "Solar"
    ),
    VIOLET_TWILIGHT(
        id = "violet_twilight",
        title = "Neon Twilight",
        description = "Cosmic violet and deep magenta frosted ambient illumination",
        accentHex = "#A855F7",
        badge = "Cosmic"
    ),
    STEALTH_OLED(
        id = "stealth_oled",
        title = "Stealth Carbon OLED",
        description = "True pure black with minimalist micro-grain for max battery efficiency",
        accentHex = "#64748B",
        badge = "OLED"
    ),
    CUSTOM_GALLERY(
        id = "custom_gallery",
        title = "Custom Gallery",
        description = "Your personal photo with optimized glassmorphic legibility scrim",
        accentHex = "#E2E8F0",
        badge = "Custom"
    )
}
