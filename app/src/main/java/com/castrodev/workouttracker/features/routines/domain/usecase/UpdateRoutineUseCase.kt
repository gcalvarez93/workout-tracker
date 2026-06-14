// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/domain/usecase/UpdateRoutineUseCase.kt
package com.castrodev.workouttracker.features.routines.domain.usecase
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity
import com.castrodev.workouttracker.features.routines.domain.repository.RoutineRepository
class UpdateRoutineUseCase(private val repository: RoutineRepository) {
    suspend operator fun invoke(routine: RoutineEntity) = repository.updateRoutine(routine)
}