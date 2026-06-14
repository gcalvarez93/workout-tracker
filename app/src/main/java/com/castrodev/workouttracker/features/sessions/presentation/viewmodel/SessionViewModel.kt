// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/presentation/viewmodel/SessionViewModel.kt
package com.castrodev.workouttracker.features.sessions.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.features.sessions.data.model.*
import com.castrodev.workouttracker.features.sessions.data.repository.SessionRepositoryImpl
import com.castrodev.workouttracker.features.sessions.domain.entity.SessionEntity
import com.castrodev.workouttracker.features.sessions.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SessionsState {
    object Loading : SessionsState()
    data class Success(val sessions: List<SessionEntity>) : SessionsState()
    data class Error(val message: String) : SessionsState()
}

sealed class ActiveSessionState {
    object Idle : ActiveSessionState()
    object Loading : ActiveSessionState()
    data class Active(val sessionId: String) : ActiveSessionState()
    object Completed : ActiveSessionState()
    data class Error(val message: String) : ActiveSessionState()
}

class SessionViewModel : ViewModel() {
    private val repository      = SessionRepositoryImpl()
    private val getSessions     = GetSessionsUseCase(repository)
    private val startSession    = StartSessionUseCase(repository)
    private val completeSession = CompleteSessionUseCase(repository)

    private val _sessionsState = MutableStateFlow<SessionsState>(SessionsState.Loading)
    val sessionsState: StateFlow<SessionsState> = _sessionsState

    private val _activeState = MutableStateFlow<ActiveSessionState>(ActiveSessionState.Idle)
    val activeState: StateFlow<ActiveSessionState> = _activeState

    init { loadSessions() }

    fun loadSessions() {
        viewModelScope.launch {
            _sessionsState.value = SessionsState.Loading
            getSessions()
                .onSuccess { _sessionsState.value = SessionsState.Success(it) }
                .onFailure { _sessionsState.value = SessionsState.Error(it.message ?: "Error") }
        }
    }

    fun startSession(routineId: String) {
        viewModelScope.launch {
            _activeState.value = ActiveSessionState.Loading
            startSession.invoke(routineId)
                .onSuccess { _activeState.value = ActiveSessionState.Active(it) }
                .onFailure { _activeState.value = ActiveSessionState.Error(it.message ?: "Error") }
        }
    }

    fun completeSession(sessionId: String, exercises: List<CompleteSessionExerciseRequest>) {
        viewModelScope.launch {
            val request = CompleteSessionRequest(exercises)
            completeSession.invoke(sessionId, request)
                .onSuccess { _activeState.value = ActiveSessionState.Completed; loadSessions() }
                .onFailure { _activeState.value = ActiveSessionState.Error(it.message ?: "Error") }
        }
    }

    fun resetActiveSession() { _activeState.value = ActiveSessionState.Idle }
}