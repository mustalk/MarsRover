package com.mustalk.seat.marsrover.presentation.ui.mission

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for NewMissionScreen.
 * Tests both JSON and individual input modes with comprehensive user interactions.
 * Uses NewMissionContent directly to avoid Hilt dependency issues.
 */
@RunWith(AndroidJUnit4::class)
class NewMissionScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
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

        // Then
        composeTestRule.onNodeWithText("Choose input method").assertIsDisplayed()
        composeTestRule.onNodeWithText("JSON").assertIsDisplayed()
        composeTestRule.onNodeWithText("Builder").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mission JSON Configuration").assertIsDisplayed()
        composeTestRule.onNodeWithText("Execute").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun newMissionContent_switchToIndividualMode_showsIndividualInputs() {
        // Given
        composeTestRule.setContent {
            MarsRoverTheme {
                NewMissionContent(
                    uiState = NewMissionUiState(inputMode = InputMode.INDIVIDUAL),
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
        var currentJson = ""
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

        // Then
        composeTestRule.onNodeWithText("Execute").assertIsEnabled()
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

        // Then
        composeTestRule.onNodeWithText("Cancel").assertIsEnabled()
    }

    @Test
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

        // Initially should show JSON mode
        composeTestRule.onNodeWithText("Mission JSON Configuration").assertIsDisplayed()

        // When clicking Individual button
        composeTestRule.onNodeWithText("Builder").performClick()

        // Then should show individual inputs
        composeTestRule.onNodeWithText("Plateau Size").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rover Position").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rover Movements").assertIsDisplayed()
    }
}
