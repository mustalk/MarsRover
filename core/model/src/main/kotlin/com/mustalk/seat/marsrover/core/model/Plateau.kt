package com.mustalk.seat.marsrover.core.model

/**
 * Represents the rectangular plateau on Mars.
 *
 * @property maxX The maximum X-coordinate (width) of the plateau.
 * @property maxY The maximum Y-coordinate (height) of the plateau.
 */
data class Plateau(
    val maxX: Int,
    val maxY: Int,
) {
    /**
     * Checks if a given position is within the bounds of this plateau.
     *
     * @param position The position to check.
     * @return True if the position is within bounds (0,0 to maxX,maxY inclusive), false otherwise.
     */
    fun isWithinBounds(position: Position): Boolean =
        position.x >= 0 &&
            position.x <= maxX &&
            position.y >= 0 &&
            position.y <= maxY
}
