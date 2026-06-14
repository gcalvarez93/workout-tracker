// Path: app/src/main/java/com/castrodev/workouttracker/navigation/MainScreen.kt
package com.castrodev.workouttracker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.castrodev.workouttracker.R
import com.castrodev.workouttracker.features.analytics.presentation.screen.AnalyticsScreen
import com.castrodev.workouttracker.features.analytics.presentation.viewmodel.AnalyticsViewModel
import com.castrodev.workouttracker.features.routines.domain.entity.RoutineEntity
import com.castrodev.workouttracker.features.routines.presentation.screen.RoutineDetailScreen
import com.castrodev.workouttracker.features.routines.presentation.screen.RoutinesScreen
import com.castrodev.workouttracker.features.routines.presentation.viewmodel.RoutineDetailState
import com.castrodev.workouttracker.features.routines.presentation.viewmodel.RoutineViewModel
import com.castrodev.workouttracker.features.sessions.presentation.screen.ActiveSessionScreen
import com.castrodev.workouttracker.features.sessions.presentation.screen.SessionsScreen
import com.castrodev.workouttracker.features.sessions.presentation.viewmodel.ActiveSessionState
import com.castrodev.workouttracker.features.sessions.presentation.viewmodel.SessionViewModel

sealed class BottomTab(val route: String, val labelRes: Int, val icon: ImageVector) {
    object Routines  : BottomTab("routines",  R.string.routines,  Icons.Default.FitnessCenter)
    object Sessions  : BottomTab("sessions",  R.string.sessions,  Icons.Default.Timer)
    object Analytics : BottomTab("analytics", R.string.analytics, Icons.Default.BarChart)
    object Profile   : BottomTab("profile",   R.string.profile,   Icons.Default.Person)
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController              = rememberNavController()
    val routineViewModel: RoutineViewModel   = viewModel()
    val sessionViewModel: SessionViewModel   = viewModel()
    val analyticsViewModel: AnalyticsViewModel = viewModel()
    val tabs = listOf(BottomTab.Routines, BottomTab.Sessions, BottomTab.Analytics, BottomTab.Profile)
    val tabRoutes = tabs.map { it.route }

    var selectedRoutine by remember { mutableStateOf<RoutineEntity?>(null) }
    val activeState by sessionViewModel.activeState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(activeState) {
        if (activeState is ActiveSessionState.Active) {
            val sessionId = (activeState as ActiveSessionState.Active).sessionId
            navController.navigate(InnerScreen.ActiveSession.createRoute(sessionId))
        }
    }

    Scaffold(
        bottomBar = {
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = tabRoutes.any { route ->
                currentDestination?.hierarchy?.any { it.route == route } == true
            }
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                            onClick  = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = BottomTab.Routines.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(BottomTab.Routines.route) {
                RoutinesScreen(
                    viewModel          = routineViewModel,
                    onNavigateToDetail = { id -> navController.navigate(InnerScreen.RoutineDetail.createRoute(id)) }
                )
            }
            composable(InnerScreen.RoutineDetail.route) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getString("routineId") ?: return@composable
                RoutineDetailScreen(
                    routineId      = routineId,
                    viewModel      = routineViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onStartSession = { id ->
                        val detail = routineViewModel.detailState.value
                        if (detail is RoutineDetailState.Success) selectedRoutine = detail.routine
                        sessionViewModel.startSession(id)
                    }
                )
            }
            composable(BottomTab.Sessions.route) {
                SessionsScreen(viewModel = sessionViewModel)
            }
            composable(InnerScreen.ActiveSession.route) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable
                val routine   = selectedRoutine ?: return@composable
                ActiveSessionScreen(
                    sessionId      = sessionId,
                    routine        = routine,
                    viewModel      = sessionViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(BottomTab.Analytics.route) {
                AnalyticsScreen(viewModel = analyticsViewModel)
            }
            composable(BottomTab.Profile.route) {
                // ProfileScreen — próximamente
            }
        }
    }
}