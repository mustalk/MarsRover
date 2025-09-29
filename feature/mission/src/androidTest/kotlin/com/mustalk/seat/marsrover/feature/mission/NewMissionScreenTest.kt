package com.mustalk.seat.marsrover.feature.mission

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
import androidx.test.platform.app.InstrumentationRegistry
import com.mustalk.seat.marsrover.core.testing.jvm.data.MarsRoverTestData
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
        val context = InstrumentationRegistry.getInstrumentation().targetContext

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
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_input_mode_selector_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_input_mode_json_short)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_input_mode_builder_short)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_json_configuration_title)).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.feature_mission_cd_execute_mission)).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.feature_mission_cd_cancel_mission)).assertIsDisplayed()
    }

    @Test
    @Ignore("Flaky in CI - works locally but fails in GitHub Actions emulator, needs investigation")
    fun newMissionContent_switchToBuilderMode_showsBuilderInputs() {
        // Given
        val context = InstrumentationRegistry.getInstrumentation().targetContext

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
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_plateau_size)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_rover_position)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_rover_movements)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_plateau_width)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_plateau_height)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_rover_start_x)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_rover_start_y)).assertIsDisplayed()
    }

    @Test
    fun newMissionContent_jsonMode_canEnterJson() {
        // Given
        var currentJson by mutableStateOf("")
        val context = InstrumentationRegistry.getInstrumentation().targetContext

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
        val testJson = MarsRoverTestData.JsonParserTestData.ValidInput.JSON
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_input_json_label)).performTextReplacement(testJson)

        // Then
        composeTestRule.runOnIdle {
            assert(currentJson == testJson)
        }
    }

    @Test
    fun newMissionContent_executeButton_isEnabledByDefault() {
        // Given
        val context = InstrumentationRegistry.getInstrumentation().targetContext

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
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.feature_mission_cd_execute_mission)).assertIsEnabled()
    }

    @Test
    fun newMissionContent_cancelButton_isEnabled() {
        // Given
        val context = InstrumentationRegistry.getInstrumentation().targetContext

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
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.feature_mission_cd_cancel_mission)).assertIsEnabled()
    }

    @Test
    @Ignore("Flaky in CI - works locally but fails in GitHub Actions emulator, needs investigation")
    fun newMissionContent_segmentedButtonsWork() {
        // Given
        val context = InstrumentationRegistry.getInstrumentation().targetContext

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
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_json_configuration_title)).assertIsDisplayed()

        // When clicking Builder button
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_input_mode_builder_short)).performClick()

        // Advance time for state change and wait
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Then should show builder inputs
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_plateau_size)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_rover_position)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.feature_mission_rover_movements)).assertIsDisplayed()
    }
}
