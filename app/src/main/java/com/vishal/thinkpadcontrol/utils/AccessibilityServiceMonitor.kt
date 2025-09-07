package com.vishal.thinkpadcontrol.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessibilityServiceMonitor @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "AccessibilityServiceMonitor"
    }

    private val _isServiceEnabled = MutableStateFlow(checkServiceEnabled())
    val isServiceEnabled: Flow<Boolean> = _isServiceEnabled.asStateFlow()

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            val newState = checkServiceEnabled()
            if (_isServiceEnabled.value != newState) {
                _isServiceEnabled.value = newState
                Log.d(TAG, "Accessibility service state changed: $newState")
            }
        }
    }

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        try {
            val uri = Settings.Secure.getUriFor(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            context.contentResolver.registerContentObserver(uri, false, contentObserver)
            Log.d(TAG, "Started monitoring accessibility service state")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start monitoring accessibility service", e)
        }
    }

    private fun checkServiceEnabled(): Boolean {
        return try {
            val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            val packageName = context.packageName
            enabledServices.any { it.resolveInfo.serviceInfo.packageName == packageName }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking accessibility service state", e)
            false
        }
    }

    fun refreshState() {
        val newState = checkServiceEnabled()
        if (_isServiceEnabled.value != newState) {
            _isServiceEnabled.value = newState
        }
    }

    fun stopMonitoring() {
        try {
            context.contentResolver.unregisterContentObserver(contentObserver)
            Log.d(TAG, "Stopped monitoring accessibility service state")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping accessibility service monitoring", e)
        }
    }
}