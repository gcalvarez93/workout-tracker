// Path: app/src/main/java/com/castrodev/workouttracker/core/network/TokenAuthenticator.kt
package com.castrodev.workouttracker.core.network

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

/**
 * Gestiona las respuestas 401 del servidor.
 *
 * 1. En el primer 401 pide a Firebase un token recién renovado y reintenta la petición una vez.
 * 2. Si el servidor vuelve a responder 401 con el token nuevo, o no hay sesión, la sesión ya no
 *    es recuperable: se avisa por [SessionEvents] y se devuelve `null` para que el 401 llegue
 *    al llamador.
 *
 * Si la renovación falla por falta de red, [TokenProvider.refreshToken] lanza [IOException]
 * y la llamada falla como un error de red normal: perder la conexión no cierra la sesión.
 *
 * OkHttp ejecuta los autenticadores en un hilo de red, así que bloquear aquí es seguro.
 */
class TokenAuthenticator(
    private val tokenProvider: TokenProvider,
    private val sessionEvents: SessionEvents
) : Authenticator {

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        // Ya se reintentó con un token renovado y el servidor lo sigue rechazando.
        if (response.attemptCount() > 1) return giveUp()

        val freshToken = tokenProvider.refreshToken() ?: return giveUp()

        return response.request.newBuilder()
            .header("Authorization", "Bearer $freshToken")
            .build()
    }

    private fun giveUp(): Request? {
        sessionEvents.notifyExpired()
        return null
    }

    /** 1 en la respuesta original; 2 o más en los reintentos encadenados. */
    private fun Response.attemptCount(): Int {
        var count = 1
        var prior = priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}