package com.mustalk.seat.marsrover.feature.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mustalk.seat.marsrover.core.testing.jvm.data.DashboardTestData
import com.mustalk.seat.marsrover.core.ui.theme.MarsRoverTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.mustalk.seat.marsrover.core.ui.R as CoreUiR

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun dashboardScreen_emptyState_displaysWelcomeMessage() {
        // Given
        val uiState = DashboardUiState()
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                DashboardContent(
                    uiState = uiState,
                    onNewMissionClick = { },
                    onErrorDismiss = { }
                )
            }
        }

        // Wait for UI to be fully rendered
        composeTestRule.waitForIdle()

        // Then
        composeTestRule
            .onNodeWithText(context.getString(R.string.feature_dashboard_welcome_title))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(context.getString(R.string.feature_dashboard_empty_title))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(context.getString(R.string.feature_dashboard_empty_subtitle))
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_withMissionResult_displaysMissionCard() {
        // Given
        val testData = DashboardTestData.SuccessfulMissions.StandardSuccess
        val missionResult =
            MissionResult(
                finalPosition = testData.FINAL_POSITION,
                isSuccess = testData.IS_SUCCESS,
                timestamp = testData.TIMESTAMP,
                originalInput = testData.ORIGINAL_INPUT
            )
        val uiState = DashboardUiState(lastMissionResult = missionResult)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                DashboardContent(
                    uiState = uiState,
                    onNewMissionClick = { },
                    onErrorDismiss = { }
                )
            }
        }

        // Wait for UI to be fully rendered
        composeTestRule.waitForIdle()

        // Then
        composeTestRule
            .onNodeWithText(context.getString(R.string.feature_dashboard_mission_result))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(context.getString(R.string.feature_dashboard_mission_completed_successfully))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                context.getString(
                    R.string.feature_dashboard_rover_final_position,
                    testData.FINAL_POSITION
                )
            ).assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_withFailedMission_displaysErrorState() {
        // Given
        val testData = DashboardTestData.FailedMissions.StandardFailure
        val missionResult =
            MissionResult(
                finalPosition = testData.FINAL_POSITION,
                isSuccess = testData.IS_SUCCESS,
                timestamp = testData.TIMESTAMP,
                originalInput = testData.ORIGINAL_INPUT
            )
        val uiState = DashboardUiState(lastMissionResult = missionResult)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                DashboardContent(
                    uiState = uiState,
                    onNewMissionClick = { },
                    onErrorDismiss = { }
                )
            }
        }

        // Wait for UI to be fully rendered
        composeTestRule.waitForIdle()

        // Then
        composeTestRule
            .onNodeWithText(context.getString(R.string.feature_dashboard_mission_failed))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                context.getString(
                    R.string.feature_dashboard_rover_final_position,
                    testData.FINAL_POSITION
                )
            ).assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_loadingState_displaysLoader() {
        // Given
        val uiState = DashboardUiState(isLoading = true)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                DashboardContent(
                    uiState = uiState,
                    onNewMissionClick = { },
                    onErrorDismiss = { }
                )
            }
        }

        // Wait for UI to be fully rendered
        composeTestRule.waitForIdle()

        // Then
        composeTestRule
            .onNodeWithText(context.getString(CoreUiR.string.core_ui_loading_mission_data))
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_withError_displaysErrorToast() {
        // Given
        val errorMessage = DashboardTestData.ErrorMessages.CONNECTION_FAILED
        val uiState = DashboardUiState(errorMessage = errorMessage)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                DashboardContent(
                    uiState = uiState,
                    onNewMissionClick = { },
                    onErrorDismiss = { }
                )
            }
        }

        // Wait for UI to be fully rendered
        composeTestRule.waitForIdle()

        // Then
        composeTestRule
            .onNodeWithText(context.getString(CoreUiR.string.core_ui_toast_mission_failed))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_fab_isClickable() {
        // Given
        val uiState = DashboardUiState()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var fabClicked = false

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                DashboardContent(
                    uiState = uiState,
                    onNewMissionClick = { fabClicked = true },
                    onErrorDismiss = { }
                )
            }
        }

        // Wait for UI to be fully rendered
        composeTestRule.waitForIdle()

        // Then
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.feature_dashboard_cd_mission_fab))
            .assertIsDisplayed()
            .performClick()

        assert(fabClicked)
    }

    @Test
    fun dashboardScreen_emptyState_showsExtendedFab() {
        // Given
        val uiState = DashboardUiState()
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                DashboardContent(
                    uiState = uiState,
                    onNewMissionClick = { },
                    onErrorDismiss = { }
                )
            }
        }

        // Wait for UI to be fully rendered
        composeTestRule.waitForIdle()

        // Then - Extended FAB should show text
        composeTestRule
            .onNodeWithText(context.getString(R.string.feature_dashboard_mission_new))
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_withMissionResult_showsRegularFab() {
        // Given
        val testData = DashboardTestData.SuccessfulMissions.SimpleSuccess
        val missionResult =
            MissionResult(
                finalPosition = testData.FINAL_POSITION,
                isSuccess = testData.IS_SUCCESS
            )
        val uiState = DashboardUiState(lastMissionResult = missionResult)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                DashboardContent(
                    uiState = uiState,
                    onNewMissionClick = { },
                    onErrorDismiss = { }
                )
            }
        }

        // Wait for UI to be fully rendered
        composeTestRule.waitForIdle()

        // Then - Regular FAB should not show text
        composeTestRule
            .onNodeWithText(context.getString(R.string.feature_dashboard_mission_new))
            .assertDoesNotExist()

        // But should still be clickable using content description
        composeTestRule
            .onNodeWithContentDescription(context.getString(R.string.feature_dashboard_cd_mission_fab))
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_welcomeHeader_displaysCorrectly() {
        // Given
        val testData = DashboardTestData.SuccessfulMissions.SimpleSuccess
        val missionResult =
            MissionResult(
                finalPosition = testData.FINAL_POSITION,
                isSuccess = testData.IS_SUCCESS
            )
        val uiState = DashboardUiState(lastMissionResult = missionResult)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                DashboardContent(
                    uiState = uiState,
                    onNewMissionClick = { },
                    onErrorDismiss = { }
                )
            }
        }

        // Wait for UI to be fully rendered
        composeTestRule.waitForIdle()

        // Then
        composeTestRule
            .onNodeWithText(context.getString(R.string.feature_dashboard_welcome_title))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(context.getString(R.string.feature_dashboard_mission_control))
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_missionWithOriginalInput_displaysInputPreview() {
        // Given
        val testData = DashboardTestData.LongInputTest
        val missionResult =
            MissionResult(
                finalPosition = testData.FINAL_POSITION,
                isSuccess = testData.IS_SUCCESS,
                originalInput = testData.LONG_INPUT
            )
        val uiState = DashboardUiState(lastMissionResult = missionResult)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                DashboardContent(
                    uiState = uiState,
                    onNewMissionClick = { },
                    onErrorDismiss = { }
                )
            }
        }

        // Wait for UI to be fully rendered
        composeTestRule.waitForIdle()

        // Then - Should display Mission Instructions label
        composeTestRule
            .onNodeWithText(context.getString(R.string.feature_dashboard_mission_instructions))
            .assertIsDisplayed()

        // And should display truncated input using structured test data (first 50 characters + "...")
        composeTestRule
            .onNodeWithText(testData.EXPECTED_TRUNCATED)
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_multipleErrorScenarios_displaysCorrectly() {
        // Given - Test multiple error scenarios
        val networkErrorState = DashboardUiState(errorMessage = DashboardTestData.ErrorMessages.PROCESSING_FAILED)
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                DashboardContent(
                    uiState = networkErrorState,
                    onNewMissionClick = { },
                    onErrorDismiss = { }
                )
            }
        }

        // Wait for UI to be fully rendered
        composeTestRule.waitForIdle()

        // Then - Using string resources and structured error message
        composeTestRule
            .onNodeWithText(context.getString(CoreUiR.string.core_ui_toast_mission_failed))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(DashboardTestData.ErrorMessages.PROCESSING_FAILED)
            .assertIsDisplayed()
    }
}
