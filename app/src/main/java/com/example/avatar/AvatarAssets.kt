package com.example.avatar

import androidx.compose.ui.graphics.Color

/**
 * Gender presentation preference for avatar base silhouette.
 */
enum class GenderPresentation(val label: String) {
    MASCULINE("Masculine"),
    FEMININE("Feminine"),
    ANDROGYNOUS("Neutral")
}

/**
 * Predefined modern skin tones with matching highlight and shadow values.
 */
enum class SkinTone(
    val label: String,
    val baseColor: Color,
    val shadowColor: Color,
    val highlightColor: Color
) {
    FAIR_PORCELAIN("Porcelain", Color(0xFFFDE8DC), Color(0xFFE5C3B3), Color(0xFFFFF4EE)),
    WARM_IVORY("Warm Ivory", Color(0xFFF8DCBE), Color(0xFFDCB796), Color(0xFFFFF0DD)),
    GOLDEN_BEIGE("Golden Sand", Color(0xFFE8C198), Color(0xFFC79E73), Color(0xFFF7D8B5)),
    RICH_HONEY("Honey Bronze", Color(0xFFD49E72), Color(0xFFB0764D), Color(0xFFE8B68E)),
    CARAMEL("Warm Caramel", Color(0xFFBA7E51), Color(0xFF965B32), Color(0xFFD4996E)),
    DEEP_CHESTNUT("Deep Chestnut", Color(0xFF8D532F), Color(0xFF6B3A1C), Color(0xFFAA6F4B)),
    ESPRESSO("Rich Espresso", Color(0xFF5A3622), Color(0xFF402313), Color(0xFF754930)),
    OBSIDIAN("Midnight Obsidian", Color(0xFF382319), Color(0xFF24150D), Color(0xFF523628))
}

/**
 * Modern digital cartoon hairstyles with unlock level requirements.
 */
enum class HairStyle(
    val label: String,
    val requiredLevel: Int = 1,
    val isFeminineOriented: Boolean = false
) {
    SHORT_FADE("Modern Fade", 1),
    TEXTURED_CROP("Textured Crop", 1),
    MESSY_QUIFF("Modern Quiff", 3),
    SLICKED_BACK("Slicked Back", 5),
    BUZZ_CUT("Clean Buzz", 1),
    AFRO_FADE("Afro Fade", 3),
    TWISTS_DREADS("Locs / Twists", 7),
    SIDE_PART("Classic Side Part", 5),
    LONG_WAVY("Long Wavy Waves", 2, true),
    HIGH_PONYTAIL("High Ponytail", 3, true),
    SLEEK_BOB("Sleek Bob", 4, true),
    CURTAIN_BANGS("Curtain Bangs", 6, true),
    BALD("Clean Shaved", 1)
}

/**
 * Hair color palette with base, shadow, and soft highlights.
 */
enum class HairColor(
    val label: String,
    val baseColor: Color,
    val highlightColor: Color,
    val shadowColor: Color
) {
    JET_BLACK("Jet Black", Color(0xFF161517), Color(0xFF2E2C30), Color(0xFF0A090A)),
    DEEP_BROWN("Deep Espresso", Color(0xFF2E1F18), Color(0xFF473328), Color(0xFF1E140F)),
    WARM_CHESTNUT("Chestnut Brown", Color(0xFF523626), Color(0xFF734D37), Color(0xFF3A2419)),
    HONEY_BLONDE("Golden Blonde", Color(0xFFD1A65E), Color(0xFFF0C883), Color(0xFFAB803A)),
    PLATINUM("Platinum Silver", Color(0xFFDDE3EA), Color(0xFFF1F5F9), Color(0xFFB0BAC7)),
    AUBURN_RED("Copper Auburn", Color(0xFF8A3423), Color(0xFFB34A34), Color(0xFF632215)),
    AZURE_ELECTRIC("Electric Azure", Color(0xFF0070F3), Color(0xFF00C8FF), Color(0xFF004499))
}

/**
 * Eye shapes for friendly, confident digital character.
 */
enum class EyeShape(val label: String) {
    ALMOND_FOCUSED("Focused Almond"),
    ROUND_WARM("Warm & Friendly"),
    CONFIDENT_SHARP("Confident & Sharp"),
    HOODED_CALM("Calm & Stoic")
}

/**
 * Eye iris colors.
 */
enum class EyeColor(val label: String, val irisColor: Color) {
    DARK_BROWN("Deep Brown", Color(0xFF38231B)),
    WARM_HAZEL("Warm Hazel", Color(0xFF664E2F)),
    AZURE_BLUE("Electric Azure", Color(0xFF0070F3)),
    EMERALD_GREEN("Forest Emerald", Color(0xFF2E6F40)),
    STEEL_GREY("Steel Grey", Color(0xFF4A5568)),
    AMBER_GOLD("Amber Gold", Color(0xFF8C5E1E))
}

/**
 * Eyebrow styles.
 */
enum class EyebrowStyle(val label: String) {
    CLEAN_NATURAL("Clean Natural"),
    STRUCTURED_THICK("Structured Thick"),
    SHARP_ARCHED("Sharp Arched"),
    MINIMAL_SOFT("Minimal Soft")
}

/**
 * Facial hair / beard styles with unlock level requirements.
 */
enum class BeardStyle(val label: String, val requiredLevel: Int = 1) {
    CLEAN_SHAVEN("Clean Shaven", 1),
    LIGHT_STUBBLE("Designer Stubble", 1),
    SHORT_BOXED("Short Boxed Beard", 3),
    FULL_BEARD("Full Sculpted Beard", 8),
    CLEAN_GOATEE("Anchor Goatee", 4),
    MODERN_MUSTACHE("Modern Mustache", 5)
}

