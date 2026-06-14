// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/data/datasource/SessionApiService.kt
package com.castrodev.workouttracker.features.sessions.data.datasource

import com.castrodev.workouttracker.features.sessions.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface SessionApiService {
    @GET("api/workout/sessions")
    suspend fun getSessions(@Query("routineId") routineId: String? = null): List<SessionModel>

    @GET("api/workout/sessions/{id}")
    suspend fun getSessionById(@Path("id") id: String): SessionModel

    @POST("api/workout/sessions/start/{routineId}")
    suspend fun startSession(@Path("routineId") routineId: String): String

    @PUT("api/workout/sessions/{id}/complete")
    suspend fun completeSession(@Path("id") id: String, @Body request: CompleteSessionRequest): Response<Unit>
}