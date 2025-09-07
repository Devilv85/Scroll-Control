# 🧠 ThinkPad Control - Your Digital Focus Companion

<div align="center">

![ThinkPad Control Logo](https://via.placeholder.com/200x200/5F85DB/FFFFFF?text=🧠)

**Take back control of your attention and boost your productivity!** 🚀

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)

</div>

---

## 🎯 What is ThinkPad Control?

ThinkPad Control is a smart Android app that helps you break free from the endless scroll of YouTube Shorts and Instagram Reels. Think of it as your personal digital wellness coach that gently redirects you when you're about to fall into the rabbit hole of short-form content.

Ever opened YouTube to watch a specific video and found yourself 2 hours deep in Shorts? Or checked Instagram for a quick message and got lost in Reels? **ThinkPad Control has your back!**

## ✨ Key Features

- **🕐 Smart Grace Period**: 5-minute window when you first open distracting content (because sometimes you genuinely need to check something!)
- **💭 Gentle Interventions**: Shows motivational quotes instead of harsh blocking - we believe in positive reinforcement
- **⏰ Soft Blocks**: 60-minute cooldown periods to help you refocus without being overly restrictive
- **📊 Progress Tracking**: See how many times you've successfully redirected your attention each day
- **🔒 Privacy First**: Everything stays on your device - no data collection, no tracking, no cloud storage
- **🎨 Modern Design**: Beautiful Material Design 3 interface that adapts to your system theme
- **⚙️ Customizable**: Advanced users can customize detection patterns for better accuracy
- **🔋 Battery Friendly**: Optimized for minimal battery usage with smart detection algorithms

## 📱 Screenshots

<div align="center">

