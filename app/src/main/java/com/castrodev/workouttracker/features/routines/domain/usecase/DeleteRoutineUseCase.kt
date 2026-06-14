// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/domain/usecase/DeleteRoutineUseCase.kt
package com.castrodev.workouttracker.features.routines.domain.usecase
import com.castrodev.workouttracker.features.routines.domain.repository.RoutineRepository
class DeleteRoutineUseCase(private val repository: RoutineRepository) {
    suspend operator fun invoke(id: String) = repository.deleteRoutine(id)
}