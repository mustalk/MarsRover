@file:Suppress("TopLevelPropertyNaming")

package com.mustalk.seat.marsrover.presentation.ui.mission

/**
 * UI state for the New Mission screen.
 * Supports both JSON input mode and individual input fields mode.
 */
data class NewMissionUiState(
    val inputMode: InputMode = InputMode.JSON,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    // JSON Input Mode
    val jsonInput: String = EXAMPLE_JSON,
    val jsonError: String? = null,
    // Individual Input Mode
    val plateauWidth: String = "5",
    val plateauHeight: String = "5",
    val roverStartX: String = "1",
    val roverStartY: String = "2",
    val roverStartDirection: String = "N",
    val movementCommands: String = "LMLMLMLMM",
    // Individual field errors
    val plateauWidthError: String? = null,
    val plateauHeightError: String? = null,
    val roverStartXError: String? = null,
    val roverStartYError: String? = null,
    val roverStartDirectionError: String? = null,
    val movementCommandsError: String? = null,
)

/**
 * Input modes for the New Mission screen
 */
enum class InputMode {
    JSON, // JSON string input
    INDIVIDUAL, // Individual form fields
}

/**
 * Mission execution result
 */
data class MissionExecutionResult(
    val finalPosition: String,
    val isSuccess: Boolean,
    val originalInput: String,
)

private const val EXAMPLE_JSON =
    """{"topRightCorner": {"x": 5, "y": 5}, "roverPosition": {"x": 1, "y": 2}, "roverDirection": "N", "movements": "LMLMLMLMM"}"""
