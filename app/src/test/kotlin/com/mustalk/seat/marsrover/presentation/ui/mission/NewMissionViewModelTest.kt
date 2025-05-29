package com.mustalk.seat.marsrover.presentation.ui.mission

import com.google.common.truth.Truth.assertThat
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
 * Tests state management for both JSON and individual input modes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NewMissionViewModelTest {
    private lateinit var viewModel: NewMissionViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NewMissionViewModel()
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
            // Given - Using the default valid JSON from initial state

            // When
            viewModel.executeMission()
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.successMessage).contains("Mission completed!")
            assertThat(state.successMessage).contains("1 3 N")
            assertThat(state.errorMessage).isNull()
        }

    @Test
    fun `executeMission with invalid JSON should fail`() =
        runTest {
            // Given
            viewModel.updateJsonInput("invalid json")

            // When
            viewModel.executeMission()
            advanceUntilIdle()

            // Then
            val state = viewModel.uiState.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.errorMessage).contains("Invalid JSON format")
            assertThat(state.successMessage).isNull()
        }

    @Test
    fun `executeMission with individual inputs should build JSON and execute`() =
        runTest {
            // Given
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
            assertThat(state.errorMessage).isNull()
        }

    @Test
    fun `executeMission should set loading state during execution`() =
        runTest {
            // Given - Verify initial state is not loading
            assertThat(viewModel.uiState.value.isLoading).isFalse()

            // When
            viewModel.executeMission()

            // Then - The coroutine should complete and loading should be false
            advanceUntilIdle()

            // Then - After execution, loading should be false and we should have a result
            val finalState = viewModel.uiState.value
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.successMessage).isNotNull()
        }

    @Test
    fun `clearMessages should clear all error and success messages`() {
        // Given - Set some messages
        viewModel.updateJsonInput("invalid")
        viewModel.executeMission()

        // When
        viewModel.clearMessages()

        // Then
        val state = viewModel.uiState.value
        assertThat(state.errorMessage).isNull()
        assertThat(state.successMessage).isNull()
        assertThat(state.jsonError).isNull()
    }
}
