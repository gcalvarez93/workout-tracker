// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/data/repository/BodyweightRepositoryImpl.kt
package com.castrodev.workouttracker.features.bodyweight.data.repository

import com.castrodev.workouttracker.core.util.safeCall
import com.castrodev.workouttracker.features.bodyweight.data.datasource.BodyweightRemoteDataSource
import com.castrodev.workouttracker.features.bodyweight.domain.entity.BodyweightEntity
import com.castrodev.workouttracker.features.bodyweight.domain.repository.BodyweightRepository
import javax.inject.Inject

class BodyweightRepositoryImpl @Inject constructor(
    private val dataSource: BodyweightRemoteDataSource
) : BodyweightRepository {
    override suspend fun getHistory(): Result<List<BodyweightEntity>> =
        safeCall("BodyweightRepositoryImpl.getHistory") {
            dataSource.getHistory().map { it.toEntity() }
        }

    override suspend fun logWeight(weightKg: Double, date: String, notes: String): Result<String> =
        safeCall("BodyweightRepositoryImpl.logWeight") {
            dataSource.logWeight(weightKg, date, notes)
        }
}