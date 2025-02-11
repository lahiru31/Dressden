package com.dressden.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.dressden.app.utils.Constants
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        SECURE_PREFS_FILENAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFS_FILENAME,
        Context.MODE_PRIVATE
    )

    // Auth Related
    var authToken: String?
        get() = securePreferences.getString(KEY_AUTH_TOKEN, null)
        set(value) = securePreferences.edit { putString(KEY_AUTH_TOKEN, value) }

    var userId: String?
        get() = securePreferences.getString(KEY_USER_ID, null)
        set(value) = securePreferences.edit { putString(KEY_USER_ID, value) }

    var userEmail: String?
        get() = securePreferences.getString(KEY_USER_EMAIL, null)
        set(value) = securePreferences.edit { putString(KEY_USER_EMAIL, value) }

    var fcmToken: String?
        get() = securePreferences.getString(KEY_FCM_TOKEN, null)
        set(value) = securePreferences.edit { putString(KEY_FCM_TOKEN, value) }

    // App Settings
    var isDarkMode: Boolean
        get() = preferences.getBoolean(KEY_DARK_MODE, false)
        set(value) = preferences.edit { putBoolean(KEY_DARK_MODE, value) }

    var notificationsEnabled: Boolean
        get() = preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = preferences.edit { putBoolean(KEY_NOTIFICATIONS_ENABLED, value) }

    var locationEnabled: Boolean
        get() = preferences.getBoolean(KEY_LOCATION_ENABLED, true)
        set(value) = preferences.edit { putBoolean(KEY_LOCATION_ENABLED, value) }

    var language: String
        get() = preferences.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = preferences.edit { putString(KEY_LANGUAGE, value) }

    var currency: String
        get() = preferences.getString(KEY_CURRENCY, "INR") ?: "INR"
        set(value) = preferences.edit { putString(KEY_CURRENCY, value) }

    // App State
    var isFirstLaunch: Boolean
        get() = preferences.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = preferences.edit { putBoolean(KEY_FIRST_LAUNCH, value) }

    var lastSyncTime: Long
        get() = preferences.getLong(KEY_LAST_SYNC, 0)
        set(value) = preferences.edit { putLong(KEY_LAST_SYNC, value) }

    var cartId: String?
        get() = preferences.getString(KEY_CART_ID, null)
        set(value) = preferences.edit { putString(KEY_CART_ID, value) }

    // Search History
    var searchHistory: Set<String>
        get() = preferences.getStringSet(KEY_SEARCH_HISTORY, emptySet()) ?: emptySet()
        set(value) = preferences.edit { putStringSet(KEY_SEARCH_HISTORY, value) }

    // Recently Viewed Products
    var recentlyViewedProducts: List<String>
        get() {
            val json = preferences.getString(KEY_RECENTLY_VIEWED, null)
            return if (json != null) {
                gson.fromJson(json, Array<String>::class.java).toList()
            } else {
                emptyList()
            }
        }
        set(value) {
            val json = gson.toJson(value)
            preferences.edit { putString(KEY_RECENTLY_VIEWED, json) }
        }

    // User Preferences
    var userPreferences: Map<String, Any>
        get() {
            val json = preferences.getString(KEY_USER_PREFERENCES, null)
            return if (json != null) {
                @Suppress("UNCHECKED_CAST")
                gson.fromJson(json, Map::class.java) as Map<String, Any>
            } else {
                emptyMap()
            }
        }
        set(value) {
            val json = gson.toJson(value)
            preferences.edit { putString(KEY_USER_PREFERENCES, json) }
        }

    // Helper Methods
    fun clearAuth() {
        securePreferences.edit {
            remove(KEY_AUTH_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
        }
    }

    fun clearAll() {
        securePreferences.edit().clear().apply()
        preferences.edit().clear().apply()
    }

    companion object {
        private const val SECURE_PREFS_FILENAME = "secure_preferences"
        private const val PREFS_FILENAME = "app_preferences"

        // Secure Keys
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_FCM_TOKEN = "fcm_token"

        // Regular Keys
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_LOCATION_ENABLED = "location_enabled"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_LAST_SYNC = "last_sync"
        private const val KEY_CART_ID = "cart_id"
        private const val KEY_SEARCH_HISTORY = "search_history"
        private const val KEY_RECENTLY_VIEWED = "recently_viewed"
        private const val KEY_USER_PREFERENCES = "user_preferences"
    }
}
