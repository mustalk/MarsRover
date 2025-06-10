package com.mustalk.seat.marsrover.feature.mission

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.core.common.network.NetworkResult
import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.core.domain.usecase.ExecuteNetworkMissionUseCase
import com.mustalk.seat.marsrover.core.domain.usecase.ExecuteRoverMissionUseCase
import com.mustalk.seat.marsrover.core.testing.jvm.data.DomainTestData
import com.mustalk.seat.marsrover.core.testing.jvm.util.MainDispatcherRule
import com.mustalk.seat.marsrover.core.ui.resource.StringResourceProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for NewMissionViewModel.
 * Tests state management for both JSON and individual input modes with UseCase integration.
 *
 * Note: We use setupViewModel() instead of @Before setup() because the ViewModel needs to be
 * recreated for each test to ensure clean state. This also ensures proper mocking setup
 * per test case. The MainDispatcherRule manages coroutine dispatchers automatically.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NewMissionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: NewMissionViewModel
    private lateinit var mockExecuteRoverMissionUseCase: ExecuteRoverMissionUseCase
    private lateinit var mockExecuteNetworkMissionUseCase: ExecuteNetworkMissionUseCase
    private lateinit var mockStringResourceProvider: StringResourceProvider

    private fun setupViewModel() {
        setupMocks()
        setupStringResourceProviderMocks()
        createViewModel()
    }

    private fun setupMocks() {
        mockExecuteRoverMissionUseCase = mockk()
        mockExecuteNetworkMissionUseCase = mockk(relaxed = true)
        mockStringResourceProvider = mockk(relaxed = true)
    }

    private fun setupStringResourceProviderMocks() {
        setupSuccessMessageMocks()
        setupSingleParameterErrorMocks()
        setupMultiParameterErrorMocks()
        setupSimpleMessageMocks()
    }

    private fun setupSuccessMessageMocks() {
        every { mockStringResourceProvider.getString(R.string.feature_mission_success, any()) } answers {
            val formatArgs = invocation.args[1] as Array<*>
            "Mission completed! Final position: ${formatArgs[0]}"
        }
        every { mockStringResourceProvider.getString(R.string.feature_mission_network_success, any()) } answers {
            val formatArgs = invocation.args[1] as Array<*>
            "Network mission completed! Final position: ${formatArgs[0]}"
        }
    }

    private fun setupSingleParameterErrorMocks() {
        every { mockStringResourceProvider.getString(R.string.feature_mission_error_json_parsing, any()) } answers {
            val formatArgs = invocation.args[1] as Array<*>
            "JSON parsing error: ${formatArgs[0]}"
        }
        every { mockStringResourceProvider.getString(R.string.feature_mission_error_mission_execution, any()) } answers {
            val formatArgs = invocation.args[1] as Array<*>
            "Mission execution error: ${formatArgs[0]}"
        }
        every { mockStringResourceProvider.getString(R.string.feature_mission_error_network, any()) } answers {
            val formatArgs = invocation.args[1] as Array<*>
            "Network error: ${formatArgs[0]}"
        }
        every { mockStringResourceProvider.getString(R.string.feature_mission_error_invalid_parameters, any()) } answers {
            val formatArgs = invocation.args[1] as Array<*>
            "Invalid input parameters: ${formatArgs[0]}"
        }
        every { mockStringResourceProvider.getString(R.string.feature_mission_error_invalid_json_format, any()) } answers {
            val formatArgs = invocation.args[1] as Array<*>
            "Invalid JSON format: ${formatArgs[0]}"
        }
        every { mockStringResourceProvider.getString(R.string.feature_mission_error_invalid_direction, any()) } answers {
            val formatArgs = invocation.args[1] as Array<*>
            "Invalid direction '${formatArgs[0]}'. Must be N, E, S, or W"
        }
        every { mockStringResourceProvider.getString(R.string.feature_mission_error_unknown, any()) } answers {
            val formatArgs = invocation.args[1] as Array<*>
            "Mission execution failed: ${formatArgs[0]}"
        }
    }

    private fun setupMultiParameterErrorMocks() {
        every { mockStringResourceProvider.getString(R.string.feature_mission_error_position_out_of_bounds, any(), any(), any(), any()) } answers {
            val formatArgs = invocation.args[1] as Array<*>
            "Rover initial position (${formatArgs[0]}, ${formatArgs[1]}) is outside plateau bounds (0,0) to (${formatArgs[2]}, ${formatArgs[3]})"
        }
        every { mockStringResourceProvider.getString(R.string.feature_mission_error_invalid_plateau_dimensions, any(), any()) } answers {
            val formatArgs = invocation.args[1] as Array<*>
            "Invalid plateau dimensions (${formatArgs[0]}, ${formatArgs[1]}). Both must be non-negative and within limits"
        }
    }

    private fun setupSimpleMessageMocks() {
        every { mockStringResourceProvider.getString(R.string.feature_mission_error_invalid_format) } returns "Invalid number format in input fields"
    }

    private fun createViewModel() {
        viewModel =
            NewMissionViewModel(
                executeRoverMissionUseCase = mockExecuteRoverMissionUseCase,
                executeNetworkMissionUseCase = mockExecuteNetworkMissionUseCase,
                stringResourceProvider = mockStringResourceProvider
            )
    }

    @Test
    fun `initial state should have default values`() {
        setupViewModel()
        val initialState = viewModel.uiState.value

        assertThat(initialState.inputMode).isEqualTo(InputMode.JSON)
        assertThat(initialState.jsonInput).contains("topRightCorner")
        assertThat(initialState.isLoading).isFalse()
        assertThat(initialState.errorMessage).isNull()
        assertThat(initialState.successMessage).isNull()
    }

    @Test
    fun `switchInputMode should update input mode and clear errors`() {
        setupViewModel()

        // Given
        viewModel.updateJsonInput("invalid")

        // When
        viewModel.switchInputMode(InputMode.BUILDER)

        // Then
        val state = viewModel.uiState.value
        assertThat(state.inputMode).isEqualTo(InputMode.BUILDER)
        assertThat(state.errorMessage).isNull()
        assertThat(state.successMessage).isNull()
        assertThat(state.jsonError).isNull()
    }

    @Test
    fun `updateJsonInput should update json input and clear errors`() {
        setupViewModel()

        // Given
        val testData = DomainTestData.UseCaseTestData.SuccessfulExecution
        val newJson = testData.STANDARD_MISSION.JSON

        // When
        viewModel.updateJsonInput(newJson)

        // Then
        val state = viewModel.uiState.value
        assertThat(state.jsonInput).isEqualTo(newJson)
        assertThat(state.jsonError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `updatePlateauWidth should update width and clear errors`() {
        setupViewModel()

        // Given
        val testConstants = DomainTestData.TestConstants

        // When
        viewModel.updatePlateauWidth(testConstants.STANDARD_PLATEAU_X.toString())

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauWidth).isEqualTo(testConstants.STANDARD_PLATEAU_X.toString())
        assertThat(state.plateauWidthError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `updatePlateauHeight should update height and clear errors`() {
        setupViewModel()

        // Given
        val testConstants = DomainTestData.TestConstants

        // When
        viewModel.updatePlateauHeight(testConstants.STANDARD_PLATEAU_Y.toString())

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauHeight).isEqualTo(testConstants.STANDARD_PLATEAU_Y.toString())
        assertThat(state.plateauHeightError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `updateRoverStartX should update X position and clear errors`() {
        setupViewModel()

        // Given
        val testConstants = DomainTestData.TestConstants

        // When
        viewModel.updateRoverStartX(testConstants.POSITION_1_2.x.toString())

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartX).isEqualTo(testConstants.POSITION_1_2.x.toString())
        assertThat(state.roverStartXError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `updateRoverStartY should update Y position and clear errors`() {
        setupViewModel()

        // Given
        val testConstants = DomainTestData.TestConstants

        // When
        viewModel.updateRoverStartY(testConstants.POSITION_1_2.y.toString())

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartY).isEqualTo(testConstants.POSITION_1_2.y.toString())
        assertThat(state.roverStartYError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `updateRoverStartDirection should update direction and clear errors`() {
        setupViewModel()

        // Given
        val testConstants = DomainTestData.TestConstants

        // When
        viewModel.updateRoverStartDirection(testConstants.DIRECTION_NORTH)

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartDirection).isEqualTo(testConstants.DIRECTION_NORTH)
        assertThat(state.roverStartDirectionError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `updateMovementCommands should update commands and clear errors`() {
        setupViewModel()

        // Given
        val testConstants = DomainTestData.TestConstants

        // When
        viewModel.updateMovementCommands(testConstants.STANDARD_MOVEMENTS)

        // Then
        val state = viewModel.uiState.value
        assertThat(state.movementCommands).isEqualTo(testConstants.STANDARD_MOVEMENTS)
        assertThat(state.movementCommandsError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `executeMission with valid JSON should succeed`() =
        runTest {
            setupViewModel()

            // Given
            val testData = DomainTestData.UseCaseTestData.SuccessfulExecution
            val expectedResult = testData.EXPECTED_FINAL_POSITION
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.success(expectedResult)

            // When
            viewModel.executeMission()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.successMessage).isEqualTo("Mission completed! Final position: $expectedResult")
            assertThat(state.errorMessage).isNull()

            coVerify { mockExecuteRoverMissionUseCase(any()) }
        }

    @Test
    fun `executeMission with invalid JSON format should show error`() =
        runTest {
            setupViewModel()

            // Given
            val error = RoverError.InvalidInputFormat("Missing required field")
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.failure(error)

            // When
            viewModel.executeMission()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).isEqualTo("Invalid JSON format: Missing required field")
            assertThat(state.successMessage).isNull()
        }

    @Test
    fun `executeMission with invalid initial position should show error`() =
        runTest {
            setupViewModel()

            // Given
            val testConstants = DomainTestData.TestConstants
            val error =
                RoverError.InvalidInitialPosition(
                    x = testConstants.POSITION_6_2.x,
                    y = testConstants.POSITION_6_2.y,
                    plateauMaxX = testConstants.STANDARD_PLATEAU_X,
                    plateauMaxY = testConstants.STANDARD_PLATEAU_Y
                )
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.failure(error)

            // When
            viewModel.executeMission()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).isEqualTo("Rover initial position (6, 2) is outside plateau bounds (0,0) to (5, 5)")
            assertThat(state.successMessage).isNull()
        }

    @Test
    fun `executeMission with invalid direction should show error`() =
        runTest {
            setupViewModel()

            // Given
            val testConstants = DomainTestData.TestConstants
            val error = RoverError.InvalidDirectionChar(testConstants.INVALID_DIRECTION_X)
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.failure(error)

            // When
            viewModel.executeMission()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).isEqualTo("Invalid direction 'X'. Must be N, E, S, or W")
            assertThat(state.successMessage).isNull()
        }

    @Test
    fun `executeMission with invalid plateau dimensions should show error`() =
        runTest {
            setupViewModel()

            // Given
            val error = RoverError.InvalidPlateauDimensions(x = -1, y = -1)
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.failure(error)

            // When
            viewModel.executeMission()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).isEqualTo("Invalid plateau dimensions (-1, -1). Both must be non-negative and within limits")
            assertThat(state.successMessage).isNull()
        }

    @Test
    fun `executeMission with individual inputs should use network execution`() =
        runTest {
            setupViewModel()

            // Given
            val constants = DomainTestData.TestConstants
            val expectedResult = constants.STANDARD_FINAL_POSITION

            coEvery {
                mockExecuteNetworkMissionUseCase.executeFromBuilderInputs(
                    plateauWidth = constants.STANDARD_PLATEAU_X,
                    plateauHeight = constants.STANDARD_PLATEAU_Y,
                    roverStartX = constants.POSITION_1_2.x,
                    roverStartY = constants.POSITION_1_2.y,
                    roverDirection = constants.DIRECTION_NORTH,
                    movements = constants.STANDARD_MOVEMENTS
                )
            } returns flowOf(NetworkResult.Success(expectedResult))

            viewModel.switchInputMode(InputMode.BUILDER)
            viewModel.updatePlateauWidth(constants.STANDARD_PLATEAU_X.toString())
            viewModel.updatePlateauHeight(constants.STANDARD_PLATEAU_Y.toString())
            viewModel.updateRoverStartX(constants.POSITION_1_2.x.toString())
            viewModel.updateRoverStartY(constants.POSITION_1_2.y.toString())
            viewModel.updateRoverStartDirection(constants.DIRECTION_NORTH)
            viewModel.updateMovementCommands(constants.STANDARD_MOVEMENTS)

            // When
            viewModel.executeMission()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.successMessage).isEqualTo("Network mission completed! Final position: $expectedResult")
            assertThat(state.errorMessage).isNull()

            coVerify {
                mockExecuteNetworkMissionUseCase.executeFromBuilderInputs(
                    plateauWidth = constants.STANDARD_PLATEAU_X,
                    plateauHeight = constants.STANDARD_PLATEAU_Y,
                    roverStartX = constants.POSITION_1_2.x,
                    roverStartY = constants.POSITION_1_2.y,
                    roverDirection = constants.DIRECTION_NORTH,
                    movements = constants.STANDARD_MOVEMENTS
                )
            }
        }

    @Test
    fun `executeMission should set loading state during execution`() =
        runTest {
            setupViewModel()

            // Given
            val constants = DomainTestData.TestConstants
            val expectedResult = constants.STANDARD_FINAL_POSITION
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.success(expectedResult)
            assertThat(viewModel.uiState.value.isLoading).isFalse()

            // When
            viewModel.executeMission()

            // Then
            val finalState = viewModel.uiState.value
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.successMessage).isEqualTo("Mission completed! Final position: $expectedResult")
        }

    @Test
    fun `executeMission with unexpected exception should show generic error`() =
        runTest {
            setupViewModel()

            // Given
            every { mockExecuteRoverMissionUseCase(any()) } throws IllegalArgumentException("Unexpected error")

            // When
            viewModel.executeMission()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).isEqualTo("Invalid input parameters: Unexpected error")
            assertThat(state.successMessage).isNull()
        }

    @Test
    fun `clearMessages should clear all messages`() {
        setupViewModel()

        // Given - simulate having messages
        val error = RoverError.InvalidInputFormat("test")
        every { mockExecuteRoverMissionUseCase(any()) } returns Result.failure(error)
        viewModel.executeMission()

        // When
        viewModel.clearMessages()

        // Then
        val finalState = viewModel.uiState.value
        assertThat(finalState.errorMessage).isNull()
        assertThat(finalState.successMessage).isNull()
    }

    @Test
    fun `updatePlateauWidth should filter out non-digit characters`() {
        setupViewModel()

        // When
        viewModel.updatePlateauWidth("12abc34")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauWidth).isEqualTo("1234")
        assertThat(state.plateauWidthError).isNull()
    }

    @Test
    fun `updatePlateauHeight should filter out non-digit characters`() {
        setupViewModel()

        // When
        viewModel.updatePlateauHeight("5#6@7")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauHeight).isEqualTo("567")
        assertThat(state.plateauHeightError).isNull()
    }

    @Test
    fun `updateRoverStartX should filter out letters and special characters`() {
        setupViewModel()

        // When
        viewModel.updateRoverStartX("a1b2c3!")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartX).isEqualTo("123")
        assertThat(state.roverStartXError).isNull()
    }

    @Test
    fun `updateRoverStartY should filter out letters and special characters`() {
        setupViewModel()

        // When
        viewModel.updateRoverStartY("ads")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartY).isEmpty()
        assertThat(state.roverStartYError).isNull()
    }

    @Test
    fun `updateRoverStartX should handle empty input after filtering`() {
        setupViewModel()

        // When
        viewModel.updateRoverStartX("abc!@#")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartX).isEmpty()
        assertThat(state.roverStartXError).isNull()
    }

    @Test
    fun `validatePlateauWidth should set error for zero input`() {
        setupViewModel()

        // Given
        viewModel.updatePlateauWidth("0")

        // When
        viewModel.validatePlateauWidth()

        // Then
        val currentState = viewModel.uiState.value
        assertThat(currentState.plateauWidthError).isEqualTo(R.string.feature_mission_error_positive_number)
    }

    @Test
    fun `validatePlateauHeight should set error for zero input`() {
        setupViewModel()

        // Given
        viewModel.updatePlateauHeight("0")

        // When
        viewModel.validatePlateauHeight()

        // Then
        val currentState = viewModel.uiState.value
        assertThat(currentState.plateauHeightError).isEqualTo(R.string.feature_mission_error_positive_number)
    }

    @Test
    fun `validatePlateauWidth should clear error for valid input`() {
        setupViewModel()

        // Given - First set an error, then valid input
        viewModel.updatePlateauWidth("0")
        viewModel.validatePlateauWidth()
        assertThat(viewModel.uiState.value.plateauWidthError).isNotNull()

        // When - Update to valid input and validate
        viewModel.updatePlateauWidth("5")
        viewModel.validatePlateauWidth()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauWidthError).isNull()
    }

    @Test
    fun `validatePlateauHeight should clear error for valid input`() {
        setupViewModel()

        // Given - First set an error, then valid input
        viewModel.updatePlateauHeight("0")
        viewModel.validatePlateauHeight()
        assertThat(viewModel.uiState.value.plateauHeightError).isNotNull()

        // When - Update to valid input and validate
        viewModel.updatePlateauHeight("5")
        viewModel.validatePlateauHeight()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauHeightError).isNull()
    }

    @Test
    fun `validateRoverStartX should not set error for blank input`() {
        setupViewModel()

        // Given
        viewModel.updateRoverStartX("")

        // When
        viewModel.validateRoverStartX()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartXError).isNull()
    }

    @Test
    fun `validateRoverStartY should not set error for blank input`() {
        setupViewModel()

        // Given
        viewModel.updateRoverStartY("")

        // When
        viewModel.validateRoverStartY()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartYError).isNull()
    }

    @Test
    fun `validateMovementCommands should not set error for blank input`() {
        setupViewModel()

        // Given
        viewModel.updateMovementCommands("")

        // When
        viewModel.validateMovementCommands()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.movementCommandsError).isNull()
    }

    @Test
    fun `validateMovementCommands should clear error for valid commands`() {
        setupViewModel()

        // Given - First set an error
        val testConstants = DomainTestData.TestConstants
        viewModel.updateMovementCommands(testConstants.INVALID_COMMANDS)
        viewModel.validateMovementCommands()
        assertThat(viewModel.uiState.value.movementCommandsError).isNotNull()

        // When - Update to valid commands and validate
        viewModel.updateMovementCommands(testConstants.STANDARD_MOVEMENTS)
        viewModel.validateMovementCommands()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.movementCommandsError).isNull()
    }

    @Test
    fun `validateMovementCommands should set error for invalid characters`() {
        setupViewModel()

        // Given
        viewModel.updateMovementCommands("LMRX") // X is invalid

        // When
        viewModel.validateMovementCommands()

        // Then
        val currentState = viewModel.uiState.value
        assertThat(currentState.movementCommandsError).isEqualTo(R.string.feature_mission_error_invalid_commands)
    }
}
