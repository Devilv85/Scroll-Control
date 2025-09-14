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

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

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
            settingsRepository.setYoutubeEnabled(enabled)
                .onFailure { error ->
                    _errorState.value = "Failed to update YouTube setting: ${error.message}"
                }
        }
    }

    fun toggleInstagram(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setInstagramEnabled(enabled)
                .onFailure { error ->
                    _errorState.value = "Failed to update Instagram setting: ${error.message}"
                }
        }
    }

    fun updateYouTubeIds(ids: List<String>) {
        viewModelScope.launch {
            viewIdRepository.updateYouTubeIds(ids)
                .onFailure { error ->
                    _errorState.value = "Failed to update YouTube IDs: ${error.message}"
                }
        }
    }

    fun updateInstagramIds(ids: List<String>) {
        viewModelScope.launch {
            viewIdRepository.updateInstagramIds(ids)
                .onFailure { error ->
                    _errorState.value = "Failed to update Instagram IDs: ${error.message}"
                }
        }
    }

    fun resetViewIdsToDefaults() {
        viewModelScope.launch {
            viewIdRepository.resetToDefaults()
                .onFailure { error ->
                    _errorState.value = "Failed to reset view IDs: ${error.message}"
                }
        }
    }

    fun clearError() {
        _errorState.value = null
    }

    fun refreshServiceState() {
        accessibilityServiceMonitor.refreshState()
    }

    override fun onCleared() {
        super.onCleared()
        runCatching {
            accessibilityServiceMonitor.stopMonitoring()
        }
    }
}