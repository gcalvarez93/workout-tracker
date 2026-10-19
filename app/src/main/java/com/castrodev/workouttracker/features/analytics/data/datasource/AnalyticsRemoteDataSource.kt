// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/data/datasource/AnalyticsRemoteDataSource.kt
package com.castrodev.workouttracker.features.analytics.data.datasource

import javax.inject.Inject

class AnalyticsRemoteDataSource @Inject constructor(
    private val api: AnalyticsApiService
) {
    suspend fun getWeeklySummary(year: Int, week: Int) = api.getWeeklySummary(year, week)
    suspend fun getExerciseProgress(exerciseName: String) = api.getExerciseProgress(exerciseName)
}