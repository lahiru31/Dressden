package com.dressden.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dressden.app.data.local.PreferenceManager
import com.dressden.app.data.models.User
import com.dressden.app.data.repository.AuthRepository
import com.dressden.app.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferenceManager: PreferenceManager,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated(currentUser.uid)
            fetchUserProfile()
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> {
                    preferenceManager.authToken = result.data.token
                    preferenceManager.userId = result.data.userId
                    _authState.value = AuthState.Authenticated(result.data.userId)
                    fetchUserProfile()
                }
                is Resource.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    fun register(name: String, email: String, password: String, phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            when (val result = authRepository.register(name, email, password, phone)) {
                is Resource.Success -> {
                    preferenceManager.authToken = result.data.token
                    preferenceManager.userId = result.data.userId
                    _authState.value = AuthState.Authenticated(result.data.userId)
                    fetchUserProfile()
                }
                is Resource.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            try {
                firebaseAuth.signOut()
                authRepository.logout()
                preferenceManager.clearAuth()
                _user.value = null
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Logout failed")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            when (val result = authRepository.resetPassword(email)) {
                is Resource.Success -> {
                    _authState.value = AuthState.ResetPasswordSuccess
                }
                is Resource.Error -> {
                    _authState.value = AuthState.Error(result.message)
                }
            }
        }
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            preferenceManager.userId?.let { userId ->
                when (val result = authRepository.getUserProfile(userId)) {
                    is Resource.Success -> {
                        _user.value = result.data
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState.Error(result.message)
                    }
                }
            }
        }
    }

    fun updateUserProfile(
        name: String? = null,
        phone: String? = null,
        address: Map<String, String>? = null
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            preferenceManager.userId?.let { userId ->
                when (val result = authRepository.updateUserProfile(userId, name, phone, address)) {
                    is Resource.Success -> {
                        _user.value = result.data
                        _authState.value = AuthState.ProfileUpdateSuccess
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState.Error(result.message)
                    }
                }
            }
        }
    }

    fun updateFcmToken(token: String) {
        viewModelScope.launch {
            preferenceManager.userId?.let { userId ->
                authRepository.updateFcmToken(userId, token)
                preferenceManager.fcmToken = token
            }
        }
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        data class Authenticated(val userId: String) : AuthState()
        object Unauthenticated : AuthState()
        object ResetPasswordSuccess : AuthState()
        object ProfileUpdateSuccess : AuthState()
        data class Error(val message: String) : AuthState()
    }
}
