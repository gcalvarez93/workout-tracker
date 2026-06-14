// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/data/datasource/SessionRemoteDataSource.kt
package com.castrodev.workouttracker.features.sessions.data.datasource

import com.castrodev.workouttracker.core.network.FirebaseTokenProvider
import com.castrodev.workouttracker.core.network.NetworkClient
import com.castrodev.workouttracker.features.sessions.data.model.CompleteSessionRequest

class SessionRemoteDataSource {
    private val api = NetworkClient.buildRetrofit(FirebaseTokenProvider()).create(SessionApiService::class.java)

    suspend fun getSessions(routineId: String? = null) = api.getSessions(routineId)
    suspend fun getSessionById(id: String) = api.getSessionById(id)
    suspend fun startSession(routineId: String) = api.startSession(routineId)
    suspend fun completeSession(id: String, request: CompleteSessionRequest) {
        api.completeSession(id, request)
    }
}