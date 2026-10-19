// Path: app/src/test/java/com/castrodev/workouttracker/features/sessions/data/datasource/SessionRemoteDataSourceTest.kt
package com.castrodev.workouttracker.features.sessions.data.datasource

import com.castrodev.workouttracker.features.sessions.data.model.CompleteSessionRequest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class SessionRemoteDataSourceTest {

    private val api = mockk<SessionApiService>()
    private val dataSource = SessionRemoteDataSource(api)
    private val request = CompleteSessionRequest(emptyList())

    @Test
    fun `completeSession no lanza si la API responde con exito`() = runTest {
        coEvery { api.completeSession("s1", request) } returns Response.success(Unit)

        dataSource.completeSession("s1", request)
    }

    @Test
    fun `completeSession lanza HttpException si la API responde con error`() = runTest {
        coEvery { api.completeSession("s1", request) } returns
                Response.error(500, "error".toResponseBody())

        try {
            dataSource.completeSession("s1", request)
            fail("Se esperaba HttpException")
        } catch (e: HttpException) {
            assertEquals(500, e.code())
        }
    }

    @Test
    fun `una respuesta de error se convierte en fallo del repositorio`() = runTest {
        coEvery { api.completeSession(any(), any()) } returns
                Response.error(403, "prohibido".toResponseBody())

        val result = runCatching { dataSource.completeSession("s1", request) }

        assertTrue(result.exceptionOrNull() is HttpException)
    }
}