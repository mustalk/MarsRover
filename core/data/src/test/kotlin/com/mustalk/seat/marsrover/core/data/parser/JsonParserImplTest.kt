package com.mustalk.seat.marsrover.core.data.parser

import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.core.model.Position
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class JsonParserImplTest {
    private lateinit var jsonParser: JsonParserImpl

    @Before
    fun setUp() {
        val json = Json { ignoreUnknownKeys = true }
        jsonParser = JsonParserImpl(json)
    }

    @Test
    fun `should parse valid JSON input correctly to RoverMissionInstructions`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 1, "y": 2 },
                "roverDirection": "N",
                "movements": "LMLMLMLMM"
            }
            """.trimIndent()

        val result = jsonParser.parseInput(jsonInput)

        assertEquals(5, result.plateauTopRightX)
        assertEquals(5, result.plateauTopRightY)
        assertEquals(Position(1, 2), result.initialRoverPosition)
        assertEquals("N", result.initialRoverDirection)
        assertEquals("LMLMLMLMM", result.movementCommands)
    }

    @Test
    fun `should handle JSON with extra unknown fields`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 3, "y": 4 },
                "roverPosition": { "x": 0, "y": 1 },
                "roverDirection": "E",
                "movements": "MR",
                "extraField": "should be ignored"
            }
            """.trimIndent()

        val result = jsonParser.parseInput(jsonInput)

        assertEquals(3, result.plateauTopRightX)
        assertEquals(4, result.plateauTopRightY)
        assertEquals(Position(0, 1), result.initialRoverPosition)
        assertEquals("E", result.initialRoverDirection)
        assertEquals("MR", result.movementCommands)
    }

    @Test(expected = RoverError.InvalidInputFormat::class)
    fun `should throw InvalidInputFormat for malformed JSON`() {
        val jsonInput = "{ invalid json structure"

        jsonParser.parseInput(jsonInput)
    }

    @Test(expected = RoverError.InvalidInputFormat::class)
    fun `should throw InvalidInputFormat for missing required fields`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 1, "y": 2 }
            }
            """.trimIndent()

        jsonParser.parseInput(jsonInput)
    }

    @Test
    fun `should handle empty movements string`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 5, "y": 5 },
                "roverPosition": { "x": 1, "y": 2 },
                "roverDirection": "S",
                "movements": ""
            }
            """.trimIndent()

        val result = jsonParser.parseInput(jsonInput)

        assertEquals("", result.movementCommands)
    }

    @Test
    fun `should map DTO fields to domain model correctly`() {
        val jsonInput =
            """
            {
                "topRightCorner": { "x": 10, "y": 8 },
                "roverPosition": { "x": 3, "y": 4 },
                "roverDirection": "W",
                "movements": "RLMR"
            }
            """.trimIndent()

        val result = jsonParser.parseInput(jsonInput)

        // Verify all DTO -> Domain model mapping
        assertEquals(10, result.plateauTopRightX)
        assertEquals(8, result.plateauTopRightY)
        assertEquals(3, result.initialRoverPosition.x)
        assertEquals(4, result.initialRoverPosition.y)
        assertEquals("W", result.initialRoverDirection)
        assertEquals("RLMR", result.movementCommands)
    }
}
