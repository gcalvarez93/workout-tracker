// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/domain/entity/BodyweightEntity.kt
package com.castrodev.workouttracker.features.bodyweight.domain.entity

data class BodyweightEntity(
    val id: String,
    val weightKg: Double,
    val date: String,
    val notes: String
)