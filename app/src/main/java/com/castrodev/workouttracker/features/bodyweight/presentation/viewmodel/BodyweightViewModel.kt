// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/presentation/viewmodel/BodyweightViewModel.kt
package com.castrodev.workouttracker.features.bodyweight.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.core.ui.toMessageRes
import com.castrodev.workouttracker.features.bodyweight.domain.entity.BodyweightEntity
import com.castrodev.workouttracker.features.bodyweight.domain.usecase.AddBodyweightUseCase
import com.castrodev.workouttracker.features.bodyweight.domain.usecase.GetBodyweightHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BodyweightState {
    object Loading : BodyweightState()
    data class Success(val entries: List<BodyweightEntity>) : BodyweightState()
    data class Error(@StringRes val messageRes: Int) : BodyweightState()
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

    /** `true` mientras se guarda un peso: la UI desactiva el botón para evitar dobles envíos. */
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    /**
     * Error al guardar un peso. Se publica aparte del historial: un fallo al guardar no debe
     * borrar los registros que ya se ven, y el diálogo sigue abierto para reintentar.
     */
    private val _actionError = MutableStateFlow<Int?>(null)
    val actionError: StateFlow<Int?> = _actionError

    private var loadJob: Job? = null

    init { loadHistory() }

    fun loadHistory() {
        // Si hay una carga en curso se descarta: gana siempre la última petición.
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = BodyweightState.Loading
            getHistory()
                .onSuccess { _state.value = BodyweightState.Success(it) }
                .onFailure { _state.value = BodyweightState.Error(it.toMessageRes()) }
        }
    }

    fun logWeight(weightKg: Double, date: String, notes: String) {
        if (_isSaving.value) return
        _isSaving.value = true
        _actionError.value = null
        viewModelScope.launch {
            try {
                addWeight(weightKg, date, notes)
                    .onSuccess { _actionSuccess.value = true; loadHistory() }
                    .onFailure { _actionError.value = it.toMessageRes() }
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun resetActionSuccess() { _actionSuccess.value = false }
    fun clearActionError() { _actionError.value = null }
}