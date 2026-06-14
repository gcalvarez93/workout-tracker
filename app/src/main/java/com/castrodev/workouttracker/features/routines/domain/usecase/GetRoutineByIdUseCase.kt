// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/domain/usecase/GetRoutineByIdUseCase.kt
package com.castrodev.workouttracker.features.routines.domain.usecase
import com.castrodev.workouttracker.features.routines.domain.repository.RoutineRepository
class GetRoutineByIdUseCase(private val repository: RoutineRepository) {
    suspend operator fun invoke(id: String) = repository.getRoutineById(id)
}