// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/data/repository/AnalyticsRepositoryImpl.kt
package com.castrodev.workouttracker.features.analytics.data.repository

import com.castrodev.workouttracker.core.util.safeCall
import com.castrodev.workouttracker.features.analytics.data.datasource.AnalyticsRemoteDataSource
import com.castrodev.workouttracker.features.analytics.domain.entity.ExerciseProgressEntity
import com.castrodev.workouttracker.features.analytics.domain.entity.WeeklySummaryEntity
import com.castrodev.workouttracker.features.analytics.domain.repository.AnalyticsRepository
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val dataSource: AnalyticsRemoteDataSource
) : AnalyticsRepository {
    override suspend fun getWeeklySummary(year: Int, week: Int): Result<WeeklySummaryEntity> =
        safeCall("AnalyticsRepositoryImpl.getWeeklySummary") {
            dataSource.getWeeklySummary(year, week).toEntity()
        }

    override suspend fun getExerciseProgress(exerciseName: String): Result<ExerciseProgressEntity> =
        safeCall("AnalyticsRepositoryImpl.getExerciseProgress") {
            dataSource.getExerciseProgress(exerciseName).toEntity()
        }
}