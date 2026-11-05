// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/presentation/viewmodel/AuthViewModel.kt
package com.castrodev.workouttracker.features.auth.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.core.network.SessionEvents
import com.castrodev.workouttracker.core.ui.toMessageRes
import com.castrodev.workouttracker.features.auth.domain.entity.UserEntity
import com.castrodev.workouttracker.features.auth.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserEntity) : AuthState()

    /** [messageRes] es un texto localizado y apto para el usuario, nunca el mensaje de la excepción. */
    data class Error(@StringRes val messageRes: Int) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signIn: SignInUseCase,
    private val signUp: SignUpUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val signOut: SignOutUseCase,
    isUserAuthenticated: IsAuthenticatedUseCase,
    sessionEvents: SessionEvents
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    private val _isAuthenticated = MutableStateFlow(isUserAuthenticated())
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    /** Avisos de que el servidor ya no acepta la sesión. Los recoge la navegación. */
    val sessionExpired: Flow<Unit> = sessionEvents.expired

    fun signIn(email: String, password: String) =
        authenticate { signIn.invoke(email, password) }

    fun signUp(email: String, password: String, name: String) =
        authenticate { signUp.invoke(email, password, name) }

    fun signInWithGoogle(idToken: String) =
        authenticate { signInWithGoogle.invoke(idToken) }

    /** Un solo intento a la vez: un segundo toque mientras hay uno en curso se ignora. */
    private fun authenticate(request: suspend () -> Result<UserEntity>) {
        if (_state.value is AuthState.Loading) return
        _state.value = AuthState.Loading
        viewModelScope.launch {
            request()
                .onSuccess { _isAuthenticated.value = true; _state.value = AuthState.Success(it) }
                .onFailure { _state.value = AuthState.Error(it.toMessageRes()) }
        }
    }

    fun signOut() {
        signOut.invoke()
        _isAuthenticated.value = false
        _state.value = AuthState.Idle
    }

    /**
     * Cierra la sesión cuando el servidor ya no la acepta y deja el motivo para que el login lo
     * muestre. Devuelve `true` si había una sesión que cerrar: varios avisos seguidos solo
     * provocan una vuelta al login.
     */
    fun onSessionExpired(): Boolean {
        if (!_isAuthenticated.value) return false
        signOut.invoke()
        _isAuthenticated.value = false
        _state.value = AuthState.Error(R.string.error_unauthorized)
        return true
    }
}