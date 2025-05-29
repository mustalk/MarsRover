@file:Suppress("TopLevelPropertyNaming")

package com.mustalk.seat.marsrover.presentation.ui.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    constructor() : ViewModel() {
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
         * Executes the mission based on current input mode.
         * In Commit 7, this will integrate with the UseCase.
         */
        @Suppress("TooGenericExceptionCaught")
        fun executeMission() {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                try {
                    // Will integrate with ExecuteRoverMissionUseCase in Commit 7
                    when (_uiState.value.inputMode) {
                        InputMode.JSON -> {
                            val result = mockExecuteJsonMission(_uiState.value.jsonInput)
                            handleMissionResult(result)
                        }
                        InputMode.INDIVIDUAL -> {
                            val jsonFromFields = buildJsonFromFields()
                            val result = mockExecuteJsonMission(jsonFromFields)
                            handleMissionResult(result)
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Mission execution failed: ${e.message}"
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
         * Handles the mission execution result
         */
        private fun handleMissionResult(result: MissionExecutionResult) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    successMessage =
                        if (result.isSuccess) {
                            "Mission completed! Final position: ${result.finalPosition}"
                        } else {
                            null
                        },
                    errorMessage = if (!result.isSuccess) result.finalPosition else null
                )
        }

        /**
         * Mock implementation for mission execution (to be replaced in Commit 7)
         */
        private fun mockExecuteJsonMission(jsonInput: String): MissionExecutionResult {
            // Simple mock that validates the example JSON
            return if (isValidMissionJson(jsonInput)) {
                MissionExecutionResult(
                    finalPosition = "1 3 N",
                    isSuccess = true,
                    originalInput = jsonInput
                )
            } else {
                MissionExecutionResult(
                    finalPosition = "Invalid JSON format",
                    isSuccess = false,
                    originalInput = jsonInput
                )
            }
        }

        /**
         * Validates if JSON contains required mission fields
         */
        private fun isValidMissionJson(jsonInput: String): Boolean =
            jsonInput.contains("topRightCorner") &&
                jsonInput.contains("roverPosition") &&
                jsonInput.contains("roverDirection") &&
                jsonInput.contains("movements")

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
