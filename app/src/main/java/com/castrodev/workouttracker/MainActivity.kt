// Path: app/src/main/java/com/castrodev/workouttracker/MainActivity.kt
package com.castrodev.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.castrodev.workouttracker.core.theme.WorkoutTrackerTheme
import com.castrodev.workouttracker.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkoutTrackerTheme {
                AppNavigation()
            }
        }
    }
}