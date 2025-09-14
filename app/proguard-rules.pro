# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Java 21 Compatibility Rules
-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn java.lang.invoke.MethodHandles$Lookup
-keep class java.lang.invoke.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ApplicationComponentManager { *; }
-keep class **_HiltModules* { *; }
-keep class **_Provide* { *; }
-keep class **_Factory* { *; }

# Keep Room entities and DAOs
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep application classes
-keep class com.vishal.thinkpadcontrol.data.** { *; }
-keep class com.vishal.thinkpadcontrol.domain.** { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep enum classes
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Keep Gson classes
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep accessibility service
-keep class com.vishal.thinkpadcontrol.services.BlockerService { *; }

# Security crypto
-keep class androidx.security.crypto.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Java 21 Language Features
-keep class java.lang.** { *; }
-keep class java.util.** { *; }
-dontwarn java.lang.reflect.**
-dontwarn java.util.concurrent.**

# Desugaring Support
-keep class j$.** { *; }
-dontwarn j$.**

# Result class optimization
-keep class kotlin.Result { *; }
-keep class kotlin.ResultKt { *; }

# Remove warnings
-dontwarn org.slf4j.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn javax.annotation.**