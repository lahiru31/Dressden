package com.dressden.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.dressden.app.utils.notifications.NotificationManager
import com.dressden.app.utils.sensors.SensorManager
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DressDenApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var sensorManager: SensorManager

    override fun onCreate() {
        super.onCreate()
        
        initializeFirebase()
        initializeWorkManager()
        initializeMaps()
        initializeManagers()
        setupFirebaseMessaging()
    }

    private fun initializeFirebase() {
        FirebaseApp.initializeApp(this)
    }

    private fun initializeWorkManager() {
        WorkManager.initialize(this, workManagerConfiguration)
    }

    private fun initializeMaps() {
        MapsInitializer.initialize(this)
    }

    private fun initializeManagers() {
        // Initialize SensorManager
        sensorManager.initialize()
    }

    private fun setupFirebaseMessaging() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                // Store FCM token or send to server
                // You might want to create a separate manager for this
            }
        }

        // Subscribe to topics for notifications
        FirebaseMessaging.getInstance().apply {
            subscribeToTopic("orders")
            subscribeToTopic("promotions")
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    companion object {
        private const val TAG = "DressDenApplication"
    }
}
