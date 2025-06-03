package com.mustalk.seat.marsrover.presentation.ui.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustalk.seat.marsrover.core.common.constants.Constants
import com.mustalk.seat.marsrover.core.common.exceptions.JsonParsingException
import com.mustalk.seat.marsrover.core.common.exceptions.MissionExecutionException
import com.mustalk.seat.marsrover.core.common.exceptions.NetworkException
import com.mustalk.seat.marsrover.core.common.network.NetworkResult
import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.domain.usecase.ExecuteNetworkMissionUseCase
import com.mustalk.seat.marsrover.domain.usecase.ExecuteRoverMissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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
@Suppress("TooManyFunctions") // Acceptable for ViewModel with proper delegation
@HiltViewModel
class NewMissionViewModel
    @Inject
    constructor(
        private val executeRoverMissionUseCase: ExecuteRoverMissionUseCase,
        private val executeNetworkMissionUseCase: ExecuteNetworkMissionUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(NewMissionUiState())
        val uiState: StateFlow<NewMissionUiState> = _uiState.asStateFlow()

        private val stateManager =
            StateManager(
                updateState = { _uiState.value = it },
                getCurrentState = { _uiState.value }
            )

        private val fieldUpdateHandler =
            FieldUpdateHandler(
                updateState = { _uiState.value = it },
                getCurrentState = { _uiState.value }
            )

        private val validationHandler =
            ValidationHandler(
                updateState = { _uiState.value = it },
                getCurrentState = { _uiState.value }
            )

        private val missionExecutor =
            MissionExecutor(
                executeRoverMissionUseCase = executeRoverMissionUseCase,
                executeNetworkMissionUseCase = executeNetworkMissionUseCase,
                stateManager = stateManager,
                getCurrentState = { _uiState.value },
                scope = viewModelScope
            )

        // Primary ViewModel functions

        /**
         * Switches between JSON and Builder input modes.
         * Clears any existing error messages when switching modes.
         */
        fun switchInputMode(mode: InputMode) = stateManager.switchInputMode(mode)

        /**
         * Clears all success and error messages from the UI state.
         */
        fun clearMessages() = stateManager.clearMessages()

        /**
         * Executes the mission based on the current input mode:
         * - JSON mode: Uses local execution with ExecuteRoverMissionUseCase
         * - Builder mode: Uses network simulation with ExecuteNetworkMissionUseCase
         */
        fun executeMission() {
            viewModelScope.launch {
                missionExecutor.executeMission()
            }
        }

        // Field update functions - delegated to handler
        fun updateJsonInput(json: String) = fieldUpdateHandler.updateJsonInput(json)

        fun updatePlateauWidth(width: String) = fieldUpdateHandler.updatePlateauWidth(width)

        fun updatePlateauHeight(height: String) = fieldUpdateHandler.updatePlateauHeight(height)

        fun updateRoverStartX(x: String) = fieldUpdateHandler.updateRoverStartX(x)

        fun updateRoverStartY(y: String) = fieldUpdateHandler.updateRoverStartY(y)

        fun updateRoverStartDirection(direction: String) = fieldUpdateHandler.updateRoverStartDirection(direction)

        fun updateMovementCommands(commands: String) = fieldUpdateHandler.updateMovementCommands(commands)

        // Validation functions - delegated to handler
        fun validatePlateauWidth() = validationHandler.validatePlateauWidth()

        fun validatePlateauHeight() = validationHandler.validatePlateauHeight()

        fun validateRoverStartX() = validationHandler.validateRoverStartX()

        fun validateRoverStartY() = validationHandler.validateRoverStartY()

        fun validateMovementCommands() = validationHandler.validateMovementCommands()
    }

/**
 * Handles field updates for the New Mission screen.
 * Manages form field state updates and clears related error messages.
 */
