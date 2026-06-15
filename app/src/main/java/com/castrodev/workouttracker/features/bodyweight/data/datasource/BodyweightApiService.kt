// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/data/datasource/BodyweightApiService.kt
package com.castrodev.workouttracker.features.bodyweight.data.datasource

import com.castrodev.workouttracker.features.bodyweight.data.model.BodyweightModel
import com.castrodev.workouttracker.features.bodyweight.data.model.LogBodyweightRequest
import retrofit2.http.*

interface BodyweightApiService {
    @GET("api/workout/bodyweight")
    suspend fun getHistory(): List<BodyweightModel>

    @POST("api/workout/bodyweight")
    suspend fun logWeight(@Body request: LogBodyweightRequest): String
}