// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/presentation/viewmodel/BodyweightViewModel.kt
package com.castrodev.workouttracker.features.bodyweight.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.features.bodyweight.domain.entity.BodyweightEntity
import com.castrodev.workouttracker.features.bodyweight.domain.usecase.AddBodyweightUseCase
import com.castrodev.workouttracker.features.bodyweight.domain.usecase.GetBodyweightHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class BodyweightState {
    object Loading : BodyweightState()
    data class Success(val entries: List<BodyweightEntity>) : BodyweightState()
    data class Error(val message: String) : BodyweightState()
}

@HiltViewModel
class BodyweightViewModel @Inject constructor(
    private val getHistory: GetBodyweightHistoryUseCase,
    private val addWeight: AddBodyweightUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<BodyweightState>(BodyweightState.Loading)
    val state: StateFlow<BodyweightState> = _state

    private val _actionSuccess = MutableStateFlow(false)
    val actionSuccess: StateFlow<Boolean> = _actionSuccess

    init { loadHistory() }

    fun loadHistory() {
        viewModelScope.launch {
            _state.value = BodyweightState.Loading
            getHistory()
                .onSuccess { _state.value = BodyweightState.Success(it) }
                .onFailure { _state.value = BodyweightState.Error(it.message ?: "Error") }
        }
    }

    fun logWeight(weightKg: Double, date: String, notes: String) {
        viewModelScope.launch {
            addWeight(weightKg, date, notes)
                .onSuccess { _actionSuccess.value = true; loadHistory() }
                .onFailure { _state.value = BodyweightState.Error(it.message ?: "Error") }
        }
    }

    fun resetActionSuccess() { _actionSuccess.value = false }
}