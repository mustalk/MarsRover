package com.mustalk.seat.marsrover.core.domain.usecase

import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.domain.service.RoverMovementService
import com.mustalk.seat.marsrover.core.domain.validator.InputValidator
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExecuteRoverMissionUseCaseTest {
    private lateinit var useCase: ExecuteRoverMissionUseCase
    private lateinit var jsonParser: JsonParser
    private lateinit var inputValidator: InputValidator
    private lateinit var roverMovementService: RoverMovementService

    @Before
    fun setUp() {
        // Create mocks for domain dependencies since core:domain is a pure Kotlin module
        jsonParser = mockk()
        inputValidator = mockk()
        roverMovementService = mockk()

        // Create use case with mocked dependencies
        useCase = ExecuteRoverMissionUseCase(jsonParser, inputValidator, roverMovementService)
    }

    @Test
    fun `should return correct final position for provided example`() {
        // Given
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 1, "y": 2 },
                "roverDirection": "N",
                "movements": "LMLMLMLMM"
            }
            """.trimIndent()

        val instructions =
            com.mustalk.seat.marsrover.core.model.RoverMissionInstructions(
                plateauTopRightX = 5,
                plateauTopRightY = 5,
                initialRoverPosition =
                    com.mustalk.seat.marsrover.core.model
                        .Position(1, 2),
                initialRoverDirection = "N",
                movementCommands = "LMLMLMLMM"
            )

        val plateau =
            com.mustalk.seat.marsrover.core.model
                .Plateau(5, 5)
        val direction = com.mustalk.seat.marsrover.core.model.Direction.NORTH
        val position =
            com.mustalk.seat.marsrover.core.model
                .Position(1, 2)

        // Mock the flow through the use case
        every { jsonParser.parseInput(jsonInput) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("N") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } returns Unit
        every { roverMovementService.executeMovements(any(), plateau, "LMLMLMLMM") } answers {
            // Mock the rover movement to end up at (1, 3) facing North
            val rover = firstArg<com.mustalk.seat.marsrover.core.model.Rover>()
            rover.position =
                com.mustalk.seat.marsrover.core.model
                    .Position(1, 3)
            rover.direction = com.mustalk.seat.marsrover.core.model.Direction.NORTH
        }

        // When
        val result = useCase(jsonInput)

        // Then
        assertTrue("Expected success result", result.isSuccess)
        assertEquals("1 3 N", result.getOrNull())
    }

    @Test
    fun `should return error for JSON parsing failure`() {
        // Given
        val invalidJson = "{ invalid json }"
        every { jsonParser.parseInput(invalidJson) } throws RoverError.InvalidInputFormat("Invalid JSON")

        // When
        val result = useCase(invalidJson)

        // Then
        assertTrue("Expected failure result", result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue("Expected RoverError.InvalidInputFormat", error is RoverError.InvalidInputFormat)
    }

    @Test
    fun `should return error for invalid direction character`() {
        // Given
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 1, "y": 2 },
                "roverDirection": "X",
                "movements": "M"
            }
            """.trimIndent()

        val instructions =
            com.mustalk.seat.marsrover.core.model.RoverMissionInstructions(
                plateauTopRightX = 5,
                plateauTopRightY = 5,
                initialRoverPosition =
                    com.mustalk.seat.marsrover.core.model
                        .Position(1, 2),
                initialRoverDirection = "X",
                movementCommands = "M"
            )

        val plateau =
            com.mustalk.seat.marsrover.core.model
                .Plateau(5, 5)

        every { jsonParser.parseInput(jsonInput) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("X") } throws RoverError.InvalidDirectionChar("X")

        // When
        val result = useCase(jsonInput)

        // Then
        assertTrue("Expected failure result", result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue("Expected RoverError.InvalidDirectionChar", error is RoverError.InvalidDirectionChar)
    }

    @Test
    fun `should return error for invalid initial position`() {
        // Given
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 6, "y": 2 },
                "roverDirection": "N",
                "movements": "M"
            }
            """.trimIndent()

        val instructions =
            com.mustalk.seat.marsrover.core.model.RoverMissionInstructions(
                plateauTopRightX = 5,
                plateauTopRightY = 5,
                initialRoverPosition =
                    com.mustalk.seat.marsrover.core.model
                        .Position(6, 2),
                initialRoverDirection = "N",
                movementCommands = "M"
            )

        val plateau =
            com.mustalk.seat.marsrover.core.model
                .Plateau(5, 5)
        val direction = com.mustalk.seat.marsrover.core.model.Direction.NORTH
        val position =
            com.mustalk.seat.marsrover.core.model
                .Position(6, 2)

        every { jsonParser.parseInput(jsonInput) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("N") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } throws
            RoverError.InvalidInitialPosition(6, 2, 5, 5)

        // When
        val result = useCase(jsonInput)

        // Then
        assertTrue("Expected failure result", result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue("Expected RoverError.InvalidInitialPosition", error is RoverError.InvalidInitialPosition)
    }
}
