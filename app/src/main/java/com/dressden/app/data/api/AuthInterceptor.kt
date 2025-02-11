package com.dressden.app.data.api

import com.dressden.app.data.local.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val preferenceManager: PreferenceManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip if the request already has an Authorization header
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        // Get the auth token
        val token = preferenceManager.authToken

        // Proceed with original request if no token is available
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // Add the Authorization header
        val modifiedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .apply {
                // Add common headers
                addHeader("Accept", "application/json")
                addHeader("Content-Type", "application/json")
                
                // Add device info
                addHeader("X-App-Version", getAppVersion())
                addHeader("X-Device-Platform", "android")
                addHeader("X-Device-Model", getDeviceModel())
                
                // Add language preference
                addHeader("Accept-Language", preferenceManager.language)
            }
            .method(originalRequest.method, originalRequest.body)
            .build()

        // Proceed with the modified request
        val response = chain.proceed(modifiedRequest)

        // Handle 401 Unauthorized response
        if (response.code == 401) {
            // Clear auth data on unauthorized response
            preferenceManager.clearAuth()
        }

        return response
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun getDeviceModel(): String {
        return "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
    }

    companion object {
        private const val TAG = "AuthInterceptor"
    }
}