/**
 * Face shapes.
 */
enum class FaceShape(val label: String) {
    OVAL_BALANCED("Oval Balanced"),
    SQUARE_STRONG("Square Jaw"),
    ROUND_SOFT("Round Soft"),
    CHISELED_V("Chiseled V-Line")
}

/**
 * Modern upper-body outfits.
 */
enum class OutfitType(
    val label: String,
    val subtitle: String,
    val requiredLevel: Int = 1
) {
    MINIMAL_HOODIE("Minimalist Heavy Hoodie", "Relaxed oversized cotton hoodie", 1),
    TECH_BOMBER("Matte Technical Bomber", "Precision ribbed tactical bomber jacket", 5),
    STRUCTURED_JACKET("Architect Track Jacket", "Sleek utility jacket with cyan seams", 10),
    CREWNECK_SWEATSHIRT("Clean Crewneck", "Modern heavyweight sweatshirt", 1),
    TAILORED_OVERCOAT("Executive Minimal Trench", "Tailored mid-length architectural coat", 20),
    SLIM_FIT_TEE("Athletic Minimal Tee", "Fitted modern streetwear tee", 1)
}

/**
 * Modern lower-body pants.
 */
enum class PantsType(val label: String, val requiredLevel: Int = 1) {
    TAILORED_JOGGERS("Tailored Joggers", 1),
    STRUCTURED_CARGO("Utility Cargo Pants", 5),
    SLIM_CHINOS("Crisp Minimalist Chinos", 10),
    RELAXED_SWEATSHORTS("Athletic Shorts", 1)
}

/**
 * Footwear styles.
 */
enum class FootwearType(val label: String, val requiredLevel: Int = 1) {
    MINIMAL_SNEAKERS("Monochrome Low Tops", 1),
    HIGH_TOP_RUNNERS("Technical High Tops", 4),
    RETRO_COURT("Retro Minimalist Court", 8),
    CHELSEA_BOOTS("Matte Obsidian Boots", 15)
}

/**
 * Accessories: Glasses.
 */
enum class GlassesType(val label: String, val requiredLevel: Int = 1) {
    NONE("No Glasses", 1),
    ROUND_WIRE("Minimal Round Wire", 2),
    SQUARE_FRAME("Modern Acetate Square", 3),
    TECH_SUNGLASSES("Cyan Tint Cyber Shades", 7),
    SLIM_ARCHITECT("Slim Titanium Frames", 12)
}

/**
 * Accessories: Wristwatches.
 */
enum class WatchType(val label: String, val requiredLevel: Int = 1) {
    NONE("No Watch", 1),
    SMART_BAND("Minimal Smart Band", 1),
    CHRONO_TITANIUM("Titanium Chronograph", 8),
    GOLD_EXECUTIVE("Executive Gold Horizon", 25)
}

/**
 * Accessories: Headwear.
 */
enum class HatType(val label: String, val requiredLevel: Int = 1) {
    NONE("No Headwear", 1),
    FITTED_CAP("Obsidian Baseball Cap", 3),
    MINIMAL_BEANIE("Ribbed Fisherman Beanie", 6),
    STREET_BUCKET("Minimal Tech Bucket", 12)
}

/**
 * Complete immutable configuration of the user's stylized identity avatar.
 */
data class AvatarConfig(
    val skinTone: SkinTone = SkinTone.WARM_IVORY,
    val faceShape: FaceShape = FaceShape.OVAL_BALANCED,
    val hairStyle: HairStyle = HairStyle.SHORT_FADE,
    val hairColor: HairColor = HairColor.JET_BLACK,
    val eyeShape: EyeShape = EyeShape.ALMOND_FOCUSED,
    val eyeColor: EyeColor = EyeColor.DARK_BROWN,
    val eyebrowStyle: EyebrowStyle = EyebrowStyle.CLEAN_NATURAL,
    val beardStyle: BeardStyle = BeardStyle.CLEAN_SHAVEN,
    val beardColor: HairColor = HairColor.JET_BLACK,
    val outfitType: OutfitType = OutfitType.MINIMAL_HOODIE,
    val outfitPrimaryColor: Long = 0xFF141A29, // Deep Navy/Charcoal
    val outfitSecondaryColor: Long = 0xFF00C8FF, // Electric Cyan
    val pantsType: PantsType = PantsType.TAILORED_JOGGERS,
    val pantsColor: Long = 0xFF0F131D,
    val footwear: FootwearType = FootwearType.MINIMAL_SNEAKERS,
    val footwearColor: Long = 0xFFE2E8F0,
    val glasses: GlassesType = GlassesType.NONE,
    val watch: WatchType = WatchType.NONE,
    val hat: HatType = HatType.NONE,
    val genderPresentation: GenderPresentation = GenderPresentation.MASCULINE,
    val version: Int = 1
) {
    companion object {
        val DEFAULT_MASCULINE = AvatarConfig(
            skinTone = SkinTone.WARM_IVORY,
            hairStyle = HairStyle.SHORT_FADE,
            hairColor = HairColor.JET_BLACK,
            outfitType = OutfitType.MINIMAL_HOODIE,
            genderPresentation = GenderPresentation.MASCULINE
        )

        val DEFAULT_FEMININE = AvatarConfig(
            skinTone = SkinTone.WARM_IVORY,
            hairStyle = HairStyle.LONG_WAVY,
            hairColor = HairColor.WARM_CHESTNUT,
            outfitType = OutfitType.MINIMAL_HOODIE,
            genderPresentation = GenderPresentation.FEMININE
        )
    }
}
