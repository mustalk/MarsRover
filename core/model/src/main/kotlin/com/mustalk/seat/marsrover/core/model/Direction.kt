package com.mustalk.seat.marsrover.core.model

/**
 * Represents the cardinal compass points for the Rover's orientation.
 */
enum class Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST,
    ;

    /**
     * Turns the rover 90 degrees to the left.
     * @return The new direction after turning left.
     */
    fun turnLeft(): Direction =
        when (this) {
            NORTH -> WEST
            EAST -> NORTH
            SOUTH -> EAST
            WEST -> SOUTH
        }

    /**
     * Turns the rover 90 degrees to the right.
     * @return The new direction after turning right.
     */
    fun turnRight(): Direction =
        when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }

    /**
     * Converts the Direction enum to its character representation.
     * @return Character representation (N, E, S, W).
     */
    fun toChar(): Char =
        when (this) {
            NORTH -> 'N'
            EAST -> 'E'
            SOUTH -> 'S'
            WEST -> 'W'
        }

    companion object {
        /**
         * Creates a Direction enum from its character representation.
         * @param char The character (N, E, S, W).
         * @return The corresponding Direction enum, or null if invalid.
         */
        fun fromChar(char: Char): Direction? =
            when (char.uppercaseChar()) {
                'N' -> NORTH
                'E' -> EAST
                'S' -> SOUTH
                'W' -> WEST
                else -> null
            }
    }
}
