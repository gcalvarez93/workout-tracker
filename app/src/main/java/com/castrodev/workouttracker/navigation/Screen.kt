// Path: app/src/main/java/com/castrodev/workouttracker/navigation/Screen.kt
package com.castrodev.workouttracker.navigation

sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Register : Screen("register")
    object Main     : Screen("main")
}

sealed class InnerScreen(val route: String) {
    object RoutineDetail : InnerScreen("routine_detail/{routineId}") {
        fun createRoute(routineId: String) = "routine_detail/$routineId"
    }
    object ActiveSession : InnerScreen("active_session/{sessionId}") {
        fun createRoute(sessionId: String) = "active_session/$sessionId"
    }
}