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
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = Constants.REPOSITORY_TAG
        private const val PREFS_NAME = "thinkpad_control_prefs"
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

    private val _youtubeEnabled = MutableStateFlow(getYoutubeEnabledInternal())
    val youtubeEnabled: Flow<Boolean> = _youtubeEnabled.asStateFlow()

    private val _instagramEnabled = MutableStateFlow(getInstagramEnabledInternal())
    val instagramEnabled: Flow<Boolean> = _instagramEnabled.asStateFlow()

    private val _youtubeInterventions = MutableStateFlow(getYoutubeInterventionsInternal())
    val youtubeInterventions: Flow<Int> = _youtubeInterventions.asStateFlow()

    private val _instagramInterventions = MutableStateFlow(getInstagramInterventionsInternal())
    val instagramInterventions: Flow<Int> = _instagramInterventions.asStateFlow()

    private fun getYoutubeEnabledInternal(): Boolean {
        return runCatching {
            sharedPreferences.getBoolean("youtube_enabled", true)
        }.getOrElse { 
            Log.e(TAG, "Error getting YouTube enabled state", it)
            true
        }
    }

    private fun getInstagramEnabledInternal(): Boolean {
        return runCatching {
            sharedPreferences.getBoolean("instagram_enabled", true)
        }.getOrElse { 
            Log.e(TAG, "Error getting Instagram enabled state", it)
            true
        }
    }

    private fun getYoutubeInterventionsInternal(): Int {
        return runCatching {
            val today = LocalDate.now().toString()
            val lastDate = sharedPreferences.getString("last_intervention_date", "")
            if (lastDate == today) {
                sharedPreferences.getInt("youtube_interventions", 0)
            } else {
                resetDailyCountersInternal()
                0
            }
        }.getOrElse { 
            Log.e(TAG, "Error getting YouTube interventions", it)
            0
        }
    }

    private fun getInstagramInterventionsInternal(): Int {
        return runCatching {
            val today = LocalDate.now().toString()
            val lastDate = sharedPreferences.getString("last_intervention_date", "")
            if (lastDate == today) {
                sharedPreferences.getInt("instagram_interventions", 0)
            } else {
                resetDailyCountersInternal()
                0
            }
        }.getOrElse { 
            Log.e(TAG, "Error getting Instagram interventions", it)
            0
        }
    }

    private fun resetDailyCountersInternal() {
        runCatching {
            val today = LocalDate.now().toString()
            sharedPreferences.edit()
                .putString("last_intervention_date", today)
                .putInt("youtube_interventions", 0)
                .putInt("instagram_interventions", 0)
                .apply()
        }.onFailure { 
            Log.e(TAG, "Error resetting daily counters", it)
        }
    }

    suspend fun setYoutubeEnabled(enabled: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            sharedPreferences.edit().putBoolean("youtube_enabled", enabled).apply()
            _youtubeEnabled.value = enabled
        }
    }

    suspend fun setInstagramEnabled(enabled: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            sharedPreferences.edit().putBoolean("instagram_enabled", enabled).apply()
            _instagramEnabled.value = enabled
        }
    }

    suspend fun incrementYoutubeInterventions(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val today = LocalDate.now().toString()
            val editor = sharedPreferences.edit()
            editor.putString("last_intervention_date", today)
            
            val current = getYoutubeInterventionsInternal()
            val new = current + 1
            editor.putInt("youtube_interventions", new)
            editor.apply()
            
            _youtubeInterventions.value = new
            Log.d(TAG, "YouTube interventions incremented to $new")
        }
    }

    suspend fun incrementInstagramInterventions(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val today = LocalDate.now().toString()
            val editor = sharedPreferences.edit()
            editor.putString("last_intervention_date", today)
            
            val current = getInstagramInterventionsInternal()
            val new = current + 1
            editor.putInt("instagram_interventions", new)
            editor.apply()
            
            _instagramInterventions.value = new
            Log.d(TAG, "Instagram interventions incremented to $new")
        }
    }
}