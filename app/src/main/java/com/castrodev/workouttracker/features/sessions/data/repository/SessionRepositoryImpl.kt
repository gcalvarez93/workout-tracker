// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/data/repository/SessionRepositoryImpl.kt
package com.castrodev.workouttracker.features.sessions.data.repository

import com.castrodev.workouttracker.core.util.safeCall
import com.castrodev.workouttracker.features.sessions.data.datasource.SessionRemoteDataSource
import com.castrodev.workouttracker.features.sessions.data.model.CompleteSessionRequest
import com.castrodev.workouttracker.features.sessions.domain.entity.CompletedExercise
import com.castrodev.workouttracker.features.sessions.domain.entity.SessionEntity
import com.castrodev.workouttracker.features.sessions.domain.repository.SessionRepository
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val dataSource: SessionRemoteDataSource
) : SessionRepository {
    override suspend fun getSessions(routineId: String?): Result<List<SessionEntity>> =
        safeCall("SessionRepositoryImpl.getSessions") {
            dataSource.getSessions(routineId).map { it.toEntity() }
        }

    override suspend fun getSessionById(id: String): Result<SessionEntity> =
        safeCall("SessionRepositoryImpl.getSessionById") { dataSource.getSessionById(id).toEntity() }

    override suspend fun startSession(routineId: String): Result<String> =
        safeCall("SessionRepositoryImpl.startSession") { dataSource.startSession(routineId) }

    override suspend fun completeSession(id: String, exercises: List<CompletedExercise>): Result<Unit> =
        safeCall("SessionRepositoryImpl.completeSession") {
            dataSource.completeSession(id, CompleteSessionRequest.from(exercises))
        }
}