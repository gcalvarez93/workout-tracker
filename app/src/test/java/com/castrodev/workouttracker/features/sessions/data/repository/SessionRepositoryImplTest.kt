// Path: app/src/test/java/com/castrodev/workouttracker/features/sessions/data/repository/SessionRepositoryImplTest.kt
package com.castrodev.workouttracker.features.sessions.data.repository

import com.castrodev.workouttracker.features.sessions.data.datasource.SessionRemoteDataSource
import com.castrodev.workouttracker.features.sessions.data.model.CompleteSessionRequest
import com.castrodev.workouttracker.features.sessions.data.model.SessionExerciseModel
import com.castrodev.workouttracker.features.sessions.data.model.SessionModel
import com.castrodev.workouttracker.features.sessions.data.model.SessionSetModel
import com.castrodev.workouttracker.features.sessions.domain.entity.CompletedExercise
import com.castrodev.workouttracker.features.sessions.domain.entity.CompletedSet
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SessionRepositoryImplTest {

    private lateinit var dataSource: SessionRemoteDataSource
    private lateinit var repository: SessionRepositoryImpl

    @Before
    fun setUp() {
        dataSource = mockk()
        repository = SessionRepositoryImpl(dataSource)
    }

    @Test
    fun `getSessions convierte los modelos de la API en entidades`() = runTest {
        coEvery { dataSource.getSessions(null) } returns listOf(
            SessionModel(
                id = "s1",
                routineName = "Push day",
                durationMinutes = 45,
                exercises = listOf(
                    SessionExerciseModel("Press banca", listOf(SessionSetModel(reps = 10, weightKg = 60.0, completed = true)))
                )
            )
        )

        val session = repository.getSessions(null).getOrThrow().single()

        assertEquals("s1", session.id)
        assertEquals("Push day", session.routineName)
        assertEquals(45, session.durationMinutes)
        val set = session.exercises.single().sets.single()
        assertEquals(10, set.reps)
        assertEquals(60.0, set.weightKg, 0.0)
        assertTrue(set.completed)
    }

    @Test
    fun `getSessions devuelve fallo cuando falla la red`() = runTest {
        val error = IOException("sin red")
        coEvery { dataSource.getSessions(any()) } throws error

        assertEquals(error, repository.getSessions(null).exceptionOrNull())
    }

    @Test
    fun `completeSession traduce los ejercicios del dominio a la peticion de la API`() = runTest {
        val request = slot<CompleteSessionRequest>()
        coEvery { dataSource.completeSession("s1", capture(request)) } returns Unit

        val result = repository.completeSession(
            "s1",
            listOf(
                CompletedExercise(
                    id = "0",
                    name = "Press banca",
                    sets = listOf(CompletedSet(setNumber = 1, reps = 8, weightKg = 62.5, isCompleted = true)),
                    isCompleted = true
                )
            )
        )

        assertTrue(result.isSuccess)
        val exercise = request.captured.exercises.single()
        assertEquals("0", exercise.exerciseId)
        assertEquals("Press banca", exercise.name)
        assertTrue(exercise.isCompleted)
        val set = exercise.sets.single()
        assertEquals(1, set.setNumber)
        assertEquals(8, set.reps)
        assertEquals(62.5, set.weightKg, 0.0)
        assertTrue(set.isCompleted)
    }

    @Test
    fun `completeSession devuelve fallo si el datasource lanza`() = runTest {
        coEvery { dataSource.completeSession(any(), any()) } throws IOException("HTTP 500")

        assertTrue(repository.completeSession("s1", emptyList()).isFailure)
    }

    @Test
    fun `una cancelacion de la corrutina no se convierte en fallo`() = runTest {
        coEvery { dataSource.getSessionById("s1") } throws CancellationException("cancelada")

        try {
            repository.getSessionById("s1")
            fail("Se esperaba que la cancelacion se propagara")
        } catch (e: CancellationException) {
            assertEquals("cancelada", e.message)
        }
    }
}