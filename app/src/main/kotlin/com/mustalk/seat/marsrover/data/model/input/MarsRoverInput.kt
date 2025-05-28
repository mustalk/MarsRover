package com.mustalk.seat.marsrover.data.model.input

import kotlinx.serialization.Serializable

/**
 * Represents the top-right corner coordinates of the plateau.
 */
@Serializable
data class TopRightCorner(
    val x: Int,
    val y: Int,
)

/**
 * Represents the rover's initial position coordinates.
 */
@Serializable
data class RoverPosition(
    val x: Int,
    val y: Int,
)

/**
 * Represents the complete Mars Rover input from JSON.
 *
 * Example: { "topRightCorner": { "x": 5, "y": 5 }, "roverPosition": { "x": 1, "y": 2 }, "roverDirection": "N", "movements": "LMLMLMLMM"}
 */
@Serializable
data class MarsRoverInput(
    val topRightCorner: TopRightCorner,
    val roverPosition: RoverPosition,
    val roverDirection: String,
    val movements: String,
)
