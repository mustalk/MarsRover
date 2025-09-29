package com.mustalk.seat.marsrover.core.domain.validator

import com.mustalk.seat.marsrover.core.model.Direction
import com.mustalk.seat.marsrover.core.model.Plateau
import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions

/**
 * Interface for validating Mars Rover mission instructions.
 */
interface InputValidator {
    /**
     * Validates and creates a plateau from mission instructions.
     *
     * @param instructions The rover mission instructions.
     * @return Valid Plateau instance.
     * @throws RoverError if validation fails.
     */
    fun validateAndCreatePlateau(instructions: RoverMissionInstructions): Plateau

    /**
     * Validates and parses rover direction from mission instructions.
     *
     * @param directionChar The direction character string.
     * @return Valid Direction enum.
     * @throws RoverError if validation fails.
     */
    fun validateAndParseDirection(directionChar: String): Direction

    /**
     * Validates rover initial position against plateau bounds.
     *
     * @param position The rover's initial position.
     * @param plateau The plateau to validate against.
     * @throws RoverError if validation fails.
     */
    fun validateInitialPosition(
        position: Position,
        plateau: Plateau,
    )
}
