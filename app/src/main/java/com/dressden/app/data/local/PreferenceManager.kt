package com.dressden.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.dressden.app.data.models.User
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

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAuthToken(token: String) {
        prefs.edit {
            putString(KEY_AUTH_TOKEN, token)
        }
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun clearAuthToken() {
        prefs.edit {
            remove(KEY_AUTH_TOKEN)
        }
    }

    fun saveUser(user: User) {
        prefs.edit {
            putString(KEY_USER, gson.toJson(user))
        }
    }

    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null)
        return userJson?.let {
            try {
                gson.fromJson(it, User::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun clearUser() {
        prefs.edit {
            remove(KEY_USER)
        }
    }

    fun saveThemeMode(isDarkMode: Boolean) {
        prefs.edit {
            putBoolean(KEY_DARK_MODE, isDarkMode)
        }
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun saveNotificationEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
        }
    }

    fun isNotificationEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun clearAll() {
        prefs.edit {
            clear()
        }
    }

    companion object {
        private const val PREF_NAME = "DressDenPrefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER = "user"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }
}
