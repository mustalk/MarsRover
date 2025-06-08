package com.mustalk.seat.marsrover.core.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mustalk.seat.marsrover.core.ui.R
import com.mustalk.seat.marsrover.core.ui.theme.MarsRoverTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MarsLoadingIndicatorTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun getString(id: Int): String = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test
    fun marsLoadingIndicator_displaysMessageCorrectly() {
        // Given
        val expectedMessage = getString(R.string.core_ui_loading_mission_process)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsLoadingIndicator()
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(expectedMessage)
            .assertIsDisplayed()
    }

    @Test
    fun marsLoadingIndicator_displaysCustomMessage() {
        // Given
        val customMessage = getString(R.string.core_ui_test_content_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsLoadingIndicator(
                    message = customMessage
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(customMessage)
            .assertIsDisplayed()
    }

    @Test
    fun marsLoadingIndicator_whenShowMessageFalse_hidesMessage() {
        // Given
        val message = getString(R.string.core_ui_test_content_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsLoadingIndicator(
                    message = message,
                    showMessage = false
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(message)
            .assertDoesNotExist()
    }

    @Test
    fun marsLoadingIndicator_withContentDescription_isAccessible() {
        // Given
        val message = getString(R.string.core_ui_test_content_example)
        val contentDesc = getString(R.string.core_ui_cd_loading)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsLoadingIndicator(
                    message = message,
                    contentDescription = contentDesc
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription(contentDesc)
            .assertIsDisplayed()
    }

    @Test
    fun marsLoadingIndicator_usesDefaultContentDescription() {
        // Given
        val expectedContentDesc = getString(R.string.core_ui_cd_loading)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsLoadingIndicator()
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription(expectedContentDesc)
            .assertIsDisplayed()
    }

    @Test
    fun marsFullScreenLoader_displaysCorrectly() {
        // Given
        val expectedMessage = getString(R.string.core_ui_loading_mission_process)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsFullScreenLoader()
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(expectedMessage)
            .assertIsDisplayed()
    }

    @Test
    fun marsFullScreenLoader_displaysCustomMessage() {
        // Given
        val customMessage = getString(R.string.core_ui_test_content_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsFullScreenLoader(
                    message = customMessage
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(customMessage)
            .assertIsDisplayed()
    }

    @Test
    fun marsLoadingIndicator_rendersInLightTheme() {
        // Given
        val expectedMessage = getString(R.string.core_ui_loading_mission_process)

        // When
        composeTestRule.setContent {
            MarsRoverTheme(darkTheme = false) {
                MarsLoadingIndicator()
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(expectedMessage)
            .assertIsDisplayed()
    }

    @Test
    fun marsLoadingIndicator_rendersInDarkTheme() {
        // Given
        val expectedMessage = getString(R.string.core_ui_loading_mission_process)

        // When
        composeTestRule.setContent {
            MarsRoverTheme(darkTheme = true) {
                MarsLoadingIndicator()
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(expectedMessage)
            .assertIsDisplayed()
    }
}
