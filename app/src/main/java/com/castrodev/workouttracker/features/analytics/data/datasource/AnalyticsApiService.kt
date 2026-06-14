// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/data/datasource/AnalyticsApiService.kt
package com.castrodev.workouttracker.features.analytics.data.datasource

import com.castrodev.workouttracker.features.analytics.data.model.ExerciseProgressModel
import com.castrodev.workouttracker.features.analytics.data.model.WeeklySummaryModel
import retrofit2.http.GET
import retrofit2.http.Path

interface AnalyticsApiService {
    @GET("api/workout/analytics/weekly/{year}/{week}")
    suspend fun getWeeklySummary(@Path("year") year: Int, @Path("week") week: Int): WeeklySummaryModel

    @GET("api/workout/analytics/progress/{exerciseName}")
    suspend fun getExerciseProgress(@Path("exerciseName") exerciseName: String): ExerciseProgressModel
}