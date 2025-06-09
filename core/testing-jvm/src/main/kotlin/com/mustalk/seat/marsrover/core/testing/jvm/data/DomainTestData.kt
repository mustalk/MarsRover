package com.mustalk.seat.marsrover.core.testing.jvm.data

import com.mustalk.seat.marsrover.core.model.Direction
import com.mustalk.seat.marsrover.core.model.Plateau
import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.model.Rover
import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions

/**
 * Test data utilities specifically for Mars Rover domain layer testing.
 * Provides predefined test instances for use cases, validators, and services.
 */
object DomainTestData {
    // Common test constants
    object TestConstants {
        // Standard plateau
        val STANDARD_PLATEAU = Plateau(5, 5)
        const val STANDARD_PLATEAU_X = 5
        const val STANDARD_PLATEAU_Y = 5

        // Small plateau for boundary testing
        val SMALL_PLATEAU = Plateau(2, 2)
        val TINY_PLATEAU = Plateau(0, 0) // Single cell plateau

        // Test positions
        val POSITION_1_2 = Position(1, 2)
        val POSITION_0_0 = Position(0, 0)
        val POSITION_1_3 = Position(1, 3)
        val POSITION_0_1 = Position(0, 1)
        val POSITION_6_2 = Position(6, 2)
        val POSITION_2_2 = Position(2, 2)
        val POSITION_2_4 = Position(2, 4)
        val POSITION_2_3 = Position(2, 3)
        val POSITION_3_2 = Position(3, 2)
        val POSITION_2_1 = Position(2, 1)
        val POSITION_1_1 = Position(1, 1)
        val POSITION_5_5 = Position(5, 5)

        // Test commands
        const val STANDARD_MOVEMENTS = "LMLMLMLMM"
        const val DIRECTION_NORTH = "N"
        const val INVALID_COMMANDS = "MXL1R@M"
        const val BOUNDARY_TEST_COMMANDS = "MMMMSMMM"
        const val SINGLE_MOVE = "M"
        const val TURN_LEFT = "L"
        const val TURN_RIGHT = "R"
        const val FOUR_LEFT_TURNS = "LLLL"
        const val TINY_PLATEAU_COMMANDS = "MRLM"
        const val EMPTY_COMMANDS = ""

        // Error messages
        const val INVALID_JSON_MESSAGE = "Invalid JSON"
        const val INVALID_DIRECTION_X = "X"
        const val INVALID_DIRECTION_NE = "NE"

        // Test plateau dimensions for invalid scenarios
        const val LARGE_PLATEAU_X = 100
        const val LARGE_PLATEAU_Y = 50
        const val SMALL_PLATEAU_X = 1
        const val SMALL_PLATEAU_Y = 1
    }

    // Use Case Test Data
    object UseCaseTestData {
        // Successful execution scenarios
        object SuccessfulExecution {
            val STANDARD_MISSION = MarsRoverTestData.JsonParserTestData.ValidInput
            const val EXPECTED_FINAL_POSITION = "1 3 N"

            val COMPLEX_MISSION = MarsRoverTestData.JsonParserTestData.ComplexInput
            const val COMPLEX_EXPECTED_POSITION = "2 4 N"

            object SimpleMove {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 5, "y": 5 },
                        "roverPosition": { "x": 0, "y": 0 },
                        "roverDirection": "N",
                        "movements": "M"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 5,
                        plateauTopRightY = 5,
                        initialRoverPosition = Position(0, 0),
                        initialRoverDirection = "N",
                        movementCommands = "M"
                    )
                const val EXPECTED_POSITION = "0 1 N"
            }

