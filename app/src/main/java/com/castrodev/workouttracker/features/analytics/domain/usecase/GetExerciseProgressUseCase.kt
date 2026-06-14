// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/domain/usecase/GetExerciseProgressUseCase.kt
package com.castrodev.workouttracker.features.analytics.domain.usecase
import com.castrodev.workouttracker.features.analytics.domain.repository.AnalyticsRepository
class GetExerciseProgressUseCase(private val repository: AnalyticsRepository) {
    suspend operator fun invoke(exerciseName: String) = repository.getExerciseProgress(exerciseName)
}