// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/data/datasource/SessionRemoteDataSource.kt
package com.castrodev.workouttracker.features.sessions.data.datasource

import com.castrodev.workouttracker.core.network.requireSuccess
import com.castrodev.workouttracker.features.sessions.data.model.CompleteSessionRequest
import javax.inject.Inject

class SessionRemoteDataSource @Inject constructor(
    private val api: SessionApiService
) {
    suspend fun getSessions(routineId: String? = null) = api.getSessions(routineId)

    suspend fun getSessionById(id: String) = api.getSessionById(id)

    suspend fun startSession(routineId: String) = api.startSession(routineId)

    suspend fun completeSession(id: String, request: CompleteSessionRequest) {
        api.completeSession(id, request).requireSuccess()
    }
}