package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.AppItem
import com.example.util.AppIconCache

/** Built-in icon styles offered by the app editor. */
data class AppIconPreset(
    val id: Int,
    val start: Color,
    val end: Color,
    val glyph: String
)

object AppIconPresets {

    val all: List<AppIconPreset> = listOf(
        AppIconPreset(0, Color(0xFF5C6BC0), Color(0xFF1A237E), "S"),
        AppIconPreset(1, Color(0xFFEF5350), Color(0xFF7F1D1D), "C"),
        AppIconPreset(2, Color(0xFF42A5F5), Color(0xFF0D3B66), "W"),
        AppIconPreset(3, Color(0xFF66BB6A), Color(0xFF0B3D1D), "E"),
        AppIconPreset(4, Color(0xFFAB47BC), Color(0xFF3A0B57), "V"),
        AppIconPreset(5, Color(0xFFFFCA28), Color(0xFF7A4B00), "A"),
        AppIconPreset(6, Color(0xFF78909C), Color(0xFF20272B), "G"),
        AppIconPreset(7, Color(0xFFEC407A), Color(0xFF6A1B3D), "R")
    )

    fun byId(id: Int?): AppIconPreset? = id?.let { wanted -> all.firstOrNull { it.id == wanted } }
}

/**
 * Draws the user's icon override (gallery image or preset) when one is set.
 *
 * @return true when an override was rendered; false tells the caller to keep
 * drawing the real system icon with its own fallback styling.
 */
@Composable
fun AtlasCustomAppIcon(app: AppItem, sizeDp: Dp, shape: Shape): Boolean {
    val preset = AppIconPresets.byId(app.iconPresetId)
    val uri = app.customIconUri
    return when {
        !uri.isNullOrBlank() -> {
            AsyncImage(
                model = uri,
                contentDescription = app.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(sizeDp)
                    .clip(shape)
            )
            true
        }

        preset != null -> {
            Box(
                modifier = Modifier
                    .size(sizeDp)
                    .clip(shape)
                    .background(Brush.linearGradient(listOf(preset.start, preset.end))),
                contentAlignment = Alignment.Center
            ) {
                val glyph = preset.glyph.ifBlank { app.displayName.take(1).uppercase() }
                Text(
                    text = glyph,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (sizeDp.value * 0.42f).sp
                )
            }
            true
        }

        else -> false
    }
}

/**
 * Unified icon content for the drawer grid and the dock: user override first,
 * then the cached system bitmap (zero main-thread conversions).
 *
 * @return true when something was drawn.
 */
@Composable
fun AtlasAppIcon(app: AppItem, sizeDp: Dp, shape: Shape): Boolean {
    if (AtlasCustomAppIcon(app, sizeDp, shape)) return true
    val bitmap = remember(app.uniqueKey) {
        AppIconCache.getOrConvert(app.uniqueKey, app.icon)
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = app.displayName,
            modifier = Modifier
                .size(sizeDp)
                .clip(shape)
        )
        return true
    }
    return false
}

/** Convenience: circular override/bitmap icon. */
@Composable
fun AtlasAppIconCircle(app: AppItem, sizeDp: Dp): Boolean =
    AtlasAppIcon(app, sizeDp, CircleShape)
