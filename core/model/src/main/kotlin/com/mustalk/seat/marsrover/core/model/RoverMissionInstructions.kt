package com.mustalk.seat.marsrover.core.model

/**
 * Represents the validated and structured instructions for a Mars Rover mission,
 * intended for use by the domain layer.
 */
data class RoverMissionInstructions(
    /** The configuration for the plateau's top-right corner. */
    val plateauTopRightX: Int,
    val plateauTopRightY: Int,
    /** The initial position of the rover. */
    val initialRoverPosition: Position,
    /** The initial direction of the rover. */
    val initialRoverDirection: String,
    /** The sequence of movement commands for the rover. */
    val movementCommands: String,
)
