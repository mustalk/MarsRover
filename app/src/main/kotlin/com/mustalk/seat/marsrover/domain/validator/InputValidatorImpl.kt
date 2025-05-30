package com.mustalk.seat.marsrover.domain.validator

import com.mustalk.seat.marsrover.core.utils.Constants
import com.mustalk.seat.marsrover.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.domain.error.RoverError
import com.mustalk.seat.marsrover.domain.model.Direction
import com.mustalk.seat.marsrover.domain.model.Plateau
import com.mustalk.seat.marsrover.domain.model.Position
import javax.inject.Inject

/**
 * Implementation of InputValidator for Mars Rover input validation.
 */
class InputValidatorImpl
    @Inject
    constructor() : InputValidator {
        override fun validateAndCreatePlateau(input: MarsRoverInput): Plateau {
            val maxX = input.topRightCorner.x
            val maxY = input.topRightCorner.y

            // Validate minimum values
            if (maxX < Constants.Validation.MIN_PLATEAU_SIZE || maxY < Constants.Validation.MIN_PLATEAU_SIZE) {
                throw RoverError.InvalidPlateauDimensions(maxX, maxY)
            }

            // Validate maximum values to prevent integer overflow
            if (maxX > Constants.Validation.MAX_PLATEAU_SIZE || maxY > Constants.Validation.MAX_PLATEAU_SIZE) {
                throw RoverError.InvalidPlateauDimensions(maxX, maxY)
            }

            return Plateau(maxX, maxY)
        }

        override fun validateAndParseDirection(directionChar: String): Direction {
            if (directionChar.length != 1) {
                throw RoverError.InvalidDirectionChar(directionChar)
            }

            if (directionChar.uppercase() !in Constants.Validation.VALID_DIRECTIONS) {
                throw RoverError.InvalidDirectionChar(directionChar)
            }

            return Direction.fromChar(directionChar.first())
                ?: throw RoverError.InvalidDirectionChar(directionChar)
        }

        override fun validateInitialPosition(
            position: Position,
            plateau: Plateau,
        ) {
            // Validate position values don't exceed maximum to prevent overflow
            if (position.x > Constants.Validation.MAX_POSITION_VALUE ||
                position.y > Constants.Validation.MAX_POSITION_VALUE
            ) {
                throw RoverError.InvalidInitialPosition(
                    position.x,
                    position.y,
                    plateau.maxX,
                    plateau.maxY
                )
            }

            if (!plateau.isWithinBounds(position)) {
                throw RoverError.InvalidInitialPosition(
                    position.x,
                    position.y,
                    plateau.maxX,
                    plateau.maxY
                )
            }
        }
    }
