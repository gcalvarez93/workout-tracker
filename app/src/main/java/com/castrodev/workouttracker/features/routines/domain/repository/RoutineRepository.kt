// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/domain/repository/RoutineRepository.kt
package com.castrodev.workouttracker.features.routines.domain.repository

import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity

interface RoutineRepository {
    suspend fun getRoutines(): Result<List<RoutineEntity>>
    suspend fun getRoutineById(id: String): Result<RoutineEntity>
    suspend fun createRoutine(routine: RoutineEntity): Result<String>
    suspend fun updateRoutine(routine: RoutineEntity): Result<Unit>
    suspend fun deleteRoutine(id: String): Result<Unit>
}