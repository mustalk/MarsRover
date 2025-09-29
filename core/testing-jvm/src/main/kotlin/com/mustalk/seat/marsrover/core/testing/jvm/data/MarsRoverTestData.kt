package com.mustalk.seat.marsrover.core.testing.jvm.data

import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions

/**
 * Test data utilities specifically for Mars Rover data layer testing.
 * Provides predefined test instances for JSON parsing, repository operations, and network calls.
 */
object MarsRoverTestData {
    // Standard Test Mission Instructions for shared use
    val TEST_MISSION_INSTRUCTIONS =
        RoverMissionInstructions(
            plateauTopRightX = 5,
            plateauTopRightY = 5,
            initialRoverPosition = Position(x = 1, y = 2),
            initialRoverDirection = "N",
            movementCommands = "LMLMLMLMM"
        )

    val COMPLEX_MISSION_INSTRUCTIONS =
        RoverMissionInstructions(
            plateauTopRightX = 7,
            plateauTopRightY = 9,
            initialRoverPosition = Position(3, 4),
            initialRoverDirection = "S",
            movementCommands = "LMLMLMLMRMRMR"
        )

    // Structured Test Data for JSON Parser Tests
    object JsonParserTestData {
        // Valid JSON Input Test Case
        object ValidInput {
            const val JSON = """
                {
                    "topRightCorner": { "x": 5, "y": 5 },
                    "roverPosition": { "x": 1, "y": 2 },
                    "roverDirection": "N",
                    "movements": "LMLMLMLMM"
                }
            """

            val EXPECTED_RESULT =
                RoverMissionInstructions(
                    plateauTopRightX = 5,
                    plateauTopRightY = 5,
                    initialRoverPosition = Position(1, 2),
                    initialRoverDirection = "N",
                    movementCommands = "LMLMLMLMM"
                )
        }

        // JSON with Extra Fields Test Case
        object ExtraFields {
            const val JSON = """
                {
                    "topRightCorner": { "x": 3, "y": 4 },
                    "roverPosition": { "x": 0, "y": 1 },
                    "roverDirection": "E",
                    "movements": "MR",
                    "extraField": "should be ignored"
                }
            """

            val EXPECTED_RESULT =
                RoverMissionInstructions(
                    plateauTopRightX = 3,
                    plateauTopRightY = 4,
                    initialRoverPosition = Position(0, 1),
                    initialRoverDirection = "E",
                    movementCommands = "MR"
                )
        }

        // Complex JSON Input Test Case
        object ComplexInput {
            const val JSON = """
                {
                    "topRightCorner": { "x": 10, "y": 8 },
                    "roverPosition": { "x": 3, "y": 4 },
                    "roverDirection": "W",
                    "movements": "RLMR"
                }
            """

            val EXPECTED_RESULT =
                RoverMissionInstructions(
                    plateauTopRightX = 10,
                    plateauTopRightY = 8,
                    initialRoverPosition = Position(3, 4),
                    initialRoverDirection = "W",
                    movementCommands = "RLMR"
                )
        }

        // Empty Movements Test Case
        object EmptyMovements {
            const val JSON = """
                {
                    "topRightCorner": { "x": 5, "y": 5 },
                    "roverPosition": { "x": 1, "y": 2 },
                    "roverDirection": "S",
                    "movements": ""
                }
            """

            val EXPECTED_RESULT =
                RoverMissionInstructions(
                    plateauTopRightX = 5,
                    plateauTopRightY = 5,
                    initialRoverPosition = Position(1, 2),
                    initialRoverDirection = "S",
                    movementCommands = ""
                )
        }

        // Invalid JSON Test Cases
        object InvalidInputs {
            const val MALFORMED_JSON = "{ invalid json structure"

            const val MISSING_FIELDS_JSON = """
                {
                    "topRightCorner": { "x": 5, "y": 5 },
                    "roverPosition": { "x": 1, "y": 2 }
                }
            """
        }
    }

    // Repository Test Data
    object RepositoryTestData {
        // Standard Mission Test Case
        object StandardMission {
            const val TOP_RIGHT_X: Int = 5
            const val TOP_RIGHT_Y: Int = 5
            const val ROVER_START_X: Int = 1
            const val ROVER_START_Y: Int = 2
            const val ROVER_START_DIRECTION: String = "N"
            const val ROVER_MOVEMENTS: String = "LMLMLMLMM"

            val INPUT = TEST_MISSION_INSTRUCTIONS
            const val FINAL_POSITION = "1 3 N"
            const val SUCCESS_MESSAGE = "Mission completed successfully"
            const val EXECUTION_TIME_MS = 1000L
        }

        // Complex Mission Test Case
        object ComplexMission {
            const val TOP_RIGHT_X: Int = 7
            const val TOP_RIGHT_Y: Int = 9
            const val ROVER_START_X: Int = 3
            const val ROVER_START_Y: Int = 4
            const val ROVER_START_DIRECTION: String = "S"
            const val ROVER_MOVEMENTS: String = "LMLMLMLMRMRMR"

            val INPUT = COMPLEX_MISSION_INSTRUCTIONS
            const val FINAL_POSITION = "2 3 E"
            const val SUCCESS_MESSAGE = "Complex mission completed"
            const val EXECUTION_TIME_MS = 2500L
        }

        // Error Scenarios
        object ErrorCases {
            const val FAILURE_MESSAGE = "Mission execution failed"
            const val VALIDATION_ERROR_CODE = "VALIDATION_ERROR"
            const val INVALID_POSITION_MESSAGE = "Invalid rover position"
            const val OUT_OF_BOUNDS_DETAILS = "Rover cannot start outside plateau bounds"
            const val EXECUTION_TIME_MS = 800L
        }

        // HTTP Error Cases
        object HttpErrors {
            const val BAD_REQUEST_CODE = 400
            const val INTERNAL_SERVER_ERROR_CODE = 500
        }

        // Network Error Messages
        object NetworkErrors {
            const val CONNECTION_REFUSED = "Connection refused"
            const val TIMEOUT = "Timeout"
            const val UNEXPECTED_ERROR = "Unexpected error"
        }
    }
}
