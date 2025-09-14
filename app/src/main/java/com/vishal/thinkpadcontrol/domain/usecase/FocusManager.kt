package com.vishal.thinkpadcontrol.domain.usecase

import com.vishal.thinkpadcontrol.data.repository.BlockedUntilRepository
import com.vishal.thinkpadcontrol.data.repository.SettingsRepository
import com.vishal.thinkpadcontrol.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusManager @Inject constructor(
    private val blockedUntilRepository: BlockedUntilRepository,
    private val settingsRepository: SettingsRepository
) {

    suspend fun performIntervention(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val blockUntil = System.currentTimeMillis() + Constants.BLOCK_DURATION_MS
            
            when (packageName) {
                Constants.YOUTUBE_PACKAGE -> {
                    blockedUntilRepository.setYoutubeBlockedUntil(blockUntil)
                    settingsRepository.incrementYoutubeInterventions()
                }
                Constants.INSTAGRAM_PACKAGE -> {
                    blockedUntilRepository.setInstagramBlockedUntil(blockUntil)
                    settingsRepository.incrementInstagramInterventions()
                }
                else -> throw IllegalArgumentException("Unknown package: $packageName")
            }
        }
    }

    suspend fun isCurrentlyBlocked(packageName: String): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val currentTime = System.currentTimeMillis()
            when (packageName) {
                Constants.YOUTUBE_PACKAGE -> {
                    val blockedUntil = blockedUntilRepository.youtubeBlockedUntil
                    kotlinx.coroutines.flow.first(blockedUntil) > currentTime
                }
                Constants.INSTAGRAM_PACKAGE -> {
                    val blockedUntil = blockedUntilRepository.instagramBlockedUntil
                    kotlinx.coroutines.flow.first(blockedUntil) > currentTime
                }
                else -> false
            }
        }
    }

    suspend fun clearBlock(packageName: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            when (packageName) {
                Constants.YOUTUBE_PACKAGE -> blockedUntilRepository.clearYoutubeBlock()
                Constants.INSTAGRAM_PACKAGE -> blockedUntilRepository.clearInstagramBlock()
                else -> throw IllegalArgumentException("Unknown package: $packageName")
            }
        }
    }
}