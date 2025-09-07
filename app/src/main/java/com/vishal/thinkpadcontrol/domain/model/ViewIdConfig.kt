package com.vishal.thinkpadcontrol.domain.model

data class ViewIdConfig(
    val youtubeIds: List<String> = emptyList(),
    val instagramIds: List<String> = emptyList(),
    val lastUpdated: Long = 0L
)