package com.mustalk.seat.marsrover.core.model

/**
 * Represents the result of a Mars Rover mission execution.
 * This is a pure domain model that abstracts away data layer concerns.
 */
data class MissionResult(
    /** Whether the mission was successful */
    val success: Boolean,
    /** The final position of the rover as a formatted string */
    val finalPosition: String,
    /** A descriptive message about the mission result */
    val message: String,
)
