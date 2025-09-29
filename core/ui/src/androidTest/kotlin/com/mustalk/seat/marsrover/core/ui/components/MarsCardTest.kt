package com.mustalk.seat.marsrover.core.ui.components

import androidx.compose.material3.Text
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
class MarsCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun getString(id: Int): String = InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test
    fun marsCard_displaysTitleCorrectly() {
        // Given
        val title = getString(R.string.core_ui_test_title_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsCard(
                    title = title
                ) {
                    Text(getString(R.string.core_ui_test_content_example))
                }
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()
    }

    @Test
    fun marsCard_displaysContentCorrectly() {
        // Given
        val content = getString(R.string.core_ui_test_content_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsCard {
                    Text(content)
                }
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(content)
            .assertIsDisplayed()
    }

    @Test
    fun marsCard_withTitleAndContent_displaysBoth() {
        // Given
        val title = getString(R.string.core_ui_test_title_example)
        val content = getString(R.string.core_ui_test_content_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsCard(
                    title = title
                ) {
                    Text(content)
                }
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(content)
            .assertIsDisplayed()
    }

    @Test
    fun marsCard_withContentDescription_isAccessible() {
        // Given
        val title = getString(R.string.core_ui_test_title_example)
        val contentDesc = getString(R.string.core_ui_card_content_description)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsCard(
                    title = title,
                    contentDescription = contentDesc
                ) {
                    Text(getString(R.string.core_ui_test_content_example))
                }
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription(contentDesc)
            .assertIsDisplayed()
    }

    @Test
    fun marsPrimaryCard_displaysCorrectly() {
        // Given
        val title = getString(R.string.core_ui_test_title_example)
        val content = getString(R.string.core_ui_test_content_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsPrimaryCard(
                    title = title
                ) {
                    Text(content)
                }
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(content)
            .assertIsDisplayed()
    }

    @Test
    fun marsSecondaryCard_displaysCorrectly() {
        // Given
        val title = getString(R.string.core_ui_test_title_example)
        val content = getString(R.string.core_ui_test_content_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsSecondaryCard(
                    title = title
                ) {
                    Text(content)
                }
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(content)
            .assertIsDisplayed()
    }

    @Test
    fun marsCard_withoutTitle_displaysOnlyContent() {
        // Given
        val content = getString(R.string.core_ui_test_content_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsCard {
                    Text(content)
                }
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(content)
            .assertIsDisplayed()
    }

    @Test
    fun marsCard_rendersInLightTheme() {
        // Given
        val title = getString(R.string.core_ui_test_title_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme(darkTheme = false) {
                MarsCard(
                    title = title
                ) {
                    Text(getString(R.string.core_ui_test_content_example))
                }
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()
    }

    @Test
    fun marsCard_rendersInDarkTheme() {
        // Given
        val title = getString(R.string.core_ui_test_title_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme(darkTheme = true) {
                MarsCard(
                    title = title
                ) {
                    Text(getString(R.string.core_ui_test_content_example))
                }
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()
    }

    @Test
    fun marsCard_multipleTextElements_displaysAll() {
        // Given
        val title = getString(R.string.core_ui_test_title_example)

        // When
        composeTestRule.setContent {
            MarsRoverTheme {
                MarsCard(
                    title = title
                ) {
                    Text(getString(R.string.core_ui_action_start))
                    Text(getString(R.string.core_ui_action_execute))
                    Text(getString(R.string.core_ui_action_cancel))
                }
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(getString(R.string.core_ui_action_start))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(getString(R.string.core_ui_action_execute))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(getString(R.string.core_ui_action_cancel))
            .assertIsDisplayed()
    }
}
