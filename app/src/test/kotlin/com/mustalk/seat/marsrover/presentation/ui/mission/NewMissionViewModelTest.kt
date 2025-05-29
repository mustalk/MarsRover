package com.mustalk.seat.marsrover.presentation.ui.mission

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.domain.error.RoverError
import com.mustalk.seat.marsrover.domain.usecase.ExecuteRoverMissionUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockExecuteRoverMissionUseCase = mockk()
        viewModel = NewMissionViewModel(mockExecuteRoverMissionUseCase)
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
        viewModel.switchInputMode(InputMode.INDIVIDUAL)

        // Then
        val state = viewModel.uiState.value
        assertThat(state.inputMode).isEqualTo(InputMode.INDIVIDUAL)
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
    fun `executeMission with individual inputs should build JSON and execute`() =
        runTest {
            // Given
            val expectedResult = "2 4 E"
            every { mockExecuteRoverMissionUseCase(any()) } returns Result.success(expectedResult)

            viewModel.switchInputMode(InputMode.INDIVIDUAL)
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
            assertThat(state.successMessage).contains("Mission completed!")
            assertThat(state.successMessage).contains(expectedResult)
            assertThat(state.errorMessage).isNull()

            verify { mockExecuteRoverMissionUseCase(any()) }
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
            every { mockExecuteRoverMissionUseCase(any()) } throws RuntimeException("Unexpected error")

            // When
            viewModel.executeMission()
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).contains("Unexpected error occurred")
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

    // Validation tests
    @Test
    fun `validatePlateauWidth with invalid input should set error`() {
        // Given
        viewModel.updatePlateauWidth("-1")

        // When
        viewModel.validatePlateauWidth()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.plateauWidthError).isEqualTo("Must be a positive number")
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
        // Given
        viewModel.updateRoverStartX("-1")

        // When
        viewModel.validateRoverStartX()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartXError).isEqualTo("Must be a non-negative number")
    }

    @Test
    fun `validateRoverStartY with non-numeric value should set error`() {
        // Given
        viewModel.updateRoverStartY("abc")

        // When
        viewModel.validateRoverStartY()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.roverStartYError).isEqualTo("Must be a non-negative number")
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
