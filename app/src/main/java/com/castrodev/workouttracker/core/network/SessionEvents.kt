// Path: app/src/main/java/com/castrodev/workouttracker/core/network/SessionEvents.kt
package com.castrodev.workouttracker.core.network

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Canal por el que la capa de red avisa de que la sesión ya no es válida.
 *
 * Lo emite [TokenAuthenticator] desde un hilo de OkHttp y lo recoge la navegación para volver
 * al login. Es un canal conflado: si llegan varios avisos seguidos solo queda uno, y si nadie
 * escucha todavía (la app en segundo plano) el aviso espera en vez de perderse.
 */
@Singleton
class SessionEvents @Inject constructor() {

    private val channel = Channel<Unit>(Channel.CONFLATED)

    /** Cada elemento es un aviso de "sesión caducada". Pensado para un único consumidor. */
    val expired: Flow<Unit> = channel.receiveAsFlow()

    fun notifyExpired() {
        channel.trySend(Unit)
    }
}