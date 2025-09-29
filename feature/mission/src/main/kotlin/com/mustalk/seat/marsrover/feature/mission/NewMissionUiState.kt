@file:Suppress("TopLevelPropertyNaming")

package com.mustalk.seat.marsrover.feature.mission

import androidx.annotation.StringRes
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
    // Validation errors for builder inputs (using resource IDs)
    @StringRes val plateauWidthError: Int? = null,
    @StringRes val plateauHeightError: Int? = null,
    @StringRes val roverStartXError: Int? = null,
    @StringRes val roverStartYError: Int? = null,
    @StringRes val roverStartDirectionError: Int? = null,
    @StringRes val movementCommandsError: Int? = null,
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
