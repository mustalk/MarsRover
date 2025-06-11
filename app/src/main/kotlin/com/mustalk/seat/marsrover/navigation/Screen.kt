package com.mustalk.seat.marsrover.navigation

/**
 * Sealed class defining all screen routes in the Mars Rover app.
 * Provides type-safe navigation with compile-time verification.
 * Note: Splash is handled by SplashScreen API, not navigation.
 */
sealed class Screen(
    val route: String,
) {
    /**
     * Dashboard screen - Main screen showing mission results and controls
     */
    data object Dashboard : Screen("dashboard")

    /**
     * New Mission screen - Dialog/screen for creating new rover missions
     */
    data object NewMission : Screen("new_mission")
}
