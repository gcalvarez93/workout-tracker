// Path: app/src/main/java/com/castrodev/workouttracker/navigation/Screen.kt
package com.castrodev.workouttracker.navigation

sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Register : Screen("register")
    object Main     : Screen("main")
}