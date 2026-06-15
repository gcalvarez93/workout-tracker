// Path: app/src/main/java/com/castrodev/workouttracker/features/bodyweight/presentation/components/BodyweightCard.kt
package com.castrodev.workouttracker.features.bodyweight.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.features.bodyweight.domain.entity.BodyweightEntity

@Composable
fun BodyweightCard(
    entry: BodyweightEntity,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.date.take(10), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                if (entry.notes.isNotBlank()) {
                    Text(entry.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            Text(
                text = "${entry.weightKg} ${stringResource(R.string.kg)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBodyweightDialog(
    onSave: (Double, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var weight by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var notes  by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    val today  = java.time.LocalDate.now().toString()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_bodyweight)) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value         = weight,
                    onValueChange = { weight = it },
                    label         = { Text(stringResource(R.string.kg)) },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true
                )
                OutlinedTextField(
                    value         = notes,
                    onValueChange = { notes = it },
                    label         = { Text(stringResource(R.string.notes)) },
                    modifier      = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick  = { weight.toDoubleOrNull()?.let { onSave(it, today, notes) } },
                enabled  = weight.toDoubleOrNull() != null
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}