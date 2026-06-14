// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/data/datasource/RoutineApiService.kt
package com.castrodev.workouttracker.features.routines.data.datasource

import com.castrodev.workouttracker.features.routines.data.model.CreateRoutineRequest
import com.castrodev.workouttracker.features.routines.data.model.RoutineModel
import retrofit2.Response
import retrofit2.http.*

interface RoutineApiService {
    @GET("api/workout/routines")
    suspend fun getRoutines(): List<RoutineModel>

    @GET("api/workout/routines/{id}")
    suspend fun getRoutineById(@Path("id") id: String): RoutineModel

    @POST("api/workout/routines")
    suspend fun createRoutine(@Body request: CreateRoutineRequest): String

    @PUT("api/workout/routines/{id}")
    suspend fun updateRoutine(@Path("id") id: String, @Body request: CreateRoutineRequest): Response<Unit>

    @DELETE("api/workout/routines/{id}")
    suspend fun deleteRoutine(@Path("id") id: String): Response<Unit>
}