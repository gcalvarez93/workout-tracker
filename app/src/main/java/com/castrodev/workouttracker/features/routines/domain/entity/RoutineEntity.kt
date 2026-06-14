// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/domain/entity/RoutineEntity.kt
package com.castrodev.workouttracker.features.routines.domain.entity

data class RoutineEntity(
    val id: String,
    val name: String,
    val description: String,
    val exercises: List<ExerciseEntity>,
    val createdAt: String
)

data class ExerciseEntity(
    val name: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val restSeconds: Int
)