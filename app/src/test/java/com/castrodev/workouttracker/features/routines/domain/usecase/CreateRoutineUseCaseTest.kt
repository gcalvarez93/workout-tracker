// Path: app/src/test/java/com/castrodev/workouttracker/features/routines/domain/usecase/CreateRoutineUseCaseTest.kt
package com.castrodev.workouttracker.features.routines.domain.usecase

import com.castrodev.workouttracker.core.error.AppError
import com.castrodev.workouttracker.fakes.FakeRoutineRepository
import com.castrodev.workouttracker.fakes.testRoutine
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CreateRoutineUseCaseTest {

    private lateinit var repository: FakeRoutineRepository
    private lateinit var useCase: CreateRoutineUseCase

    @Before
    fun setUp() {
        repository = FakeRoutineRepository()
        useCase = CreateRoutineUseCase(repository)
    }

    @Test
    fun `rechaza una rutina sin nombre con un error de validacion y no llama al repositorio`() = runTest {
        val error = useCase(testRoutine(name = "")).exceptionOrNull()

        assertTrue(error is AppError.Validation)
        assertEquals(AppError.Validation.Reason.RoutineNameRequired, (error as AppError.Validation).reason)
        assertTrue(repository.created.isEmpty())
    }

    @Test
    fun `rechaza un nombre formado solo por espacios`() = runTest {
        val result = useCase(testRoutine(name = "   "))

        assertTrue(result.exceptionOrNull() is AppError.Validation)
        assertTrue(repository.created.isEmpty())
    }

    @Test
    fun `delega en el repositorio cuando la rutina es valida`() = runTest {
        repository.createResult = Result.success("abc")
        val routine = testRoutine(name = "Pierna")

        val result = useCase(routine)

        assertEquals("abc", result.getOrThrow())
        assertEquals(listOf(routine), repository.created)
    }
}