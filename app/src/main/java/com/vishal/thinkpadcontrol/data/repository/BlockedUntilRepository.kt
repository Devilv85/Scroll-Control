package com.vishal.thinkpadcontrol.data.repository

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.vishal.thinkpadcontrol.utils.Constants
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
        private const val TAG = Constants.REPOSITORY_TAG
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
            Log.e(TAG, "Failed to create encrypted preferences", e)
            throw SecurityException(Constants.ENCRYPTED_PREFS_ERROR, e)
        }
    }

    private val _youtubeBlockedUntil = MutableStateFlow(getYoutubeBlockedUntilInternal())
    val youtubeBlockedUntil: Flow<Long> = _youtubeBlockedUntil.asStateFlow()

    private val _instagramBlockedUntil = MutableStateFlow(getInstagramBlockedUntilInternal())
    val instagramBlockedUntil: Flow<Long> = _instagramBlockedUntil.asStateFlow()

    private fun getYoutubeBlockedUntilInternal(): Long {
        return runCatching {
            val timestamp = sharedPreferences.getLong("youtube_blocked_until", 0L)
            if (timestamp > 0 && System.currentTimeMillis() >= timestamp) {
                clearYoutubeBlockInternal()
                0L
            } else {
                timestamp
            }
        }.getOrElse { 
            Log.e(TAG, "Error getting YouTube blocked until", it)
            0L
        }
    }

    private fun getInstagramBlockedUntilInternal(): Long {
        return runCatching {
            val timestamp = sharedPreferences.getLong("instagram_blocked_until", 0L)
            if (timestamp > 0 && System.currentTimeMillis() >= timestamp) {
                clearInstagramBlockInternal()
                0L
            } else {
                timestamp
            }
        }.getOrElse { 
            Log.e(TAG, "Error getting Instagram blocked until", it)
            0L
        }
    }

    suspend fun setYoutubeBlockedUntil(timestamp: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            sharedPreferences.edit().putLong("youtube_blocked_until", timestamp).apply()
            _youtubeBlockedUntil.value = timestamp
            Log.d(TAG, "YouTube blocked until: $timestamp")
        }
    }

    suspend fun setInstagramBlockedUntil(timestamp: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            sharedPreferences.edit().putLong("instagram_blocked_until", timestamp).apply()
            _instagramBlockedUntil.value = timestamp
            Log.d(TAG, "Instagram blocked until: $timestamp")
        }
    }

    suspend fun clearYoutubeBlock(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            clearYoutubeBlockInternal()
            _youtubeBlockedUntil.value = 0L
        }
    }

    suspend fun clearInstagramBlock(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            clearInstagramBlockInternal()
            _instagramBlockedUntil.value = 0L
        }
    }

    private fun clearYoutubeBlockInternal() {
        runCatching {
            sharedPreferences.edit().putLong("youtube_blocked_until", 0L).apply()
        }.onFailure { 
            Log.e(TAG, "Error clearing YouTube block", it)
        }
    }

    private fun clearInstagramBlockInternal() {
        runCatching {
            sharedPreferences.edit().putLong("instagram_blocked_until", 0L).apply()
        }.onFailure { 
            Log.e(TAG, "Error clearing Instagram block", it)
        }
    }
}