private class FieldUpdateHandler(
    private val updateState: (NewMissionUiState) -> Unit,
    private val getCurrentState: () -> NewMissionUiState,
) {
    fun updateJsonInput(json: String) {
        updateState(
            getCurrentState().copy(
                jsonInput = json,
                jsonError = null,
                errorMessage = null
            )
        )
    }

    fun updatePlateauWidth(width: String) {
        // Filter to allow only digits
        val filteredWidth = width.filter { it.isDigit() }
        updateState(
            getCurrentState().copy(
                plateauWidth = filteredWidth,
                plateauWidthError = null,
                errorMessage = null
            )
        )
    }

    fun updatePlateauHeight(height: String) {
        // Filter to allow only digits
        val filteredHeight = height.filter { it.isDigit() }
        updateState(
            getCurrentState().copy(
                plateauHeight = filteredHeight,
                plateauHeightError = null,
                errorMessage = null
            )
        )
    }

    fun updateRoverStartX(x: String) {
        // Filter to allow only digits (no negative sign since we don't allow negative coordinates)
        val filteredX = x.filter { it.isDigit() }
        updateState(
            getCurrentState().copy(
                roverStartX = filteredX,
                roverStartXError = null,
                errorMessage = null
            )
        )
    }

    fun updateRoverStartY(y: String) {
        // Filter to allow only digits (no negative sign since we don't allow negative coordinates)
        val filteredY = y.filter { it.isDigit() }
        updateState(
            getCurrentState().copy(
                roverStartY = filteredY,
                roverStartYError = null,
                errorMessage = null
            )
        )
    }

    fun updateRoverStartDirection(direction: String) {
        updateState(
            getCurrentState().copy(
                roverStartDirection = direction,
                roverStartDirectionError = null,
                errorMessage = null
            )
        )
    }

    fun updateMovementCommands(commands: String) {
        updateState(
            getCurrentState().copy(
                movementCommands = commands,
                movementCommandsError = null,
                errorMessage = null
            )
        )
    }
}

/**
 * Handles field validation for the New Mission screen.
 * Validates user input and sets appropriate error messages.
 */
private class ValidationHandler(
    private val updateState: (NewMissionUiState) -> Unit,
    private val getCurrentState: () -> NewMissionUiState,
) {
    fun validatePlateauWidth() {
        val width = getCurrentState().plateauWidth
        if (width.isNotBlank()) {
            val widthValue = width.toIntOrNull()
            when {
                widthValue == null -> {
                    updateState(
                        getCurrentState().copy(
                            plateauWidthError = "Must be a valid number"
                        )
                    )
                }

                widthValue <= 0 -> {
                    updateState(
                        getCurrentState().copy(
                            plateauWidthError = "Must be a positive number"
                        )
                    )
                }
            }
        }
    }

    fun validatePlateauHeight() {
        val height = getCurrentState().plateauHeight
        if (height.isNotBlank()) {
            val heightValue = height.toIntOrNull()
            when {
                heightValue == null -> {
                    updateState(
                        getCurrentState().copy(
                            plateauHeightError = "Must be a valid number"
                        )
                    )
                }

                heightValue <= 0 -> {
                    updateState(
                        getCurrentState().copy(
                            plateauHeightError = "Must be a positive number"
                        )
                    )
                }
            }
        }
    }

    fun validateRoverStartX() {
        val x = getCurrentState().roverStartX
        if (x.isNotBlank()) {
            val xValue = x.toIntOrNull()
            when {
                xValue == null -> {
                    updateState(
                        getCurrentState().copy(
                            roverStartXError = "Must be a valid number"
                        )
                    )
                }

                xValue < 0 -> {
                    updateState(
                        getCurrentState().copy(
                            roverStartXError = "Must be a non-negative number"
                        )
                    )
                }
            }
        }
    }

    fun validateRoverStartY() {
        val y = getCurrentState().roverStartY
        if (y.isNotBlank()) {
            val yValue = y.toIntOrNull()
            when {
                yValue == null -> {
                    updateState(
                        getCurrentState().copy(
                            roverStartYError = "Must be a valid number"
                        )
                    )
                }

                yValue < 0 -> {
                    updateState(
                        getCurrentState().copy(
                            roverStartYError = "Must be a non-negative number"
                        )
                    )
                }
            }
        }
    }

    fun validateMovementCommands() {
        val commands = getCurrentState().movementCommands
        if (commands.isNotBlank()) {
            if (commands.any { it !in Constants.Validation.VALID_MOVEMENT_CHARS }) {
                updateState(
                    getCurrentState().copy(
                        movementCommandsError = "Must contain only L, R, M characters"
                    )
                )
            }
        }
    }
}

