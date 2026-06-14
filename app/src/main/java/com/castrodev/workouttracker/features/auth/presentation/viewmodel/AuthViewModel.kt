// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/presentation/viewmodel/AuthViewModel.kt
package com.castrodev.workouttracker.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.features.auth.data.repository.AuthRepositoryImpl
import com.castrodev.workouttracker.features.auth.domain.entity.UserEntity
import com.castrodev.workouttracker.features.auth.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserEntity) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repository       = AuthRepositoryImpl()
    private val signIn           = SignInUseCase(repository)
    private val signUp           = SignUpUseCase(repository)
    private val signInWithGoogle = SignInWithGoogleUseCase(repository)
    private val signOut          = SignOutUseCase(repository)

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    private val _isAuthenticated = MutableStateFlow(repository.isAuthenticated())
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            signIn.invoke(email, password)
                .onSuccess { _state.value = AuthState.Success(it); _isAuthenticated.value = true }
                .onFailure { _state.value = AuthState.Error(it.message ?: "Error") }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            signUp.invoke(email, password, name)
                .onSuccess { _state.value = AuthState.Success(it); _isAuthenticated.value = true }
                .onFailure { _state.value = AuthState.Error(it.message ?: "Error") }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            signInWithGoogle.invoke(idToken)
                .onSuccess { _state.value = AuthState.Success(it); _isAuthenticated.value = true }
                .onFailure { _state.value = AuthState.Error(it.message ?: "Error") }
        }
    }

    fun signOut() {
        signOut.invoke()
        _isAuthenticated.value = false
        _state.value = AuthState.Idle
    }
}