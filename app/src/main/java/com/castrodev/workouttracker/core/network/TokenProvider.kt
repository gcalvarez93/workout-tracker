// Path: app/src/main/java/com/castrodev/workouttracker/core/network/TokenProvider.kt
package com.castrodev.workouttracker.core.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.io.IOException
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

    /**
     * Fuerza la renovación del token y lo devuelve, o `null` si no hay sesión o Firebase la
     * rechaza (usuario deshabilitado, sesión revocada).
     *
     * Lanza [IOException] si la renovación falla por falta de red: no es un problema de la
     * sesión y quien llama no debe darla por caducada.
     *
     * Bloquea el hilo que lo llama: usar solo desde un hilo de red.
     */
    @Throws(IOException::class)
    fun refreshToken(): String?
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

    override fun refreshToken(): String? {
        val user = auth.currentUser ?: return null
        return try {
            Tasks.await(user.getIdToken(true)).token
        } catch (e: ExecutionException) {
            if (e.cause is FirebaseNetworkException) {
                throw IOException("Token refresh failed: no network", e)
            }
            Timber.w(e, "FirebaseTokenProvider.refreshToken")
            null
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            Timber.w(e, "FirebaseTokenProvider.refreshToken interrupted")
            null
        }
    }
}