// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/data/repository/BodyweightRepositoryImpl.kt
package com.castrodev.workouttracker.features.bodyweight.data.repository

import com.castrodev.workouttracker.features.bodyweight.data.datasource.BodyweightRemoteDataSource
import com.castrodev.workouttracker.features.bodyweight.domain.entity.BodyweightEntity
import com.castrodev.workouttracker.features.bodyweight.domain.repository.BodyweightRepository

class BodyweightRepositoryImpl(
    private val dataSource: BodyweightRemoteDataSource = BodyweightRemoteDataSource()
) : BodyweightRepository {
    override suspend fun getHistory(): Result<List<BodyweightEntity>> =
        runCatching { dataSource.getHistory().map { it.toEntity() } }

    override suspend fun logWeight(weightKg: Double, date: String, notes: String): Result<String> =
        runCatching { dataSource.logWeight(weightKg, date, notes) }
}