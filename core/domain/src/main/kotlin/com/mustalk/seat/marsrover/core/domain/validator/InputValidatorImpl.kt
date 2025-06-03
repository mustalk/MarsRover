package com.mustalk.seat.marsrover.core.domain.validator

import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.core.model.Direction
import com.mustalk.seat.marsrover.core.model.Plateau
import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions

/**
 * Implementation of InputValidator for Mars Rover mission instruction validation.
 * Note: This implementation uses plain constructor (no @Inject) as it will be managed by Hilt
 * modules in the app layer when this core:domain module is a pure Kotlin module.
 */
class InputValidatorImpl : InputValidator {
    companion object {
        private const val MIN_PLATEAU_SIZE = 1
        private const val MAX_PLATEAU_SIZE = 100
        private const val MAX_POSITION_VALUE = 100
        private val VALID_DIRECTIONS = setOf("N", "S", "E", "W")
    }

    override fun validateAndCreatePlateau(instructions: RoverMissionInstructions): Plateau {
        val maxX = instructions.plateauTopRightX
        val maxY = instructions.plateauTopRightY

        // Validate minimum values
        if (maxX < MIN_PLATEAU_SIZE || maxY < MIN_PLATEAU_SIZE) {
            throw RoverError.InvalidPlateauDimensions(maxX, maxY)
        }

        // Validate maximum values to prevent integer overflow
        if (maxX > MAX_PLATEAU_SIZE || maxY > MAX_PLATEAU_SIZE) {
            throw RoverError.InvalidPlateauDimensions(maxX, maxY)
        }

        return Plateau(maxX, maxY)
    }

    override fun validateAndParseDirection(directionChar: String): Direction {
        if (directionChar.length != 1) {
            throw RoverError.InvalidDirectionChar(directionChar)
        }

        if (directionChar.uppercase() !in VALID_DIRECTIONS) {
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
        if (position.x > MAX_POSITION_VALUE || position.y > MAX_POSITION_VALUE) {
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
