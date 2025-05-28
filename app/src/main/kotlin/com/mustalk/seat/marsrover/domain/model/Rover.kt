package com.mustalk.seat.marsrover.domain.model

/**
 * Represents a Mars rover with its current position and direction.
 * The position and direction are mutable to allow modification during movement simulation.
 *
 * @property position The current position of the rover on the plateau.
 * @property direction The current direction the rover is facing.
 */
data class Rover(
    var position: Position,
    var direction: Direction,
)
