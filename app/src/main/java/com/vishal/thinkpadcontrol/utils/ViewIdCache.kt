package com.vishal.thinkpadcontrol.utils

import android.util.Log
import com.vishal.thinkpadcontrol.domain.model.ViewIdConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewIdCache @Inject constructor() {
    companion object {
        private const val TAG = "ViewIdCache"
        private const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"
    }

    private val _cachedYouTubeIds = MutableStateFlow<Set<String>>(emptySet())
    val cachedYouTubeIds: Flow<Set<String>> = _cachedYouTubeIds.asStateFlow()

    private val _cachedInstagramIds = MutableStateFlow<Set<String>>(emptySet())
    val cachedInstagramIds: Flow<Set<String>> = _cachedInstagramIds.asStateFlow()

    private var lastUpdateTime = 0L

    fun updateCache(config: ViewIdConfig) {
        if (config.lastUpdated > lastUpdateTime) {
            val youtubeSet = config.youtubeIds.map { "$YOUTUBE_PACKAGE:id/$it" }.toSet()
            val instagramSet = config.instagramIds.map { "$INSTAGRAM_PACKAGE:id/$it" }.toSet()
            
            _cachedYouTubeIds.value = youtubeSet
            _cachedInstagramIds.value = instagramSet
            lastUpdateTime = config.lastUpdated
            
            Log.d(TAG, "Cache updated - YouTube: ${youtubeSet.size}, Instagram: ${instagramSet.size}")
        }
    }

    fun containsYouTubeId(viewId: String): Boolean {
        return _cachedYouTubeIds.value.contains(viewId)
    }

    fun containsInstagramId(viewId: String): Boolean {
        return _cachedInstagramIds.value.contains(viewId)
    }

    fun getYouTubeIds(): Set<String> = _cachedYouTubeIds.value
    fun getInstagramIds(): Set<String> = _cachedInstagramIds.value

    fun clearCache() {
        _cachedYouTubeIds.value = emptySet()
        _cachedInstagramIds.value = emptySet()
        lastUpdateTime = 0L
        Log.d(TAG, "Cache cleared")
    }
}