package com.vishal.thinkpadcontrol.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings

fun openAccessibilitySettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to general settings if accessibility settings can't be opened
        try {
            val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
            fallbackIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(fallbackIntent)
        } catch (fallbackException: Exception) {
            // Log error but don't crash the app
        }
    }
}