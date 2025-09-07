package com.vishal.thinkpadcontrol.domain.model

data class UiState(
    val youtubeEnabled: Boolean = true,
    val instagramEnabled: Boolean = true,
    val youtubeBlockedUntil: Long = 0L,
    val instagramBlockedUntil: Long = 0L,
    val youtubeInterventions: Int = 0,
    val instagramInterventions: Int = 0
)