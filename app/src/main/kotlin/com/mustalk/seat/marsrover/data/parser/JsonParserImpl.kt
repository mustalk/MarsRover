package com.mustalk.seat.marsrover.data.parser

import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.domain.parser.JsonParser
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Implementation of JsonParser using kotlinx.serialization.
 */
class JsonParserImpl
    @Inject
    constructor(
        private val json: Json,
    ) : JsonParser {
        override fun parseInput(jsonInput: String): MarsRoverInput =
            try {
                json.decodeFromString<MarsRoverInput>(jsonInput)
            } catch (e: SerializationException) {
                throw RoverError.InvalidInputFormat("Failed to parse JSON: ${e.message}").apply {
                    initCause(e)
                }
            } catch (e: IllegalArgumentException) {
                throw RoverError.InvalidInputFormat("Invalid JSON structure: ${e.message}").apply {
                    initCause(e)
                }
            }
    }
