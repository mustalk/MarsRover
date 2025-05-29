package com.mustalk.seat.marsrover.presentation.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustalk.seat.marsrover.presentation.ui.theme.MarsRoverTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MarsToastTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun marsToast_displaysMessageCorrectly() {
        // Given
        val message = "Mission completed successfully"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsToast(
                    message = message,
                    type = MarsToastType.Info
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(message)
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_withTitle_displaysBothTitleAndMessage() {
        // Given
        val title = "Mission Status"
        val message = "Rover has reached target destination"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsToast(
                    title = title,
                    message = message,
                    type = MarsToastType.Info
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(message)
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_errorType_displaysWithErrorIcon() {
        // Given
        val message = "Invalid JSON format"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsToast(
                    message = message,
                    type = MarsToastType.Error
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(message)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Error")
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_warningType_displaysWithWarningIcon() {
        // Given
        val message = "Rover approaching plateau boundary"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsToast(
                    message = message,
                    type = MarsToastType.Warning
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(message)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Warning")
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_infoType_displaysWithInfoIcon() {
        // Given
        val message = "Mission data uploaded successfully"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsToast(
                    message = message,
                    type = MarsToastType.Info
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(message)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Information")
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_withContentDescription_isAccessible() {
        // Given
        val message = "Connection established"
        val contentDesc = "Success notification"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsToast(
                    message = message,
                    type = MarsToastType.Info,
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
    fun marsToast_allTypes_renderCorrectlyInLightTheme() {
        // When
        composeTestRule.setContent {
            MarsRoverTheme(darkTheme = false) {
                MarsToast(
                    title = "Light Theme Test",
                    message = "Testing error toast in light theme",
                    type = MarsToastType.Error
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Light Theme Test")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Testing error toast in light theme")
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_allTypes_renderCorrectlyInDarkTheme() {
        // When
        composeTestRule.setContent {
            MarsRoverTheme(darkTheme = true) {
                MarsToast(
                    title = "Dark Theme Test",
                    message = "Testing warning toast in dark theme",
                    type = MarsToastType.Warning
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Dark Theme Test")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Testing warning toast in dark theme")
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_longMessage_displaysCorrectly() {
        // Given
        val longMessage =
            "This is a very long message that should still display correctly in the toast component and wrap properly to multiple lines if needed"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsToast(
                    message = longMessage,
                    type = MarsToastType.Info
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(longMessage)
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_withoutTitle_displaysOnlyMessage() {
        // Given
        val message = "Simple message without title"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsToast(
                    message = message,
                    type = MarsToastType.Info
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(message)
            .assertIsDisplayed()
    }
}
