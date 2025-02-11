package com.dressden.app.di

import android.content.Context
import com.dressden.app.utils.location.LocationManager
import com.dressden.app.utils.media.MediaManager
import com.dressden.app.utils.notifications.NotificationManager
import com.dressden.app.utils.permissions.PermissionManager
import com.dressden.app.utils.sensors.SensorManager
import com.dressden.app.utils.telephony.TelephonyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    @Singleton
    fun providePermissionManager(
        @ApplicationContext context: Context
    ): PermissionManager {
        return PermissionManager(context)
    }

    @Provides
    @Singleton
    fun provideLocationManager(
        @ApplicationContext context: Context,
        permissionManager: PermissionManager
    ): LocationManager {
        return LocationManager(context, permissionManager)
    }

    @Provides
    @Singleton
    fun provideMediaManager(
        @ApplicationContext context: Context,
        permissionManager: PermissionManager
    ): MediaManager {
        return MediaManager(context, permissionManager)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return NotificationManager(context)
    }

    @Provides
    @Singleton
    fun provideSensorManager(
        @ApplicationContext context: Context
    ): SensorManager {
        return SensorManager(context)
    }

    @Provides
    @Singleton
    fun provideTelephonyManager(
        @ApplicationContext context: Context,
        permissionManager: PermissionManager
    ): TelephonyManager {
        return TelephonyManager(context, permissionManager)
    }
}
