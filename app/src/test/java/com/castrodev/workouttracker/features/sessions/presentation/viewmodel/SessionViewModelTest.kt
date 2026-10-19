// Path: app/src/test/java/com/castrodev/workouttracker/features/sessions/presentation/viewmodel/SessionViewModelTest.kt
package com.castrodev.workouttracker.features.sessions.presentation.viewmodel

import com.castrodev.workouttracker.features.sessions.domain.entity.CompletedExercise
import com.castrodev.workouttracker.features.sessions.domain.entity.CompletedSet
import com.castrodev.workouttracker.features.sessions.domain.usecase.CompleteSessionUseCase
import com.castrodev.workouttracker.features.sessions.domain.usecase.GetSessionsUseCase
import com.castrodev.workouttracker.features.sessions.domain.usecase.StartSessionUseCase
import com.castrodev.workouttracker.fakes.FakeSessionRepository
import com.castrodev.workouttracker.fakes.testSession
import com.castrodev.workouttracker.util.MainDispatcherRule
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

    private fun viewModel() = SessionViewModel(
        getSessions = GetSessionsUseCase(repository),
        startSession = StartSessionUseCase(repository),
        completeSession = CompleteSessionUseCase(repository)
    )

    @Test
    fun `al crearse carga el historial de sesiones`() {
        repository.sessionsResult = Result.success(listOf(testSession("s1"), testSession("s2")))

        val vm = viewModel()

        val state = vm.sessionsState.value
        assertEquals(listOf("s1", "s2"), (state as SessionsState.Success).sessions.map { it.id })
    }

    @Test
    fun `publica el error cuando falla la carga del historial`() {
        repository.sessionsResult = Result.failure(IOException("sin red"))

        val vm = viewModel()

        assertEquals(SessionsState.Error("sin red"), vm.sessionsState.value)
    }

    @Test
    fun `empezar una sesion publica su identificador`() {
        repository.startResult = Result.success("session-9")
        val vm = viewModel()

        vm.startSession("r1")

        assertEquals(ActiveSessionState.Active("session-9"), vm.activeState.value)
        assertEquals(listOf("r1"), repository.startedRoutineIds)
    }

    @Test
    fun `si falla empezar la sesion publica el error`() {
        repository.startResult = Result.failure(IOException("HTTP 500"))
        val vm = viewModel()

        vm.startSession("r1")

        assertEquals(ActiveSessionState.Error("HTTP 500"), vm.activeState.value)
    }

    @Test
    fun `completar la sesion envia los ejercicios, marca completada y recarga el historial`() {
        val vm = viewModel()

        vm.completeSession("s1", exercises)

        assertEquals(ActiveSessionState.Completed, vm.activeState.value)
        assertEquals(listOf("s1" to exercises), repository.completed)
        assertEquals(2, repository.getSessionsCalls)
    }

    @Test
    fun `si falla completar la sesion publica el error y no recarga`() {
        repository.completeResult = Result.failure(IOException("HTTP 400"))
        val vm = viewModel()

        vm.completeSession("s1", exercises)

        assertEquals(ActiveSessionState.Error("HTTP 400"), vm.activeState.value)
        assertEquals(1, repository.getSessionsCalls)
    }

    @Test
    fun `resetActiveSession vuelve al estado inicial`() {
        val vm = viewModel()
        vm.startSession("r1")

        vm.resetActiveSession()

        assertEquals(ActiveSessionState.Idle, vm.activeState.value)
    }
}