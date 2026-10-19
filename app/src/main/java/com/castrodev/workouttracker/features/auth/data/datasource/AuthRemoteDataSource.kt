// Path: app/src/main/java/com/castrodev/workouttracker/features/auth/data/datasource/AuthRemoteDataSource.kt
package com.castrodev.workouttracker.features.auth.data.datasource

import com.castrodev.workouttracker.features.auth.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    suspend fun signIn(email: String, password: String): UserModel {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Sign in returned no user")
        return user.toModel()
    }

    suspend fun signUp(email: String, password: String, name: String): UserModel {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Sign up returned no user")
        user.updateProfile(userProfileChangeRequest { displayName = name }).await()
        return user.toModel(nameOverride = name)
    }

    suspend fun signInWithGoogle(idToken: String): UserModel {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val user = result.user ?: throw IllegalStateException("Google sign in returned no user")
        return user.toModel()
    }

    fun signOut() = auth.signOut()

    fun isAuthenticated(): Boolean = auth.currentUser != null

    fun getCurrentUser(): UserModel? = auth.currentUser?.toModel()

    private fun FirebaseUser.toModel(nameOverride: String? = null) = UserModel(
        id = uid,
        name = nameOverride ?: displayName.orEmpty(),
        email = email.orEmpty(),
        photoUrl = photoUrl?.toString().orEmpty()
    )
}