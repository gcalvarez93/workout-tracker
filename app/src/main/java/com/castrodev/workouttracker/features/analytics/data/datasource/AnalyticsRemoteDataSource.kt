// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/data/datasource/AnalyticsRemoteDataSource.kt
package com.castrodev.workouttracker.features.analytics.data.datasource

import com.castrodev.workouttracker.core.network.FirebaseTokenProvider
import com.castrodev.workouttracker.core.network.NetworkClient

class AnalyticsRemoteDataSource {
    private val api = NetworkClient.buildRetrofit(FirebaseTokenProvider()).create(AnalyticsApiService::class.java)

    suspend fun getWeeklySummary(year: Int, week: Int) = api.getWeeklySummary(year, week)
    suspend fun getExerciseProgress(exerciseName: String) = api.getExerciseProgress(exerciseName)
}