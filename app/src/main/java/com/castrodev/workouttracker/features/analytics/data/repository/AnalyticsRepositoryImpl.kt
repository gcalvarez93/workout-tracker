// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/data/repository/AnalyticsRepositoryImpl.kt
package com.castrodev.workouttracker.features.analytics.data.repository

import com.castrodev.workouttracker.features.analytics.data.datasource.AnalyticsRemoteDataSource
import com.castrodev.workouttracker.features.analytics.domain.entity.ExerciseProgressEntity
import com.castrodev.workouttracker.features.analytics.domain.entity.WeeklySummaryEntity
import com.castrodev.workouttracker.features.analytics.domain.repository.AnalyticsRepository

class AnalyticsRepositoryImpl(
    private val dataSource: AnalyticsRemoteDataSource = AnalyticsRemoteDataSource()
) : AnalyticsRepository {
    override suspend fun getWeeklySummary(year: Int, week: Int): Result<WeeklySummaryEntity> =
        runCatching { dataSource.getWeeklySummary(year, week).toEntity() }

    override suspend fun getExerciseProgress(exerciseName: String): Result<ExerciseProgressEntity> =
        runCatching { dataSource.getExerciseProgress(exerciseName).toEntity() }
}