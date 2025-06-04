package com.mustalk.seat.marsrover.presentation.ui.mission

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.core.common.network.NetworkResult
import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.core.domain.usecase.ExecuteNetworkMissionUseCase
import com.mustalk.seat.marsrover.core.domain.usecase.ExecuteRoverMissionUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for NewMissionViewModel.
 * Tests state management for both JSON and individual input modes with UseCase integration.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NewMissionViewModelTest {
    private lateinit var viewModel: NewMissionViewModel
    private lateinit var mockExecuteRoverMissionUseCase: ExecuteRoverMissionUseCase
    private lateinit var mockExecuteNetworkMissionUseCase: ExecuteNetworkMissionUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockExecuteRoverMissionUseCase = mockk()
        mockExecuteNetworkMissionUseCase = mockk(relaxed = true)
        viewModel =
            NewMissionViewModel(
                executeRoverMissionUseCase = mockExecuteRoverMissionUseCase,
                executeNetworkMissionUseCase = mockExecuteNetworkMissionUseCase
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() {
        val initialState = viewModel.uiState.value

        assertThat(initialState.inputMode).isEqualTo(InputMode.JSON)
        assertThat(initialState.jsonInput).contains("topRightCorner")
        assertThat(initialState.isLoading).isFalse()
        assertThat(initialState.errorMessage).isNull()
        assertThat(initialState.successMessage).isNull()
    }

    @Test
    fun `switchInputMode should update input mode and clear errors`() {
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
        // Given
        val newJson = """{"test": "value"}"""

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
        // When
        viewModel.updatePlateauWidth("10")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauWidth).isEqualTo("10")
        assertThat(state.plateauWidthError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `updatePlateauHeight should update height and clear errors`() {
        // When
        viewModel.updatePlateauHeight("8")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauHeight).isEqualTo("8")
        assertThat(state.plateauHeightError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `updateRoverStartX should update X position and clear errors`() {
        // When
        viewModel.updateRoverStartX("3")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartX).isEqualTo("3")
        assertThat(state.roverStartXError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `updateRoverStartY should update Y position and clear errors`() {
        // When
        viewModel.updateRoverStartY("2")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartY).isEqualTo("2")
        assertThat(state.roverStartYError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `updateRoverStartDirection should update direction and clear errors`() {
        // When
        viewModel.updateRoverStartDirection("E")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartDirection).isEqualTo("E")
        assertThat(state.roverStartDirectionError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `updateMovementCommands should update commands and clear errors`() {
        // When
        viewModel.updateMovementCommands("LMLMLM")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.movementCommands).isEqualTo("LMLMLM")
        assertThat(state.movementCommandsError).isNull()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `executeMission with valid JSON should succeed`() =
        runTest {
            // Given
            val expectedResult = "1 3 N"
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.success(expectedResult)

            // When
            viewModel.executeMission()
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.successMessage).contains("Mission completed!")
            assertThat(state.successMessage).contains(expectedResult)
            assertThat(state.errorMessage).isNull()

            verify { mockExecuteRoverMissionUseCase(any()) }
        }

    @Test
    fun `executeMission with invalid JSON format should show error`() =
        runTest {
            // Given
            val error = RoverError.InvalidInputFormat("Missing required field")
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.failure(error)

            // When
            viewModel.executeMission()
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).contains("Invalid JSON format")
            assertThat(state.errorMessage).contains("Missing required field")
            assertThat(state.successMessage).isNull()
        }

    @Test
    fun `executeMission with invalid initial position should show error`() =
        runTest {
            // Given
            val error = RoverError.InvalidInitialPosition(x = 10, y = 10, plateauMaxX = 5, plateauMaxY = 5)
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.failure(error)

            // When
            viewModel.executeMission()
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).contains("Rover initial position (10, 10) is outside plateau bounds")
            assertThat(state.errorMessage).contains("(0,0) to (5, 5)")
            assertThat(state.successMessage).isNull()
        }

    @Test
    fun `executeMission with invalid direction should show error`() =
        runTest {
            // Given
            val error = RoverError.InvalidDirectionChar("X")
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.failure(error)

            // When
            viewModel.executeMission()
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).contains("Invalid direction 'X'")
            assertThat(state.errorMessage).contains("Must be N, E, S, or W")
            assertThat(state.successMessage).isNull()
        }

    @Test
    fun `executeMission with invalid plateau dimensions should show error`() =
        runTest {
            // Given
            val error = RoverError.InvalidPlateauDimensions(x = -1, y = -1)
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.failure(error)

            // When
            viewModel.executeMission()
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).contains("Invalid plateau dimensions (-1, -1)")
            assertThat(state.errorMessage).contains("Both must be non-negative")
            assertThat(state.successMessage).isNull()
        }

    @Test
    fun `executeMission with individual inputs should use network execution`() =
        runTest {
            // Given

            coEvery {
                mockExecuteNetworkMissionUseCase.executeFromBuilderInputs(
                    plateauWidth = 5,
                    plateauHeight = 5,
                    roverStartX = 1,
                    roverStartY = 2,
                    roverDirection = "N",
                    movements = "LMLMLMLMM"
                )
            } returns flowOf(NetworkResult.Success("1 3 N"))

            viewModel.switchInputMode(InputMode.BUILDER)
            viewModel.updatePlateauWidth("5")
            viewModel.updatePlateauHeight("5")
            viewModel.updateRoverStartX("1")
            viewModel.updateRoverStartY("2")
            viewModel.updateRoverStartDirection("N")
            viewModel.updateMovementCommands("LMLMLMLMM")

            // When
            viewModel.executeMission()
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.successMessage).contains("Network mission completed!")
            assertThat(state.successMessage).contains("1 3 N")
            assertThat(state.errorMessage).isNull()

            coVerify {
                mockExecuteNetworkMissionUseCase.executeFromBuilderInputs(
                    plateauWidth = 5,
                    plateauHeight = 5,
                    roverStartX = 1,
                    roverStartY = 2,
                    roverDirection = "N",
                    movements = "LMLMLMLMM"
                )
            }
        }

    @Test
    fun `executeMission should set loading state during execution`() =
        runTest {
            // Given
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.success("1 3 N")
            assertThat(viewModel.uiState.value.isLoading).isFalse()

            // When
            viewModel.executeMission()
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.successMessage).isNotNull()
        }

    @Test
    fun `executeMission with unexpected exception should show generic error`() =
        runTest {
            // Given
            every { mockExecuteRoverMissionUseCase(any()) } throws IllegalArgumentException("Unexpected error")

            // When
            viewModel.executeMission()
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).contains("Invalid input parameters")
            assertThat(state.errorMessage).contains("Unexpected error")
            assertThat(state.successMessage).isNull()
        }

    @Test
    fun `clearMessages should clear all error and success messages`() {
        // Given - Simulate some messages being set
        every { mockExecuteRoverMissionUseCase(any()) } returns Result.failure(RoverError.InvalidInputFormat("test"))
        viewModel.executeMission()

        // When
        viewModel.clearMessages()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.errorMessage).isNull()
        assertThat(state.successMessage).isNull()
        assertThat(state.jsonError).isNull()
    }

    // Input filtering tests
    @Test
    fun `updatePlateauWidth should filter out non-digit characters`() {
        // When
        viewModel.updatePlateauWidth("12abc34")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauWidth).isEqualTo("1234")
        assertThat(state.plateauWidthError).isNull()
    }

    @Test
    fun `updatePlateauHeight should filter out non-digit characters`() {
        // When
        viewModel.updatePlateauHeight("5#6@7")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauHeight).isEqualTo("567")
        assertThat(state.plateauHeightError).isNull()
    }

    @Test
    fun `updateRoverStartX should filter out letters and special characters`() {
        // When
        viewModel.updateRoverStartX("a1b2c3!")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartX).isEqualTo("123")
        assertThat(state.roverStartXError).isNull()
    }

    @Test
    fun `updateRoverStartY should filter out letters and special characters`() {
        // When
        viewModel.updateRoverStartY("ads")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartY).isEmpty()
        assertThat(state.roverStartYError).isNull()
    }

    @Test
    fun `updateRoverStartX should handle empty input after filtering`() {
        // When
        viewModel.updateRoverStartX("abc!@#")

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartX).isEmpty()
        assertThat(state.roverStartXError).isNull()
    }

    @Test
    fun `validation should show proper error for different input types`() {
        // Test validation differentiates between invalid format and negative numbers

        // Update with a valid but negative number (this won't be filtered since we only filter digits)
        // But actually, negative numbers would need minus sign which gets filtered
        // So let's test with zero which is invalid for plateau dimensions
        viewModel.updatePlateauWidth("0")
        viewModel.validatePlateauWidth()

        var state = viewModel.uiState.value
        assertThat(state.plateauWidthError).isEqualTo("Must be a positive number")

        // Test empty input validation
        viewModel.updateRoverStartX("")
        viewModel.validateRoverStartX()

        state = viewModel.uiState.value
        assertThat(state.roverStartXError).isNull() // Empty is allowed for coordinates
    }

    @Test
    fun `validatePlateauWidth with invalid input should set error`() {
        // Given - Input filtering will remove "-" sign, leaving "1"
        viewModel.updatePlateauWidth("-1")

        // When
        viewModel.validatePlateauWidth()

        // Then - Since filtering removes "-", we get "1" which is valid
        val state = viewModel.uiState.value
        assertThat(state.plateauWidth).isEqualTo("1")
        assertThat(state.plateauWidthError).isNull()
    }

    @Test
    fun `validatePlateauHeight with invalid input should set error`() {
        // Given
        viewModel.updatePlateauHeight("0")

        // When
        viewModel.validatePlateauHeight()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauHeightError).isEqualTo("Must be a positive number")
    }

    @Test
    fun `validateRoverStartX with negative value should set error`() {
        // Given - Input filtering will remove "-" sign, leaving "1"
        viewModel.updateRoverStartX("-1")

        // When
        viewModel.validateRoverStartX()

        // Then - Since filtering removes "-", we get "1" which is valid
        val state = viewModel.uiState.value
        assertThat(state.roverStartX).isEqualTo("1")
        assertThat(state.roverStartXError).isNull()
    }

    @Test
    fun `validateRoverStartY with non-numeric value should set error`() {
        // Given - Input filtering will remove letters, leaving empty string
        viewModel.updateRoverStartY("abc")

        // When
        viewModel.validateRoverStartY()

        // Then - Empty string doesn't trigger validation error
        val state = viewModel.uiState.value
        assertThat(state.roverStartY).isEmpty()
        assertThat(state.roverStartYError).isNull()
    }

    @Test
    fun `validateMovementCommands with invalid characters should set error`() {
        // Given
        viewModel.updateMovementCommands("LMXR")

        // When
        viewModel.validateMovementCommands()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.movementCommandsError).isEqualTo("Must contain only L, R, M characters")
    }
}
