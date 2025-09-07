package com.vishal.thinkpadcontrol.di

import android.content.Context
import com.vishal.thinkpadcontrol.data.repository.BlockedUntilRepository
import com.vishal.thinkpadcontrol.data.repository.QuoteRepository
import com.vishal.thinkpadcontrol.data.repository.SettingsRepository
import com.vishal.thinkpadcontrol.data.repository.ViewIdRepository
import com.vishal.thinkpadcontrol.utils.AccessibilityServiceMonitor
import com.vishal.thinkpadcontrol.utils.NavigationController
import com.vishal.thinkpadcontrol.utils.ViewIdCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepository(context)
    }

    @Provides
    @Singleton
    fun provideBlockedUntilRepository(@ApplicationContext context: Context): BlockedUntilRepository {
        return BlockedUntilRepository(context)
    }

    @Provides
    @Singleton
    fun provideQuoteRepository(@ApplicationContext context: Context): QuoteRepository {
        return QuoteRepository(context)
    }

    @Provides
    @Singleton
    fun provideViewIdRepository(@ApplicationContext context: Context): ViewIdRepository {
        return ViewIdRepository(context)
    }

    @Provides
    @Singleton
    fun provideViewIdCache(): ViewIdCache {
        return ViewIdCache()
    }

    @Provides
    @Singleton
    fun provideAccessibilityServiceMonitor(@ApplicationContext context: Context): AccessibilityServiceMonitor {
        return AccessibilityServiceMonitor(context)
    }

    @Provides
    @Singleton
    fun provideNavigationController(): NavigationController {
        return NavigationController()
    }
}