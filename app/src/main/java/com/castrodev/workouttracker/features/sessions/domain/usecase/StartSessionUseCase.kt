// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/domain/usecase/StartSessionUseCase.kt
package com.castrodev.workouttracker.features.sessions.domain.usecase

import com.castrodev.workouttracker.features.sessions.domain.repository.SessionRepository
import javax.inject.Inject

class StartSessionUseCase @Inject constructor(private val repository: SessionRepository) {
    suspend operator fun invoke(routineId: String) = repository.startSession(routineId)
}