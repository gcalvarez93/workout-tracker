// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/presentation/viewmodel/ProfileViewModel.kt
package com.castrodev.workouttracker.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.features.profile.domain.entity.ProfileEntity
import com.castrodev.workouttracker.features.profile.domain.usecase.GetProfileUseCase
import com.castrodev.workouttracker.features.profile.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: ProfileEntity) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val updateProfile: UpdateProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading
            getProfile()
                .onSuccess { _state.value = ProfileState.Success(it) }
                .onFailure { _state.value = ProfileState.Error(it.message ?: "Error") }
        }
    }

    fun update(name: String, language: String, notifications: Boolean) {
        viewModelScope.launch {
            updateProfile(name, language, notifications)
                .onSuccess { _state.value = ProfileState.Success(it) }
                .onFailure { _state.value = ProfileState.Error(it.message ?: "Error") }
        }
    }
}