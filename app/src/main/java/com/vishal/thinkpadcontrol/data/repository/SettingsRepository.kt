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
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import com.vishal.thinkpadcontrol.utils.ErrorHandler

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "SettingsRepository"
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
            Log.e(TAG, "Failed to create encrypted preferences, falling back to regular preferences", e)
            throw SecurityException("Unable to create secure storage. Please ensure device security is properly configured.", e)
        }
    }

    private val _youtubeEnabled = MutableStateFlow(getYoutubeEnabled())
    val youtubeEnabled: Flow<Boolean> = _youtubeEnabled.asStateFlow()

    private val _instagramEnabled = MutableStateFlow(getInstagramEnabled())
    val instagramEnabled: Flow<Boolean> = _instagramEnabled.asStateFlow()

    private val _youtubeInterventions = MutableStateFlow(getYoutubeInterventions())
    val youtubeInterventions: Flow<Int> = _youtubeInterventions.asStateFlow()

    private val _instagramInterventions = MutableStateFlow(getInstagramInterventions())
    val instagramInterventions: Flow<Int> = _instagramInterventions.asStateFlow()

    private fun getYoutubeEnabled(): Boolean {
        return try {
            sharedPreferences.getBoolean("youtube_enabled", true)
        } catch (e: Exception) {
            ErrorHandler.handleError("getYoutubeEnabled", e)
            true
        }
    }

    private fun getInstagramEnabled(): Boolean {
        return try {
            sharedPreferences.getBoolean("instagram_enabled", true)
        } catch (e: Exception) {
            ErrorHandler.handleError("getInstagramEnabled", e)
            true
        }
    }

    private fun getYoutubeInterventions(): Int {
        return try {
            val today = LocalDate.now().toString()
            val lastDate = sharedPreferences.getString("last_intervention_date", "")
            if (lastDate == today) {
                sharedPreferences.getInt("youtube_interventions", 0)
            } else {
                resetDailyCounters()
                0
            }
        } catch (e: Exception) {
            ErrorHandler.handleError("getYoutubeInterventions", e)
            0
        }
    }

    private fun getInstagramInterventions(): Int {
        return try {
            val today = LocalDate.now().toString()
            val lastDate = sharedPreferences.getString("last_intervention_date", "")
            if (lastDate == today) {
                sharedPreferences.getInt("instagram_interventions", 0)
            } else {
                resetDailyCounters()
                0
            }
        } catch (e: Exception) {
            ErrorHandler.handleError("getInstagramInterventions", e)
            0
        }
    }

    private fun resetDailyCounters() {
        try {
            val today = LocalDate.now().toString()
            sharedPreferences.edit()
                .putString("last_intervention_date", today)
                .putInt("youtube_interventions", 0)
                .putInt("instagram_interventions", 0)
                .apply()
        } catch (e: Exception) {
            ErrorHandler.handleError("resetDailyCounters", e)
        }
    }

    suspend fun setYoutubeEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                sharedPreferences.edit().putBoolean("youtube_enabled", enabled).apply()
                _youtubeEnabled.value = enabled
            } catch (e: Exception) {
                ErrorHandler.handleError("setYoutubeEnabled", e)
                throw e
            }
        }
    }

    suspend fun setInstagramEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                sharedPreferences.edit().putBoolean("instagram_enabled", enabled).apply()
                _instagramEnabled.value = enabled
            } catch (e: Exception) {
                ErrorHandler.handleError("setInstagramEnabled", e)
                throw e
            }
        }
    }

    suspend fun incrementYoutubeInterventions() {
        withContext(Dispatchers.IO) {
            try {
                val today = LocalDate.now().toString()
                val editor = sharedPreferences.edit()
                editor.putString("last_intervention_date", today)
                
                val current = getYoutubeInterventions()
                val new = current + 1
                editor.putInt("youtube_interventions", new)
                editor.apply()
                
                _youtubeInterventions.value = new
                Log.d(TAG, "YouTube interventions incremented to $new")
            } catch (e: Exception) {
                ErrorHandler.handleError("incrementYoutubeInterventions", e)
                throw e
            }
        }
    }

    suspend fun incrementInstagramInterventions() {
        withContext(Dispatchers.IO) {
            try {
                val today = LocalDate.now().toString()
                val editor = sharedPreferences.edit()
                editor.putString("last_intervention_date", today)
                
                val current = getInstagramInterventions()
                val new = current + 1
                editor.putInt("instagram_interventions", new)
                editor.apply()
                
                _instagramInterventions.value = new
                Log.d(TAG, "Instagram interventions incremented to $new")
            } catch (e: Exception) {
                ErrorHandler.handleError("incrementInstagramInterventions", e)
                throw e
            }
        }
    }
}