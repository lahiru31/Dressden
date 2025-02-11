package com.dressden.app.data.repository

import com.dressden.app.data.api.ApiService
import com.dressden.app.data.local.PreferenceManager
import com.dressden.app.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager
) {
    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Authentication failed")
            
            // Get user details from backend and ensure profile is updated
            val user = apiService.getUserProfile(firebaseUser.uid)
            preferenceManager.saveUser(user)
            preferenceManager.saveAuthToken(firebaseUser.getIdToken(false).await().token ?: "")
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User creation failed")
            
            // Create user profile in backend
            val user = User(
                id = firebaseUser.uid,
                email = email,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber
            )
            
            val createdUser = apiService.createUserProfile(user)
            preferenceManager.saveUser(createdUser)
            preferenceManager.saveAuthToken(firebaseUser.getIdToken(false).await().token ?: "")
            
            Result.success(createdUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Google authentication failed")
            
            // Get or create user profile in backend
            val user = try {
                apiService.getUserProfile(firebaseUser.uid)
            } catch (e: Exception) {
                // User doesn't exist, create new profile
                val newUser = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    firstName = firebaseUser.displayName?.split(" ")?.firstOrNull() ?: "",
                    lastName = firebaseUser.displayName?.split(" ")?.lastOrNull() ?: "",
                    profileImageUrl = firebaseUser.photoUrl?.toString()
                )
                apiService.createUserProfile(newUser)
            }
            
            preferenceManager.saveUser(user)
            preferenceManager.saveAuthToken(firebaseUser.getIdToken(false).await().token ?: "")
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        firebaseAuth.signOut()
        preferenceManager.clearAll()
    }

    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null && preferenceManager.getAuthToken() != null
    }

    suspend fun getCurrentUser(): User? {
        return preferenceManager.getUser()
    }
}
