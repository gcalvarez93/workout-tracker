// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/data/datasource/RoutineRemoteDataSource.kt
package com.castrodev.workouttracker.features.routines.data.datasource

import com.castrodev.workouttracker.core.network.FirebaseTokenProvider
import com.castrodev.workouttracker.core.network.NetworkClient
import com.castrodev.workouttracker.features.routines.data.model.CreateRoutineRequest
import com.castrodev.workouttracker.features.routines.data.model.ExerciseModel
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity

class RoutineRemoteDataSource {
    private val api = NetworkClient.buildRetrofit(FirebaseTokenProvider()).create(RoutineApiService::class.java)

    suspend fun getRoutines() = api.getRoutines()
    suspend fun getRoutineById(id: String) = api.getRoutineById(id)
    suspend fun createRoutine(routine: RoutineEntity) = api.createRoutine(
        CreateRoutineRequest(
            name = routine.name, description = routine.description,
            exercises = routine.exercises.map { ExerciseModel(it.name, it.sets, it.reps, it.weightKg, it.restSeconds) }
        )
    )
    suspend fun updateRoutine(routine: RoutineEntity) { api.updateRoutine(
        routine.id, CreateRoutineRequest(
            name = routine.name, description = routine.description,
            exercises = routine.exercises.map { ExerciseModel(it.name, it.sets, it.reps, it.weightKg, it.restSeconds) }
        )
    ) }
    suspend fun deleteRoutine(id: String) { api.deleteRoutine(id) }
}