/**
 * Handles state updates for the New Mission screen.
 * Manages loading, success, error, and mode switching states.
 */
private class StateManager(
    private val updateState: (NewMissionUiState) -> Unit,
    private val getCurrentState: () -> NewMissionUiState,
) {
    fun switchInputMode(mode: InputMode) {
        updateState(
            getCurrentState().copy(
                inputMode = mode,
                errorMessage = null,
                successMessage = null,
                jsonError = null
            )
        )
    }

    fun setLoadingState() {
        updateState(
            getCurrentState().copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )
        )
    }

    fun setSuccessState(message: String) {
        updateState(
            getCurrentState().copy(
                isLoading = false,
                successMessage = message,
                errorMessage = null
            )
        )
    }

    fun setErrorState(message: String) {
        updateState(
            getCurrentState().copy(
                isLoading = false,
                errorMessage = message,
                successMessage = null
            )
        )
    }

    fun clearMessages() {
        updateState(
            getCurrentState().copy(
                errorMessage = null,
                successMessage = null,
                jsonError = null
            )
        )
    }
}

/**
 * Handles mission execution logic.
 * Coordinates between JSON and Builder mode execution with proper error handling.
 */
private class MissionExecutor(
    private val executeRoverMissionUseCase: ExecuteRoverMissionUseCase,
    private val executeNetworkMissionUseCase: ExecuteNetworkMissionUseCase,
    private val stateManager: StateManager,
    private val getCurrentState: () -> NewMissionUiState,
    private val scope: CoroutineScope,
) {
    suspend fun executeMission() {
        stateManager.setLoadingState()

        try {
            when (getCurrentState().inputMode) {
                InputMode.JSON -> executeJsonMission()
                InputMode.BUILDER -> executeBuilderMission()
            }
        } catch (e: JsonParsingException) {
            stateManager.setErrorState("JSON parsing error: ${e.message}")
        } catch (e: MissionExecutionException) {
            stateManager.setErrorState("Mission execution error: ${e.message}")
        } catch (e: NetworkException) {
            stateManager.setErrorState("Network error: ${e.message}")
        } catch (e: NumberFormatException) {
            stateManager.setErrorState("Invalid number format in input fields")
        } catch (e: IllegalArgumentException) {
            stateManager.setErrorState("Invalid input parameters: ${e.message}")
        }
    }

    /**
     * Executes mission using JSON input mode (local execution).
     */
    private suspend fun executeJsonMission() {
        val result = executeRoverMissionUseCase(getCurrentState().jsonInput)

        result.fold(
            onSuccess = { finalPosition ->
                stateManager.setSuccessState("Mission completed! Final position: $finalPosition")
            },
            onFailure = { error ->
                val errorMessage = mapRoverErrorToMessage(error)
                stateManager.setErrorState(errorMessage)
            }
        )
    }

    /**
     * Executes mission using builder input mode (network API simulation).
     */
    private suspend fun executeBuilderMission() {
        val state = getCurrentState()

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
            }.launchIn(scope)
    }

    private fun handleNetworkResult(networkResult: NetworkResult<String>) {
        when (networkResult) {
            is NetworkResult.Success -> {
                stateManager.setSuccessState("Network mission completed! Final position: ${networkResult.data}")
            }

            is NetworkResult.Error -> {
                stateManager.setErrorState("Network error: ${networkResult.message}")
            }

            is NetworkResult.Loading -> {
                stateManager.setLoadingState()
            }
        }
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
}
