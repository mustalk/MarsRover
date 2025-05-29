@file:Suppress("TopLevelPropertyNaming")

package com.mustalk.seat.marsrover.presentation.ui.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustalk.seat.marsrover.domain.error.RoverError
import com.mustalk.seat.marsrover.domain.usecase.ExecuteRoverMissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the New Mission screen.
 * Manages state for both JSON input and individual field input modes.
 */
@HiltViewModel
@Suppress("TooManyFunctions")
class NewMissionViewModel
    @Inject
    constructor(
        private val executeRoverMissionUseCase: ExecuteRoverMissionUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(NewMissionUiState())
        val uiState: StateFlow<NewMissionUiState> = _uiState.asStateFlow()

        /**
         * Switches between JSON and Individual input modes
         */
        fun switchInputMode(mode: InputMode) {
            _uiState.value =
                _uiState.value.copy(
                    inputMode = mode,
                    errorMessage = null,
                    successMessage = null,
                    jsonError = null
                )
        }

        /**
         * Updates the JSON input string
         */
        fun updateJsonInput(json: String) {
            _uiState.value =
                _uiState.value.copy(
                    jsonInput = json,
                    jsonError = null,
                    errorMessage = null
                )
        }

        /**
         * Updates individual plateau width field
         */
        fun updatePlateauWidth(width: String) {
            _uiState.value =
                _uiState.value.copy(
                    plateauWidth = width,
                    plateauWidthError = null,
                    errorMessage = null
                )
        }

        /**
         * Updates individual plateau height field
         */
        fun updatePlateauHeight(height: String) {
            _uiState.value =
                _uiState.value.copy(
                    plateauHeight = height,
                    plateauHeightError = null,
                    errorMessage = null
                )
        }

        /**
         * Updates rover start X position
         */
        fun updateRoverStartX(x: String) {
            _uiState.value =
                _uiState.value.copy(
                    roverStartX = x,
                    roverStartXError = null,
                    errorMessage = null
                )
        }

        /**
         * Updates rover start Y position
         */
        fun updateRoverStartY(y: String) {
            _uiState.value =
                _uiState.value.copy(
                    roverStartY = y,
                    roverStartYError = null,
                    errorMessage = null
                )
        }

        /**
         * Updates rover start direction
         */
        fun updateRoverStartDirection(direction: String) {
            _uiState.value =
                _uiState.value.copy(
                    roverStartDirection = direction,
                    roverStartDirectionError = null,
                    errorMessage = null
                )
        }

        /**
         * Updates movement commands
         */
        fun updateMovementCommands(commands: String) {
            _uiState.value =
                _uiState.value.copy(
                    movementCommands = commands,
                    movementCommandsError = null,
                    errorMessage = null
                )
        }

        /**
         * Executes the mission based on current input mode using the real UseCase.
         */
        @Suppress("TooGenericExceptionCaught")
        fun executeMission() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                try {
                    val jsonInput =
                        when (_uiState.value.inputMode) {
                            InputMode.JSON -> _uiState.value.jsonInput
                            InputMode.INDIVIDUAL -> buildJsonFromFields()
                        }

                    // Execute mission using the real UseCase
                    val result = executeRoverMissionUseCase(jsonInput)

                    result.fold(
                        onSuccess = { finalPosition ->
                            _uiState.value =
                                _uiState.value.copy(
                                    isLoading = false,
                                    successMessage = "Mission completed! Final position: $finalPosition",
                                    errorMessage = null
                                )
                        },
                        onFailure = { error ->
                            val errorMessage =
                                when (error) {
                                    is RoverError.InvalidInputFormat ->
                                        "Invalid JSON format: ${error.details}"

                                    is RoverError.InvalidInitialPosition ->
                                        "Rover initial position (${error.x}, ${error.y}) is outside plateau bounds " +
                                            "(0,0) to (${error.plateauMaxX}, ${error.plateauMaxY})"

                                    is RoverError.InvalidDirectionChar ->
                                        "Invalid direction '${error.char}'. Must be N, E, S, or W"

                                    is RoverError.InvalidPlateauDimensions ->
                                        "Invalid plateau dimensions (${error.x}, ${error.y}). Both must be non-negative"

                                    else ->
                                        "Mission execution failed: ${error.message}"
                                }

                            _uiState.value =
                                _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = errorMessage,
                                    successMessage = null
                                )
                        }
                    )
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Unexpected error occurred: ${e.message}",
                            successMessage = null
                        )
                }
            }
        }

        /**
         * Builds JSON string from individual input fields
         */
        private fun buildJsonFromFields(): String {
            val state = _uiState.value
            return """
                {
                    "topRightCorner": {"x": ${state.plateauWidth}, "y": ${state.plateauHeight}},
                    "roverPosition": {"x": ${state.roverStartX}, "y": ${state.roverStartY}},
                    "roverDirection": "${state.roverStartDirection}",
                    "movements": "${state.movementCommands}"
                }
                """.trimIndent()
        }

        /**
         * Validates plateau width on focus out
         */
        fun validatePlateauWidth() {
            val width = _uiState.value.plateauWidth
            if (width.isNotBlank()) {
                val widthValue = width.toIntOrNull()
                if (widthValue == null || widthValue <= 0) {
                    _uiState.value =
                        _uiState.value.copy(
                            plateauWidthError = "Must be a positive number"
                        )
                }
            }
        }

        /**
         * Validates plateau height on focus out
         */
        fun validatePlateauHeight() {
            val height = _uiState.value.plateauHeight
            if (height.isNotBlank()) {
                val heightValue = height.toIntOrNull()
                if (heightValue == null || heightValue <= 0) {
                    _uiState.value =
                        _uiState.value.copy(
                            plateauHeightError = "Must be a positive number"
                        )
                }
            }
        }

        /**
         * Validates rover start X position on focus out
         */
        fun validateRoverStartX() {
            val x = _uiState.value.roverStartX
            if (x.isNotBlank()) {
                val xValue = x.toIntOrNull()
                if (xValue == null || xValue < 0) {
                    _uiState.value =
                        _uiState.value.copy(
                            roverStartXError = "Must be a non-negative number"
                        )
                }
            }
        }

        /**
         * Validates rover start Y position on focus out
         */
        fun validateRoverStartY() {
            val y = _uiState.value.roverStartY
            if (y.isNotBlank()) {
                val yValue = y.toIntOrNull()
                if (yValue == null || yValue < 0) {
                    _uiState.value =
                        _uiState.value.copy(
                            roverStartYError = "Must be a non-negative number"
                        )
                }
            }
        }

        /**
         * Validates movement commands on focus out
         */
        fun validateMovementCommands() {
            val commands = _uiState.value.movementCommands
            if (commands.isNotBlank()) {
                if (commands.any { it !in listOf('L', 'R', 'M') }) {
                    _uiState.value =
                        _uiState.value.copy(
                            movementCommandsError = "Must contain only L, R, M characters"
                        )
                }
            }
        }

        /**
         * Clears all success and error messages
         */
        fun clearMessages() {
            _uiState.value =
                _uiState.value.copy(
                    errorMessage = null,
                    successMessage = null,
                    jsonError = null
                )
        }
    }
