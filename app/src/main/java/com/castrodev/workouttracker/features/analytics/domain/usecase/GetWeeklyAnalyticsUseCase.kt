// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/domain/usecase/GetWeeklyAnalyticsUseCase.kt
package com.castrodev.workouttracker.features.analytics.domain.usecase

import com.castrodev.workouttracker.features.analytics.domain.repository.AnalyticsRepository
import javax.inject.Inject

class GetWeeklyAnalyticsUseCase @Inject constructor(private val repository: AnalyticsRepository) {
    suspend operator fun invoke(year: Int, week: Int) = repository.getWeeklySummary(year, week)
}