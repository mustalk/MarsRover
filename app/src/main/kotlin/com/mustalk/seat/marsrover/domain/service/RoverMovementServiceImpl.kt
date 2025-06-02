package com.mustalk.seat.marsrover.domain.service

import com.mustalk.seat.marsrover.core.model.Direction
import com.mustalk.seat.marsrover.core.model.Plateau
import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.model.Rover
import javax.inject.Inject

/**
 * Implementation of RoverMovementService for handling rover movements.
 */
class RoverMovementServiceImpl
    @Inject
    constructor() : RoverMovementService {
        override fun executeMovements(
            rover: Rover,
            plateau: Plateau,
            movements: String,
        ) {
            movements.forEach { command ->
                when (command.uppercaseChar()) {
                    'L' -> rover.direction = rover.direction.turnLeft()
                    'R' -> rover.direction = rover.direction.turnRight()
                    'M' -> moveForward(rover, plateau)
                    // Ignore invalid characters as per requirement: "try to process the correct ones"
                }
            }
        }

        private fun moveForward(
            rover: Rover,
            plateau: Plateau,
        ) {
            val nextPosition =
                when (rover.direction) {
                    Direction.NORTH -> Position(rover.position.x, rover.position.y + 1)
                    Direction.EAST -> Position(rover.position.x + 1, rover.position.y)
                    Direction.SOUTH -> Position(rover.position.x, rover.position.y - 1)
                    Direction.WEST -> Position(rover.position.x - 1, rover.position.y)
                }

            // Only move if the next position is within plateau bounds
            if (plateau.isWithinBounds(nextPosition)) {
                rover.position = nextPosition
            }
            // Otherwise, ignore the move (rover won't move as per requirement)
        }
    }
