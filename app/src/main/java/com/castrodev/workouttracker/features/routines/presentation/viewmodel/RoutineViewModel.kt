// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/presentation/viewmodel/RoutineViewModel.kt
package com.castrodev.workouttracker.features.routines.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.features.routines.data.repository.RoutineRepositoryImpl
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity
import com.castrodev.workouttracker.features.routines.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RoutinesState {
    object Loading : RoutinesState()
    data class Success(val routines: List<RoutineEntity>) : RoutinesState()
    data class Error(val message: String) : RoutinesState()
}

sealed class RoutineDetailState {
    object Idle : RoutineDetailState()
    object Loading : RoutineDetailState()
    data class Success(val routine: RoutineEntity) : RoutineDetailState()
    data class Error(val message: String) : RoutineDetailState()
}

class RoutineViewModel : ViewModel() {
    private val repository    = RoutineRepositoryImpl()
    private val getRoutines   = GetRoutinesUseCase(repository)
    private val getRoutineById = GetRoutineByIdUseCase(repository)
    private val createRoutine = CreateRoutineUseCase(repository)
    private val updateRoutine = UpdateRoutineUseCase(repository)
    private val deleteRoutine = DeleteRoutineUseCase(repository)

    private val _routinesState = MutableStateFlow<RoutinesState>(RoutinesState.Loading)
    val routinesState: StateFlow<RoutinesState> = _routinesState

    private val _detailState = MutableStateFlow<RoutineDetailState>(RoutineDetailState.Idle)
    val detailState: StateFlow<RoutineDetailState> = _detailState

    private val _actionSuccess = MutableStateFlow(false)
    val actionSuccess: StateFlow<Boolean> = _actionSuccess

    init { loadRoutines() }

    fun loadRoutines() {
        viewModelScope.launch {
            _routinesState.value = RoutinesState.Loading
            getRoutines()
                .onSuccess { _routinesState.value = RoutinesState.Success(it) }
                .onFailure { _routinesState.value = RoutinesState.Error(it.message ?: "Error") }
        }
    }

    fun loadRoutineById(id: String) {
        viewModelScope.launch {
            _detailState.value = RoutineDetailState.Loading
            getRoutineById(id)
                .onSuccess { _detailState.value = RoutineDetailState.Success(it) }
                .onFailure { _detailState.value = RoutineDetailState.Error(it.message ?: "Error") }
        }
    }

    fun create(routine: RoutineEntity) {
        viewModelScope.launch {
            createRoutine(routine)
                .onSuccess { _actionSuccess.value = true; loadRoutines() }
                .onFailure { _routinesState.value = RoutinesState.Error(it.message ?: "Error") }
        }
    }

    fun update(routine: RoutineEntity) {
        viewModelScope.launch {
            updateRoutine(routine)
                .onSuccess { _actionSuccess.value = true; loadRoutines() }
                .onFailure { _routinesState.value = RoutinesState.Error(it.message ?: "Error") }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            deleteRoutine(id)
                .onSuccess { loadRoutines() }
                .onFailure { _routinesState.value = RoutinesState.Error(it.message ?: "Error") }
        }
    }

    fun resetActionSuccess() { _actionSuccess.value = false }
}