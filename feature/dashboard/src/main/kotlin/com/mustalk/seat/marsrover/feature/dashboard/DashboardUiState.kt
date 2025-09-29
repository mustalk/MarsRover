package com.mustalk.seat.marsrover.feature.dashboard

/**
 * UI state for the Dashboard screen.
 * Represents the current state of mission results and UI elements.
 */
data class DashboardUiState(
    val lastMissionResult: MissionResult? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showNewMissionDialog: Boolean = false,
)

/**
 * Represents the result of a completed rover mission.
 */
data class MissionResult(
    val finalPosition: String,
    val isSuccess: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val originalInput: String? = null,
)
