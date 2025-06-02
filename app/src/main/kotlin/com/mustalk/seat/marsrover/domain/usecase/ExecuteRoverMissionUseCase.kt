package com.mustalk.seat.marsrover.domain.usecase

import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.model.Rover
import com.mustalk.seat.marsrover.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.domain.error.RoverError
import com.mustalk.seat.marsrover.domain.parser.JsonParser
import com.mustalk.seat.marsrover.domain.service.RoverMovementService
import com.mustalk.seat.marsrover.domain.validator.InputValidator
import javax.inject.Inject

/**
 * UseCase that executes Mars Rover mission from JSON input.
 * Handles orchestration of parsing, validation, and rover movement simulation.
 *
 * Following Single Responsibility Principle - this class only orchestrates the flow,
 * delegating specific responsibilities to dedicated services.
 */
class ExecuteRoverMissionUseCase
/**
     * @Inject constructor tells Hilt to automatically provide dependencies when creating this class.
     * Hilt will look at AppModule to find implementations for these interfaces:
     * - JsonParser -> JsonParserImpl (from @Binds)
     * - InputValidator -> InputValidatorImpl (from @Binds)
     * - RoverMovementService -> RoverMovementServiceImpl (from @Binds)
     *
     * This enables dependency inversion - UseCase depends on abstractions, not concrete classes.
     */
    @Inject
    constructor(
        private val jsonParser: JsonParser,
        private val inputValidator: InputValidator,
        private val roverMovementService: RoverMovementService,
    ) {
        /**
         * Executes rover mission from JSON input and returns the final position and direction.
         *
         * @param jsonInput The JSON string containing rover mission instructions.
         * @return Result with final rover position string or RoverError.
         */
        operator fun invoke(jsonInput: String): Result<String> =
            try {
                // Parse JSON input
                val input = jsonParser.parseInput(jsonInput)

                // Execute mission using the common logic
                executeMission(input)
            } catch (error: RoverError) {
                Result.failure(error)
            }

        /**
         * Executes rover mission from MarsRoverInput and returns the final position and direction.
         * This method is useful for API simulation where we already have parsed input.
         *
         * @param input The MarsRoverInput containing rover mission instructions.
         * @return Result with final rover position string or RoverError.
         */
        fun execute(input: MarsRoverInput): Result<String> =
            try {
                executeMission(input)
            } catch (error: RoverError) {
                Result.failure(error)
            }

        /**
         * Common execution logic for both JSON and MarsRoverInput.
         */
        private fun executeMission(input: MarsRoverInput): Result<String> {
            // Validate and create plateau
            val plateau = inputValidator.validateAndCreatePlateau(input)

            // Parse and validate rover direction
            val initialDirection = inputValidator.validateAndParseDirection(input.roverDirection)

            // Validate initial rover position
            val initialPosition = Position(input.roverPosition.x, input.roverPosition.y)
            inputValidator.validateInitialPosition(initialPosition, plateau)

            // Create rover and execute movements
            val rover = Rover(initialPosition, initialDirection)
            roverMovementService.executeMovements(rover, plateau, input.movements)

            // Format and return final position
            val result = "${rover.position.x} ${rover.position.y} ${rover.direction.toChar()}"
            return Result.success(result)
        }
    }
