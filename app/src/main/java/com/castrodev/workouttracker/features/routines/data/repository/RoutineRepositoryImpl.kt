// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/data/repository/RoutineRepositoryImpl.kt
package com.castrodev.workouttracker.features.routines.data.repository

import com.castrodev.workouttracker.core.util.safeCall
import com.castrodev.workouttracker.features.routines.data.datasource.RoutineRemoteDataSource
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity
import com.castrodev.workouttracker.features.routines.domain.repository.RoutineRepository
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val dataSource: RoutineRemoteDataSource
) : RoutineRepository {
    override suspend fun getRoutines(): Result<List<RoutineEntity>> =
        safeCall("RoutineRepositoryImpl.getRoutines") { dataSource.getRoutines().map { it.toEntity() } }

    override suspend fun getRoutineById(id: String): Result<RoutineEntity> =
        safeCall("RoutineRepositoryImpl.getRoutineById") { dataSource.getRoutineById(id).toEntity() }

    override suspend fun createRoutine(routine: RoutineEntity): Result<String> =
        safeCall("RoutineRepositoryImpl.createRoutine") { dataSource.createRoutine(routine) }

    override suspend fun updateRoutine(routine: RoutineEntity): Result<Unit> =
        safeCall("RoutineRepositoryImpl.updateRoutine") { dataSource.updateRoutine(routine) }

    override suspend fun deleteRoutine(id: String): Result<Unit> =
        safeCall("RoutineRepositoryImpl.deleteRoutine") { dataSource.deleteRoutine(id) }
}