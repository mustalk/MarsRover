package com.mustalk.seat.marsrover.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
            // Placeholder implementation - Dashboard coming in next commit
            androidx.compose.material3.Text("Dashboard Screen - Coming Soon")
        }

        // New Mission Screen - Dialog/screen for creating new rover missions
        composable(Screen.NewMission.route) {
            // Placeholder implementation - New Mission coming in future commits
            androidx.compose.material3.Text("New Mission Screen - Coming Soon")
        }
    }
}
