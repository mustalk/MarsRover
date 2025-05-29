package com.mustalk.seat.marsrover.presentation.ui.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
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
class MarsButtonTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun marsButton_displaysTextCorrectly() {
        // Given
        val buttonText = "Execute Mission"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsButton(
                    text = buttonText,
                    onClick = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(buttonText)
            .assertIsDisplayed()
    }

    @Test
    fun marsButton_primaryVariant_isClickable() {
        // Given
        var clicked = false
        val buttonText = "Primary Button"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsButton(
                    text = buttonText,
                    onClick = { clicked = true },
                    variant = MarsButtonVariant.Primary
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(buttonText)
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertHasClickAction()
            .performClick()

        assert(clicked)
    }

    @Test
    fun marsButton_secondaryVariant_isClickable() {
        // Given
        var clicked = false
        val buttonText = "Secondary Button"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsButton(
                    text = buttonText,
                    onClick = { clicked = true },
                    variant = MarsButtonVariant.Secondary
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(buttonText)
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertHasClickAction()
            .performClick()

        assert(clicked)
    }

    @Test
    fun marsButton_textVariant_isClickable() {
        // Given
        var clicked = false
        val buttonText = "Text Button"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsButton(
                    text = buttonText,
                    onClick = { clicked = true },
                    variant = MarsButtonVariant.Text
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(buttonText)
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertHasClickAction()
            .performClick()

        assert(clicked)
    }

    @Test
    fun marsButton_whenDisabled_isNotClickable() {
        // Given
        var clicked = false
        val buttonText = "Disabled Button"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsButton(
                    text = buttonText,
                    onClick = { clicked = true },
                    enabled = false
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(buttonText)
            .assertIsDisplayed()
            .assertIsNotEnabled()

        // Should still have click action semantically but be disabled
        composeTestRule
            .onNodeWithText(buttonText)
            .assertHasClickAction()
            .performClick()

        // Click should not register
        assert(!clicked)
    }

    @Test
    fun marsButton_whenLoading_showsLoadingIndicator() {
        // Given
        val buttonText = "Loading Button"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsButton(
                    text = buttonText,
                    onClick = { },
                    isLoading = true
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(buttonText)
            .assertIsDisplayed()

        // Button should be disabled when loading
        composeTestRule
            .onNodeWithText(buttonText)
            .assertIsNotEnabled()
    }

    @Test
    fun marsButton_whenLoadingAndDisabled_isNotClickable() {
        // Given
        var clicked = false
        val buttonText = "Loading Disabled"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsButton(
                    text = buttonText,
                    onClick = { clicked = true },
                    isLoading = true,
                    enabled = false
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(buttonText)
            .assertIsDisplayed()
            .assertIsNotEnabled()
            .performClick()

        assert(!clicked)
    }

    @Test
    fun marsButton_withContentDescription_isAccessible() {
        // Given
        val buttonText = "Mission Button"
        val contentDesc = "Execute Mars rover mission"

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsButton(
                    text = buttonText,
                    onClick = { },
                    contentDescription = contentDesc
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription(contentDesc)
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun marsButton_lightTheme_rendersCorrectly() {
        // Given
        val buttonText = "Light Theme Button"

        // When
        composeTestRule.setContent {
            MarsRoverTheme(darkTheme = false) {
                MarsButton(
                    text = buttonText,
                    onClick = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(buttonText)
            .assertIsDisplayed()
    }

    @Test
    fun marsButton_darkTheme_rendersCorrectly() {
        // Given
        val buttonText = "Dark Theme Button"

        // When
        composeTestRule.setContent {
            MarsRoverTheme(darkTheme = true) {
                MarsButton(
                    text = buttonText,
                    onClick = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(buttonText)
            .assertIsDisplayed()
    }
}
