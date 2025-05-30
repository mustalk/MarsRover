package com.mustalk.seat.marsrover.presentation.ui.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustalk.seat.marsrover.core.utils.Constants
import com.mustalk.seat.marsrover.core.utils.NetworkResult
import com.mustalk.seat.marsrover.core.utils.exceptions.JsonParsingException
import com.mustalk.seat.marsrover.core.utils.exceptions.MissionExecutionException
import com.mustalk.seat.marsrover.core.utils.exceptions.NetworkException
import com.mustalk.seat.marsrover.domain.error.RoverError
import com.mustalk.seat.marsrover.domain.usecase.ExecuteNetworkMissionUseCase
import com.mustalk.seat.marsrover.domain.usecase.ExecuteRoverMissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the New Mission screen.
 * Manages state for both JSON input and builder field input modes.
 *
 * Execution strategy:
 * - JSON input mode: Uses local execution (ExecuteRoverMissionUseCase)
 * - Builder input mode: Uses network API execution (ExecuteNetworkMissionUseCase)
 */
@HiltViewModel
@Suppress("TooManyFunctions")
class NewMissionViewModel
    @Inject
    constructor(
        private val executeRoverMissionUseCase: ExecuteRoverMissionUseCase,
        private val executeNetworkMissionUseCase: ExecuteNetworkMissionUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(NewMissionUiState())
        val uiState: StateFlow<NewMissionUiState> = _uiState.asStateFlow()

        /**
         * Switches between JSON and Builder input modes
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
         * Executes the mission based on input mode:
         * - JSON mode: Local execution
         * - Builder mode: Network API execution
         */
        fun executeMission() {
            viewModelScope.launch {
                setLoadingState()

                try {
                    when (_uiState.value.inputMode) {
                        InputMode.JSON -> executeJsonMission()
                        InputMode.BUILDER -> executeBuilderMission()
                    }
                } catch (e: JsonParsingException) {
                    setErrorState("JSON parsing error: ${e.message}")
                } catch (e: MissionExecutionException) {
                    setErrorState("Mission execution error: ${e.message}")
                } catch (e: NetworkException) {
                    setErrorState("Network error: ${e.message}")
                } catch (e: NumberFormatException) {
                    setErrorState("Invalid number format in input fields")
                } catch (e: IllegalArgumentException) {
                    setErrorState("Invalid input parameters: ${e.message}")
                }
            }
        }

        /**
         * Executes mission using JSON input mode (local execution).
         */
        private suspend fun executeJsonMission() {
            val result = executeRoverMissionUseCase(_uiState.value.jsonInput)

            result.fold(
                onSuccess = { finalPosition ->
                    setSuccessState("Mission completed! Final position: $finalPosition")
                },
                onFailure = { error ->
                    val errorMessage = mapRoverErrorToMessage(error)
                    setErrorState(errorMessage)
                }
            )
        }

        /**
         * Executes mission using builder input mode (network API execution).
         */
        private suspend fun executeBuilderMission() {
            val state = _uiState.value

            executeNetworkMissionUseCase
                .executeFromBuilderInputs(
                    plateauWidth = state.plateauWidth.toIntOrNull() ?: 0,
                    plateauHeight = state.plateauHeight.toIntOrNull() ?: 0,
                    roverStartX = state.roverStartX.toIntOrNull() ?: 0,
                    roverStartY = state.roverStartY.toIntOrNull() ?: 0,
                    roverDirection = state.roverStartDirection,
                    movements = state.movementCommands
                ).onEach { networkResult ->
                    handleNetworkResult(networkResult)
                }.launchIn(viewModelScope)
        }

        /**
         * Handles network result from builder mission execution.
         */
        private fun handleNetworkResult(networkResult: NetworkResult<String>) {
            when (networkResult) {
                is NetworkResult.Success -> {
                    setSuccessState("Network mission completed! Final position: ${networkResult.data}")
                }
                is NetworkResult.Error -> {
                    setErrorState("Network error: ${networkResult.message}")
                }
                is NetworkResult.Loading -> {
                    setLoadingState()
                }
            }
        }

        /**
         * Sets the UI to loading state.
         */
        private fun setLoadingState() {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
        }

        /**
         * Sets the UI to success state with message.
         */
        private fun setSuccessState(message: String) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    successMessage = message,
                    errorMessage = null
                )
        }

        /**
         * Sets the UI to error state with message.
         */
        private fun setErrorState(message: String) {
            _uiState.value =
                _uiState.value.copy(
                    isLoading = false,
                    errorMessage = message,
                    successMessage = null
                )
        }

        /**
         * Maps RoverError to user-friendly error message.
         */
        private fun mapRoverErrorToMessage(error: Throwable): String =
            when (error) {
                is RoverError.InvalidInputFormat ->
                    "Invalid JSON format: ${error.details}"

                is RoverError.InvalidInitialPosition ->
                    "Rover initial position (${error.x}, ${error.y}) is outside plateau bounds " +
                        "(0,0) to (${error.plateauMaxX}, ${error.plateauMaxY})"

                is RoverError.InvalidDirectionChar ->
                    "Invalid direction '${error.char}'. Must be N, E, S, or W"

                is RoverError.InvalidPlateauDimensions ->
                    "Invalid plateau dimensions (${error.x}, ${error.y}). Both must be non-negative and within limits"

                else ->
                    "Mission execution failed: ${error.message}"
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
                if (commands.any { it !in Constants.Validation.VALID_MOVEMENT_CHARS }) {
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
