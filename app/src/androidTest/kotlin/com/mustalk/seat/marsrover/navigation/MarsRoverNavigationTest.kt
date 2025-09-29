package com.mustalk.seat.marsrover.presentation.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustalk.seat.marsrover.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for Mars Rover navigation flow.
 * Tests navigation between Dashboard and NewMission screens including fade animations.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MarsRoverNavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun navigation_startsOnDashboardScreen() {
        // Verify Dashboard screen is displayed initially
        composeTestRule
            .onNodeWithContentDescription("Welcome screen - tap to start new mission")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_dashboardToNewMission_withFab() {
        // Click on FAB to navigate to New Mission screen
        composeTestRule
            .onNodeWithContentDescription("Start new mission")
            .performClick()

        // Wait for navigation
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Verify New Mission screen is displayed
        composeTestRule
            .onNodeWithText("New Mission")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_dashboardToNewMission_withEmptyStateCard() {
        // Click on empty state card to navigate to New Mission screen
        composeTestRule
            .onNodeWithContentDescription("Welcome screen - tap to start new mission")
            .performClick()

        // Wait for navigation
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Verify New Mission screen is displayed
        composeTestRule
            .onNodeWithText("New Mission")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_newMissionToDashboard_withBackButton() {
        // Navigate to New Mission screen
        composeTestRule
            .onNodeWithContentDescription("Start new mission")
            .performClick()

        // Wait for navigation
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Click back button
        composeTestRule
            .onNodeWithContentDescription("Navigate back")
            .performClick()

        // Wait for navigation back
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Verify Dashboard screen is displayed using content description
        composeTestRule
            .onNodeWithContentDescription("Welcome screen - tap to start new mission")
            .assertIsDisplayed()
    }

    @Test
    @Ignore("Flaky in CI - works locally but fails in GitHub Actions emulator, needs investigation")
    fun navigation_newMissionToDashboard_withCancelButton() {
        // Navigate to New Mission screen
        composeTestRule
            .onNodeWithContentDescription("Start new mission")
            .performClick()

        // Allow navigation animation to complete
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Click cancel button
        composeTestRule
            .onNodeWithContentDescription("Cancel and return to previous screen")
            .performClick()

        // Allow navigation back animation to complete
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Verify Dashboard screen is displayed using content description
        composeTestRule
            .onNodeWithContentDescription("Welcome screen - tap to start new mission")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_inputModeToggle_worksCorrectly() {
        // Navigate to New Mission screen
        composeTestRule
            .onNodeWithContentDescription("Start new mission")
            .performClick()

        // Wait for navigation
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Verify JSON mode is selected by default
        composeTestRule
            .onNodeWithText("Mission JSON Configuration")
            .assertIsDisplayed()

        // Switch to Builder mode
        composeTestRule
            .onNodeWithText("Builder")
            .performClick()

        // Wait for mode change
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()

        // Verify Builder mode is displayed
        composeTestRule
            .onNodeWithText("Plateau Size")
            .assertIsDisplayed()

        // Switch back to JSON mode
        composeTestRule
            .onNodeWithText("JSON")
            .performClick()

        // Wait for mode change
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()

        // Verify JSON mode is displayed again
        composeTestRule
            .onNodeWithText("Mission JSON Configuration")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_preservesInputMode_onScreenNavigation() {
        // Navigate to New Mission screen
        composeTestRule
            .onNodeWithContentDescription("Start new mission")
            .performClick()

        // Wait for navigation
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Switch to Builder mode
        composeTestRule
            .onNodeWithText("Builder")
            .performClick()

        // Wait for mode change
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()

        // Verify Builder mode is displayed
        composeTestRule
            .onNodeWithText("Plateau Size")
            .assertIsDisplayed()

        // Navigate back to Dashboard
        composeTestRule
            .onNodeWithContentDescription("Navigate back")
            .performClick()

        // Wait for navigation back
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Navigate to New Mission screen again
        composeTestRule
            .onNodeWithContentDescription("Start new mission")
            .performClick()

        // Wait for navigation
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Verify JSON mode is reset (default behavior)
        composeTestRule
            .onNodeWithText("Mission JSON Configuration")
            .assertIsDisplayed()
    }
}
