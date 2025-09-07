package com.vishal.thinkpadcontrol.utils

import android.util.Log

/**
 * Centralized error handling utility for the application
 */
object ErrorHandler {
    private const val TAG = "ThinkPadControl"

    /**
     * Handle and log errors with context
     */
    fun handleError(context: String, error: Throwable, showToUser: Boolean = false) {
        Log.e(TAG, "Error in $context: ${error.message}", error)
        
        // In a production app, you might want to:
        // - Send crash reports to analytics
        // - Show user-friendly error messages
        // - Implement retry mechanisms
        
        if (showToUser) {
            // Could show a toast or snackbar here
            Log.w(TAG, "User-facing error in $context: ${error.message}")
        }
    }

    /**
     * Handle non-critical warnings
     */
    fun handleWarning(context: String, message: String) {
        Log.w(TAG, "Warning in $context: $message")
    }

    /**
     * Log debug information
     */
    fun logDebug(context: String, message: String) {
        Log.d(TAG, "$context: $message")
    }
}