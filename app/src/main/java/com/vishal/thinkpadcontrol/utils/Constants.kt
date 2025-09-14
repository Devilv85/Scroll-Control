package com.vishal.thinkpadcontrol.utils

object Constants {
    const val YOUTUBE_PACKAGE = "com.google.android.youtube"
    const val INSTAGRAM_PACKAGE = "com.instagram.android"
    
    const val GRACE_PERIOD_MS = 5 * 60 * 1000L
    const val BLOCK_DURATION_MS = 60 * 60 * 1000L
    
    const val NOTIFICATION_ID = 1001
    const val CHANNEL_ID = "thinkpad_control_channel"
    const val CHANNEL_NAME = "ThinkPad Control Service"

    val YOUTUBE_SHORTS_KEYWORDS = setOf("shorts", "short", "reel")
    val INSTAGRAM_REELS_KEYWORDS = setOf("reels", "reel", "clips")

    const val VIEW_ID_MIN_LENGTH = 3
    const val VIEW_ID_MAX_LENGTH = 100
    const val MAX_VIEW_IDS = 50

    const val LOG_TAG = "ThinkPadControl"
}