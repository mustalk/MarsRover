package com.mustalk.seat.marsrover.presentation.ui.dashboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    private lateinit var viewModel: DashboardViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DashboardViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() =
        runTest {
            val initialState = viewModel.uiState.value

            assertNull(initialState.lastMissionResult)
            assertFalse(initialState.isLoading)
            assertNull(initialState.errorMessage)
            assertFalse(initialState.showNewMissionDialog)
        }

    @Test
    fun `onNewMissionClicked should set showNewMissionDialog to true`() =
        runTest {
            viewModel.onNewMissionClicked()

            val updatedState = viewModel.uiState.value
            assertTrue(updatedState.showNewMissionDialog)
        }

    @Test
    fun `onNewMissionDialogDismissed should set showNewMissionDialog to false`() =
        runTest {
            // First set dialog to show
            viewModel.onNewMissionClicked()
            assertTrue(viewModel.uiState.value.showNewMissionDialog)

            viewModel.onNewMissionDialogDismissed()

            val updatedState = viewModel.uiState.value
            assertFalse(updatedState.showNewMissionDialog)
        }

    @Test
    fun `updateMissionResult should update state with new result`() =
        runTest {
            val missionResult =
                MissionResult(
                    finalPosition = "1 3 N",
                    isSuccess = true,
                    originalInput = """{"test": "input"}"""
                )

            viewModel.updateMissionResult(missionResult)
            advanceUntilIdle()

            val updatedState = viewModel.uiState.value
            assertEquals(missionResult, updatedState.lastMissionResult)
            assertFalse(updatedState.isLoading)
            assertNull(updatedState.errorMessage)
        }

    @Test
    fun `clearError should remove error message`() =
        runTest {
            // First set an error
            viewModel.showError("Test error")
            assertEquals("Test error", viewModel.uiState.value.errorMessage)

            viewModel.clearError()

            val updatedState = viewModel.uiState.value
            assertNull(updatedState.errorMessage)
        }

    @Test
    fun `setLoading should update loading state`() =
        runTest {
            viewModel.setLoading(true)
            assertTrue(viewModel.uiState.value.isLoading)

            viewModel.setLoading(false)
            assertFalse(viewModel.uiState.value.isLoading)
        }

    @Test
    fun `showError should set error message and stop loading`() =
        runTest {
            val errorMessage = "Mission failed"

            // First set loading to true
            viewModel.setLoading(true)
            assertTrue(viewModel.uiState.value.isLoading)

            viewModel.showError(errorMessage)

            val errorState = viewModel.uiState.value
            assertEquals(errorMessage, errorState.errorMessage)
            assertFalse(errorState.isLoading)
        }

    @Test
    fun `updateMissionResult should clear previous error and loading state`() =
        runTest {
            val missionResult =
                MissionResult(
                    finalPosition = "2 4 E",
                    isSuccess = false
                )

            // First set error and loading
            viewModel.setLoading(true)
            viewModel.showError("Previous error")
            assertEquals("Previous error", viewModel.uiState.value.errorMessage)

            viewModel.updateMissionResult(missionResult)
            advanceUntilIdle()

            val updatedState = viewModel.uiState.value
            assertEquals(missionResult, updatedState.lastMissionResult)
            assertFalse(updatedState.isLoading)
            assertNull(updatedState.errorMessage)
        }

    @Test
    fun `mission result with success true should be handled correctly`() =
        runTest {
            val successResult =
                MissionResult(
                    finalPosition = "3 2 S",
                    isSuccess = true,
                    timestamp = 1234567890L,
                    originalInput = """{"movements": "LRM"}"""
                )

            viewModel.updateMissionResult(successResult)
            advanceUntilIdle()

            val updatedState = viewModel.uiState.value
            assertEquals(successResult, updatedState.lastMissionResult)
            assertTrue(updatedState.lastMissionResult!!.isSuccess)
            assertEquals("3 2 S", updatedState.lastMissionResult!!.finalPosition)
            assertEquals(1234567890L, updatedState.lastMissionResult!!.timestamp)
            assertEquals("""{"movements": "LRM"}""", updatedState.lastMissionResult!!.originalInput)
        }

    @Test
    fun `mission result with success false should be handled correctly`() =
        runTest {
            val failureResult =
                MissionResult(
                    finalPosition = "Error: Invalid input",
                    isSuccess = false
                )

            viewModel.updateMissionResult(failureResult)
            advanceUntilIdle()

            val updatedState = viewModel.uiState.value
            assertEquals(failureResult, updatedState.lastMissionResult)
            assertFalse(updatedState.lastMissionResult!!.isSuccess)
            assertEquals("Error: Invalid input", updatedState.lastMissionResult!!.finalPosition)
        }
}
