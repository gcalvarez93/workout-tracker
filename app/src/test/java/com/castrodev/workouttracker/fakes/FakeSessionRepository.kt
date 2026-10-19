// Path: app/src/test/java/com/castrodev/workouttracker/fakes/FakeSessionRepository.kt
package com.castrodev.workouttracker.fakes

import com.castrodev.workouttracker.features.sessions.domain.entity.CompletedExercise
import com.castrodev.workouttracker.features.sessions.domain.entity.SessionEntity
import com.castrodev.workouttracker.features.sessions.domain.repository.SessionRepository

fun testSession(id: String = "s1") = SessionEntity(
    id = id,
    routineId = "r1",
    routineName = "Push day",
    status = "completed",
    startedAt = "2026-01-01T10:00:00Z",
    completedAt = "2026-01-01T11:00:00Z",
    durationMinutes = 60,
    notes = "",
    exercises = emptyList()
)

/** Repositorio de sesiones en memoria con resultados configurables y registro de llamadas. */
class FakeSessionRepository : SessionRepository {
    var sessionsResult: Result<List<SessionEntity>> = Result.success(emptyList())
    var startResult: Result<String> = Result.success("s1")
    var completeResult: Result<Unit> = Result.success(Unit)

    var getSessionsCalls = 0
    val startedRoutineIds = mutableListOf<String>()
    val completed = mutableListOf<Pair<String, List<CompletedExercise>>>()

    override suspend fun getSessions(routineId: String?): Result<List<SessionEntity>> {
        getSessionsCalls++
        return sessionsResult
    }

    override suspend fun getSessionById(id: String): Result<SessionEntity> =
        Result.success(testSession(id))

    override suspend fun startSession(routineId: String): Result<String> {
        startedRoutineIds += routineId
        return startResult
    }

    override suspend fun completeSession(id: String, exercises: List<CompletedExercise>): Result<Unit> {
        completed += id to exercises
        return completeResult
    }
}