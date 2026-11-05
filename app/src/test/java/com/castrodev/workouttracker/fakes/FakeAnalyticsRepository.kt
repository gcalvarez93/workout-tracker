// Path: app/src/test/java/com/castrodev/workouttracker/fakes/FakeAnalyticsRepository.kt
package com.castrodev.workouttracker.fakes

import com.castrodev.workouttracker.features.analytics.domain.entity.ExerciseProgressEntity
import com.castrodev.workouttracker.features.analytics.domain.entity.WeeklySummaryEntity
import com.castrodev.workouttracker.features.analytics.domain.repository.AnalyticsRepository

fun testWeeklySummary(year: Int = 2026, week: Int = 1) = WeeklySummaryEntity(
    year = year,
    week = week,
    totalSessions = 3,
    totalVolumeKg = 1200.0,
    totalDurationMinutes = 150,
    sessionsByDay = mapOf("Mon" to 1, "Wed" to 2)
)

fun testExerciseProgress(name: String = "Press banca") = ExerciseProgressEntity(
    exerciseName = name,
    entries = emptyList()
)

/** Repositorio de analíticas en memoria con resultados configurables y registro de llamadas. */
class FakeAnalyticsRepository : AnalyticsRepository {
    var summaryResult: Result<WeeklySummaryEntity> = Result.success(testWeeklySummary())
    var progressResult: Result<ExerciseProgressEntity> = Result.success(testExerciseProgress())

    val summaryRequests = mutableListOf<Pair<Int, Int>>()
    val progressRequests = mutableListOf<String>()

    override suspend fun getWeeklySummary(year: Int, week: Int): Result<WeeklySummaryEntity> {
        summaryRequests += year to week
        return summaryResult
    }

    override suspend fun getExerciseProgress(exerciseName: String): Result<ExerciseProgressEntity> {
        progressRequests += exerciseName
        return progressResult
    }
}