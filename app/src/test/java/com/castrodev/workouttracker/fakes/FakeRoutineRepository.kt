// Path: app/src/test/java/com/castrodev/workouttracker/fakes/FakeRoutineRepository.kt
package com.castrodev.workouttracker.fakes

import com.castrodev.workouttracker.features.routines.domain.entity.ExerciseEntity
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity
import com.castrodev.workouttracker.features.routines.domain.repository.RoutineRepository

fun testRoutine(id: String = "r1", name: String = "Push day") = RoutineEntity(
    id = id,
    name = name,
    description = "Pecho y triceps",
    exercises = listOf(ExerciseEntity(name = "Press banca", sets = 3, reps = 10, weightKg = 60.0, restSeconds = 90)),
    createdAt = "2026-01-01"
)

/** Repositorio de rutinas en memoria con resultados configurables y registro de llamadas. */
class FakeRoutineRepository : RoutineRepository {
    var routinesResult: Result<List<RoutineEntity>> = Result.success(emptyList())
    var routineByIdResult: Result<RoutineEntity> = Result.success(testRoutine())
    var createResult: Result<String> = Result.success("new-id")
    var updateResult: Result<Unit> = Result.success(Unit)
    var deleteResult: Result<Unit> = Result.success(Unit)

    var getRoutinesCalls = 0
    val created = mutableListOf<RoutineEntity>()
    val updated = mutableListOf<RoutineEntity>()
    val deletedIds = mutableListOf<String>()

    override suspend fun getRoutines(): Result<List<RoutineEntity>> {
        getRoutinesCalls++
        return routinesResult
    }

    override suspend fun getRoutineById(id: String): Result<RoutineEntity> = routineByIdResult

    override suspend fun createRoutine(routine: RoutineEntity): Result<String> {
        created += routine
        return createResult
    }

    override suspend fun updateRoutine(routine: RoutineEntity): Result<Unit> {
        updated += routine
        return updateResult
    }

    override suspend fun deleteRoutine(id: String): Result<Unit> {
        deletedIds += id
        return deleteResult
    }
}