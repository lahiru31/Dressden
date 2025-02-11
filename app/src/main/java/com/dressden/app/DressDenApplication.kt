package com.dressden.app

import android.app.Application
import com.dressden.app.di.appModule
import dagger.hilt.android.HiltAndroidApp
import com.google.firebase.FirebaseApp

@HiltAndroidApp
class DressDenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize other third-party libraries
        setupThirdPartyLibraries()
    }
    
    private fun setupThirdPartyLibraries() {
        // Initialize any other third-party libraries here
        // For example: Timber for logging, etc.
    }
}
