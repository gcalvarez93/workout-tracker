// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/presentation/components/RoutineFormSheet.kt
package com.castrodev.workouttracker.features.routines.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.features.routines.domain.entity.ExerciseEntity
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity

private data class ExerciseForm(
    val name: String = "",
    val sets: String = "3",
    val reps: String = "10",
    val weightKg: String = "0",
    val restSeconds: String = "60"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineFormSheet(
    routine: RoutineEntity? = null,
    onSave: (RoutineEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name        by remember { mutableStateOf(routine?.name ?: "") }
    var description by remember { mutableStateOf(routine?.description ?: "") }
    var exercises: List<ExerciseForm> by remember {
        mutableStateOf(
            routine?.exercises?.map { ExerciseForm(it.name, it.sets.toString(), it.reps.toString(), it.weightKg.toString(), it.restSeconds.toString()) }
                ?: emptyList()
        )
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
        ) {
            Text(
                text  = if (routine == null) stringResource(R.string.add_routine) else stringResource(R.string.edit_routine),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.routine_name)) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.description)) }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Spacer(Modifier.height(16.dp))

            Text(stringResource(R.string.exercises), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            exercises.forEachIndexed { index, exercise ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = exercise.name,
                                onValueChange = { new ->
                                    val list = exercises.toMutableList()
                                    list[index] = exercise.copy(name = new)
                                    exercises = list
                                },
                                label    = { Text(stringResource(R.string.name)) },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                val list = exercises.toMutableList()
                                list.removeAt(index)
                                exercises = list
                            }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = exercise.sets,
                                onValueChange = { new -> val l = exercises.toMutableList(); l[index] = exercise.copy(sets = new); exercises = l },
                                label = { Text(stringResource(R.string.sets)) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = exercise.reps,
                                onValueChange = { new -> val l = exercises.toMutableList(); l[index] = exercise.copy(reps = new); exercises = l },
                                label = { Text(stringResource(R.string.reps)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = exercise.weightKg,
                                onValueChange = { new -> val l = exercises.toMutableList(); l[index] = exercise.copy(weightKg = new); exercises = l },
                                label = { Text(stringResource(R.string.weight)) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = exercise.restSeconds,
                                onValueChange = { new -> val l = exercises.toMutableList(); l[index] = exercise.copy(restSeconds = new); exercises = l },
                                label = { Text(stringResource(R.string.rest_seconds)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            TextButton(onClick = { exercises = exercises + listOf(ExerciseForm()) }) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.add_exercise))
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    onSave(RoutineEntity(
                        id          = routine?.id ?: "",
                        name        = name,
                        description = description,
                        exercises   = exercises.map { ExerciseEntity(
                            name        = it.name,
                            sets        = it.sets.toIntOrNull() ?: 3,
                            reps        = it.reps.toIntOrNull() ?: 10,
                            weightKg    = it.weightKg.toDoubleOrNull() ?: 0.0,
                            restSeconds = it.restSeconds.toIntOrNull() ?: 60
                        )},
                        createdAt = routine?.createdAt ?: ""
                    ))
                },
                enabled  = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.save)) }
            Spacer(Modifier.height(32.dp))
        }
    }
}