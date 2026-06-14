// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/domain/entity/SessionEntity.kt
package com.castrodev.workouttracker.features.sessions.domain.entity

data class SessionEntity(
    val id: String,
    val routineId: String,
    val routineName: String,
    val status: String,
    val startedAt: String,
    val completedAt: String?,
    val durationMinutes: Int,
    val notes: String,
    val exercises: List<SessionExerciseEntity>
)

data class SessionExerciseEntity(
    val name: String,
    val sets: List<SessionSetEntity>
)

data class SessionSetEntity(
    val reps: Int,
    val weightKg: Double,
    val completed: Boolean
)