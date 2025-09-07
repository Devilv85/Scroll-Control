package com.vishal.thinkpadcontrol.services

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.vishal.thinkpadcontrol.data.repository.BlockedUntilRepository
import com.vishal.thinkpadcontrol.data.repository.SettingsRepository
import com.vishal.thinkpadcontrol.data.repository.ViewIdRepository
import com.vishal.thinkpadcontrol.utils.Constants
import com.vishal.thinkpadcontrol.utils.NavigationController
import com.vishal.thinkpadcontrol.utils.NotificationHelper
import com.vishal.thinkpadcontrol.utils.ViewIdCache
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class BlockerService : AccessibilityService() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var blockedUntilRepository: BlockedUntilRepository

    @Inject
    lateinit var viewIdRepository: ViewIdRepository

    @Inject
    lateinit var viewIdCache: ViewIdCache

    @Inject
    lateinit var navigationController: NavigationController

    private var serviceScope: CoroutineScope? = null
    private val graceTimers = mutableMapOf<String, Runnable>()
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var notificationHelper: NotificationHelper? = null

    companion object {
        private const val TAG = "BlockerService"
        private val MONITORED_EVENT_TYPES = intArrayOf(
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_VIEW_SCROLLED,
            AccessibilityEvent.TYPE_VIEW_FOCUSED
        )
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Service connected")
        
        serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        
        try {
            notificationHelper = NotificationHelper(this)
            startForeground(Constants.NOTIFICATION_ID, notificationHelper!!.buildNotification())
            
            setupViewIdCacheUpdates()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting foreground service", e)
        }
    }

    private fun setupViewIdCacheUpdates() {
        serviceScope?.let { scope ->
            viewIdRepository.viewIdConfig
                .onEach { config ->
                    viewIdCache.updateCache(config)
                }
                .launchIn(scope)
        }
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let { accessibilityEvent ->
            if (!isEventTypeMonitored(accessibilityEvent.eventType)) {
                return
            }
            
            try {
                handleAccessibilityEvent(accessibilityEvent)
            } catch (e: Exception) {
                Log.e(TAG, "Error handling accessibility event", e)
            }
        }
    }

    private fun isEventTypeMonitored(eventType: Int): Boolean {
        return MONITORED_EVENT_TYPES.contains(eventType)
    }
    private fun handleAccessibilityEvent(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return

        when (packageName) {
            Constants.YOUTUBE_PACKAGE -> handleYouTubeEvent(event)
            Constants.INSTAGRAM_PACKAGE -> handleInstagramEvent(event)
        }
    }

    private fun handleYouTubeEvent(event: AccessibilityEvent) {
        serviceScope?.launch {
            try {
                if (!settingsRepository.youtubeEnabled.first()) return@launch

                val blockedUntil = blockedUntilRepository.youtubeBlockedUntil.first()
                if (System.currentTimeMillis() < blockedUntil) {
                    navigationController.performSafeNavigation(this@BlockerService)
                    return@launch
                }

                if (detectYouTubeShorts()) {
                    startGraceTimer(Constants.YOUTUBE_PACKAGE)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling YouTube event", e)
            }
        }
    }

    private fun handleInstagramEvent(event: AccessibilityEvent) {
        serviceScope?.launch {
            try {
                if (!settingsRepository.instagramEnabled.first()) return@launch

                val blockedUntil = blockedUntilRepository.instagramBlockedUntil.first()
                if (System.currentTimeMillis() < blockedUntil) {
                    navigationController.performSafeNavigation(this@BlockerService)
                    return@launch
                }

                if (detectInstagramReels()) {
                    startGraceTimer(Constants.INSTAGRAM_PACKAGE)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling Instagram event", e)
            }
        }
    }

    private fun detectYouTubeShorts(): Boolean {
        return try {
            val rootNode = rootInActiveWindow ?: return false
            val viewIds = viewIdCache.getYouTubeIds()
            
            detectViewIds(rootNode, viewIds)
        } catch (e: Exception) {
            Log.e(TAG, "Error detecting YouTube Shorts", e)
            false
        }
    }

    private fun detectInstagramReels(): Boolean {
        return try {
            val rootNode = rootInActiveWindow ?: return false
            val viewIds = viewIdCache.getInstagramIds()
            
            detectViewIds(rootNode, viewIds)
        } catch (e: Exception) {
            Log.e(TAG, "Error detecting Instagram Reels", e)
            false
        }
    }

    private fun detectViewIds(rootNode: AccessibilityNodeInfo, viewIds: Set<String>): Boolean {
        return viewIds.any { viewId ->
            try {
                rootNode.findAccessibilityNodeInfosByViewId(viewId)?.isNotEmpty() == true
            } catch (e: Exception) {
                Log.w(TAG, "Error checking view ID: $viewId", e)
                false
            }
        }
    }
    private fun startGraceTimer(packageName: String) {
        // Cancel existing timer for this package
        graceTimers[packageName]?.let { 
            if (it is Job) {
                it.cancel()
            }
        }

        val timer = Runnable {
            serviceScope?.launch {
                try {
                    showInterventionDialog(packageName)
                } catch (e: Exception) {
                    Log.e(TAG, "Error showing intervention for $packageName", e)
                }
            }
        }

        handler.postDelayed(timer, Constants.GRACE_PERIOD_MS)
        graceTimers[packageName] = timer
        
        Log.d(TAG, "Started grace timer for $packageName")
    }

    private suspend fun showInterventionDialog(packageName: String) {
        try {
            Log.d(TAG, "Showing intervention for $packageName")
            
            navigationController.performSafeNavigation(this)
            
            val blockUntil = System.currentTimeMillis() + Constants.BLOCK_DURATION_MS
            when (packageName) {
                Constants.YOUTUBE_PACKAGE -> {
                    blockedUntilRepository.setYoutubeBlockedUntil(blockUntil)
                    settingsRepository.incrementYoutubeInterventions()
                }
                Constants.INSTAGRAM_PACKAGE -> {
                    blockedUntilRepository.setInstagramBlockedUntil(blockUntil)
                    settingsRepository.incrementInstagramInterventions()
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error showing intervention", e)
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
        cleanup()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        cleanup()
    }

    private fun cleanup() {
        try {
            graceTimers.values.forEach { 
                if (it is Runnable) {
                    handler.removeCallbacks(it)
                }
            }
            graceTimers.clear()
            
            serviceScope?.cancel()
            serviceScope = null
            
            navigationController.cleanup()
            viewIdCache.clearCache()
            
            stopForeground(Service.STOP_FOREGROUND_REMOVE)