| Main Dashboard | Service Setup | Active Blocks | Focus Stats |
|:-:|:-:|:-:|:-:|
| ![Dashboard](https://via.placeholder.com/200x400/E3F2FD/1976D2?text=Dashboard) | ![Setup](https://via.placeholder.com/200x400/F3E5F5/7B1FA2?text=Setup) | ![Blocks](https://via.placeholder.com/200x400/E8F5E8/388E3C?text=Blocks) | ![Stats](https://via.placeholder.com/200x400/FFF3E0/F57C00?text=Stats) |

*Screenshots showing the clean, intuitive interface*

</div>

## 🚀 Quick Start

### Prerequisites
- **Android 7.0+** (API level 24 or higher)
- **~10MB** of storage space
- **Accessibility service permission** (required for content detection)

### Installation

1. **Download the APK** from the [Releases](../../releases) section
2. **Enable Unknown Sources** in your Android settings if needed
3. **Install the APK** by tapping on it
4. **Open ThinkPad Control** from your app drawer

### Initial Setup

1. **Launch the app** - you'll see the main dashboard
2. **Tap "Enable Service"** - this opens Android's accessibility settings
3. **Find "ThinkPad Control"** in the accessibility services list
4. **Toggle it ON** and confirm the permission
5. **Return to the app** - you should see "🎉 Service Active!"
6. **Toggle the apps** you want to monitor (YouTube Shorts and/or Instagram Reels)
7. **Start browsing** with confidence! 🎯

## 🛡️ Privacy & Security

We take your privacy seriously. Here's our commitment:

- **🔒 100% Offline**: No internet connection required for core functionality
- **📵 Zero Data Collection**: We don't collect, store, or transmit any personal data
- **🔐 Encrypted Storage**: Your preferences are securely encrypted on your device
- **👁️ Limited Scope**: Only monitors YouTube and Instagram for specific content types
- **🔓 Open Source**: Full transparency with GPL-3 license - you can review every line of code
- **⚡ Minimal Permissions**: Only uses accessibility service for content detection

### Accessibility Service Usage

This app requires accessibility service permissions to detect when you're viewing short-form content. **We promise:**

- Only monitors YouTube and Instagram for specific content types
- Never stores, transmits, or analyzes your personal data
- Can be disabled at any time through system settings
- Limited scope - only detects short-form video content patterns

## 🔧 How It Works

ThinkPad Control uses a smart, multi-stage approach to help you maintain focus:

### 🕐 **Stage 1: Grace Period (5 minutes)**
When you open YouTube or Instagram, you get a 5-minute grace period to browse normally. This accounts for legitimate usage like checking notifications or watching specific content.

### 💭 **Stage 2: Gentle Intervention**
After the grace period, if you're still in the "scroll zone" (Shorts/Reels), the app shows a motivational quote and gently navigates you away from the distracting content.

### ⏰ **Stage 3: Soft Block (60 minutes)**
A 60-minute cooldown period begins where the app will immediately redirect you if you try to access the blocked content again. This gives your brain time to refocus.

### 📊 **Stage 4: Progress Tracking**
Every successful intervention is counted as a "focus win" and displayed in your daily statistics, helping you see your progress over time.

## 🎨 Technical Highlights

### Modern Android Architecture
- **🏗️ MVVM Pattern**: Clean separation of concerns with ViewModels and repositories
- **🔄 Reactive Programming**: Kotlin Coroutines and Flow for smooth, responsive UI
- **💉 Dependency Injection**: Hilt for robust, testable architecture
- **🎨 Jetpack Compose**: Modern, declarative UI framework
- **🗃️ Encrypted Storage**: Secure data persistence with EncryptedSharedPreferences

### Performance & Compatibility
- **📱 Wide Compatibility**: Supports Android 7.0 through Android 15
- **🔋 Battery Optimized**: Smart detection algorithms minimize battery usage
- **🚀 Smooth Performance**: Optimized for 60fps animations and transitions
- **📐 Responsive Design**: Adapts beautifully to phones, tablets, and foldables
- **🌙 Theme Support**: Automatic dark/light theme switching

### Code Quality
- **✅ Type Safety**: 100% Kotlin with strong typing throughout
- **🧪 Error Handling**: Comprehensive error handling and recovery
- **📝 Documentation**: Well-documented code with clear comments
- **🔍 Static Analysis**: ProGuard optimization for release builds
- **🛡️ Security**: Secure coding practices and encrypted data storage

## 📁 Project Structure

```
app/src/main/java/com/vishal/thinkpadcontrol/
├── 📱 MainActivity.kt                    # Main entry point
├── 🏗️ ThinkPadControlApplication.kt     # Application class with Hilt setup
├── 
├── 🎨 presentation/
│   ├── MainScreen.kt                     # Main UI composable
│   └── viewmodels/
│       └── MainViewModel.kt              # Main screen view model
├── 
├── 🗃️ data/
│   └── repository/                       # Data layer
│       ├── SettingsRepository.kt         # App settings management
│       ├── BlockedUntilRepository.kt     # Block timing management
│       ├── QuoteRepository.kt            # Motivational quotes
│       └── ViewIdRepository.kt           # UI element detection patterns
├── 
├── 🏢 domain/
│   └── model/                           # Domain models
│       ├── UiState.kt                   # UI state representation
│       └── ViewIdConfig.kt              # Detection configuration
├── 
├── 🔧 services/
│   └── BlockerService.kt                # Accessibility service
├── 
├── 🛠️ utils/                            # Utility classes
│   ├── Constants.kt                     # App constants
│   ├── ErrorHandler.kt                  # Centralized error handling
│   ├── NotificationHelper.kt            # Notification management
│   ├── NavigationController.kt          # Safe navigation logic
│   ├── AccessibilityServiceMonitor.kt   # Service state monitoring
│   ├── ViewIdCache.kt                   # Detection pattern caching
│   └── PermissionManager.kt             # Permission utilities
├── 
└── 💉 di/
    └── AppModule.kt                     # Dependency injection setup
```

## 🔧 Building from Source

### Prerequisites
- **Android Studio** Arctic Fox or newer
- **JDK 11** or higher
- **Android SDK** with API 35
- **Git** for version control

### Build Steps

```bash
# Clone the repository
git clone https://github.com/yourusername/thinkpad-control.git
cd thinkpad-control

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Install on connected device
./gradlew installDebug
```

### Build Variants
- **Debug**: Development build with logging and debugging enabled
- **Release**: Optimized production build with ProGuard obfuscation

## 🤝 Contributing

We welcome contributions from the community! Whether you're fixing bugs, improving documentation, or adding new features, your help is appreciated.

### How to Contribute

1. **🍴 Fork** the repository
2. **🌿 Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **💻 Make** your changes
4. **✅ Test** thoroughly on different Android versions
5. **📝 Commit** your changes (`git commit -m 'Add amazing feature'`)
6. **🚀 Push** to the branch (`git push origin feature/amazing-feature`)
7. **🔄 Open** a Pull Request

### Contribution Guidelines

- **📋 Follow** existing code style and conventions
- **🧪 Add tests** for new functionality
- **📖 Update documentation** as needed
- **🔍 Test** on multiple Android versions (minimum API 24)
- **💬 Be respectful** and constructive in discussions

### Areas Where We Need Help

- **🌍 Translations**: Help us support more languages
- **🧪 Testing**: Test on different devices and Android versions
- **📖 Documentation**: Improve guides and code comments
- **🎨 UI/UX**: Design improvements and accessibility enhancements
- **🔧 Features**: New ideas for digital wellness features

## 🐛 Bug Reports & Feature Requests

Found a bug or have a great idea? We'd love to hear from you!

### 🐛 Reporting Bugs

When reporting bugs, please include:
- **📱 Device model** and Android version
- **📋 Steps to reproduce** the issue
- **📸 Screenshots** if applicable
- **📝 Expected vs actual behavior**
- **🔍 Any error messages** you see

### 💡 Feature Requests

For feature requests, please describe:
- **🎯 The problem** you're trying to solve
- **💭 Your proposed solution**
- **🔄 Alternative solutions** you've considered
- **📊 How it would benefit** other users

## 📄 License

This project is licensed under the **GNU General Public License v3.0** - see the [LICENSE](LICENSE) file for details.

### What this means:
- ✅ **Use** the app freely for personal or commercial purposes
- ✅ **Modify** the source code to suit your needs
- ✅ **Distribute** copies of the app
- ✅ **Contribute** improvements back to the community
- ❗ **Share alike** - any modifications must also be open source
- ❗ **Include license** - keep the GPL license in any distributions

## 🆘 Support & FAQ

### 🙋‍♀️ Frequently Asked Questions

**Q: Is this app safe to use?**
A: Absolutely! The app is open source, uses minimal permissions, and keeps all data on your device. No personal information is collected or transmitted.

**Q: Will it drain my battery?**
A: No! The app is optimized for minimal battery usage and only activates when you're using YouTube or Instagram.

**Q: Can I customize the blocking duration?**
A: Currently, it's set to 60 minutes, but we're working on customization options in future updates.

**Q: Does it work with other apps?**
A: Right now it focuses on YouTube Shorts and Instagram Reels, but we're considering expanding to other platforms based on user feedback.

**Q: What if I need to access blocked content for work?**
A: You can temporarily disable the service through Android's accessibility settings, or toggle off specific apps in the ThinkPad Control settings.

**Q: How accurate is the detection?**
A: The app uses advanced pattern recognition to detect short-form content with high accuracy. You can also customize detection patterns in the advanced settings.

### 🆘 Getting Help

- **📧 GitHub Issues**: [Open an issue](../../issues) for bugs or feature requests
- **💬 Discussions**: [Join the discussion](../../discussions) for questions and ideas
- **📖 Documentation**: Check this README and code comments
- **🔍 Search**: Look through existing issues before creating new ones

### 🏥 Troubleshooting

**Service not working?**
1. Check that accessibility service is enabled in Android Settings
2. Restart the app after enabling the service
3. Ensure you're using supported app versions (latest YouTube/Instagram)

**Detection not accurate?**
1. Try updating the view ID patterns in advanced settings
2. Report the issue with your device model and app versions
3. Check if the apps have been recently updated

**App crashes?**
1. Clear the app's cache and data
2. Restart your device
3. Report the crash with device details

## 🌟 Acknowledgments

### 🙏 Special Thanks

- **Android Team** for the excellent accessibility APIs
- **Jetpack Compose Team** for the modern UI framework
- **Material Design Team** for the beautiful design system
- **Open Source Community** for inspiration and best practices
- **Beta Testers** who helped refine the app experience

### 🔧 Built With

- **[Kotlin](https://kotlinlang.org/)** - Modern programming language for Android
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Modern UI toolkit
- **[Hilt](https://dagger.dev/hilt/)** - Dependency injection framework
- **[Material Design 3](https://m3.material.io/)** - Design system and components
- **[Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** - Asynchronous programming
- **[EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)** - Secure data storage

---

<div align="center">

**Ready to take control of your digital attention?** 

Download ThinkPad Control today and start building healthier tech habits! 🎯

*Made with ❤️ for digital wellness and productivity*

**[⬇️ Download Latest Release](../../releases/latest)** | **[🐛 Report Issues](../../issues)** | **[💬 Join Discussion](../../discussions)**

</div>

---

<div align="center">
<sub>ThinkPad Control - Empowering mindful technology use, one scroll at a time.</sub>
</div>