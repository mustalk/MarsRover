package com.mustalk.seat.marsrover.core.domain.error

/**
 * Sealed class representing all possible errors that can occur during Mars Rover operations.
 */
sealed class RoverError(
    override val message: String,
) : Throwable(message) {
    /**
     * Error when the input JSON format is invalid or cannot be parsed.
     */
    data class InvalidInputFormat(
        val details: String,
    ) : RoverError("Invalid input format: $details")

    /**
     * Error when the rover's initial position is outside the plateau bounds.
     */
    data class InvalidInitialPosition(
        val x: Int,
        val y: Int,
        val plateauMaxX: Int,
        val plateauMaxY: Int,
    ) : RoverError("Invalid initial position ($x, $y) - must be within plateau bounds (0,0) to ($plateauMaxX, $plateauMaxY)")

    /**
     * Error when the rover direction character is not recognized.
     */
    data class InvalidDirectionChar(
        val char: String,
    ) : RoverError("Invalid direction character: '$char' - must be N, E, S, or W")

    /**
     * Error when plateau dimensions are invalid (negative values).
     */
    data class InvalidPlateauDimensions(
        val x: Int,
        val y: Int,
    ) : RoverError("Invalid plateau dimensions: ($x, $y) - both must be non-negative")
}
