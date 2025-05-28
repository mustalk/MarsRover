package com.mustalk.seat.marsrover.domain.validator

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

            if (maxX < 0 || maxY < 0) {
                throw RoverError.InvalidPlateauDimensions(maxX, maxY)
            }

            return Plateau(maxX, maxY)
        }

        override fun validateAndParseDirection(directionChar: String): Direction {
            if (directionChar.length != 1) {
                throw RoverError.InvalidDirectionChar(directionChar)
            }

            return Direction.fromChar(directionChar.first())
                ?: throw RoverError.InvalidDirectionChar(directionChar)
        }

        override fun validateInitialPosition(
            position: Position,
            plateau: Plateau,
        ) {
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
