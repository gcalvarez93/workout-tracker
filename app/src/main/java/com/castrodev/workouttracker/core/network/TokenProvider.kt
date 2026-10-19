// Path: app/src/main/java/com/castrodev/workouttracker/core/network/TokenProvider.kt
package com.castrodev.workouttracker.core.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

/** Abstracción del origen del token, para poder sustituirlo en los tests. */
interface TokenProvider {
    /** Devuelve el ID token del usuario o `null` si no hay sesión o falla la obtención. */
    suspend fun getToken(): String?
}

class FirebaseTokenProvider @Inject constructor(
    private val auth: FirebaseAuth
) : TokenProvider {
    override suspend fun getToken(): String? =
        try {
            auth.currentUser?.getIdToken(false)?.await()?.token
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.w(e, "FirebaseTokenProvider.getToken")
            null
        }
}