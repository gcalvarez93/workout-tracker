// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/domain/usecase/AddBodyweightUseCase.kt
package com.castrodev.workouttracker.features.bodyweight.domain.usecase
import com.castrodev.workouttracker.features.bodyweight.domain.repository.BodyweightRepository
class AddBodyweightUseCase(private val repository: BodyweightRepository) {
    suspend operator fun invoke(weightKg: Double, date: String, notes: String) =
        repository.logWeight(weightKg, date, notes)
}