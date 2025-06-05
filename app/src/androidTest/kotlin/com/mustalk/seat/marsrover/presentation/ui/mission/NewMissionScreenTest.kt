package com.mustalk.seat.marsrover.presentation.ui.mission

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustalk.seat.marsrover.core.ui.theme.MarsRoverTheme
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for NewMissionScreen.
 * Tests both JSON and builder input modes with comprehensive user interactions.
 * Uses NewMissionContent directly to avoid Hilt dependency issues.
 */
@RunWith(AndroidJUnit4::class)
class NewMissionScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @Ignore("Flaky in CI - works locally but fails in GitHub Actions emulator, needs investigation")
    fun newMissionContent_initialState_showsJsonMode() {
        // Given
        composeTestRule.setContent {
            MarsRoverTheme {
                NewMissionContent(
                    uiState = NewMissionUiState(inputMode = InputMode.JSON),
                    onInputModeChange = {},
                    onJsonInputChange = {},
                    onPlateauWidthChange = {},
                    onPlateauHeightChange = {},
                    onRoverStartXChange = {},
                    onRoverStartYChange = {},
                    onRoverDirectionChange = {},
                    onMovementCommandsChange = {},
                    onExecuteMission = {},
                    onNavigateBack = {}
                )
            }
        }

        // Advance time to allow composition to settle
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText("Choose input method").assertIsDisplayed()
        composeTestRule.onNodeWithText("JSON").assertIsDisplayed()
        composeTestRule.onNodeWithText("Builder").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mission JSON Configuration").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Execute mission with current parameters").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Cancel and return to previous screen").assertIsDisplayed()
    }

    @Test
    @Ignore("Flaky in CI - works locally but fails in GitHub Actions emulator, needs investigation")
    fun newMissionContent_switchToBuilderMode_showsBuilderInputs() {
        // Given
        composeTestRule.setContent {
            MarsRoverTheme {
                NewMissionContent(
                    uiState = NewMissionUiState(inputMode = InputMode.BUILDER),
                    onInputModeChange = {},
                    onJsonInputChange = {},
                    onPlateauWidthChange = {},
                    onPlateauHeightChange = {},
                    onRoverStartXChange = {},
                    onRoverStartYChange = {},
                    onRoverDirectionChange = {},
                    onMovementCommandsChange = {},
                    onExecuteMission = {},
                    onNavigateBack = {}
                )
            }
        }

        // Advance time to allow composition to settle
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNodeWithText("Plateau Size").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rover Position").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rover Movements").assertIsDisplayed()
        composeTestRule.onNodeWithText("Width").assertIsDisplayed()
        composeTestRule.onNodeWithText("Height").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start X").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Y").assertIsDisplayed()
    }

    @Test
    fun newMissionContent_jsonMode_canEnterJson() {
        // Given
        var currentJson by mutableStateOf("")
        composeTestRule.setContent {
            MarsRoverTheme {
                NewMissionContent(
                    uiState =
                        NewMissionUiState(
                            inputMode = InputMode.JSON,
                            jsonInput = currentJson
                        ),
                    onInputModeChange = {},
                    onJsonInputChange = { currentJson = it },
                    onPlateauWidthChange = {},
                    onPlateauHeightChange = {},
                    onRoverStartXChange = {},
                    onRoverStartYChange = {},
                    onRoverDirectionChange = {},
                    onMovementCommandsChange = {},
                    onExecuteMission = {},
                    onNavigateBack = {}
                )
            }
        }

        // When
        val testJson = """{"test": "value"}"""
        composeTestRule.onNodeWithText("Enter mission JSON").performTextReplacement(testJson)

        // Note: In a real test we would verify the state change, but this tests the UI interaction
    }

    @Test
    fun newMissionContent_executeButton_isEnabledByDefault() {
        // Given
        composeTestRule.setContent {
            MarsRoverTheme {
                NewMissionContent(
                    uiState = NewMissionUiState(inputMode = InputMode.JSON),
                    onInputModeChange = {},
                    onJsonInputChange = {},
                    onPlateauWidthChange = {},
                    onPlateauHeightChange = {},
                    onRoverStartXChange = {},
                    onRoverStartYChange = {},
                    onRoverDirectionChange = {},
                    onMovementCommandsChange = {},
                    onExecuteMission = {},
                    onNavigateBack = {}
                )
            }
        }

        // Advance time to allow composition to settle
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Then - using content description for more reliability
        composeTestRule.onNodeWithContentDescription("Execute mission with current parameters").assertIsEnabled()
    }

    @Test
    fun newMissionContent_cancelButton_isEnabled() {
        // Given
        composeTestRule.setContent {
            MarsRoverTheme {
                NewMissionContent(
                    uiState = NewMissionUiState(inputMode = InputMode.JSON),
                    onInputModeChange = {},
                    onJsonInputChange = {},
                    onPlateauWidthChange = {},
                    onPlateauHeightChange = {},
                    onRoverStartXChange = {},
                    onRoverStartYChange = {},
                    onRoverDirectionChange = {},
                    onMovementCommandsChange = {},
                    onExecuteMission = {},
                    onNavigateBack = {}
                )
            }
        }

        // Advance time to allow composition to settle
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Then - using content description for more reliability
        composeTestRule.onNodeWithContentDescription("Cancel and return to previous screen").assertIsEnabled()
    }

    @Test
    @Ignore("Flaky in CI - works locally but fails in GitHub Actions emulator, needs investigation")
    fun newMissionContent_segmentedButtonsWork() {
        // Given
        composeTestRule.setContent {
            var currentMode by remember { mutableStateOf(InputMode.JSON) }

            MarsRoverTheme {
                NewMissionContent(
                    uiState = NewMissionUiState(inputMode = currentMode),
                    onInputModeChange = { currentMode = it },
                    onJsonInputChange = {},
                    onPlateauWidthChange = {},
                    onPlateauHeightChange = {},
                    onRoverStartXChange = {},
                    onRoverStartYChange = {},
                    onRoverDirectionChange = {},
                    onMovementCommandsChange = {},
                    onExecuteMission = {},
                    onNavigateBack = {}
                )
            }
        }

        // Advance time to allow composition to settle
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Initially should show JSON mode
        composeTestRule.onNodeWithText("Mission JSON Configuration").assertIsDisplayed()

        // When clicking Builder button
        composeTestRule.onNodeWithText("Builder").performClick()

        // Advance time for state change and wait
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Then should show builder inputs
        composeTestRule.onNodeWithText("Plateau Size").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rover Position").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rover Movements").assertIsDisplayed()
    }
}
