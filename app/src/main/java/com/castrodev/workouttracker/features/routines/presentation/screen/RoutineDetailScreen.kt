// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/presentation/screen/RoutineDetailScreen.kt
package com.castrodev.workouttracker.features.routines.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.features.routines.presentation.components.RoutineFormSheet
import com.castrodev.workouttracker.features.routines.presentation.viewmodel.RoutineDetailState
import com.castrodev.workouttracker.features.routines.presentation.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(
    routineId: String,
    viewModel: RoutineViewModel,
    onNavigateBack: () -> Unit,
    onStartSession: (String) -> Unit
) {
    val state         by viewModel.detailState.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()
    var showEditSheet    by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(routineId) { viewModel.loadRoutineById(routineId) }
    LaunchedEffect(actionSuccess) {
        if (actionSuccess) { showEditSheet = false; viewModel.resetActionSuccess(); viewModel.loadRoutineById(routineId) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = {
                    if (state is RoutineDetailState.Success) {
                        IconButton(onClick = { showEditSheet = true }) { Icon(Icons.Default.Edit, null) }
                        IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is RoutineDetailState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is RoutineDetailState.Error   -> Text(s.message, modifier = Modifier.align(Alignment.Center))
                is RoutineDetailState.Success -> {
                    val routine = s.routine
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            Text(routine.name, style = MaterialTheme.typography.headlineMedium)
                            if (routine.description.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(routine.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(stringResource(R.string.exercises), style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                        }
                        items(routine.exercises) { exercise ->
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(exercise.name, style = MaterialTheme.typography.titleSmall)
                                    Spacer(Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Text("${exercise.sets} ${stringResource(R.string.sets)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        Text("${exercise.reps} ${stringResource(R.string.reps)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        if (exercise.weightKg > 0) Text("${exercise.weightKg} ${stringResource(R.string.kg)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick  = { onStartSession(routineId) },
                                modifier = Modifier.fillMaxWidth().height(52.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.start_session))
                            }
                        }
                    }

                    if (showEditSheet) {
                        RoutineFormSheet(routine = routine, onSave = { viewModel.update(it) }, onDismiss = { showEditSheet = false })
                    }
                }
                else -> Unit
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_routine)) },
            text  = { Text(stringResource(R.string.delete_routine_confirm)) },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(routineId); showDeleteDialog = false; onNavigateBack() }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}