package com.vishal.thinkpadcontrol.data.repository

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vishal.thinkpadcontrol.domain.model.ViewIdConfig
import com.vishal.thinkpadcontrol.utils.InputValidator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewIdRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "ViewIdRepository"
        private const val PREFS_NAME = "view_id_prefs"
        private const val KEY_YOUTUBE_IDS = "youtube_view_ids"
        private const val KEY_INSTAGRAM_IDS = "instagram_view_ids"
        private const val KEY_LAST_UPDATED = "last_updated"
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
            throw SecurityException("Unable to create secure storage for view IDs. Please ensure device security is properly configured.")
        }
    }

    private val _viewIdConfig = MutableStateFlow(loadViewIdConfig())
    val viewIdConfig: Flow<ViewIdConfig> = _viewIdConfig.asStateFlow()

    private fun loadViewIdConfig(): ViewIdConfig {
        return runCatching {
            val youtubeJson = sharedPreferences.getString(KEY_YOUTUBE_IDS, null)
            val instagramJson = sharedPreferences.getString(KEY_INSTAGRAM_IDS, null)
            val lastUpdated = sharedPreferences.getLong(KEY_LAST_UPDATED, 0L)

            val youtubeIds = if (youtubeJson != null) {
                val type = object : TypeToken<List<String>>() {}.type
                Gson().fromJson<List<String>>(youtubeJson, type)
            } else {
                loadDefaultYouTubeIds()
            }

            val instagramIds = if (instagramJson != null) {
                val type = object : TypeToken<List<String>>() {}.type
                Gson().fromJson<List<String>>(instagramJson, type)
            } else {
                loadDefaultInstagramIds()
            }

            ViewIdConfig(
                youtubeIds = youtubeIds,
                instagramIds = instagramIds,
                lastUpdated = lastUpdated
            )
        }.getOrElse { 
            Log.e(TAG, "Error loading view ID config", it)
            ViewIdConfig(
                youtubeIds = loadDefaultYouTubeIds(),
                instagramIds = loadDefaultInstagramIds(),
                lastUpdated = 0L
            )
        }
    }

    private fun loadDefaultYouTubeIds(): List<String> {
        return runCatching {
            val json = context.assets.open("view_ids.json").bufferedReader().use { it.readText() }
            val viewIds = Gson().fromJson(json, com.vishal.thinkpadcontrol.utils.ViewIds::class.java)
            viewIds.youtube
        }.getOrElse { 
            Log.w(TAG, "Failed to load default YouTube IDs", it)
            listOf(
                "shorts_player_page",
                "reel_player_page",
                "shorts_container",
                "shorts_video_container"
            )
        }
    }

    private fun loadDefaultInstagramIds(): List<String> {
        return runCatching {
            val json = context.assets.open("view_ids.json").bufferedReader().use { it.readText() }
            val viewIds = Gson().fromJson(json, com.vishal.thinkpadcontrol.utils.ViewIds::class.java)
            viewIds.instagram
        }.getOrElse { 
            Log.w(TAG, "Failed to load default Instagram IDs", it)
            listOf(
                "clips_viewer_view_pager",
                "reel_viewer_fragment_container",
                "clips_tab",
                "reel_feed_timeline"
            )
        }
    }

    suspend fun updateYouTubeIds(ids: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val validationResult = InputValidator.validateViewIdList(ids)
            if (validationResult is InputValidator.ValidationResult.Invalid) {
                throw IllegalArgumentException(validationResult.message)
            }

            val json = Gson().toJson(ids)
            val currentTime = System.currentTimeMillis()
            
            sharedPreferences.edit()
                .putString(KEY_YOUTUBE_IDS, json)
                .putLong(KEY_LAST_UPDATED, currentTime)
                .apply()

            _viewIdConfig.value = _viewIdConfig.value.copy(
                youtubeIds = ids,
                lastUpdated = currentTime
            )
            
            Log.d(TAG, "Updated YouTube IDs: ${ids.size} entries")
        }
    }

    suspend fun updateInstagramIds(ids: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val validationResult = InputValidator.validateViewIdList(ids)
            if (validationResult is InputValidator.ValidationResult.Invalid) {
                throw IllegalArgumentException(validationResult.message)
            }

            val json = Gson().toJson(ids)
            val currentTime = System.currentTimeMillis()
            
            sharedPreferences.edit()
                .putString(KEY_INSTAGRAM_IDS, json)
                .putLong(KEY_LAST_UPDATED, currentTime)
                .apply()

            _viewIdConfig.value = _viewIdConfig.value.copy(
                instagramIds = ids,
                lastUpdated = currentTime
            )
            
            Log.d(TAG, "Updated Instagram IDs: ${ids.size} entries")
        }
    }

    suspend fun resetToDefaults(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val defaultYoutube = loadDefaultYouTubeIds()
            val defaultInstagram = loadDefaultInstagramIds()
            val currentTime = System.currentTimeMillis()

            sharedPreferences.edit()
                .putString(KEY_YOUTUBE_IDS, Gson().toJson(defaultYoutube))
                .putString(KEY_INSTAGRAM_IDS, Gson().toJson(defaultInstagram))
                .putLong(KEY_LAST_UPDATED, currentTime)
                .apply()

            _viewIdConfig.value = ViewIdConfig(
                youtubeIds = defaultYoutube,
                instagramIds = defaultInstagram,
                lastUpdated = currentTime
            )
            
            Log.d(TAG, "Reset view IDs to defaults")
        }
    }
}