// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/presentation/viewmodel/RoutineViewModel.kt
package com.castrodev.workouttracker.features.routines.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.core.ui.toMessageRes
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity
import com.castrodev.workouttracker.features.routines.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RoutinesState {
    object Loading : RoutinesState()
    data class Success(val routines: List<RoutineEntity>) : RoutinesState()
    data class Error(@StringRes val messageRes: Int) : RoutinesState()
}

sealed class RoutineDetailState {
    object Idle : RoutineDetailState()
    object Loading : RoutineDetailState()
    data class Success(val routine: RoutineEntity) : RoutineDetailState()
    data class Error(@StringRes val messageRes: Int) : RoutineDetailState()
}

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val getRoutines: GetRoutinesUseCase,
    private val getRoutineById: GetRoutineByIdUseCase,
    private val createRoutine: CreateRoutineUseCase,
    private val updateRoutine: UpdateRoutineUseCase,
    private val deleteRoutine: DeleteRoutineUseCase
) : ViewModel() {

    private val _routinesState = MutableStateFlow<RoutinesState>(RoutinesState.Loading)
    val routinesState: StateFlow<RoutinesState> = _routinesState

    private val _detailState = MutableStateFlow<RoutineDetailState>(RoutineDetailState.Idle)
    val detailState: StateFlow<RoutineDetailState> = _detailState

    private val _actionSuccess = MutableStateFlow(false)
    val actionSuccess: StateFlow<Boolean> = _actionSuccess

    /** `true` mientras se guarda o borra: la UI desactiva el botón para evitar dobles envíos. */
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    /**
     * Error de la última escritura (crear, editar, borrar). Se publica aparte del estado de la
     * lista: un fallo al guardar no debe borrar lo que ya está cargado.
     */
    private val _actionError = MutableStateFlow<Int?>(null)
    val actionError: StateFlow<Int?> = _actionError

    private var loadJob: Job? = null

    init { loadRoutines() }

    /**
     * Carga la lista. Si ya hay rutinas en pantalla, la recarga se hace en segundo plano: no se
     * sustituye la lista por el indicador de carga y, si falla, se avisa por [actionError] y se
     * conserva lo que ya se veía. Solo cuando no hay datos se pasa por [RoutinesState.Loading] y
     * [RoutinesState.Error] (pantalla de error con botón de reintento).
     */
    fun loadRoutines() {
        // Si hay una carga en curso se descarta: gana siempre la última petición.
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (!hasLoadedRoutines()) _routinesState.value = RoutinesState.Loading
            getRoutines()
                .onSuccess { _routinesState.value = RoutinesState.Success(it) }
                .onFailure {
                    if (hasLoadedRoutines()) {
                        _actionError.value = it.toMessageRes()
                    } else {
                        _routinesState.value = RoutinesState.Error(it.toMessageRes())
                    }
                }
        }
    }

    private fun hasLoadedRoutines(): Boolean =
        (_routinesState.value as? RoutinesState.Success)?.routines?.isNotEmpty() == true

    fun loadRoutineById(id: String) {
        viewModelScope.launch {
            _detailState.value = RoutineDetailState.Loading
            getRoutineById(id)
                .onSuccess { _detailState.value = RoutineDetailState.Success(it) }
                .onFailure { _detailState.value = RoutineDetailState.Error(it.toMessageRes()) }
        }
    }

    fun create(routine: RoutineEntity) = perform(markSuccess = true) { createRoutine(routine) }

    fun update(routine: RoutineEntity) = perform(markSuccess = true) { updateRoutine(routine) }

    fun delete(id: String) = perform(markSuccess = false) { deleteRoutine(id) }

    /**
     * Ejecuta una escritura. Si falla, el error se publica en [actionError] y la lista cargada
     * y el formulario abierto se quedan como están. Si va bien, se recarga la lista.
     */
    private fun perform(markSuccess: Boolean, action: suspend () -> Result<Any?>) {
        if (_isSaving.value) return
        _isSaving.value = true
        _actionError.value = null
        viewModelScope.launch {
            try {
                action()
                    .onSuccess {
                        if (markSuccess) _actionSuccess.value = true
                        loadRoutines()
                    }
                    .onFailure { _actionError.value = it.toMessageRes() }
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun resetActionSuccess() { _actionSuccess.value = false }
    fun clearActionError() { _actionError.value = null }
}