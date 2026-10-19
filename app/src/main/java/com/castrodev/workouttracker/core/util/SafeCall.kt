// Path: app/src/main/java/com/castrodev/workouttracker/core/util/SafeCall.kt
package com.castrodev.workouttracker.core.util

import kotlinx.coroutines.CancellationException
import timber.log.Timber

/**
 * Ejecuta [block] y devuelve su resultado envuelto en [Result].
 *
 * A diferencia de `runCatching`, no captura [CancellationException]: si la
 * corrutina se cancela, la cancelación se propaga. El resto de errores se
 * registran con Timber indicando el [context] en el que ocurrieron.
 */
suspend fun <T> safeCall(context: String, block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Timber.e(e, context)
        Result.failure(e)
    }