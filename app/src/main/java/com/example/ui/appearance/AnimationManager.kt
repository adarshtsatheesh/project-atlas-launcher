package com.example.ui.appearance

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Enterprise Animation and Motion Manager.
 * Translates user animation preferences into optimized Compose AnimationSpecs.
 */
object AnimationManager {

    /**
     * Default spring animation spec adjusted for the current preset and toggles.
     */
    fun <T> getSpringSpec(
        preset: AnimationPreset = AnimationPreset.BALANCED,
        reduceMotion: Boolean = false,
        enableBounce: Boolean = true
    ): AnimationSpec<T> {
        if (reduceMotion) {
            return tween(durationMillis = 100)
        }

        val stiffness = when (preset) {
            AnimationPreset.FAST -> Spring.StiffnessHigh
            AnimationPreset.MINIMAL -> Spring.StiffnessMedium
            AnimationPreset.BALANCED -> 350f
            AnimationPreset.DYNAMIC -> 280f
            AnimationPreset.SLOW -> Spring.StiffnessLow
        }

        val dampingRatio = if (enableBounce && preset == AnimationPreset.DYNAMIC) {
            Spring.DampingRatioMediumBouncy
        } else if (enableBounce && preset == AnimationPreset.BALANCED) {
            0.82f
        } else {
            Spring.DampingRatioNoBouncy
        }

        return spring(
            dampingRatio = dampingRatio,
            stiffness = stiffness
        )
    }

    /**
     * Tween animation duration multiplier.
     */
    fun getDuration(baseMillis: Int, preset: AnimationPreset, reduceMotion: Boolean): Int {
        if (reduceMotion) return (baseMillis * 0.4f).toInt().coerceAtLeast(50)
        return (baseMillis * preset.speedMultiplier).toInt().coerceIn(50, 1200)
    }
}
