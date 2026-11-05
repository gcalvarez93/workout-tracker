// Path: app/src/test/java/com/castrodev/workouttracker/features/sessions/presentation/viewmodel/SessionViewModelTest.kt
package com.castrodev.workouttracker.features.sessions.presentation.viewmodel

import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.core.error.AppError
import com.castrodev.workouttracker.fakes.FakeSessionRepository
import com.castrodev.workouttracker.fakes.testSession
import com.castrodev.workouttracker.features.sessions.domain.entity.CompletedExercise
import com.castrodev.workouttracker.features.sessions.domain.entity.CompletedSet
import com.castrodev.workouttracker.features.sessions.domain.entity.SessionEntity
import com.castrodev.workouttracker.features.sessions.domain.repository.SessionRepository
import com.castrodev.workouttracker.features.sessions.domain.usecase.CompleteSessionUseCase
import com.castrodev.workouttracker.features.sessions.domain.usecase.GetSessionsUseCase
import com.castrodev.workouttracker.features.sessions.domain.usecase.StartSessionUseCase
import com.castrodev.workouttracker.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class SessionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeSessionRepository

    private val exercises = listOf(
        CompletedExercise(
            id = "0",
            name = "Press banca",
            sets = listOf(CompletedSet(setNumber = 1, reps = 10, weightKg = 60.0, isCompleted = true)),
            isCompleted = true
        )
    )

    @Before
    fun setUp() {
        repository = FakeSessionRepository()
    }

    private fun viewModel(source: SessionRepository = repository) = SessionViewModel(
        getSessions = GetSessionsUseCase(source),
        startSession = StartSessionUseCase(source),
        completeSession = CompleteSessionUseCase(source)
    )

    // --- Historial -------------------------------------------------------------------------

    @Test
    fun `al crearse carga el historial de sesiones`() {
        repository.sessionsResult = Result.success(listOf(testSession("s1"), testSession("s2")))

        val vm = viewModel()

        val state = vm.sessionsState.value
        assertEquals(listOf("s1", "s2"), (state as SessionsState.Success).sessions.map { it.id })
    }

    @Test
    fun `si la carga falla por la red publica el aviso de red`() {
        repository.sessionsResult = Result.failure(IOException("sin red"))

        val vm = viewModel()

        assertEquals(SessionsState.Error(R.string.error_network), vm.sessionsState.value)
    }

    @Test
    fun `el mensaje tecnico de una excepcion nunca se muestra`() {
        repository.sessionsResult = Result.failure(IllegalStateException("Cannot invoke method on null at Foo.kt:42"))

        val vm = viewModel()

        assertEquals(SessionsState.Error(R.string.error_unknown), vm.sessionsState.value)
    }

    @Test
    fun `gana siempre la ultima carga del historial`() {
        val first = CompletableDeferred<Result<List<SessionEntity>>>()
        var calls = 0
        val newest = listOf(testSession("nueva"))
        val source = object : SessionRepository by repository {
            override suspend fun getSessions(routineId: String?): Result<List<SessionEntity>> =
                if (++calls == 1) first.await() else Result.success(newest)
        }
        val vm = viewModel(source)

        vm.loadSessions()
        first.complete(Result.success(listOf(testSession("vieja"))))

        assertEquals(SessionsState.Success(newest), vm.sessionsState.value)
    }

    // --- Empezar ---------------------------------------------------------------------------

    @Test
    fun `empezar una sesion publica su identificador`() {
        repository.startResult = Result.success("session-9")
        val vm = viewModel()

        vm.startSession("r1")

        assertEquals(ActiveSessionState.Active("session-9"), vm.activeState.value)
        assertEquals(listOf("r1"), repository.startedRoutineIds)
    }

    @Test
    fun `si falla empezar la sesion publica el aviso`() {
        repository.startResult = Result.failure(AppError.Server())
        val vm = viewModel()

        vm.startSession("r1")

        assertEquals(ActiveSessionState.Error(R.string.error_server), vm.activeState.value)
    }

    @Test
    fun `un segundo toque mientras se inicia la sesion se ignora`() {
        val gate = CompletableDeferred<Result<String>>()
        var calls = 0
        val source = object : SessionRepository by repository {
            override suspend fun startSession(routineId: String): Result<String> {
                calls++
                return gate.await()
            }
        }
        val vm = viewModel(source)

        vm.startSession("r1")
        vm.startSession("r1")

        assertEquals(1, calls)
        assertEquals(ActiveSessionState.Loading, vm.activeState.value)

        gate.complete(Result.success("s1"))
        assertEquals(ActiveSessionState.Active("s1"), vm.activeState.value)
    }

    // --- Completar -------------------------------------------------------------------------

    @Test
    fun `completar la sesion envia los ejercicios, marca completada y recarga el historial`() {
        val vm = viewModel()

        vm.completeSession("s1", exercises)

        assertEquals(ActiveSessionState.Completed, vm.activeState.value)
        assertEquals(listOf("s1" to exercises), repository.completed)
        assertEquals(2, repository.getSessionsCalls)
    }

    @Test
    fun `si falla completar la sesion publica el aviso y no recarga`() {
        repository.completeResult = Result.failure(IOException("sin red"))
        val vm = viewModel()

        vm.completeSession("s1", exercises)

        assertEquals(ActiveSessionState.Error(R.string.error_network), vm.activeState.value)
        assertEquals(1, repository.getSessionsCalls)
    }

    @Test
    fun `tras un fallo al completar se puede reintentar con los mismos ejercicios`() {
        repository.completeResult = Result.failure(IOException("sin red"))
        val vm = viewModel()
        vm.completeSession("s1", exercises)

        repository.completeResult = Result.success(Unit)
        vm.completeSession("s1", exercises)

        assertEquals(ActiveSessionState.Completed, vm.activeState.value)
        assertEquals(listOf("s1" to exercises, "s1" to exercises), repository.completed)
    }

    @Test
    fun `un segundo toque mientras se completa la sesion se ignora`() {
        val gate = CompletableDeferred<Result<Unit>>()
        var calls = 0
        val source = object : SessionRepository by repository {
            override suspend fun completeSession(id: String, exercises: List<CompletedExercise>): Result<Unit> {
                calls++
                return gate.await()
            }
        }
        val vm = viewModel(source)

        vm.completeSession("s1", exercises)
        vm.completeSession("s1", exercises)

        assertEquals(1, calls)
        assertEquals(ActiveSessionState.Loading, vm.activeState.value)

        gate.complete(Result.success(Unit))
        assertEquals(ActiveSessionState.Completed, vm.activeState.value)
    }

    @Test
    fun `resetActiveSession vuelve al estado inicial`() {
        val vm = viewModel()
        vm.startSession("r1")

        vm.resetActiveSession()

        assertEquals(ActiveSessionState.Idle, vm.activeState.value)
    }
}