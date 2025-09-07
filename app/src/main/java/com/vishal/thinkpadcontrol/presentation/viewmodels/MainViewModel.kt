package com.vishal.thinkpadcontrol.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal.thinkpadcontrol.data.repository.BlockedUntilRepository
import com.vishal.thinkpadcontrol.data.repository.SettingsRepository
import com.vishal.thinkpadcontrol.data.repository.ViewIdRepository
import com.vishal.thinkpadcontrol.domain.model.UiState
import com.vishal.thinkpadcontrol.domain.model.ViewIdConfig
import com.vishal.thinkpadcontrol.utils.AccessibilityServiceMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val blockedUntilRepository: BlockedUntilRepository,
    private val viewIdRepository: ViewIdRepository,
    private val accessibilityServiceMonitor: AccessibilityServiceMonitor
) : ViewModel() {

    val isServiceEnabled = accessibilityServiceMonitor.isServiceEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val viewIdConfig = viewIdRepository.viewIdConfig
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ViewIdConfig()
        )

    val uiState: StateFlow<UiState> = combine(
        settingsRepository.youtubeEnabled,
        settingsRepository.instagramEnabled,
        blockedUntilRepository.youtubeBlockedUntil,
        blockedUntilRepository.instagramBlockedUntil,
        settingsRepository.youtubeInterventions,
        settingsRepository.instagramInterventions
    ) { youtube, instagram, youtubeBlocked, instagramBlocked, youtubeCount, instagramCount ->
        UiState(
            youtubeEnabled = youtube,
            instagramEnabled = instagram,
            youtubeBlockedUntil = youtubeBlocked,
            instagramBlockedUntil = instagramBlocked,
            youtubeInterventions = youtubeCount,
            instagramInterventions = instagramCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState()
    )

    fun toggleYoutube(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setYoutubeEnabled(enabled)
            } catch (e: Exception) {
                // Handle error - could emit to UI state or show error message
            }
        }
    }

    fun toggleInstagram(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setInstagramEnabled(enabled)
            } catch (e: Exception) {
                // Handle error - could emit to UI state or show error message
            }
        }
    }

    fun updateYouTubeIds(ids: List<String>) {
        viewModelScope.launch {
            try {
                viewIdRepository.updateYouTubeIds(ids)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateInstagramIds(ids: List<String>) {
        viewModelScope.launch {
            try {
                viewIdRepository.updateInstagramIds(ids)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun resetViewIdsToDefaults() {
        viewModelScope.launch {
            try {
                viewIdRepository.resetToDefaults()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun refreshServiceState() {
        accessibilityServiceMonitor.refreshState()
    }

    override fun onCleared() {
        super.onCleared()
        try {
            accessibilityServiceMonitor.stopMonitoring()
        } catch (e: Exception) {
            // Log error but don't crash
        }
    }
}