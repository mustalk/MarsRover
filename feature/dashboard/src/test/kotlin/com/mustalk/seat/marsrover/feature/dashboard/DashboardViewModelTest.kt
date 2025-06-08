package com.mustalk.seat.marsrover.feature.dashboard

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.core.testing.jvm.data.DashboardTestData
import com.mustalk.seat.marsrover.core.testing.jvm.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        viewModel = DashboardViewModel()
    }

    @Test
    fun `initial state should be correct`() =
        runTest {
            val initialState = viewModel.uiState.value

            assertThat(initialState.lastMissionResult).isNull()
            assertThat(initialState.isLoading).isFalse()
            assertThat(initialState.errorMessage).isNull()
            assertThat(initialState.showNewMissionDialog).isFalse()
        }

    @Test
    fun `onNewMissionClicked should set showNewMissionDialog to true`() =
        runTest {
            viewModel.onNewMissionClicked()

            val updatedState = viewModel.uiState.value
            assertThat(updatedState.showNewMissionDialog).isTrue()
        }

    @Test
    fun `onNewMissionDialogDismissed should set showNewMissionDialog to false`() =
        runTest {
            // First set dialog to show
            viewModel.onNewMissionClicked()
            assertThat(viewModel.uiState.value.showNewMissionDialog).isTrue()

            viewModel.onNewMissionDialogDismissed()

            val updatedState = viewModel.uiState.value
            assertThat(updatedState.showNewMissionDialog).isFalse()
        }

    @Test
    fun `updateMissionResult should update state with new result`() =
        runTest {
            val testData = DashboardTestData.SuccessfulMissions.StandardSuccess
            val missionResult =
                MissionResult(
                    finalPosition = testData.FINAL_POSITION,
                    isSuccess = testData.IS_SUCCESS,
                    timestamp = testData.TIMESTAMP,
                    originalInput = testData.ORIGINAL_INPUT
                )

            viewModel.updateMissionResult(missionResult)
            advanceUntilIdle()

            val updatedState = viewModel.uiState.value
            assertThat(updatedState.lastMissionResult).isEqualTo(missionResult)
            assertThat(updatedState.isLoading).isFalse()
            assertThat(updatedState.errorMessage).isNull()
        }

    @Test
    fun `clearError should remove error message`() =
        runTest {
            // First set an error
            val errorMessage = DashboardTestData.ErrorMessages.MISSION_FAILED
            viewModel.showError(errorMessage)
            assertThat(viewModel.uiState.value.errorMessage).isEqualTo(errorMessage)

            viewModel.clearError()

            val updatedState = viewModel.uiState.value
            assertThat(updatedState.errorMessage).isNull()
        }

    @Test
    fun `setLoading should update loading state`() =
        runTest {
            viewModel.setLoading(true)
            assertThat(viewModel.uiState.value.isLoading).isTrue()

            viewModel.setLoading(false)
            assertThat(viewModel.uiState.value.isLoading).isFalse()
        }

    @Test
    fun `showError should set error message and stop loading`() =
        runTest {
            val errorMessage = DashboardTestData.ErrorMessages.MISSION_FAILED

            // First set loading to true
            viewModel.setLoading(true)
            assertThat(viewModel.uiState.value.isLoading).isTrue()

            viewModel.showError(errorMessage)

            val errorState = viewModel.uiState.value
            assertThat(errorState.errorMessage).isEqualTo(errorMessage)
            assertThat(errorState.isLoading).isFalse()
        }

    @Test
    fun `updateMissionResult should clear previous error and loading state`() =
        runTest {
            val testData = DashboardTestData.FailedMissions.ValidationFailure
            val missionResult =
                MissionResult(
                    finalPosition = testData.FINAL_POSITION,
                    isSuccess = testData.IS_SUCCESS,
                    timestamp = testData.TIMESTAMP,
                    originalInput = testData.ORIGINAL_INPUT
                )

            // First set error and loading
            viewModel.setLoading(true)
            viewModel.showError(DashboardTestData.ErrorMessages.PREVIOUS_ERROR)
            assertThat(viewModel.uiState.value.errorMessage).isEqualTo(DashboardTestData.ErrorMessages.PREVIOUS_ERROR)

            viewModel.updateMissionResult(missionResult)
            advanceUntilIdle()

            val updatedState = viewModel.uiState.value
            assertThat(updatedState.lastMissionResult).isEqualTo(missionResult)
            assertThat(updatedState.isLoading).isFalse()
            assertThat(updatedState.errorMessage).isNull()
        }

    @Test
    fun `mission result with success true should be handled correctly`() =
        runTest {
            val testData = DashboardTestData.SuccessfulMissions.ComplexSuccess
            val successResult =
                MissionResult(
                    finalPosition = testData.FINAL_POSITION,
                    isSuccess = testData.IS_SUCCESS,
                    timestamp = testData.TIMESTAMP,
                    originalInput = testData.ORIGINAL_INPUT
                )

            viewModel.updateMissionResult(successResult)
            advanceUntilIdle()

            val updatedState = viewModel.uiState.value
            assertThat(updatedState.lastMissionResult).isEqualTo(successResult)
            assertThat(updatedState.lastMissionResult!!.isSuccess).isTrue()
            assertThat(updatedState.lastMissionResult!!.finalPosition).isEqualTo(testData.FINAL_POSITION)
            assertThat(updatedState.lastMissionResult!!.timestamp).isEqualTo(testData.TIMESTAMP)
            assertThat(updatedState.lastMissionResult!!.originalInput).isEqualTo(testData.ORIGINAL_INPUT)
        }

    @Test
    fun `mission result with success false should be handled correctly`() =
        runTest {
            val testData = DashboardTestData.FailedMissions.StandardFailure
            val failureResult =
                MissionResult(
                    finalPosition = testData.FINAL_POSITION,
                    isSuccess = testData.IS_SUCCESS,
                    timestamp = testData.TIMESTAMP,
                    originalInput = testData.ORIGINAL_INPUT
                )

            viewModel.updateMissionResult(failureResult)
            advanceUntilIdle()

            val updatedState = viewModel.uiState.value
            assertThat(updatedState.lastMissionResult).isEqualTo(failureResult)
            assertThat(updatedState.lastMissionResult!!.isSuccess).isFalse()
            assertThat(updatedState.lastMissionResult!!.finalPosition).isEqualTo(testData.FINAL_POSITION)
        }

    @Test
    fun `should handle multiple error scenarios with structured test data`() =
        runTest {
            // Test connection failed error
            viewModel.showError(DashboardTestData.ErrorMessages.CONNECTION_FAILED)
            assertThat(viewModel.uiState.value.errorMessage).isEqualTo(DashboardTestData.ErrorMessages.CONNECTION_FAILED)

            viewModel.clearError()

            // Test processing failed error
            viewModel.showError(DashboardTestData.ErrorMessages.PROCESSING_FAILED)
            assertThat(viewModel.uiState.value.errorMessage).isEqualTo(DashboardTestData.ErrorMessages.PROCESSING_FAILED)

            viewModel.clearError()

            // Test validation failed error
            viewModel.showError(DashboardTestData.ErrorMessages.VALIDATION_FAILED)
            assertThat(viewModel.uiState.value.errorMessage).isEqualTo(DashboardTestData.ErrorMessages.VALIDATION_FAILED)
        }

    @Test
    fun `should handle mission without original input correctly`() =
        runTest {
            val testData = DashboardTestData.SuccessfulMissions.SimpleSuccess
            val missionResult =
                MissionResult(
                    finalPosition = testData.FINAL_POSITION,
                    isSuccess = testData.IS_SUCCESS,
                    timestamp = testData.TIMESTAMP,
                    originalInput = testData.ORIGINAL_INPUT
                )

            viewModel.updateMissionResult(missionResult)
            advanceUntilIdle()

            val updatedState = viewModel.uiState.value
            assertThat(updatedState.lastMissionResult).isEqualTo(missionResult)
            assertThat(updatedState.lastMissionResult!!.originalInput).isEqualTo("")
        }
}
