// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/domain/repository/BodyweightRepository.kt
package com.castrodev.workouttracker.features.bodyweight.domain.repository

import com.castrodev.workouttracker.features.bodyweight.domain.entity.BodyweightEntity

interface BodyweightRepository {
    suspend fun getHistory(): Result<List<BodyweightEntity>>
    suspend fun logWeight(weightKg: Double, date: String, notes: String): Result<String>
}