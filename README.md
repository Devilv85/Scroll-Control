# ThinkPad Control

ThinkPad Control is an Android accessibility service application designed to help users maintain focus by detecting and redirecting attention away from short-form video content on YouTube Shorts and Instagram Reels. The app implements a grace period system with motivational interventions and temporary blocking to encourage mindful usage patterns.

## Features

- **Smart Content Detection**: Monitors YouTube and Instagram for short-form video content using accessibility service APIs with fallback text-based detection
- **Grace Period System**: 5-minute initial grace period before intervention
- **Gentle Interventions**: Shows motivational quotes instead of harsh blocking
- **Temporary Blocking**: 60-minute cooldown periods after interventions
- **Usage Statistics**: Daily intervention tracking for both platforms
- **Customizable Detection**: Advanced users can modify view ID patterns for better accuracy with input validation
- **Privacy-First Design**: All data stored locally with encrypted preferences and secure fallback handling
- **Material Design 3**: Modern UI with dynamic theming support and accessibility compliance
- **Battery Optimized**: Minimal resource usage with optimized detection algorithms and proper memory management

## How It Works

The app implements a multi-stage intervention system:

1. **Detection Phase**: Uses AccessibilityService to monitor YouTube and Instagram for specific UI elements and text content that indicate short-form videos
2. **Grace Period**: Allows 5 minutes of normal usage when apps are first opened
3. **Intervention**: After grace period, displays motivational quotes and navigates away from distracting content
4. **Soft Block**: Implements 60-minute cooldown where immediate redirection occurs
5. **Statistics**: Tracks successful interventions as daily focus metrics

The detection system uses configurable view ID patterns stored in encrypted preferences with fallback text-based detection for improved robustness.

## Requirements

- **Android**: API 24+ (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35
- **JDK**: 11 or higher
- **Android Studio**: Arctic Fox or newer
- **Gradle**: 8.13
- **AGP**: 8.13.0

## Installation

### Android Studio
1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run on device or emulator

### Command Line
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install debug APK
./gradlew installDebug

# Run tests
./gradlew test
```

**APK Output Paths:**
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## Permissions

The app requires the following permissions:

- `android.permission.FOREGROUND_SERVICE` - Run accessibility service in background
- `android.permission.FOREGROUND_SERVICE_SPECIAL_USE` - Special use foreground service for accessibility
- `android.permission.POST_NOTIFICATIONS` - Show focus notifications (Android 13+)
- `android.permission.WAKE_LOCK` - Maintain service during interventions
- `android.permission.BIND_ACCESSIBILITY_SERVICE` - Core accessibility service functionality

### Accessibility Service Configuration
- **Event Types**: Window state changes, view focus, view scrolling
- **Packages**: `com.google.android.youtube`, `com.instagram.android`
- **Capabilities**: Retrieve window content, filter key events
- **Feedback Type**: Generic feedback

## Privacy & Security

ThinkPad Control prioritizes user privacy and data security:

- **Encrypted Local Storage**: All data stored on device using EncryptedSharedPreferences with secure fallback handling
- **No Network Communication**: App functions entirely offline
- **Limited Scope**: Only monitors YouTube and Instagram for specific content types
- **No Data Collection**: No analytics, tracking, or data transmission
- **Accessibility Service**: Used solely for detecting short-form video content patterns
- **Input Validation**: All user inputs are validated to prevent security issues
- **Memory Management**: Proper resource cleanup and memory leak prevention

The accessibility service monitors only the specified apps for predefined UI elements and text patterns, and does not access, store, or transmit personal content.

## Architecture

The app follows modern Android development best practices:

- **MVVM Architecture**: Clean separation of concerns with ViewModels and repositories
- **Dependency Injection**: Hilt for dependency management
- **Jetpack Compose**: Modern declarative UI framework
- **Coroutines**: Asynchronous programming with proper error handling
- **Result Wrappers**: Consistent error handling using Kotlin Result types
- **Repository Pattern**: Data layer abstraction with encrypted storage
- **Use Cases**: Business logic encapsulation for complex operations

## Troubleshooting

### Accessibility Service Setup
1. Open Android Settings → Accessibility
2. Find "ThinkPad Control" in services list
3. Toggle ON and confirm permissions
4. Return to app to verify "Service Active" status

### Common Issues
- **Service Not Working**: Ensure accessibility service is enabled and app has necessary permissions
- **Battery Optimization**: Add app to battery optimization whitelist for consistent operation
- **Detection Issues**: Update view ID patterns in advanced settings if apps have been updated
- **Notifications**: Grant notification permission on Android 13+ for status updates
- **Encryption Errors**: Ensure device has proper security configuration (screen lock, etc.)

## Known Limitations

- Detection patterns may need updates when YouTube/Instagram modify their UI structure
- Accessibility service requires manual enablement through system settings
- Grace period and block duration are currently fixed (5 minutes and 60 minutes respectively)
- Limited to YouTube Shorts and Instagram Reels detection only
- Requires Android 7.0+ due to accessibility service requirements
- Encrypted storage requires device security features to be enabled

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Follow existing code style and architecture patterns
4. Add tests for new functionality
5. Update documentation as needed
6. Submit a pull request

For major changes, please open an issue first to discuss the proposed changes.

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

The GPL-3.0 license ensures the software remains free and open source, requiring any modifications or distributions to also be open source under the same license terms.