package com.vishal.thinkpadcontrol.services

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.vishal.thinkpadcontrol.data.repository.BlockedUntilRepository
import com.vishal.thinkpadcontrol.data.repository.SettingsRepository
import com.vishal.thinkpadcontrol.data.repository.ViewIdRepository
import com.vishal.thinkpadcontrol.domain.usecase.FocusManager
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

    @Inject
    lateinit var focusManager: FocusManager

    private var serviceScope: CoroutineScope? = null
    private val graceTimers = mutableMapOf<String, Job>()
    private var notificationHelper: NotificationHelper? = null

    companion object {
        private const val TAG = Constants.SERVICE_TAG
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
            restoreActiveBlocks()
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

    private fun restoreActiveBlocks() {
        serviceScope?.launch {
            try {
                focusManager.checkAndEnforceExistingBlocks(Constants.YOUTUBE_PACKAGE)
                focusManager.checkAndEnforceExistingBlocks(Constants.INSTAGRAM_PACKAGE)
            } catch (e: Exception) {
                Log.e(TAG, "Error restoring active blocks", e)
            }
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

                val isBlocked = focusManager.isCurrentlyBlocked(Constants.YOUTUBE_PACKAGE).getOrElse { false }
                if (isBlocked) {
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

                val isBlocked = focusManager.isCurrentlyBlocked(Constants.INSTAGRAM_PACKAGE).getOrElse { false }
                if (isBlocked) {
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
        val rootNode = rootInActiveWindow ?: return false
        return try {
            val viewIds = viewIdCache.getYouTubeIds()
            detectContent(rootNode, viewIds, Constants.YOUTUBE_SHORTS_KEYWORDS)
        } catch (e: Exception) {
            Log.e(TAG, "Error detecting YouTube Shorts", e)
            false
        } finally {
            rootNode.recycle()
        }
    }

    private fun detectInstagramReels(): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        return try {
            val viewIds = viewIdCache.getInstagramIds()
            detectContent(rootNode, viewIds, Constants.INSTAGRAM_REELS_KEYWORDS)
        } catch (e: Exception) {
            Log.e(TAG, "Error detecting Instagram Reels", e)
            false
        } finally {
            rootNode.recycle()
        }
    }

    private fun detectContent(rootNode: AccessibilityNodeInfo, viewIds: Set<String>, keywords: Set<String>): Boolean {
        return detectViewIds(rootNode, viewIds) || detectByText(rootNode, keywords)
    }

    private fun detectViewIds(rootNode: AccessibilityNodeInfo, viewIds: Set<String>): Boolean {
        // Single-pass tree traversal for efficiency
        return traverseNodeTreeForViewIds(rootNode, viewIds)
    }
    
    private fun traverseNodeTreeForViewIds(node: AccessibilityNodeInfo, viewIds: Set<String>): Boolean {
        try {
            // Check current node's view ID
            val nodeViewId = node.viewIdResourceName
            if (nodeViewId != null && viewIds.contains(nodeViewId)) {
                return true
            }
            
            // Traverse children
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    try {
                        if (traverseNodeTreeForViewIds(child, viewIds)) {
                            return true
                        }
                    } finally {
                        child.recycle()
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error traversing node tree for view IDs", e)
        }
        return false
    }

    private fun detectViewIdsOld(rootNode: AccessibilityNodeInfo, viewIds: Set<String>): Boolean {
        return viewIds.any { viewId ->
            try {
                val nodes = rootNode.findAccessibilityNodeInfosByViewId(viewId)
                val found = nodes?.isNotEmpty() == true
                nodes?.forEach { node ->
                    try {
                        node.recycle()
                    } catch (e: Exception) {
                        Log.w(TAG, "Error recycling node", e)
                    }
                }
                found
            } catch (e: Exception) {
                Log.w(TAG, "Error checking view ID: $viewId", e)
                false
            }
        }
    }

    private fun detectByText(rootNode: AccessibilityNodeInfo, keywords: Set<String>): Boolean {
        return traverseNodeTree(rootNode) { node ->
            val text = node.text?.toString()?.lowercase() ?: ""
            val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""
            keywords.any { keyword ->
                text.contains(keyword) || contentDesc.contains(keyword)
            }
        }
    }

    private fun traverseNodeTree(node: AccessibilityNodeInfo, predicate: (AccessibilityNodeInfo) -> Boolean): Boolean {
        try {
            if (predicate(node)) {
                return true
            }

            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    try {
                        if (traverseNodeTree(child, predicate)) {
                            return true
                        }
                    } finally {
                        child.recycle()
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error traversing node tree", e)
        }
        return false
    }

    private fun startGraceTimer(packageName: String) {
        serviceScope?.launch {
            // Cancel existing timer for this package
            graceTimers[packageName]?.cancel()

            val timer = launch {
                try {
                    delay(Constants.GRACE_PERIOD_MS)
                    showInterventionDialog(packageName)
                } catch (e: CancellationException) {
                    Log.d(TAG, "Grace timer cancelled for $packageName")
                } catch (e: Exception) {
                    Log.e(TAG, "Error in grace timer for $packageName", e)
                }
            }

            graceTimers[packageName] = timer
            Log.d(TAG, "Started grace timer for $packageName")
        }
    }

    private suspend fun showInterventionDialog(packageName: String) {
        try {
            Log.d(TAG, "Showing intervention for $packageName")
            
            val result = focusManager.performIntervention(packageName)
            result.fold(
                onSuccess = {
                    navigationController.performSafeNavigation(this)
                    Log.d(TAG, "Intervention successful for $packageName")
                },
                onFailure = { error ->
                    Log.e(TAG, "Intervention failed for $packageName", error)
                }
            )
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
            // Cancel all grace timers
            graceTimers.values.forEach { job ->
                try {
                    job.cancel()
                } catch (e: Exception) {
                    Log.w(TAG, "Error cancelling grace timer", e)
                }
            }
            graceTimers.clear()
            
            // Cancel service scope
            try {
                serviceScope?.cancel()
                serviceScope = null
            } catch (e: Exception) {
                Log.w(TAG, "Error cancelling service scope", e)
            }
            
            // Cleanup components
            try {
                navigationController.cleanup()
            } catch (e: Exception) {
                Log.w(TAG, "Error cleaning up navigation controller", e)
            }
            
            try {
                viewIdCache.clearCache()
            } catch (e: Exception) {
                Log.w(TAG, "Error clearing view ID cache", e)
            }
            
            // Stop foreground service
            try {
                stopForeground(Service.STOP_FOREGROUND_REMOVE)
            } catch (e: Exception) {
                Log.w(TAG, "Error stopping foreground service", e)
            }
            
            Log.d(TAG, "Service cleanup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup", e)
        } finally {
            // Ensure cleanup state is reset
            serviceScope = null
        }
    }
}