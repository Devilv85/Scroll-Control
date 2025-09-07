package com.vishal.thinkpadcontrol.data.repository

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockedUntilRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "BlockedUntilRepository"
        private const val PREFS_NAME = "blocked_until_prefs"
    }

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val sharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create encrypted preferences, falling back to regular preferences", e)
            throw SecurityException("Unable to create secure storage. Please ensure device security is properly configured.", e)
        }
    }

    private val _youtubeBlockedUntil = MutableStateFlow(getYoutubeBlockedUntil())
    val youtubeBlockedUntil: Flow<Long> = _youtubeBlockedUntil.asStateFlow()

    private val _instagramBlockedUntil = MutableStateFlow(getInstagramBlockedUntil())
    val instagramBlockedUntil: Flow<Long> = _instagramBlockedUntil.asStateFlow()

    private fun getYoutubeBlockedUntil(): Long {
        return try {
            val timestamp = sharedPreferences.getLong("youtube_blocked_until", 0L)
            // Clear expired blocks
            if (timestamp > 0 && System.currentTimeMillis() >= timestamp) {
                clearYoutubeBlockInternal()
                0L
            } else {
                timestamp
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting YouTube blocked until", e)
            0L
        }
    }

    private fun getInstagramBlockedUntil(): Long {
        return try {
            val timestamp = sharedPreferences.getLong("instagram_blocked_until", 0L)
            // Clear expired blocks
            if (timestamp > 0 && System.currentTimeMillis() >= timestamp) {
                clearInstagramBlockInternal()
                0L
            } else {
                timestamp
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Instagram blocked until", e)
            0L
        }
    }

    suspend fun setYoutubeBlockedUntil(timestamp: Long) {
        withContext(Dispatchers.IO) {
            try {
                sharedPreferences.edit().putLong("youtube_blocked_until", timestamp).apply()
                _youtubeBlockedUntil.value = timestamp
                Log.d(TAG, "YouTube blocked until: $timestamp")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting YouTube blocked until", e)
                throw e
            }
        }
    }

    suspend fun setInstagramBlockedUntil(timestamp: Long) {
        withContext(Dispatchers.IO) {
            try {
                sharedPreferences.edit().putLong("instagram_blocked_until", timestamp).apply()
                _instagramBlockedUntil.value = timestamp
                Log.d(TAG, "Instagram blocked until: $timestamp")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting Instagram blocked until", e)
                throw e
            }
        }
    }

    suspend fun clearYoutubeBlock() {
        withContext(Dispatchers.IO) {
            clearYoutubeBlockInternal()
            _youtubeBlockedUntil.value = 0L
        }
    }

    suspend fun clearInstagramBlock() {
        withContext(Dispatchers.IO) {
            clearInstagramBlockInternal()
            _instagramBlockedUntil.value = 0L
        }
    }

    private fun clearYoutubeBlockInternal() {
        try {
            sharedPreferences.edit().putLong("youtube_blocked_until", 0L).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing YouTube block", e)
        }
    }

    private fun clearInstagramBlockInternal() {
        try {
            sharedPreferences.edit().putLong("instagram_blocked_until", 0L).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing Instagram block", e)
        }
    }
}