// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/data/repository/SessionRepositoryImpl.kt
package com.castrodev.workouttracker.features.sessions.data.repository

import com.castrodev.workouttracker.features.sessions.data.datasource.SessionRemoteDataSource
import com.castrodev.workouttracker.features.sessions.data.model.CompleteSessionRequest
import com.castrodev.workouttracker.features.sessions.domain.entity.SessionEntity
import com.castrodev.workouttracker.features.sessions.domain.repository.SessionRepository

class SessionRepositoryImpl(
    private val dataSource: SessionRemoteDataSource = SessionRemoteDataSource()
) : SessionRepository {
    override suspend fun getSessions(routineId: String?) = runCatching { dataSource.getSessions(routineId).map { it.toEntity() } }
    override suspend fun getSessionById(id: String) = runCatching { dataSource.getSessionById(id).toEntity() }
    override suspend fun startSession(routineId: String) = runCatching { dataSource.startSession(routineId) }
    override suspend fun completeSession(id: String, request: CompleteSessionRequest) = runCatching { dataSource.completeSession(id, request) }
}