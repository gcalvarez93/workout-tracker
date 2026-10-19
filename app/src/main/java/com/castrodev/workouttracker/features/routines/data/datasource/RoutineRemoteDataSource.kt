// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/data/datasource/RoutineRemoteDataSource.kt
package com.castrodev.workouttracker.features.routines.data.datasource

import com.castrodev.workouttracker.core.network.requireSuccess
import com.castrodev.workouttracker.features.routines.data.model.CreateRoutineRequest
import com.castrodev.workouttracker.features.routines.data.model.ExerciseModel
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity
import javax.inject.Inject

class RoutineRemoteDataSource @Inject constructor(
    private val api: RoutineApiService
) {
    suspend fun getRoutines() = api.getRoutines()

    suspend fun getRoutineById(id: String) = api.getRoutineById(id)

    suspend fun createRoutine(routine: RoutineEntity) = api.createRoutine(routine.toRequest())

    suspend fun updateRoutine(routine: RoutineEntity) {
        api.updateRoutine(routine.id, routine.toRequest()).requireSuccess()
    }

    suspend fun deleteRoutine(id: String) {
        api.deleteRoutine(id).requireSuccess()
    }

    private fun RoutineEntity.toRequest() = CreateRoutineRequest(
        name = name,
        description = description,
        exercises = exercises.map { ExerciseModel(it.name, it.sets, it.reps, it.weightKg, it.restSeconds) }
    )
}