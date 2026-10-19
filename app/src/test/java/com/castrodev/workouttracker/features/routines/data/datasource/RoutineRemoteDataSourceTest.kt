// Path: app/src/test/java/com/castrodev/workouttracker/features/routines/data/datasource/RoutineRemoteDataSourceTest.kt
package com.castrodev.workouttracker.features.routines.data.datasource

import com.castrodev.workouttracker.fakes.testRoutine
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import com.castrodev.workouttracker.features.routines.data.model.CreateRoutineRequest

class RoutineRemoteDataSourceTest {

    private val api = mockk<RoutineApiService>()
    private val dataSource = RoutineRemoteDataSource(api)

    @Test
    fun `deleteRoutine lanza HttpException si la API responde con error`() = runTest {
        coEvery { api.deleteRoutine("r1") } returns Response.error(404, "no existe".toResponseBody())

        try {
            dataSource.deleteRoutine("r1")
            fail("Se esperaba HttpException")
        } catch (e: HttpException) {
            assertEquals(404, e.code())
        }
    }

    @Test
    fun `updateRoutine lanza HttpException si la API responde con error`() = runTest {
        coEvery { api.updateRoutine(any(), any()) } returns Response.error(500, "error".toResponseBody())

        try {
            dataSource.updateRoutine(testRoutine())
            fail("Se esperaba HttpException")
        } catch (e: HttpException) {
            assertEquals(500, e.code())
        }
    }

    @Test
    fun `createRoutine envia a la API los datos de la rutina`() = runTest {
        val request = slot<CreateRoutineRequest>()
        coEvery { api.createRoutine(capture(request)) } returns "new-id"

        val id = dataSource.createRoutine(testRoutine(name = "Pierna"))

        assertEquals("new-id", id)
        assertEquals("Pierna", request.captured.name)
        val exercise = request.captured.exercises.single()
        assertEquals("Press banca", exercise.name)
        assertEquals(60.0, exercise.weightKg, 0.0)
        assertEquals(90, exercise.restSeconds)
    }
}