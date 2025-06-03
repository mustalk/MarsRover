@file:Suppress("TopLevelPropertyNaming")

package com.mustalk.seat.marsrover.presentation.ui.mission

import com.mustalk.seat.marsrover.core.common.constants.Constants

/**
 * UI state for the New Mission screen.
 * Supports both JSON input mode and builder input fields mode.
 */
data class NewMissionUiState(
    // Input mode state
    val inputMode: InputMode = InputMode.JSON,
    // JSON input state
    val jsonInput: String = Constants.Examples.JSON_INPUT,
    val jsonError: String? = null,
    // Builder input state
    val plateauWidth: String = "5",
    val plateauHeight: String = "5",
    val roverStartX: String = "1",
    val roverStartY: String = "2",
    val roverStartDirection: String = "N",
    val movementCommands: String = "LMLMLMLMM",
    // Validation errors for builder inputs
    val plateauWidthError: String? = null,
    val plateauHeightError: String? = null,
    val roverStartXError: String? = null,
    val roverStartYError: String? = null,
    val roverStartDirectionError: String? = null,
    val movementCommandsError: String? = null,
    // Mission execution state
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)

/**
 * Input modes for the New Mission screen
 */
enum class InputMode {
    JSON, // JSON string input
    BUILDER, // Builder form fields
}
