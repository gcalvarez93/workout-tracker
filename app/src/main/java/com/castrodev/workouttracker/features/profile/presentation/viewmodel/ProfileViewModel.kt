// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/presentation/viewmodel/ProfileViewModel.kt
package com.castrodev.workouttracker.features.profile.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.core.ui.toMessageRes
import com.castrodev.workouttracker.features.profile.domain.entity.ProfileEntity
import com.castrodev.workouttracker.features.profile.domain.usecase.GetProfileUseCase
import com.castrodev.workouttracker.features.profile.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: ProfileEntity) : ProfileState()
    data class Error(@StringRes val messageRes: Int) : ProfileState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val updateProfile: UpdateProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    /** `true` mientras se guarda un cambio: la UI desactiva el interruptor y evita envíos en paralelo. */
    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating

    /** Error al guardar el perfil. El perfil que ya se ve no se sustituye por una pantalla de error. */
    private val _actionError = MutableStateFlow<Int?>(null)
    val actionError: StateFlow<Int?> = _actionError

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            getProfile()
                .onSuccess { _state.value = ProfileState.Success(it) }
                .onFailure { _state.value = ProfileState.Error(it.toMessageRes()) }
        }
    }

    fun update(name: String, language: String, notifications: Boolean) {
        if (_isUpdating.value) return
        _isUpdating.value = true
        _actionError.value = null
        viewModelScope.launch {
            try {
                updateProfile(name, language, notifications)
                    .onSuccess { _state.value = ProfileState.Success(it) }
                    .onFailure { _actionError.value = it.toMessageRes() }
            } finally {
                _isUpdating.value = false
            }
        }
    }

    fun clearActionError() { _actionError.value = null }
}