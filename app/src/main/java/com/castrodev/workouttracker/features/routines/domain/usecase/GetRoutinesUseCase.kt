// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/domain/usecase/GetRoutinesUseCase.kt
package com.castrodev.workouttracker.features.routines.domain.usecase

import com.castrodev.workouttracker.features.routines.domain.repository.RoutineRepository
import javax.inject.Inject

class GetRoutinesUseCase @Inject constructor(private val repository: RoutineRepository) {
    suspend operator fun invoke() = repository.getRoutines()
}