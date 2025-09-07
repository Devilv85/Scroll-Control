package com.vishal.thinkpadcontrol.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vishal.thinkpadcontrol.presentation.viewmodels.MainViewModel
import com.vishal.thinkpadcontrol.domain.model.UiState
import com.vishal.thinkpadcontrol.domain.model.ViewIdConfig
import com.vishal.thinkpadcontrol.utils.formatTime
import com.vishal.thinkpadcontrol.utils.openAccessibilitySettings
import com.vishal.thinkpadcontrol.utils.InputValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isServiceEnabled by viewModel.isServiceEnabled.collectAsStateWithLifecycle()
    val viewIdConfig by viewModel.viewIdConfig.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ThinkPad Control",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { openAccessibilitySettings(context) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Open Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ServiceStatusCard(
                isServiceEnabled = isServiceEnabled,
                onEnableService = {
                    openAccessibilitySettings(context)
                }
            )

            AppControlsCard(
                uiState = uiState,
                onToggleYoutube = viewModel::toggleYoutube,
                onToggleInstagram = viewModel::toggleInstagram,
                isServiceEnabled = isServiceEnabled
            )

            if (isServiceEnabled) {
                ViewIdManagementCard(
                    viewIdConfig = viewIdConfig,
                    onUpdateYouTubeIds = viewModel::updateYouTubeIds,
                    onUpdateInstagramIds = viewModel::updateInstagramIds,
                    onResetToDefaults = viewModel::resetViewIdsToDefaults
                )
            }

            AnimatedVisibility(
                visible = uiState.youtubeBlockedUntil > 0 || uiState.instagramBlockedUntil > 0,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                ActiveBlocksCard(uiState = uiState)
            }

            InterventionsCard(uiState = uiState)

            if (isServiceEnabled) {
                HowItWorksCard()
            }
        }
    }
}

@Composable
private fun ServiceStatusCard(
    isServiceEnabled: Boolean,
    onEnableService: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isServiceEnabled) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isServiceEnabled) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = if (isServiceEnabled) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isServiceEnabled) "🎉 Service Active!" else "⚠️ Service Inactive",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isServiceEnabled) 
                    "ThinkPad Control is actively monitoring your apps and helping you stay focused" 
                else 
                    "Enable the accessibility service to start blocking distracting content",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            if (!isServiceEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onEnableService,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enable Service")
                }
            }
        }
    }
}

@Composable
private fun AppControlsCard(
    uiState: UiState,
    onToggleYoutube: (Boolean) -> Unit,
    onToggleInstagram: (Boolean) -> Unit,
    isServiceEnabled: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📱 App Controls",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            AppControlRow(
                title = "YouTube Shorts",
                description = "Block short-form videos on YouTube",
                enabled = uiState.youtubeEnabled,
                onToggle = onToggleYoutube,
                isServiceEnabled = isServiceEnabled
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp
            )

            AppControlRow(
                title = "Instagram Reels",
                description = "Block reels and short videos on Instagram",
                enabled = uiState.instagramEnabled,
                onToggle = onToggleInstagram,
                isServiceEnabled = isServiceEnabled
            )
        }
    }
}

@Composable
private fun AppControlRow(
    title: String,
    description: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    isServiceEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            enabled = isServiceEnabled
        )
    }
}

@Composable
private fun ViewIdManagementCard(
    viewIdConfig: ViewIdConfig,
    onUpdateYouTubeIds: (List<String>) -> Unit,
    onUpdateInstagramIds: (List<String>) -> Unit,
    onResetToDefaults: () -> Unit
) {
    var showYouTubeEditor by remember { mutableStateOf(false) }
    var showInstagramEditor by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🔧 View ID Management",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Customize detection patterns for better accuracy",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showYouTubeEditor = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("YouTube IDs (${viewIdConfig.youtubeIds.size})")
                }

                Button(
                    onClick = { showInstagramEditor = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Instagram IDs (${viewIdConfig.instagramIds.size})")
                }
            }

            OutlinedButton(
                onClick = onResetToDefaults,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset to Defaults")
            }

            if (viewIdConfig.lastUpdated > 0) {
                Text(
                    text = "Last updated: ${java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(viewIdConfig.lastUpdated))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showYouTubeEditor) {
        ViewIdEditorDialog(
            title = "YouTube View IDs",
            currentIds = viewIdConfig.youtubeIds,
            onSave = { ids ->
                onUpdateYouTubeIds(ids)
                showYouTubeEditor = false
            },
            onDismiss = { showYouTubeEditor = false }
        )
    }

    if (showInstagramEditor) {
        ViewIdEditorDialog(
            title = "Instagram View IDs",
            currentIds = viewIdConfig.instagramIds,
            onSave = { ids ->
                onUpdateInstagramIds(ids)
                showInstagramEditor = false
            },
            onDismiss = { showInstagramEditor = false }
        )
    }
}

@Composable
private fun ViewIdEditorDialog(
    title: String,
    currentIds: List<String>,
    onSave: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var idsText by remember { mutableStateOf(currentIds.joinToString("\n")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Text(
                    text = "Enter one view ID per line:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = idsText,
                    onValueChange = { idsText = it },
                    modifier = Modifier
                        errorMessage = null
                        .fillMaxWidth()
                        .height(200.dp),
                    placeholder = { Text("view_id_1\nview_id_2\n...") },
                    isError = errorMessage != null
                )
                
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val ids = idsText.split("\n")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    
                    val validationResult = InputValidator.validateViewIdList(ids)
                    when (validationResult) {
                        is InputValidator.ValidationResult.Valid -> {
                            onSave(ids)
                        }
                        is InputValidator.ValidationResult.Invalid -> {
                            errorMessage = validationResult.message
                        }
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ActiveBlocksCard(
    uiState: UiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🚫 Active Blocks",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.youtubeBlockedUntil > 0) {
                BlockRow(
                    appName = "YouTube Shorts",
                    blockedUntil = uiState.youtubeBlockedUntil
                )
            }

            if (uiState.instagramBlockedUntil > 0) {
                BlockRow(
                    appName = "Instagram Reels",
                    blockedUntil = uiState.instagramBlockedUntil
                )
            }
        }
    }
}

@Composable
private fun BlockRow(
    appName: String,
    blockedUntil: Long
) {
    val remainingTime = maxOf(0, blockedUntil - System.currentTimeMillis())
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = appName,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Blocked for focus",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (remainingTime > 0) formatTime(remainingTime / 1000) else "Unblocked",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun InterventionsCard(
    uiState: UiState
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📊 Today's Focus Stats",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InterventionStatCard(
                    appName = "YouTube",
                    count = uiState.youtubeInterventions,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                InterventionStatCard(
                    appName = "Instagram",
                    count = uiState.instagramInterventions,
                    modifier = Modifier.weight(1f)
                )
            }
            
            val totalInterventions = uiState.youtubeInterventions + uiState.instagramInterventions
            if (totalInterventions > 0) {
                Text(
                    text = "🎯 Great job! You've redirected your attention $totalInterventions time${if (totalInterventions != 1) "s" else ""} today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun InterventionStatCard(
    appName: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = appName,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "interventions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HowItWorksCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "💡 How It Works",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            val steps = listOf(
                "🕐 5-minute grace period when you open distracting content",
                "💭 Motivational quote appears if you continue scrolling",
                "⏰ 60-minute soft block to help you refocus",
                "📈 Track your daily focus improvements"
            )
            
            steps.forEach { step ->
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}