// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/presentation/viewmodel/SessionViewModel.kt
package com.castrodev.workouttracker.features.sessions.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.castrodev.workouttracker.core.ui.toMessageRes
import com.castrodev.workouttracker.features.sessions.domain.entity.CompletedExercise
import com.castrodev.workouttracker.features.sessions.domain.entity.SessionEntity
import com.castrodev.workouttracker.features.sessions.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SessionsState {
    object Loading : SessionsState()
    data class Success(val sessions: List<SessionEntity>) : SessionsState()
    data class Error(@StringRes val messageRes: Int) : SessionsState()
}

sealed class ActiveSessionState {
    object Idle : ActiveSessionState()
    object Loading : ActiveSessionState()
    data class Active(val sessionId: String) : ActiveSessionState()
    object Completed : ActiveSessionState()
    data class Error(@StringRes val messageRes: Int) : ActiveSessionState()
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val getSessions: GetSessionsUseCase,
    private val startSession: StartSessionUseCase,
    private val completeSession: CompleteSessionUseCase
) : ViewModel() {

    private val _sessionsState = MutableStateFlow<SessionsState>(SessionsState.Loading)
    val sessionsState: StateFlow<SessionsState> = _sessionsState

    private val _activeState = MutableStateFlow<ActiveSessionState>(ActiveSessionState.Idle)
    val activeState: StateFlow<ActiveSessionState> = _activeState

    private var loadJob: Job? = null

    init { loadSessions() }

    fun loadSessions() {
        // Si hay una carga en curso se descarta: gana siempre la última petición.
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _sessionsState.value = SessionsState.Loading
            getSessions()
                .onSuccess { _sessionsState.value = SessionsState.Success(it) }
                .onFailure { _sessionsState.value = SessionsState.Error(it.toMessageRes()) }
        }
    }

    /** Un segundo toque mientras la sesión se está iniciando se ignora. */
    fun startSession(routineId: String) {
        if (_activeState.value is ActiveSessionState.Loading) return
        viewModelScope.launch {
            _activeState.value = ActiveSessionState.Loading
            startSession.invoke(routineId)
                .onSuccess { _activeState.value = ActiveSessionState.Active(it) }
                .onFailure { _activeState.value = ActiveSessionState.Error(it.toMessageRes()) }
        }
    }

    /**
     * Si falla, la pantalla de la sesión sigue abierta con el error visible y las series ya
     * marcadas se conservan (viven en la pantalla), así que se puede reintentar.
     */
    fun completeSession(sessionId: String, exercises: List<CompletedExercise>) {
        if (_activeState.value is ActiveSessionState.Loading) return
        viewModelScope.launch {
            _activeState.value = ActiveSessionState.Loading
            completeSession.invoke(sessionId, exercises)
                .onSuccess { _activeState.value = ActiveSessionState.Completed; loadSessions() }
                .onFailure { _activeState.value = ActiveSessionState.Error(it.toMessageRes()) }
        }
    }

    fun resetActiveSession() { _activeState.value = ActiveSessionState.Idle }
}