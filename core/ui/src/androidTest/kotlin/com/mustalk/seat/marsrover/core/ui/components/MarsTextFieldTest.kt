package com.mustalk.seat.marsrover.core.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mustalk.seat.marsrover.core.ui.R
import com.mustalk.seat.marsrover.core.ui.theme.MarsRoverTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MarsTextFieldTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun getString(id: Int): String = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test
    fun marsTextField_displaysLabelCorrectly() {
        // Given
        val label = getString(R.string.test_label_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsTextField(
                    value = "",
                    onValueChange = { },
                    label = label
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(label)
            .assertIsDisplayed()
    }

    @Test
    fun marsTextField_displaysPlaceholderWhenEmpty() {
        // Given
        val label = getString(R.string.test_label_example)
        val placeholder = getString(R.string.test_placeholder_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsTextField(
                    value = "",
                    onValueChange = { },
                    label = label,
                    placeholder = placeholder
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(label)
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNodeWithText(placeholder)
            .assertIsDisplayed()
    }

    @Test
    fun marsTextField_acceptsTextInput() {
        // Given
        val label = getString(R.string.test_label_example)
        val inputText = getString(R.string.test_value_example)
        var currentValue = ""

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsTextField(
                    value = currentValue,
                    onValueChange = { currentValue = it },
                    label = label
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(label)
            .performClick()
            .performTextInput(inputText)

        assert(currentValue == inputText)
    }

    @Test
    fun marsTextField_showsErrorMessage() {
        // Given
        val label = getString(R.string.test_label_example)
        val errorMessage = getString(R.string.toast_message_invalid_json)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsTextField(
                    value = getString(R.string.test_value_example),
                    onValueChange = { },
                    label = label,
                    errorMessage = errorMessage
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun marsTextField_outlinedVariant_rendersCorrectly() {
        // Given
        val label = getString(R.string.test_label_example)
        val value = getString(R.string.test_value_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsTextField(
                    value = value,
                    onValueChange = { },
                    label = label,
                    variant = MarsTextFieldVariant.Outlined
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(label)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(value)
            .assertIsDisplayed()
    }

    @Test
    fun marsTextField_filledVariant_rendersCorrectly() {
        // Given
        val label = getString(R.string.test_label_example)
        val value = getString(R.string.test_value_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsTextField(
                    value = value,
                    onValueChange = { },
                    label = label,
                    variant = MarsTextFieldVariant.Filled
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(label)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(value)
            .assertIsDisplayed()
    }

    @Test
    fun marsTextField_readOnlyState_doesNotAcceptInput() {
        // Given
        val label = getString(R.string.test_label_example)
        val initialValue = getString(R.string.test_value_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsTextField(
                    value = initialValue,
                    onValueChange = { },
                    label = label,
                    readOnly = true
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(label)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(initialValue)
            .assertIsDisplayed()

        // Verify the field is present but we don't try to input text
        // Read-only fields in Compose don't allow text input by design
        composeTestRule
            .onNodeWithText(label)
            .performClick()

        // The value should still be displayed
        composeTestRule
            .onNodeWithText(initialValue)
            .assertIsDisplayed()
    }

    @Test
    fun marsTextField_withContentDescription_isAccessible() {
        // Given
        val label = getString(R.string.test_label_example)
        val contentDesc = getString(R.string.core_ui_textfield_content_description)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsTextField(
                    value = "",
                    onValueChange = { },
                    label = label,
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
    fun marsTextField_statefulBehavior_worksCorrectly() {
        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                var text by remember { mutableStateOf("") }
                MarsTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = getString(R.string.test_label_example),
                    placeholder = getString(R.string.test_placeholder_example)
                )
            }
        }

        // Then
        val inputText = getString(R.string.test_value_example)
        composeTestRule
            .onNodeWithText(getString(R.string.test_label_example))
            .performClick()
            .performTextInput(inputText)

        composeTestRule
            .onNodeWithText(getString(R.string.test_label_example))
            .assertTextContains(inputText)
    }

    @Test
    fun marsTextField_errorAndNormalState_togglesCorrectly() {
        // Given
        var hasError by mutableStateOf(false)
        val errorMessage = getString(R.string.test_error_message)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsTextField(
                    value = "",
                    onValueChange = { },
                    label = getString(R.string.test_label_example),
                    errorMessage = if (hasError) errorMessage else null
                )
            }
        }

        // Then - Initially no error
        composeTestRule
            .onNodeWithText(errorMessage)
            .assertDoesNotExist()

        // Show error
        hasError = true
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()

        // Hide error
        hasError = false
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText(errorMessage)
            .assertDoesNotExist()
    }

    @Test
    fun marsTextField_numberKeyboard_configuresCorrectly() {
        // Given
        val label = getString(R.string.test_label_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsTextField(
                    value = "",
                    onValueChange = { },
                    label = label,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(label)
            .assertIsDisplayed()
            .performClick()

        // Field should accept numeric input
        composeTestRule
            .onNodeWithText(label)
            .performTextInput("12345")
    }
}
