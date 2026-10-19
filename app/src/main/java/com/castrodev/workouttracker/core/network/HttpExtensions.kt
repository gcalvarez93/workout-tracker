// Path: app/src/main/java/com/castrodev/workouttracker/core/network/HttpExtensions.kt
package com.castrodev.workouttracker.core.network

import retrofit2.HttpException
import retrofit2.Response

/**
 * Retrofit no lanza excepción cuando una respuesta `Response<T>` llega con un
 * código HTTP de error. Este helper la lanza para que el repositorio la trate
 * como fallo en lugar de darla por buena.
 */
fun Response<Unit>.requireSuccess() {
    if (!isSuccessful) throw HttpException(this)
}