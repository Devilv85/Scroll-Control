# ThinkPad Control

A digital wellness Android application that helps users maintain focus by intelligently detecting and redirecting attention away from short-form video content on YouTube Shorts and Instagram Reels. The app uses Android's Accessibility Service to implement a grace period system with motivational interventions and temporary blocking to encourage mindful usage patterns.

## Features

### Core Functionality
- **Smart Content Detection**: Monitors YouTube and Instagram for short-form video content using accessibility service APIs with fallback text-based detection
- **Grace Period System**: 5-minute initial grace period before intervention to allow intentional usage
- **Gentle Interventions**: Shows motivational quotes instead of harsh blocking to encourage reflection
- **Temporary Blocking**: 60-minute cooldown periods after interventions to promote focus
- **Usage Statistics**: Daily intervention tracking for both platforms with visual progress indicators

### Advanced Features
- **Customizable Detection**: Advanced users can modify view ID patterns for better accuracy with input validation
- **Privacy-First Design**: All data stored locally with encrypted preferences and secure fallback handling
- **Material Design 3**: Modern UI with dynamic theming support and comprehensive accessibility compliance
- **Battery Optimized**: Minimal resource usage with optimized detection algorithms and proper memory management

## Technical Architecture

### Modern Android Stack
- **Language**: Kotlin with coroutines for asynchronous operations
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM pattern with clean architecture principles
- **Dependency Injection**: Hilt for dependency management
- **Data Layer**: Encrypted SharedPreferences with Repository pattern
- **Background Processing**: Accessibility Service with foreground service implementation

### Security & Privacy
- **Encrypted Local Storage**: All data stored on device using EncryptedSharedPreferences
- **No Network Communication**: App functions entirely offline
- **Limited Scope**: Only monitors YouTube and Instagram for specific content types
- **No Data Collection**: No analytics, tracking, or data transmission
- **Input Validation**: All user inputs validated to prevent security issues
- **Memory Management**: Proper resource cleanup and memory leak prevention

## Requirements

### System Requirements
- **Android**: API 24+ (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35
- **Storage**: ~10MB

### Development Requirements
- **JDK**: 17
- **Android Studio**: Flamingo or newer
- **Gradle**: 9.0
- **AGP**: 8.4.1
- **Kotlin**: 1.9.22

## Installation

### From Source
1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run on device or emulator

### Build Commands
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

## Setup & Configuration

### Required Permissions
The app requires the following permissions:

- `FOREGROUND_SERVICE` - Run accessibility service in background
- `FOREGROUND_SERVICE_SPECIAL_USE` - Special use foreground service for accessibility
- `POST_NOTIFICATIONS` - Show focus notifications (Android 13+)
- `WAKE_LOCK` - Maintain service during interventions
- `BIND_ACCESSIBILITY_SERVICE` - Core accessibility service functionality

### Accessibility Service Setup
**Critical**: The app requires manual enablement of the accessibility service:

1. Open Android Settings → Accessibility
2. Find "ThinkPad Control" in services list
3. Toggle ON and confirm permissions
4. Return to app to verify "Service Active" status

### Accessibility Service Configuration
- **Event Types**: Window state changes, view focus, view scrolling
- **Packages**: `com.google.android.youtube`, `com.instagram.android`
- **Capabilities**: Retrieve window content, filter key events
- **Feedback Type**: Generic feedback

## How It Works

The app implements a multi-stage intervention system:

### Detection Phase
Uses AccessibilityService to monitor YouTube and Instagram for specific UI elements and text content that indicate short-form videos. The detection system uses configurable view ID patterns stored in encrypted preferences with fallback text-based detection for improved robustness.

### Intervention Flow
1. **Grace Period**: Allows 5 minutes of normal usage when apps are first opened
2. **Intervention**: After grace period, displays motivational quotes and navigates away from distracting content
3. **Soft Block**: Implements 60-minute cooldown where immediate redirection occurs
4. **Statistics**: Tracks successful interventions as daily focus metrics

### Advanced Customization
Users can modify view ID patterns in the advanced settings for better detection accuracy when apps update their UI structure.

## Troubleshooting

### Common Issues

**Accessibility Service Not Working**
- Ensure accessibility service is enabled in Android Settings
- Verify app has necessary permissions
- Restart the app after enabling the service

**Battery Optimization**
- Add app to battery optimization whitelist
- Ensure "Don't optimize" is selected for consistent operation

**Detection Issues**
- Update view ID patterns in advanced settings if apps have been updated
- Check that both YouTube and Instagram monitoring are enabled
- Verify apps are the official versions from Google Play Store

**Notifications Not Showing**
- Grant notification permission on Android 13+
- Check notification settings for the app
- Ensure "Do Not Disturb" is not blocking notifications

**Encryption Errors**
- Ensure device has proper security configuration (screen lock, etc.)
- Device must support hardware-backed keystore
- Clear app data if encryption keys become corrupted

## Known Limitations

- Detection patterns may need updates when YouTube/Instagram modify their UI structure
- Accessibility service requires manual enablement through system settings
- Grace period and block duration are currently fixed (5 minutes and 60 minutes respectively)
- Limited to YouTube Shorts and Instagram Reels detection only
- Requires Android 7.0+ due to accessibility service requirements
- Encrypted storage requires device security features to be enabled

## Privacy & Data Handling

### Data Storage
- All data stored locally on device using Android's EncryptedSharedPreferences
- No cloud storage or data synchronization
- Data includes: app settings, intervention counts, temporary block timestamps, custom view ID patterns

### Data Access
- App only accesses UI elements of YouTube and Instagram
- No personal content, messages, or user data is read or stored
- Accessibility service used solely for detecting specific UI patterns

### Data Retention
- Intervention statistics reset daily
- Block timestamps automatically expire
- Settings persist until manually changed or app uninstalled

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

## Support

For support, bug reports, or feature requests:
- Open an issue on GitHub
- Provide device model, Android version, and steps to reproduce
- Include screenshots or screen recordings when helpful

## Acknowledgments

Built with modern Android development tools and libraries:
- Jetpack Compose for UI
- Hilt for dependency injection
- Kotlin Coroutines for asynchronous operations
- Material Design 3 for theming
- Android Accessibility Services for content detection