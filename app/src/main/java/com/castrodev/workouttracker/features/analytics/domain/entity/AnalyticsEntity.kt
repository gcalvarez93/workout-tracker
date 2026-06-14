// Path: app/src/main/java/com/castrodev/workouttracker/features/analytics/domain/entity/AnalyticsEntity.kt
package com.castrodev.workouttracker.features.analytics.domain.entity

data class WeeklySummaryEntity(
    val year: Int,
    val week: Int,
    val totalSessions: Int,
    val totalVolumeKg: Double,
    val totalDurationMinutes: Int,
    val sessionsByDay: Map<String, Int>
)

data class ExerciseProgressEntity(
    val exerciseName: String,
    val entries: List<ExerciseProgressEntryEntity>
)

data class ExerciseProgressEntryEntity(
    val date: String,
    val maxWeightKg: Double,
    val totalReps: Int,
    val totalVolume: Double
)