// Path: app/src/main/java/com/castrodev/workouttracker/features/profile/data/repository/ProfileRepositoryImpl.kt
package com.castrodev.workouttracker.features.profile.data.repository

import com.castrodev.workouttracker.features.profile.data.datasource.ProfileRemoteDataSource
import com.castrodev.workouttracker.features.profile.domain.entity.ProfileEntity
import com.castrodev.workouttracker.features.profile.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val dataSource: ProfileRemoteDataSource = ProfileRemoteDataSource()
) : ProfileRepository {
    override suspend fun getProfile(): Result<ProfileEntity> =
        runCatching { dataSource.getProfile().toEntity() }

    override suspend fun updateProfile(name: String, language: String, notifications: Boolean): Result<ProfileEntity> =
        runCatching { dataSource.updateProfile(name, language, notifications).toEntity() }
}