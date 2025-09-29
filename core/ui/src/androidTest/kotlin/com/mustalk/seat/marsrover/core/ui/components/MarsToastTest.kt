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
class MarsToastTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun getString(id: Int): String = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test
    fun marsToast_displaysMessageCorrectly() {
        // Given
        val message = getString(R.string.core_ui_test_content_example)

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
        val title = getString(R.string.core_ui_test_title_example)
        val message = getString(R.string.core_ui_test_content_example)

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
        val message = getString(R.string.core_ui_toast_message_invalid_json)

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
            .onNodeWithContentDescription(getString(R.string.core_ui_cd_error_icon))
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_warningType_displaysWithWarningIcon() {
        // Given
        val message = getString(R.string.core_ui_test_content_example)

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
            .onNodeWithContentDescription(getString(R.string.core_ui_cd_warning_icon))
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_infoType_displaysWithInfoIcon() {
        // Given
        val message = getString(R.string.core_ui_test_content_example)

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
            .onNodeWithContentDescription(getString(R.string.core_ui_cd_info_icon))
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_withContentDescription_isAccessible() {
        // Given
        val message = getString(R.string.core_ui_test_content_example)
        val contentDesc = getString(R.string.core_ui_cd_info_toast)

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
                    title = getString(R.string.core_ui_test_title_example),
                    message = getString(R.string.core_ui_test_content_example),
                    type = MarsToastType.Error
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(getString(R.string.core_ui_test_title_example))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(getString(R.string.core_ui_test_content_example))
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_allTypes_renderCorrectlyInDarkTheme() {
        // When
        composeTestRule.setContent {
            MarsRoverTheme(darkTheme = true) {
                MarsToast(
                    title = getString(R.string.core_ui_test_title_example),
                    message = getString(R.string.core_ui_test_content_example),
                    type = MarsToastType.Warning
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(getString(R.string.core_ui_test_title_example))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(getString(R.string.core_ui_test_content_example))
            .assertIsDisplayed()
    }

    @Test
    fun marsToast_longMessage_displaysCorrectly() {
        // Given
        val longMessage = getString(R.string.core_ui_test_message_long)

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
        val message = getString(R.string.core_ui_test_content_example)

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
