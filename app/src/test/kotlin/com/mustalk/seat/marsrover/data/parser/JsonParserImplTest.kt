package com.mustalk.seat.marsrover.data.parser

import com.mustalk.seat.marsrover.domain.error.RoverError
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
    fun `should parse valid JSON input correctly`() {
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

        assertEquals(5, result.topRightCorner.x)
        assertEquals(5, result.topRightCorner.y)
        assertEquals(1, result.roverPosition.x)
        assertEquals(2, result.roverPosition.y)
        assertEquals("N", result.roverDirection)
        assertEquals("LMLMLMLMM", result.movements)
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

        assertEquals(3, result.topRightCorner.x)
        assertEquals(4, result.topRightCorner.y)
        assertEquals(0, result.roverPosition.x)
        assertEquals(1, result.roverPosition.y)
        assertEquals("E", result.roverDirection)
        assertEquals("MR", result.movements)
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

        assertEquals("", result.movements)
    }
}
