package com.mustalk.seat.marsrover.domain.usecase

import com.mustalk.seat.marsrover.data.parser.JsonParserImpl
import com.mustalk.seat.marsrover.domain.error.RoverError
import com.mustalk.seat.marsrover.domain.service.RoverMovementServiceImpl
import com.mustalk.seat.marsrover.domain.validator.InputValidatorImpl
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExecuteRoverMissionUseCaseTest {
    private lateinit var useCase: ExecuteRoverMissionUseCase

    @Before
    fun setUp() {
        // In tests, we manually create dependencies instead of using Hilt for simplicity.
        // This gives us full control over the instances and avoids the complexity of
        // setting up Hilt testing infrastructure for unit tests.

        // Create Json instance with same configuration as AppModule
        val json = Json { ignoreUnknownKeys = true }

        // Create concrete implementations manually
        val jsonParser = JsonParserImpl(json)
        val inputValidator = InputValidatorImpl()
        val roverMovementService = RoverMovementServiceImpl()

        // Manually inject dependencies into UseCase constructor
        // In production, Hilt would do this automatically via @Inject constructor
        useCase = ExecuteRoverMissionUseCase(jsonParser, inputValidator, roverMovementService)
    }

    @Test
    fun `should return correct final position for provided example`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 1, "y": 2 },
                "roverDirection": "N",
                "movements": "LMLMLMLMM"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected success result", result.isSuccess)
        assertEquals("1 3 N", result.getOrNull())
    }

    @Test
    fun `should handle rover at origin moving north`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 0, "y": 0 },
                "roverDirection": "N",
                "movements": "M"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected success result", result.isSuccess)
        assertEquals("0 1 N", result.getOrNull())
    }

    @Test
    fun `should handle rover moving to plateau boundary`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 2, "y": 2 },
                "roverPosition": { "x": 1, "y": 1 },
                "roverDirection": "E",
                "movements": "M"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected success result", result.isSuccess)
        assertEquals("2 1 E", result.getOrNull())
    }

    @Test
    fun `should not move when trying to go beyond plateau boundary`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 2, "y": 2 },
                "roverPosition": { "x": 2, "y": 2 },
                "roverDirection": "N",
                "movements": "M"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected success result", result.isSuccess)
        assertEquals("2 2 N", result.getOrNull())
    }

    @Test
    fun `should handle multiple boundary crossing attempts`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 1, "y": 1 },
                "roverPosition": { "x": 0, "y": 0 },
                "roverDirection": "W",
                "movements": "MMMMSMMM"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected success result", result.isSuccess)
        // Starting at (0,0) facing W:
        // M - can't move west (out of bounds), stay at (0,0) facing W
        // M - can't move west (out of bounds), stay at (0,0) facing W
        // M - can't move west (out of bounds), stay at (0,0) facing W
        // M - can't move west (out of bounds), stay at (0,0) facing W
        // S - invalid command, ignore (stays facing W)
        // M - can't move west (out of bounds), stay at (0,0) facing W
        // M - can't move west (out of bounds), stay at (0,0) facing W
        // M - can't move west (out of bounds), stay at (0,0) facing W
        assertEquals("0 0 W", result.getOrNull())
    }

    @Test
    fun `should ignore invalid movement characters`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 1, "y": 1 },
                "roverDirection": "N",
                "movements": "MXL1R@M"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected success result", result.isSuccess)
        // Starting at (1,1) facing N:
        // M - move north to (1,2) facing N
        // X - invalid, ignore
        // L - turn left to face W
        // 1 - invalid, ignore
        // R - turn right to face N
        // @ - invalid, ignore
        // M - move north to (1,3) facing N
        assertEquals("1 3 N", result.getOrNull())
    }

    @Test
    fun `should handle empty movement string`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 2, "y": 3 },
                "roverDirection": "E",
                "movements": ""
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected success result", result.isSuccess)
        assertEquals("2 3 E", result.getOrNull())
    }

    @Test
    fun `should handle only turning movements`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 2, "y": 3 },
                "roverDirection": "N",
                "movements": "LLLL"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected success result", result.isSuccess)
        assertEquals("2 3 N", result.getOrNull())
    }

    @Test
    fun `should return error for malformed JSON`() {
        val jsonInput = "{ invalid json }"

        val result = useCase(jsonInput)

        assertTrue("Expected failure result", result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue("Expected RoverError.InvalidInputFormat", error is RoverError.InvalidInputFormat)
    }

    @Test
    fun `should return error for missing required fields`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 1, "y": 2 }
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected failure result", result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue("Expected RoverError.InvalidInputFormat", error is RoverError.InvalidInputFormat)
    }

    @Test
    fun `should return error for negative plateau dimensions`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": -1, "y": 5 },
                "roverPosition": { "x": 1, "y": 2 },
                "roverDirection": "N",
                "movements": "M"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected failure result", result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue("Expected RoverError.InvalidPlateauDimensions", error is RoverError.InvalidPlateauDimensions)
    }

    @Test
    fun `should return error for invalid direction character`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 1, "y": 2 },
                "roverDirection": "X",
                "movements": "M"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected failure result", result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue("Expected RoverError.InvalidDirectionChar", error is RoverError.InvalidDirectionChar)
    }

    @Test
    fun `should return error for multi-character direction string`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 1, "y": 2 },
                "roverDirection": "NE",
                "movements": "M"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected success result", result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue("Expected RoverError.InvalidDirectionChar", error is RoverError.InvalidDirectionChar)
    }

    @Test
    fun `should return error for rover initial position outside plateau bounds`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 6, "y": 2 },
                "roverDirection": "N",
                "movements": "M"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected failure result", result.isFailure)
        val error = result.exceptionOrNull()
        assertTrue("Expected RoverError.InvalidInitialPosition", error is RoverError.InvalidInitialPosition)
    }

    @Test
    fun `should handle lowercase direction character`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 1, "y": 2 },
                "roverDirection": "n",
                "movements": "M"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected success result", result.isSuccess)
        assertEquals("1 3 N", result.getOrNull())
    }

    @Test
    fun `should handle single cell plateau`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 0, "y": 0 },
                "roverPosition": { "x": 0, "y": 0 },
                "roverDirection": "N",
                "movements": "MRLM"
            }
            """.trimIndent()

        val result = useCase(jsonInput)

        assertTrue("Expected success result", result.isSuccess)
        // Starting at (0,0) facing N on a 1x1 plateau:
        // M - can't move north (out of bounds), stay at (0,0) facing N
        // R - turn right to face E
        // L - turn left to face N
        // M - can't move north (out of bounds), stay at (0,0) facing N
        assertEquals("0 0 N", result.getOrNull())
    }
}
