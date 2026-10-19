// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/domain/repository/SessionRepository.kt
package com.castrodev.workouttracker.features.sessions.domain.repository

import com.castrodev.workouttracker.features.sessions.domain.entity.CompletedExercise
import com.castrodev.workouttracker.features.sessions.domain.entity.SessionEntity

interface SessionRepository {
    suspend fun getSessions(routineId: String? = null): Result<List<SessionEntity>>
    suspend fun getSessionById(id: String): Result<SessionEntity>
    suspend fun startSession(routineId: String): Result<String>
    suspend fun completeSession(id: String, exercises: List<CompletedExercise>): Result<Unit>
}