// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/data/datasource/BodyweightRemoteDataSource.kt
package com.castrodev.workouttracker.features.bodyweight.data.datasource

import com.castrodev.workouttracker.features.bodyweight.data.model.LogBodyweightRequest
import javax.inject.Inject

class BodyweightRemoteDataSource @Inject constructor(
    private val api: BodyweightApiService
) {
    suspend fun getHistory() = api.getHistory()

    suspend fun logWeight(weightKg: Double, date: String, notes: String) =
        api.logWeight(LogBodyweightRequest(weightKg, date, notes))
}