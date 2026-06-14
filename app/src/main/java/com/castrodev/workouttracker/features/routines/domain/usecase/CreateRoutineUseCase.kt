// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/domain/usecase/CreateRoutineUseCase.kt
package com.castrodev.workouttracker.features.routines.domain.usecase
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity
import com.castrodev.workouttracker.features.routines.domain.repository.RoutineRepository
class CreateRoutineUseCase(private val repository: RoutineRepository) {
    suspend operator fun invoke(routine: RoutineEntity): Result<String> {
        if (routine.name.isBlank()) return Result.failure(IllegalArgumentException("Name cannot be empty"))
        return repository.createRoutine(routine)
    }
}