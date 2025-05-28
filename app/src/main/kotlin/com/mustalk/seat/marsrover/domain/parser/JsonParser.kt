package com.mustalk.seat.marsrover.domain.parser

import com.mustalk.seat.marsrover.data.model.input.MarsRoverInput

/**
 * Interface for parsing JSON input into domain objects.
 */
interface JsonParser {
    /**
     * Parses JSON string into MarsRoverInput.
     *
     * @param jsonInput The JSON string to parse.
     * @return Parsed MarsRoverInput object.
     * @throws Exception if parsing fails.
     */
    fun parseInput(jsonInput: String): MarsRoverInput
}
