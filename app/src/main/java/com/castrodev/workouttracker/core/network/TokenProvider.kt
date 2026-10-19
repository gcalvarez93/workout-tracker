// Path: app/src/main/java/com/castrodev/workouttracker/core/network/TokenProvider.kt
package com.castrodev.workouttracker.core.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.util.concurrent.ExecutionException
import javax.inject.Inject

/** Abstracción del origen del token, para poder sustituirlo en los tests. */
interface TokenProvider {
    /**
     * Devuelve el ID token del usuario o `null` si no hay sesión o falla la obtención.
     *
     * Bloquea el hilo que lo llama: usar solo desde un hilo de red, como el de un
     * interceptor de OkHttp, nunca desde el hilo principal.
     */
    fun getToken(): String?
}

class FirebaseTokenProvider @Inject constructor(
    private val auth: FirebaseAuth
) : TokenProvider {
    override fun getToken(): String? {
        val user = auth.currentUser ?: return null
        return try {
            Tasks.await(user.getIdToken(false)).token
        } catch (e: ExecutionException) {
            Timber.w(e, "FirebaseTokenProvider.getToken")
            null
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            Timber.w(e, "FirebaseTokenProvider.getToken interrupted")
            null
        }
    }
}