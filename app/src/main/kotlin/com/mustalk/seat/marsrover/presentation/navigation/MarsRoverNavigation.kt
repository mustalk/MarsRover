package com.mustalk.seat.marsrover.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mustalk.seat.marsrover.presentation.ui.dashboard.DashboardScreen
import com.mustalk.seat.marsrover.presentation.ui.mission.NewMissionScreen

/**
 * Main navigation graph for the Mars Rover app.
 * Handles navigation between all screens using Jetpack Navigation Compose.
 * Splash experience is handled by SplashScreen API with Lottie exit animation.
 *
 * @param navController Navigation controller for managing navigation state
 * @param contentPadding Padding values from the parent Scaffold
 */
@Composable
fun MarsRoverNavigation(
    navController: NavHostController,
    contentPadding: PaddingValues = PaddingValues(),
) {
    NavHost(
        navController = navController,
        // Start directly at Dashboard - splash is handled by SplashScreen API
        startDestination = Screen.Dashboard.route,
        modifier = Modifier.padding(contentPadding)
    ) {
        // Dashboard Screen - Main screen showing mission results and controls
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToNewMission = {
                    navController.navigate(Screen.NewMission.route)
                }
            )
        }

        // New Mission Screen - Dialog/screen for creating new rover missions
        composable(Screen.NewMission.route) {
            NewMissionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMissionCompleted = { finalPosition, isSuccess, originalInput ->
                    // Will integrate with mission result handling in Commit 7
                    navController.popBackStack()
                }
            )
        }
    }
}
