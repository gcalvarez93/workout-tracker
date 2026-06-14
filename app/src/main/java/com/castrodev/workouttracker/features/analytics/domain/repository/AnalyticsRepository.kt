// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/domain/repository/AnalyticsRepository.kt
package com.castrodev.workouttracker.features.analytics.domain.repository

import com.castrodev.workouttracker.features.analytics.domain.entity.ExerciseProgressEntity
import com.castrodev.workouttracker.features.analytics.domain.entity.WeeklySummaryEntity

interface AnalyticsRepository {
    suspend fun getWeeklySummary(year: Int, week: Int): Result<WeeklySummaryEntity>
    suspend fun getExerciseProgress(exerciseName: String): Result<ExerciseProgressEntity>
}