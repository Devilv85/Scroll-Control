package com.vishal.thinkpadcontrol.utils

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import com.vishal.thinkpadcontrol.utils.Constants
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationController @Inject constructor() {
    companion object {
        private const val TAG = Constants.SERVICE_TAG
    }

    private var isNavigating = false

    suspend fun performSafeNavigation(service: AccessibilityService) {
        if (isNavigating) {
            Log.d(TAG, "Navigation already in progress, skipping")
            return
        }

        isNavigating = true
        try {
            Log.d(TAG, "Starting safe navigation sequence")
            
            // First, try a single back action
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            delay(Constants.BACK_ACTION_DELAY)
            
            // Additional back actions with controlled timing
            repeat(Constants.MAX_BACK_ATTEMPTS - 1) {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                delay(Constants.BACK_ACTION_DELAY)
            }
            
            // Wait before launching home intent
            delay(Constants.HOME_LAUNCH_DELAY)
            
            // Launch home intent as fallback
            launchHomeScreen(service)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during safe navigation", e)
        } finally {
            isNavigating = false
        }
    }

    private fun launchHomeScreen(service: AccessibilityService) {
        try {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or 
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            service.startActivity(homeIntent)
            Log.d(TAG, "Launched home screen")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch home screen", e)
            // Fallback to home action
            try {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
            } catch (homeException: Exception) {
                Log.e(TAG, "Failed to perform home action", homeException)
            }
        }
    }

    fun cleanup() {
        isNavigating = false
        Log.d(TAG, "NavigationController cleanup completed")
    }
}