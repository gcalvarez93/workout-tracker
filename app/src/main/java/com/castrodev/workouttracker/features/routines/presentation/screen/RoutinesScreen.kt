// Path: app/src/main/java/com/castrodev/workouttracker/features/routines/presentation/screen/RoutinesScreen.kt
package com.castrodev.workouttracker.features.routines.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.features.routines.presentation.components.RoutineCard
import com.castrodev.workouttracker.features.routines.presentation.components.RoutineFormSheet
import com.castrodev.workouttracker.features.routines.presentation.viewmodel.RoutineViewModel
import com.castrodev.workouttracker.features.routines.presentation.viewmodel.RoutinesState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    viewModel: RoutineViewModel = viewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val state         by viewModel.routinesState.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()
    var showAddSheet  by remember { mutableStateOf(false) }

    LaunchedEffect(actionSuccess) {
        if (actionSuccess) { showAddSheet = false; viewModel.resetActionSuccess() }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.routines)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is RoutinesState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is RoutinesState.Error   -> Text(s.message, modifier = Modifier.align(Alignment.Center))
                is RoutinesState.Success -> {
                    if (s.routines.isEmpty()) {
                        Text(stringResource(R.string.no_routines), modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(s.routines, key = { it.id }) { routine ->
                                RoutineCard(routine = routine, onClick = { onNavigateToDetail(routine.id) })
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        RoutineFormSheet(onSave = { viewModel.create(it) }, onDismiss = { showAddSheet = false })
    }
}