package com.mustalk.seat.marsrover.core.domain.parser

import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions

/**
 * Interface for parsing JSON input into the domain-specific RoverMissionInstructions model.
 */
interface JsonParser {
    /**
     * Parses a JSON string representing mission details into RoverMissionInstructions.
     *
     * @param jsonInput The JSON string to parse.
     * @return RoverMissionInstructions object containing the parsed mission details.
     * @throws RoverError.InvalidInputFormat if parsing fails due to malformed JSON or incorrect structure.
     */
    fun parseInput(jsonInput: String): RoverMissionInstructions
}
