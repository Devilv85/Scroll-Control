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

    // Service constants
    const val BACK_ACTION_DELAY = 300L
    const val MAX_BACK_ATTEMPTS = 3
    const val HOME_LAUNCH_DELAY = 500L
    
    // Logging tags
    const val LOG_TAG = "ThinkPadControl"
    const val SERVICE_TAG = "BlockerService"
    const val REPOSITORY_TAG = "Repository"
    const val FOCUS_MANAGER_TAG = "FocusManager"
    
    // Error messages
    const val ENCRYPTED_PREFS_ERROR = "Unable to create secure storage. Please ensure device security is properly configured."
    const val VIEW_ID_VALIDATION_ERROR = "View ID must be in format 'com.package:id/view_name' or 'view_name'"
}