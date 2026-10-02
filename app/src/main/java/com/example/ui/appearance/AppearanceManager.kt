package com.example.ui.appearance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * CompositionLocal exposing the live [AppearanceState].
 */
val LocalAppearanceState = compositionLocalOf {
    AppearanceState()
}

/**
 * Reusable Appearance styling utilities and extensions.
 */
object AppearanceManager {

    @Composable
    fun getCornerRadius(factorMultiplier: Float = 1.0f): Dp {
        val appearance = LocalAppearanceState.current
        return ((appearance.cornerRadiusFactor * 24f) * factorMultiplier).dp
    }

    @Composable
    fun getCardShape(extraRadius: Dp = 0.dp): RoundedCornerShape {
        val appearance = LocalAppearanceState.current
        val radius = (appearance.cardRadiusDp).dp + extraRadius
        return RoundedCornerShape(radius)
    }

    @Composable
    fun getButtonShape(): RoundedCornerShape {
        val appearance = LocalAppearanceState.current
        return RoundedCornerShape(appearance.buttonRadiusDp.dp)
    }

    @Composable
    fun getSmallShape(): RoundedCornerShape {
        val appearance = LocalAppearanceState.current
        return RoundedCornerShape(appearance.smallRadiusDp.dp)
    }
}
