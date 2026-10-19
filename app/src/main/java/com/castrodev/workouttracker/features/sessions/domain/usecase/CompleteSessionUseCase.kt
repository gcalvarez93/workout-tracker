// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/domain/usecase/CompleteSessionUseCase.kt
package com.castrodev.workouttracker.features.sessions.domain.usecase

import com.castrodev.workouttracker.features.sessions.domain.entity.CompletedExercise
import com.castrodev.workouttracker.features.sessions.domain.repository.SessionRepository
import javax.inject.Inject

class CompleteSessionUseCase @Inject constructor(private val repository: SessionRepository) {
    suspend operator fun invoke(id: String, exercises: List<CompletedExercise>) =
        repository.completeSession(id, exercises)
}