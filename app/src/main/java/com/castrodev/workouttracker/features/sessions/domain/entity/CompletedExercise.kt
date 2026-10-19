// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/domain/entity/CompletedExercise.kt
package com.castrodev.workouttracker.features.sessions.domain.entity

/** Resultado de un ejercicio al cerrar una sesión de entrenamiento. */
data class CompletedExercise(
    val id: String,
    val name: String,
    val sets: List<CompletedSet>,
    val isCompleted: Boolean
)

data class CompletedSet(
    val setNumber: Int,
    val reps: Int,
    val weightKg: Double,
    val isCompleted: Boolean
)