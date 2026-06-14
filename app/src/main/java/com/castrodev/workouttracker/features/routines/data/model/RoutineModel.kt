// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/data/model/RoutineModel.kt
package com.castrodev.workouttracker.features.routines.data.model

import com.castrodev.workouttracker.features.routines.domain.entity.ExerciseEntity
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity

data class RoutineModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val exercises: List<ExerciseModel> = emptyList(),
    val createdAt: String = ""
) {
    fun toEntity() = RoutineEntity(
        id = id, name = name, description = description,
        exercises = exercises.map { ExerciseEntity(it.name, it.sets, it.reps, it.weightKg, it.restSeconds) },
        createdAt = createdAt
    )
}

data class ExerciseModel(
    val name: String = "",
    val sets: Int = 3,
    val reps: Int = 10,
    val weightKg: Double = 0.0,
    val restSeconds: Int = 60
)

data class CreateRoutineRequest(
    val name: String,
    val description: String,
    val exercises: List<ExerciseModel>
)