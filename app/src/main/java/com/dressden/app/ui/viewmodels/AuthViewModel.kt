package com.dressden.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dressden.app.data.models.User
import com.dressden.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init {
        viewModelScope.launch {
            _user.value = authRepository.getCurrentUser()
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signInWithEmail(email, password)
                .onSuccess { user ->
                    _user.value = user
                    _authState.value = AuthState.Success(user)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Authentication failed")
                }
        }
    }

    fun signUpWithEmail(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String?
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signUpWithEmail(email, password, firstName, lastName, phoneNumber)
                .onSuccess { user ->
                    _user.value = user
                    _authState.value = AuthState.Success(user)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Registration failed")
                }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    _user.value = user
                    _authState.value = AuthState.Success(user)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Google sign-in failed")
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _user.value = null
            _authState.value = AuthState.SignedOut
        }
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        data class Success(val user: User) : AuthState()
        data class Error(val message: String) : AuthState()
        object SignedOut : AuthState()
    }
}
