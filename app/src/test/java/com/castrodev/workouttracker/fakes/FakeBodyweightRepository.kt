// Path: app/src/test/java/com/castrodev/workouttracker/fakes/FakeBodyweightRepository.kt
package com.castrodev.workouttracker.fakes

import com.castrodev.workouttracker.features.bodyweight.domain.entity.BodyweightEntity
import com.castrodev.workouttracker.features.bodyweight.domain.repository.BodyweightRepository

fun testBodyweight(id: String = "b1", weightKg: Double = 80.0) = BodyweightEntity(
    id = id,
    weightKg = weightKg,
    date = "2026-01-01",
    notes = ""
)

/** Repositorio de peso corporal en memoria con resultados configurables y registro de llamadas. */
class FakeBodyweightRepository : BodyweightRepository {
    var historyResult: Result<List<BodyweightEntity>> = Result.success(emptyList())
    var logResult: Result<String> = Result.success("new-id")

    var getHistoryCalls = 0
    val logged = mutableListOf<Triple<Double, String, String>>()

    override suspend fun getHistory(): Result<List<BodyweightEntity>> {
        getHistoryCalls++
        return historyResult
    }

    override suspend fun logWeight(weightKg: Double, date: String, notes: String): Result<String> {
        logged += Triple(weightKg, date, notes)
        return logResult
    }
}