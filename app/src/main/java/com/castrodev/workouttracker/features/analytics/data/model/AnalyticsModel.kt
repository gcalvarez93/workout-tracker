// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/data/model/AnalyticsModel.kt
package com.castrodev.workouttracker.features.analytics.data.model

import com.castrodev.workouttracker.features.analytics.domain.entity.*

data class WeeklySummaryModel(
    val year: Int = 0,
    val week: Int = 0,
    val totalSessions: Int = 0,
    val totalVolumeKg: Double = 0.0,
    val totalDurationMinutes: Int = 0,
    val sessionsByDay: Map<String, Int> = emptyMap()
) {
    fun toEntity() = WeeklySummaryEntity(year, week, totalSessions, totalVolumeKg, totalDurationMinutes, sessionsByDay)
}

data class ExerciseProgressModel(
    val exerciseName: String = "",
    val entries: List<ExerciseProgressEntryModel> = emptyList()
) {
    fun toEntity() = ExerciseProgressEntity(exerciseName, entries.map { ExerciseProgressEntryEntity(it.date, it.maxWeightKg, it.totalReps, it.totalVolume) })
}

data class ExerciseProgressEntryModel(
    val date: String = "",
    val maxWeightKg: Double = 0.0,
    val totalReps: Int = 0,
    val totalVolume: Double = 0.0
)