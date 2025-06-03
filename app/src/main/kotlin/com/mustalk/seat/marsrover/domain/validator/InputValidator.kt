package com.mustalk.seat.marsrover.domain.validator

import com.mustalk.seat.marsrover.core.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.core.model.Direction
import com.mustalk.seat.marsrover.core.model.Plateau
import com.mustalk.seat.marsrover.core.model.Position

/**
 * Interface for validating Mars Rover input data.
 */
interface InputValidator {
    /**
     * Validates and creates a plateau from input data.
     *
     * @param input The Mars Rover input data.
     * @return Valid Plateau instance.
     * @throws RoverError if validation fails.
     */
    fun validateAndCreatePlateau(input: MarsRoverInput): Plateau

    /**
     * Validates and parses rover direction from input data.
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
