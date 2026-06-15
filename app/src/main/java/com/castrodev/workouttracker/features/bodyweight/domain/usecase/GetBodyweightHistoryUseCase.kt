// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/domain/usecase/GetBodyweightHistoryUseCase.kt
package com.castrodev.workouttracker.features.bodyweight.domain.usecase
import com.castrodev.workouttracker.features.bodyweight.domain.repository.BodyweightRepository
class GetBodyweightHistoryUseCase(private val repository: BodyweightRepository) {
    suspend operator fun invoke() = repository.getHistory()
}