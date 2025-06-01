package com.mustalk.seat.marsrover.presentation.ui.dashboard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dashboardScreen_emptyState_displaysWelcomeMessage() {
        // Given
        val uiState = DashboardUiState()

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
            .onNodeWithText("🚀 Mars Rover Mission Control")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Ready to deploy your first Mars rover mission?")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Tap the + button to get started")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_withMissionResult_displaysMissionCard() {
        // Given
        val missionResult =
            MissionResult(
                finalPosition = "1 3 N",
                isSuccess = true,
                originalInput = """{"test": "input"}"""
            )
        val uiState = DashboardUiState(lastMissionResult = missionResult)

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
            .onNodeWithText("Mission Result")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Mission Completed Successfully")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Final Position: 1 3 N")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_withFailedMission_displaysErrorState() {
        // Given
        val missionResult =
            MissionResult(
                finalPosition = "Error: Invalid JSON",
                isSuccess = false
            )
        val uiState = DashboardUiState(lastMissionResult = missionResult)

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
            .onNodeWithText("Mission Failed")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Final Position: Error: Invalid JSON")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_loadingState_displaysLoader() {
        // Given
        val uiState = DashboardUiState(isLoading = true)

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
            .onNodeWithText("Processing mission data…")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_withError_displaysErrorToast() {
        // Given
        val uiState = DashboardUiState(errorMessage = "Connection failed")

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
            .onNodeWithText("Mission Failed")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Connection failed")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_fab_isClickable() {
        // Given
        val uiState = DashboardUiState()
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
            .onNodeWithContentDescription("Start new mission")
            .assertIsDisplayed()
            .performClick()

        assert(fabClicked)
    }

    @Test
    fun dashboardScreen_emptyState_showsExtendedFab() {
        // Given
        val uiState = DashboardUiState()

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
            .onNodeWithText("New Mission")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_withMissionResult_showsRegularFab() {
        // Given
        val missionResult =
            MissionResult(
                finalPosition = "2 2 E",
                isSuccess = true
            )
        val uiState = DashboardUiState(lastMissionResult = missionResult)

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
            .onNodeWithText("New Mission")
            .assertDoesNotExist()

        // But should still be clickable
        composeTestRule
            .onNodeWithContentDescription("Start new mission")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_welcomeHeader_displaysCorrectly() {
        // Given
        val missionResult =
            MissionResult(
                finalPosition = "0 0 N",
                isSuccess = true
            )
        val uiState = DashboardUiState(lastMissionResult = missionResult)

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
            .onNodeWithText("🚀 Mars Rover Mission Control")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Mission Control Dashboard")
            .assertIsDisplayed()
    }

    @Test
    fun dashboardScreen_missionWithOriginalInput_displaysInputPreview() {
        // Given
        val longInput = """{"topRightCorner": {"x": 5, "y": 5}, "roverPosition": {"x": 1, "y": 2}, "roverDirection": "N", "movements": "LMLMLMLMM"}"""
        val missionResult =
            MissionResult(
                finalPosition = "1 3 N",
                isSuccess = true,
                originalInput = longInput
            )
        val uiState = DashboardUiState(lastMissionResult = missionResult)

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
            .onNodeWithText("Mission Instructions:")
            .assertIsDisplayed()

        // And should display truncated input (first 50 characters + "...")
        val expectedTruncated = longInput.take(50) + "..."
        composeTestRule
            .onNodeWithText(expectedTruncated)
            .assertIsDisplayed()
    }
}
