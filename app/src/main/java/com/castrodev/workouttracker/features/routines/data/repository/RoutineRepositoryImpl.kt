// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/data/repository/RoutineRepositoryImpl.kt
package com.castrodev.workouttracker.features.routines.data.repository

import com.castrodev.workouttracker.features.routines.data.datasource.RoutineRemoteDataSource
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity
import com.castrodev.workouttracker.features.routines.domain.repository.RoutineRepository

class RoutineRepositoryImpl(
    private val dataSource: RoutineRemoteDataSource = RoutineRemoteDataSource()
) : RoutineRepository {
    override suspend fun getRoutines() = runCatching { dataSource.getRoutines().map { it.toEntity() } }
    override suspend fun getRoutineById(id: String) = runCatching { dataSource.getRoutineById(id).toEntity() }
    override suspend fun createRoutine(routine: RoutineEntity) = runCatching { dataSource.createRoutine(routine) }
    override suspend fun updateRoutine(routine: RoutineEntity) = runCatching { dataSource.updateRoutine(routine) }
    override suspend fun deleteRoutine(id: String) = runCatching { dataSource.deleteRoutine(id) }
}