            object BoundaryMovement {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 2, "y": 2 },
                        "roverPosition": { "x": 1, "y": 1 },
                        "roverDirection": "E",
                        "movements": "M"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 2,
                        plateauTopRightY = 2,
                        initialRoverPosition = Position(1, 1),
                        initialRoverDirection = "E",
                        movementCommands = "M"
                    )
                const val EXPECTED_POSITION = "2 1 E"
            }

            object BoundaryBlocked {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 2, "y": 2 },
                        "roverPosition": { "x": 2, "y": 2 },
                        "roverDirection": "N",
                        "movements": "M"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 2,
                        plateauTopRightY = 2,
                        initialRoverPosition = Position(2, 2),
                        initialRoverDirection = "N",
                        movementCommands = "M"
                    )
                const val EXPECTED_POSITION = "2 2 N"
            }

            object MultipleBoundaryAttempts {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 1, "y": 1 },
                        "roverPosition": { "x": 0, "y": 0 },
                        "roverDirection": "W",
                        "movements": "MMMMSMMM"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 1,
                        plateauTopRightY = 1,
                        initialRoverPosition = Position(0, 0),
                        initialRoverDirection = "W",
                        movementCommands = "MMMMSMMM"
                    )
                const val EXPECTED_POSITION = "0 0 W"
            }

            object InvalidCharacterIgnore {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 5, "y": 5 },
                        "roverPosition": { "x": 1, "y": 1 },
                        "roverDirection": "N",
                        "movements": "MXL1R@M"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 5,
                        plateauTopRightY = 5,
                        initialRoverPosition = Position(1, 1),
                        initialRoverDirection = "N",
                        movementCommands = "MXL1R@M"
                    )
                const val EXPECTED_POSITION = "1 3 N"
            }

            object EmptyMovements {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 5, "y": 5 },
                        "roverPosition": { "x": 2, "y": 3 },
                        "roverDirection": "E",
                        "movements": ""
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 5,
                        plateauTopRightY = 5,
                        initialRoverPosition = Position(2, 3),
                        initialRoverDirection = "E",
                        movementCommands = ""
                    )
                const val EXPECTED_POSITION = "2 3 E"
            }

            object OnlyRotations {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 5, "y": 5 },
                        "roverPosition": { "x": 2, "y": 3 },
                        "roverDirection": "N",
                        "movements": "LLLL"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 5,
                        plateauTopRightY = 5,
                        initialRoverPosition = Position(2, 3),
                        initialRoverDirection = "N",
                        movementCommands = "LLLL"
                    )
                const val EXPECTED_POSITION = "2 3 N"
            }

            object LowercaseDirection {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 5, "y": 5 },
                        "roverPosition": { "x": 1, "y": 2 },
                        "roverDirection": "n",
                        "movements": "M"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 5,
                        plateauTopRightY = 5,
                        initialRoverPosition = Position(1, 2),
                        initialRoverDirection = "n",
                        movementCommands = "M"
                    )
                const val EXPECTED_POSITION = "1 3 N"
            }

            object SingleCellPlateau {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 0, "y": 0 },
                        "roverPosition": { "x": 0, "y": 0 },
                        "roverDirection": "N",
                        "movements": "MRLM"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 0,
                        plateauTopRightY = 0,
                        initialRoverPosition = Position(0, 0),
                        initialRoverDirection = "N",
                        movementCommands = "MRLM"
                    )
                const val EXPECTED_POSITION = "0 0 N"
            }
        }

        // Error scenarios for use case testing
        object ErrorScenarios {
            val INVALID_JSON = MarsRoverTestData.JsonParserTestData.InvalidInputs.MALFORMED_JSON

            object InvalidDirection {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 5, "y": 5 },
                        "roverPosition": { "x": 1, "y": 2 },
                        "roverDirection": "X",
                        "movements": "M"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 5,
                        plateauTopRightY = 5,
                        initialRoverPosition = Position(1, 2),
                        initialRoverDirection = "X",
                        movementCommands = "M"
                    )
            }

            object OutOfBoundsPosition {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 5, "y": 5 },
                        "roverPosition": { "x": 6, "y": 2 },
                        "roverDirection": "N",
                        "movements": "M"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 5,
                        plateauTopRightY = 5,
                        initialRoverPosition = Position(6, 2),
                        initialRoverDirection = "N",
                        movementCommands = "M"
                    )
            }

            object MissingRequiredFields {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 5, "y": 5 },
                        "roverPosition": { "x": 1, "y": 2 }
                    }
                """
            }

            object NegativePlateauDimensions {
                const val JSON = """
                    {
                        "topRightCorner": { "x": -1, "y": 5 },
                        "roverPosition": { "x": 1, "y": 2 },
                        "roverDirection": "N",
                        "movements": "M"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = -1,
                        plateauTopRightY = 5,
                        initialRoverPosition = Position(1, 2),
                        initialRoverDirection = "N",
                        movementCommands = "M"
                    )
            }

            object MultiCharacterDirection {
                const val JSON = """
                    {
                        "topRightCorner": { "x": 5, "y": 5 },
                        "roverPosition": { "x": 1, "y": 2 },
                        "roverDirection": "NE",
                        "movements": "M"
                    }
                """
                val INSTRUCTIONS =
                    RoverMissionInstructions(
                        plateauTopRightX = 5,
                        plateauTopRightY = 5,
                        initialRoverPosition = Position(1, 2),
                        initialRoverDirection = "NE",
                        movementCommands = "M"
                    )
            }
        }
    }

    // Validator Test Data
    object ValidatorTestData {
        // Valid plateau creation scenarios
        object ValidPlateaus {
            val STANDARD_PLATEAU =
                RoverMissionInstructions(
                    plateauTopRightX = TestConstants.STANDARD_PLATEAU_X,
                    plateauTopRightY = TestConstants.STANDARD_PLATEAU_Y,
                    initialRoverPosition = TestConstants.POSITION_1_2,
                    initialRoverDirection = "N",
                    movementCommands = "LM"
                )

            val LARGE_PLATEAU =
                RoverMissionInstructions(
                    plateauTopRightX = TestConstants.LARGE_PLATEAU_X,
                    plateauTopRightY = TestConstants.LARGE_PLATEAU_Y,
                    initialRoverPosition = Position(10, 20),
                    initialRoverDirection = "E",
                    movementCommands = "MRLM"
                )

            val SMALL_PLATEAU =
                RoverMissionInstructions(
                    plateauTopRightX = TestConstants.SMALL_PLATEAU_X,
                    plateauTopRightY = TestConstants.SMALL_PLATEAU_Y,
                    initialRoverPosition = Position(0, 0),
                    initialRoverDirection = "S",
                    movementCommands = "L"
                )
        }

        // Invalid plateau scenarios
        object InvalidPlateaus {
            val NEGATIVE_X =
                RoverMissionInstructions(
                    plateauTopRightX = -1,
                    plateauTopRightY = 5,
                    initialRoverPosition = Position(1, 2),
                    initialRoverDirection = "N",
                    movementCommands = "LM"
                )

            val NEGATIVE_Y =
                RoverMissionInstructions(
                    plateauTopRightX = 5,
                    plateauTopRightY = -1,
                    initialRoverPosition = Position(1, 2),
                    initialRoverDirection = "N",
                    movementCommands = "LM"
                )

            val ZERO_DIMENSIONS =
                RoverMissionInstructions(
                    plateauTopRightX = 0,
                    plateauTopRightY = 0,
                    initialRoverPosition = Position(0, 0),
                    initialRoverDirection = "N",
                    movementCommands = "M"
                )
        }

        // Direction validation scenarios
        object DirectionValidation {
            val VALID_DIRECTIONS =
                mapOf(
                    "N" to Direction.NORTH,
                    "E" to Direction.EAST,
                    "S" to Direction.SOUTH,
                    "W" to Direction.WEST,
                    "n" to Direction.NORTH,
                    "e" to Direction.EAST,
                    "s" to Direction.SOUTH,
                    "w" to Direction.WEST
                )

            val INVALID_DIRECTIONS = listOf("X", "NE", "", "NORTH", "1", "n e")
        }

        // Position validation scenarios
        object PositionValidation {
            val STANDARD_PLATEAU = TestConstants.STANDARD_PLATEAU

            val VALID_POSITIONS =
                listOf(
                    Position(0, 0),
                    Position(5, 5),
                    Position(2, 3),
                    Position(0, 5),
                    Position(5, 0)
                )

            val INVALID_POSITIONS =
                listOf(
                    Position(6, 3),
                    Position(3, 6),
                    Position(-1, 3),
                    Position(3, -1),
                    Position(-1, -1),
                    Position(6, 6)
                )
        }
    }

    // Movement Service Test Data
    object MovementServiceTestData {
        // Basic movement scenarios
        object BasicMovements {
            object MoveNorth {
                val INITIAL_ROVER = Rover(Position(1, 1), Direction.NORTH)
                const val COMMAND = "M"
                val EXPECTED_POSITION = Position(1, 2)
                val EXPECTED_DIRECTION = Direction.NORTH
            }
        }

        // Rotation scenarios
        object Rotations {
            object TurnLeft {
                val INITIAL_ROVER = Rover(Position(1, 1), Direction.NORTH)
                const val COMMAND = "L"
                val EXPECTED_POSITION = Position(1, 1)
                val EXPECTED_DIRECTION = Direction.WEST
            }

            object TurnRight {
                val INITIAL_ROVER = Rover(Position(1, 1), Direction.NORTH)
                const val COMMAND = "R"
                val EXPECTED_POSITION = Position(1, 1)
                val EXPECTED_DIRECTION = Direction.EAST
            }
        }

        // Complex movement scenarios
        object ComplexMovements {
            object StandardMission {
                val INITIAL_ROVER = Rover(TestConstants.POSITION_1_2, Direction.NORTH)
                val PLATEAU = TestConstants.STANDARD_PLATEAU
                const val COMMANDS = "LMLMLMLMM"
                val EXPECTED_POSITION = TestConstants.POSITION_1_3
                val EXPECTED_DIRECTION = Direction.NORTH
            }
        }

        // Invalid command handling
        object InvalidCommands {
            val INITIAL_ROVER = Rover(TestConstants.POSITION_2_2, Direction.NORTH)
            val PLATEAU = TestConstants.STANDARD_PLATEAU
            const val COMMAND = "MXL1R@M" // Mixed valid and invalid commands
            val EXPECTED_POSITION = TestConstants.POSITION_2_4 // After executing valid commands M, L, R, M
            val EXPECTED_DIRECTION = Direction.NORTH
        }

        // Empty command handling
        object EmptyCommands {
            val INITIAL_ROVER = Rover(TestConstants.POSITION_2_2, Direction.EAST)
            val PLATEAU = TestConstants.STANDARD_PLATEAU
            const val COMMAND = ""
            val EXPECTED_POSITION = TestConstants.POSITION_2_2 // Should not move
            val EXPECTED_DIRECTION = Direction.EAST
        }

        // Boundary scenarios
        object BoundaryTests {
            val PLATEAU = Plateau(3, 3)

            object HitNorthBoundary {
                val INITIAL_ROVER = Rover(Position(1, 3), Direction.NORTH)
                const val COMMAND = "M"
                val EXPECTED_POSITION = Position(1, 3) // Should not move
                val EXPECTED_DIRECTION = Direction.NORTH
            }
        }
    }
}
