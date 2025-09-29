package com.mustalk.seat.marsrover.core.testing.jvm.data

/**
 * Test data utilities specifically for Dashboard feature testing.
 * Provides predefined test instances for mission results, UI states, and error scenarios.
 */
object DashboardTestData {
    // Successful Mission Results
    const val STANDARD_JSON_INPUT =
        """{"topRightCorner": {"x": 5, "y": 5}, "roverPosition": {"x": 1, "y": 2}, "roverDirection": "N", "movements": "LMLMLMLMM"}"""

    object SuccessfulMissions {
        // Standard successful mission with input
        object StandardSuccess {
            const val FINAL_POSITION = "1 3 N"
            const val IS_SUCCESS = true
            const val TIMESTAMP = 1748649600000L
            const val ORIGINAL_INPUT = STANDARD_JSON_INPUT
        }

        // Complex successful mission
        object ComplexSuccess {
            const val FINAL_POSITION = "3 2 S"
            const val IS_SUCCESS = true
            const val TIMESTAMP = 1748649600000L
            const val ORIGINAL_INPUT = """{"movements": "LRM"}"""
        }

        // Simple successful mission without input
        object SimpleSuccess {
            const val FINAL_POSITION = "2 2 E"
            const val IS_SUCCESS = true
            const val TIMESTAMP = 1748649600000L // 2025-06-01 00:00:00
            const val ORIGINAL_INPUT = ""
        }
    }

    // Failed Mission Results
    object FailedMissions {
        // Standard failure
        object StandardFailure {
            const val FINAL_POSITION = "Error: Invalid JSON"
            const val IS_SUCCESS = false
            const val TIMESTAMP = 1748649600000L
            const val ORIGINAL_INPUT = """{"invalid": "json"}"""
        }

        // Input validation failure
        object ValidationFailure {
            const val FINAL_POSITION = "Error: Invalid input"
            const val IS_SUCCESS = false
            const val TIMESTAMP = 1748649600000L
            const val ORIGINAL_INPUT = ""
        }
    }

    // Long Input for Testing Truncation
    object LongInputTest {
        const val LONG_INPUT = STANDARD_JSON_INPUT
        const val FINAL_POSITION = "1 3 N"
        const val IS_SUCCESS = true
        private const val TRUNCATION_LENGTH = 50
        val EXPECTED_TRUNCATED = LONG_INPUT.take(TRUNCATION_LENGTH) + "..."
    }

    // Test Error Messages
    object ErrorMessages {
        const val CONNECTION_FAILED = "Connection failed"
        const val MISSION_FAILED = "Mission failed"
        const val PROCESSING_FAILED = "Failed to process mission data"
        const val VALIDATION_FAILED = "Validation failed"
        const val PREVIOUS_ERROR = "Previous error"
    }
}
