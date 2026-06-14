// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/data/model/SessionModel.kt
package com.castrodev.workouttracker.features.sessions.data.model

import com.castrodev.workouttracker.features.sessions.domain.entity.*

data class SessionModel(
    val id: String = "",
    val routineId: String = "",
    val routineName: String = "",
    val status: String = "active",
    val startedAt: String = "",
    val completedAt: String? = null,
    val durationMinutes: Int = 0,
    val notes: String = "",
    val exercises: List<SessionExerciseModel> = emptyList()
) {
    fun toEntity() = SessionEntity(
        id = id, routineId = routineId, routineName = routineName,
        status = status, startedAt = startedAt, completedAt = completedAt,
        durationMinutes = durationMinutes, notes = notes,
        exercises = exercises.map { ex ->
            SessionExerciseEntity(ex.name, ex.sets.map { SessionSetEntity(it.reps, it.weightKg, it.completed) })
        }
    )
}

data class SessionExerciseModel(val name: String = "", val sets: List<SessionSetModel> = emptyList())
data class SessionSetModel(val reps: Int = 0, val weightKg: Double = 0.0, val completed: Boolean = false)

data class StartSessionRequest(val routineId: String)

data class CompleteSessionRequest(val exercises: List<CompleteSessionExerciseRequest>)
data class CompleteSessionExerciseRequest(
    val exerciseId: String,
    val name: String,
    val sets: List<CompleteSessionSetRequest>,
    val isCompleted: Boolean
)
data class CompleteSessionSetRequest(
    val setNumber: Int,
    val reps: Int,
    val weightKg: Double,
    val isCompleted: Boolean
)