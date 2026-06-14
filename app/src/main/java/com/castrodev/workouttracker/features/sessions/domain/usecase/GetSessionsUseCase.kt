// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/domain/usecase/GetSessionsUseCase.kt
package com.castrodev.workouttracker.features.sessions.domain.usecase
import com.castrodev.workouttracker.features.sessions.domain.repository.SessionRepository
class GetSessionsUseCase(private val repository: SessionRepository) {
    suspend operator fun invoke(routineId: String? = null) = repository.getSessions(routineId)
}