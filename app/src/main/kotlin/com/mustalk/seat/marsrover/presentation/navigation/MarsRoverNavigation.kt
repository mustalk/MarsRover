package com.mustalk.seat.marsrover.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mustalk.seat.marsrover.feature.dashboard.DashboardScreen
import com.mustalk.seat.marsrover.feature.dashboard.DashboardViewModel
import com.mustalk.seat.marsrover.feature.dashboard.MissionResult
import com.mustalk.seat.marsrover.presentation.ui.mission.NewMissionScreen

/**
 * Main navigation graph for the Mars Rover app.
 * Handles navigation between all screens using Jetpack Navigation Compose.
 * Splash experience is handled by SplashScreen API with Lottie exit animation.
 *
 * @param navController Navigation controller for managing navigation state
 */
@Composable
fun MarsRoverNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        // Start directly at Dashboard - splash is handled by SplashScreen API
        startDestination = Screen.Dashboard.route
    ) {
        // Dashboard Screen - Main screen showing mission results and controls
        composable(
            route = Screen.Dashboard.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }
        ) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()

            DashboardScreen(
                onNavigateToNewMission = {
                    navController.navigate(Screen.NewMission.route)
                },
                viewModel = dashboardViewModel
            )
        }

        // New Mission Screen - Dialog/screen for creating new rover missions
        composable(
            route = Screen.NewMission.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            // Get the Dashboard ViewModel from the parent entry to share state
            val dashboardViewModel: DashboardViewModel =
                hiltViewModel(
                    navController.previousBackStackEntry ?: navController.currentBackStackEntry!!
                )

            NewMissionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMissionCompleted = { finalPosition, isSuccess, originalInput ->
                    // Update Dashboard with mission result
                    val missionResult =
                        MissionResult(
                            finalPosition = finalPosition,
                            isSuccess = isSuccess,
                            originalInput = originalInput
                        )
                    dashboardViewModel.updateMissionResult(missionResult)
                    navController.popBackStack()
                }
            )
        }
    }
}
