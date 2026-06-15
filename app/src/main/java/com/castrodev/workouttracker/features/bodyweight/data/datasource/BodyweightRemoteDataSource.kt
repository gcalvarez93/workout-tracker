// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/data/datasource/BodyweightRemoteDataSource.kt
package com.castrodev.workouttracker.features.bodyweight.data.datasource

import com.castrodev.workouttracker.core.network.FirebaseTokenProvider
import com.castrodev.workouttracker.core.network.NetworkClient
import com.castrodev.workouttracker.features.bodyweight.data.model.LogBodyweightRequest

class BodyweightRemoteDataSource {
    private val api = NetworkClient.buildRetrofit(FirebaseTokenProvider()).create(BodyweightApiService::class.java)

    suspend fun getHistory() = api.getHistory()
    suspend fun logWeight(weightKg: Double, date: String, notes: String) =
        api.logWeight(LogBodyweightRequest(weightKg, date, notes))
}