// Path: app/src/main/java/com/castrodev/workouttracker/features/sessions/presentation/screen/ActiveSessionScreen.kt
package com.castrodev.workouttracker.features.sessions.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity
import com.castrodev.workouttracker.features.sessions.data.model.*
import com.castrodev.workouttracker.features.sessions.presentation.viewmodel.ActiveSessionState
import com.castrodev.workouttracker.features.sessions.presentation.viewmodel.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSessionScreen(
    sessionId: String,
    routine: RoutineEntity,
    viewModel: SessionViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.activeState.collectAsState()

    // Estado mutable de los sets por ejercicio
    val exerciseStates = remember {
        routine.exercises.map { exercise ->
            exercise to (1..exercise.sets).map { setNum ->
                mutableStateOf(
                    CompleteSessionSetRequest(
                        setNumber   = setNum,
                        reps        = exercise.reps,
                        weightKg    = exercise.weightKg,
                        isCompleted = false
                    )
                )
            }
        }
    }

    LaunchedEffect(state) {
        if (state is ActiveSessionState.Completed) { viewModel.resetActiveSession(); onNavigateBack() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(routine.name) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state is ActiveSessionState.Error) {
                item { Text((state as ActiveSessionState.Error).message, color = MaterialTheme.colorScheme.error) }
            }

            itemsIndexed(exerciseStates) { _, (exercise, sets) ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(exercise.name, style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        sets.forEachIndexed { setIndex, setState ->
                            val set = setState.value
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Set ${set.setNumber}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(48.dp))
                                Text("${set.reps} reps · ${set.weightKg} kg", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                Checkbox(
                                    checked = set.isCompleted,
                                    onCheckedChange = { checked ->
                                        setState.value = set.copy(isCompleted = checked)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val exercises = exerciseStates.mapIndexed { index, (exercise, sets) ->
                            CompleteSessionExerciseRequest(
                                exerciseId  = index.toString(),
                                name        = exercise.name,
                                sets        = sets.map { it.value },
                                isCompleted = sets.any { it.value.isCompleted }
                            )
                        }
                        viewModel.completeSession(sessionId, exercises)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled  = state !is ActiveSessionState.Loading
                ) {
                    if (state is ActiveSessionState.Loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text(stringResource(R.string.complete_session))
                }
            }
        }
    }
}