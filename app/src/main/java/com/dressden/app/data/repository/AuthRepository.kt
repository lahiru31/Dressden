package com.dressden.app.data.repository

import com.dressden.app.data.api.ApiService
import com.dressden.app.data.local.PreferenceManager
import com.dressden.app.data.models.User
import com.dressden.app.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val preferenceManager: PreferenceManager
) {
    data class AuthResult(
        val userId: String,
        val token: String
    )

    suspend fun login(email: String, password: String): Resource<AuthResult> {
        return try {
            // Firebase Authentication
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Authentication failed")
            
            // Get ID token
            val token = user.getIdToken(false).await().token
                ?: throw Exception("Failed to get authentication token")

            // Sync with backend
            val response = apiService.login(email, token)
            if (!response.isSuccessful) {
                throw Exception(response.message())
            }

            Resource.Success(AuthResult(user.uid, token))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String
    ): Resource<AuthResult> {
        return try {
            // Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Registration failed")
            
            // Get ID token
            val token = user.getIdToken(false).await().token
                ?: throw Exception("Failed to get authentication token")

            // Create user profile in Firestore
            val userProfile = hashMapOf(
                "id" to user.uid,
                "name" to name,
                "email" to email,
                "phone" to phone,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(user.uid)
                .set(userProfile)
                .await()

            // Sync with backend
            val response = apiService.register(name, email, phone, token)
            if (!response.isSuccessful) {
                throw Exception(response.message())
            }

            Resource.Success(AuthResult(user.uid, token))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    suspend fun logout() {
        try {
            // Firebase logout
            firebaseAuth.signOut()

            // Backend logout
            val token = preferenceManager.authToken
            if (!token.isNullOrEmpty()) {
                apiService.logout("Bearer $token")
            }

            // Clear local data
            preferenceManager.clearAuth()
        } catch (e: Exception) {
            // Log error but don't throw - we want to clear local data even if server sync fails
            e.printStackTrace()
        }
    }

    suspend fun resetPassword(email: String): Resource<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Password reset failed")
        }
    }

    suspend fun getUserProfile(userId: String): Resource<User> {
        return try {
            // Get from Firestore
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (!document.exists()) {
                throw Exception("User profile not found")
            }

            // Get from backend for additional data
            val response = apiService.getUserProfile("Bearer ${preferenceManager.authToken}")
            if (!response.isSuccessful) {
                throw Exception(response.message())
            }

            val user = response.body() ?: throw Exception("Failed to get user profile")
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get user profile")
        }
    }

    suspend fun updateUserProfile(
        userId: String,
        name: String?,
        phone: String?,
        address: Map<String, String>?
    ): Resource<User> {
        return try {
            val updates = mutableMapOf<String, Any>()
            name?.let { updates["name"] = it }
            phone?.let { updates["phone"] = it }
            address?.let { updates["address"] = it }
            updates["updatedAt"] = System.currentTimeMillis()

            // Update Firestore
            firestore.collection("users")
                .document(userId)
                .update(updates)
                .await()

            // Update backend
            val response = apiService.updateUserProfile(
                "Bearer ${preferenceManager.authToken}",
                name,
                phone,
                address
            )
            if (!response.isSuccessful) {
                throw Exception(response.message())
            }

            val updatedUser = response.body() ?: throw Exception("Failed to update user profile")
            Resource.Success(updatedUser)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update user profile")
        }
    }

    suspend fun updateFcmToken(userId: String, token: String) {
        try {
            // Update Firestore
            firestore.collection("users")
                .document(userId)
                .update("fcmToken", token)
                .await()

            // Update backend
            val response = apiService.updateFcmToken(
                "Bearer ${preferenceManager.authToken}",
                token
            )
            if (!response.isSuccessful) {
                throw Exception(response.message())
            }
        } catch (e: Exception) {
            // Log error but don't throw - FCM token updates are not critical
            e.printStackTrace()
        }
    }
}
