// Path: app/src/test/java/com/castrodev/workouttracker/features/routines/presentation/viewmodel/RoutineViewModelTest.kt
package com.castrodev.workouttracker.features.routines.presentation.viewmodel

import com.castrodev.workouttracker.features.routines.domain.usecase.CreateRoutineUseCase
import com.castrodev.workouttracker.features.routines.domain.usecase.DeleteRoutineUseCase
import com.castrodev.workouttracker.features.routines.domain.usecase.GetRoutineByIdUseCase
import com.castrodev.workouttracker.features.routines.domain.usecase.GetRoutinesUseCase
import com.castrodev.workouttracker.features.routines.domain.usecase.UpdateRoutineUseCase
import com.castrodev.workouttracker.fakes.FakeRoutineRepository
import com.castrodev.workouttracker.fakes.testRoutine
import com.castrodev.workouttracker.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class RoutineViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeRoutineRepository

    @Before
    fun setUp() {
        repository = FakeRoutineRepository()
    }

    private fun viewModel() = RoutineViewModel(
        getRoutines = GetRoutinesUseCase(repository),
        getRoutineById = GetRoutineByIdUseCase(repository),
        createRoutine = CreateRoutineUseCase(repository),
        updateRoutine = UpdateRoutineUseCase(repository),
        deleteRoutine = DeleteRoutineUseCase(repository)
    )

    @Test
    fun `al crearse carga las rutinas`() {
        val routines = listOf(testRoutine("r1"), testRoutine("r2"))
        repository.routinesResult = Result.success(routines)

        val vm = viewModel()

        assertEquals(RoutinesState.Success(routines), vm.routinesState.value)
    }

    @Test
    fun `publica el error cuando falla la carga`() {
        repository.routinesResult = Result.failure(IOException("sin red"))

        val vm = viewModel()

        assertEquals(RoutinesState.Error("sin red"), vm.routinesState.value)
    }

    @Test
    fun `crear una rutina marca la accion como correcta y recarga la lista`() {
        val vm = viewModel()

        vm.create(testRoutine(name = "Pierna"))

        assertTrue(vm.actionSuccess.value)
        assertEquals(listOf("Pierna"), repository.created.map { it.name })
        assertEquals(2, repository.getRoutinesCalls)
    }

    @Test
    fun `crear una rutina sin nombre publica el error de validacion`() {
        val vm = viewModel()

        vm.create(testRoutine(name = ""))

        assertEquals(RoutinesState.Error("Name cannot be empty"), vm.routinesState.value)
        assertFalse(vm.actionSuccess.value)
        assertTrue(repository.created.isEmpty())
    }

    @Test
    fun `borrar una rutina llama al repositorio y recarga`() {
        val vm = viewModel()

        vm.delete("r1")

        assertEquals(listOf("r1"), repository.deletedIds)
        assertEquals(2, repository.getRoutinesCalls)
    }

    @Test
    fun `si falla el borrado publica el error y no recarga`() {
        repository.deleteResult = Result.failure(IOException("HTTP 500"))
        val vm = viewModel()

        vm.delete("r1")

        assertEquals(RoutinesState.Error("HTTP 500"), vm.routinesState.value)
        assertEquals(1, repository.getRoutinesCalls)
    }

    @Test
    fun `resetActionSuccess limpia la marca`() {
        val vm = viewModel()
        vm.create(testRoutine())

        vm.resetActionSuccess()

        assertFalse(vm.actionSuccess.value)
    }
}