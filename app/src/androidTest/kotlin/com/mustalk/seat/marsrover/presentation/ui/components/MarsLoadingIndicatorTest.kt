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
class MarsLoadingIndicatorTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun marsLoadingIndicator_displaysMessageCorrectly() {
        // Given
        val message = "Processing mission data..."

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsLoadingIndicator(
                    message = message
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(message)
            .assertIsDisplayed()
    }

    @Test
    fun marsLoadingIndicator_whenShowMessageFalse_hidesMessage() {
        // Given
        val message = "Hidden message"

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
        val message = "Loading data"
        val contentDesc = "Loading mission information"

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
    fun marsLoadingIndicator_usesMessageAsDefaultContentDescription() {
        // Given
        val message = "Default content description"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsLoadingIndicator(
                    message = message
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription(message)
            .assertIsDisplayed()
    }

    @Test
    fun marsFullScreenLoader_displaysCorrectly() {
        // Given
        val message = "Full screen loading"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsFullScreenLoader(
                    message = message
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(message)
            .assertIsDisplayed()
    }

    @Test
    fun marsLoadingIndicator_rendersInLightTheme() {
        // Given
        val message = "Light theme loading"

        // When
        composeTestRule.setContent {
            MarsRoverTheme(darkTheme = false) {
                MarsLoadingIndicator(
                    message = message
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(message)
            .assertIsDisplayed()
    }

    @Test
    fun marsLoadingIndicator_rendersInDarkTheme() {
        // Given
        val message = "Dark theme loading"

        // When
        composeTestRule.setContent {
            MarsRoverTheme(darkTheme = true) {
                MarsLoadingIndicator(
                    message = message
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(message)
            .assertIsDisplayed()
    }
}
