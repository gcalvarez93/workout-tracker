// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/data/model/BodyweightModel.kt
package com.castrodev.workouttracker.features.bodyweight.data.model

import com.castrodev.workouttracker.features.bodyweight.domain.entity.BodyweightEntity

data class BodyweightModel(
    val id: String = "",
    val weightKg: Double = 0.0,
    val date: String = "",
    val notes: String = ""
) {
    fun toEntity() = BodyweightEntity(id = id, weightKg = weightKg, date = date, notes = notes)
}

data class LogBodyweightRequest(
    val weightKg: Double,
    val date: String,
    val notes: String
)