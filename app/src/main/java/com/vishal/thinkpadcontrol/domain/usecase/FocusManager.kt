package com.vishal.thinkpadcontrol.domain.usecase

import android.util.Log
import com.vishal.thinkpadcontrol.data.repository.BlockedUntilRepository
import com.vishal.thinkpadcontrol.data.repository.SettingsRepository
import com.vishal.thinkpadcontrol.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusManager @Inject constructor(
    private val blockedUntilRepository: BlockedUntilRepository,
    private val settingsRepository: SettingsRepository
) {
    companion object {
        private const val TAG = Constants.FOCUS_MANAGER_TAG
    }

    suspend fun performIntervention(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Log.d(TAG, "Performing intervention for $packageName")
            val blockUntil = System.currentTimeMillis() + Constants.BLOCK_DURATION_MS
            
            when (packageName) {
                Constants.YOUTUBE_PACKAGE -> {
                    blockedUntilRepository.setYoutubeBlockedUntil(blockUntil).getOrThrow()
                    settingsRepository.incrementYoutubeInterventions().getOrThrow()
                }
                Constants.INSTAGRAM_PACKAGE -> {
                    blockedUntilRepository.setInstagramBlockedUntil(blockUntil).getOrThrow()
                    settingsRepository.incrementInstagramInterventions().getOrThrow()
                }
                else -> throw IllegalArgumentException("Unknown package: $packageName")
            }
            Log.d(TAG, "Intervention completed for $packageName until $blockUntil")
        }
    }

    suspend fun isCurrentlyBlocked(packageName: String): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val currentTime = System.currentTimeMillis()
            when (packageName) {
                Constants.YOUTUBE_PACKAGE -> {
                    blockedUntilRepository.youtubeBlockedUntil.first() > currentTime
                }
                Constants.INSTAGRAM_PACKAGE -> {
                    blockedUntilRepository.instagramBlockedUntil.first() > currentTime
                }
                else -> false
            }
        }
    }

    suspend fun clearBlock(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Log.d(TAG, "Clearing block for $packageName")
            when (packageName) {
                Constants.YOUTUBE_PACKAGE -> blockedUntilRepository.clearYoutubeBlock().getOrThrow()
                Constants.INSTAGRAM_PACKAGE -> blockedUntilRepository.clearInstagramBlock().getOrThrow()
                else -> throw IllegalArgumentException("Unknown package: $packageName")
            }
        }
    }
    
    suspend fun checkAndEnforceExistingBlocks(packageName: String): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val isBlocked = isCurrentlyBlocked(packageName).getOrElse { false }
            if (isBlocked) {
                Log.d(TAG, "Enforcing existing block for $packageName")
            }
            isBlocked
        }
    }
}