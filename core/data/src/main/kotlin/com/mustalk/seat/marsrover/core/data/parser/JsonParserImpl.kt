package com.mustalk.seat.marsrover.core.data.parser

import com.mustalk.seat.marsrover.core.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Implementation of JsonParser that parses JSON to DTO and maps to domain model.
 * This implementation bridges the data layer (DTOs) with the domain layer (domain models).
 */
class JsonParserImpl
    @Inject
    constructor(
        private val json: Json,
    ) : JsonParser {
        override fun parseInput(jsonInput: String): RoverMissionInstructions =
            try {
                // Parse JSON to DTO
                val marsRoverInput = json.decodeFromString<MarsRoverInput>(jsonInput)

                // Map DTO to domain model
                mapToRoverMissionInstructions(marsRoverInput)
            } catch (e: SerializationException) {
                throw RoverError.InvalidInputFormat("Failed to parse JSON: ${e.message}").apply {
                    initCause(e)
                }
            } catch (e: IllegalArgumentException) {
                throw RoverError.InvalidInputFormat("Invalid JSON structure: ${e.message}").apply {
                    initCause(e)
                }
            }

        /**
         * Maps MarsRoverInput DTO to RoverMissionInstructions domain model.
         */
        private fun mapToRoverMissionInstructions(input: MarsRoverInput): RoverMissionInstructions =
            RoverMissionInstructions(
                plateauTopRightX = input.topRightCorner.x,
                plateauTopRightY = input.topRightCorner.y,
                initialRoverPosition =
                    Position(
                        x = input.roverPosition.x,
                        y = input.roverPosition.y
                    ),
                initialRoverDirection = input.roverDirection,
                movementCommands = input.movements
            )
    }
