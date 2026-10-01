package com.example.avatar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Thread-safe high performance in-memory cache for the user's active AvatarConfig.
 * Prevents unnecessary disk access and enables instant reactive UI updates.
 */
object AvatarCache {

    private val _avatarState = MutableStateFlow(AvatarConfig.DEFAULT_MASCULINE)
    val avatarState: StateFlow<AvatarConfig> = _avatarState.asStateFlow()

    @Volatile
    private var cachedConfig: AvatarConfig? = null

    fun get(): AvatarConfig? = cachedConfig

    fun update(config: AvatarConfig) {
        cachedConfig = config
        _avatarState.value = config
    }

    fun clear() {
        cachedConfig = null
        _avatarState.value = AvatarConfig.DEFAULT_MASCULINE
    }
}
