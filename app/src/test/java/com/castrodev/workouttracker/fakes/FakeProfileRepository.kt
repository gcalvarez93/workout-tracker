// Path: app/src/test/java/com/castrodev/workouttracker/fakes/FakeProfileRepository.kt
package com.castrodev.workouttracker.fakes

import com.castrodev.workouttracker.features.profile.domain.entity.ProfileEntity
import com.castrodev.workouttracker.features.profile.domain.repository.ProfileRepository

fun testProfile(name: String = "Ana", notifications: Boolean = true) = ProfileEntity(
    id = "u1",
    name = name,
    email = "ana@test.com",
    photoUrl = "",
    language = "es",
    notifications = notifications
)

/** Repositorio de perfil en memoria con resultados configurables y registro de llamadas. */
class FakeProfileRepository : ProfileRepository {
    var profileResult: Result<ProfileEntity> = Result.success(testProfile())
    var updateResult: Result<ProfileEntity> = Result.success(testProfile())

    var getProfileCalls = 0
    val updates = mutableListOf<Triple<String, String, Boolean>>()

    override suspend fun getProfile(): Result<ProfileEntity> {
        getProfileCalls++
        return profileResult
    }

    override suspend fun updateProfile(name: String, language: String, notifications: Boolean): Result<ProfileEntity> {
        updates += Triple(name, language, notifications)
        return updateResult
    }
}