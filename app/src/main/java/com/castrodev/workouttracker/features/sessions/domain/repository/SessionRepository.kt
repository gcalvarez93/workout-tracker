// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/domain/repository/SessionRepository.kt
package com.castrodev.workouttracker.features.sessions.domain.repository

import com.castrodev.workouttracker.features.sessions.domain.entity.SessionEntity
import com.castrodev.workouttracker.features.sessions.data.model.CompleteSessionRequest

interface SessionRepository {
    suspend fun getSessions(routineId: String? = null): Result<List<SessionEntity>>
    suspend fun getSessionById(id: String): Result<SessionEntity>
    suspend fun startSession(routineId: String): Result<String>
    suspend fun completeSession(id: String, request: CompleteSessionRequest): Result<Unit>
}