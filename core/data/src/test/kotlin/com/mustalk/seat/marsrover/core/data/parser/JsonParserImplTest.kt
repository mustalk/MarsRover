package com.mustalk.seat.marsrover.core.data.parser

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.core.testing.jvm.data.MarsRoverTestData
import kotlinx.serialization.json.Json
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
        val testData = MarsRoverTestData.JsonParserTestData.ValidInput
        val result = jsonParser.parseInput(testData.JSON)

        assertThat(result).isEqualTo(testData.EXPECTED_RESULT)
    }

    @Test
    fun `should handle JSON with extra unknown fields`() {
        val testData = MarsRoverTestData.JsonParserTestData.ExtraFields
        val result = jsonParser.parseInput(testData.JSON)

        assertThat(result).isEqualTo(testData.EXPECTED_RESULT)
    }

    @Test(expected = RoverError.InvalidInputFormat::class)
    fun `should throw InvalidInputFormat for malformed JSON`() {
        jsonParser.parseInput(MarsRoverTestData.JsonParserTestData.InvalidInputs.MALFORMED_JSON)
    }

    @Test(expected = RoverError.InvalidInputFormat::class)
    fun `should throw InvalidInputFormat for missing required fields`() {
        jsonParser.parseInput(MarsRoverTestData.JsonParserTestData.InvalidInputs.MISSING_FIELDS_JSON)
    }

    @Test
    fun `should handle empty movements string`() {
        val testData = MarsRoverTestData.JsonParserTestData.EmptyMovements
        val result = jsonParser.parseInput(testData.JSON)

        assertThat(result).isEqualTo(testData.EXPECTED_RESULT)
    }

    @Test
    fun `should map DTO fields to domain model correctly`() {
        val testData = MarsRoverTestData.JsonParserTestData.ComplexInput
        val result = jsonParser.parseInput(testData.JSON)

        // Verify all DTO -> Domain model mapping with structured test data
        assertThat(result).isEqualTo(testData.EXPECTED_RESULT)
    }
}
