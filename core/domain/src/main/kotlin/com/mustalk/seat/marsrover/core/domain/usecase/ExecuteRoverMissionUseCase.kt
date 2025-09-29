package com.mustalk.seat.marsrover.core.domain.usecase

import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.domain.service.RoverMovementService
import com.mustalk.seat.marsrover.core.domain.validator.InputValidator
import com.mustalk.seat.marsrover.core.model.Rover
import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions

/**
 * UseCase that executes Mars Rover mission from JSON input.
 * Handles orchestration of parsing, validation, and rover movement simulation.
 *
 * Following Single Responsibility Principle - this class only orchestrates the flow,
 * delegating specific responsibilities to dedicated services.
 *
 * Note: This implementation uses plain constructor (no @Inject) as it will be managed by Hilt
 * modules in the app layer when this core:domain module is a pure Kotlin module.
 */
class ExecuteRoverMissionUseCase(
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
            // Parse JSON input into domain model
            val instructions = jsonParser.parseInput(jsonInput)

            // Execute mission using the domain model
            executeMission(instructions)
        } catch (error: RoverError) {
            Result.failure(error)
        }

    /**
     * Executes rover mission from RoverMissionInstructions and returns the final position and direction.
     * This method is useful for API simulation where we already have parsed input.
     *
     * @param instructions The RoverMissionInstructions containing rover mission details.
     * @return Result with final rover position string or RoverError.
     */
    fun execute(instructions: RoverMissionInstructions): Result<String> =
        try {
            executeMission(instructions)
        } catch (error: RoverError) {
            Result.failure(error)
        }

    /**
     * Common execution logic for RoverMissionInstructions.
     */
    private fun executeMission(instructions: RoverMissionInstructions): Result<String> {
        // Validate and create plateau
        val plateau = inputValidator.validateAndCreatePlateau(instructions)

        // Parse and validate rover direction
        val initialDirection = inputValidator.validateAndParseDirection(instructions.initialRoverDirection)

        // Validate initial rover position
        val initialPosition = instructions.initialRoverPosition
        inputValidator.validateInitialPosition(initialPosition, plateau)

        // Create rover and execute movements
        val rover = Rover(initialPosition, initialDirection)
        roverMovementService.executeMovements(rover, plateau, instructions.movementCommands)

        // Format and return final position
        val result = "${rover.position.x} ${rover.position.y} ${rover.direction.toChar()}"
        return Result.success(result)
    }
}
