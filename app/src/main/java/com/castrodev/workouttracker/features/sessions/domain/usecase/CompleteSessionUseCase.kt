// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/domain/usecase/CompleteSessionUseCase.kt
package com.castrodev.workouttracker.features.sessions.domain.usecase

import com.castrodev.workouttracker.features.sessions.data.model.CompleteSessionRequest
import com.castrodev.workouttracker.features.sessions.domain.repository.SessionRepository

class CompleteSessionUseCase(private val repository: SessionRepository) {
    suspend operator fun invoke(id: String, request: CompleteSessionRequest) =
        repository.completeSession(id, request)
}