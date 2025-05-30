package com.mustalk.seat.marsrover.data.network.model

import kotlinx.serialization.Serializable

/**
 * Represents the response from a Mars rover mission execution.
 *
 * Contains the final rover position and additional metadata about the mission execution.
 */
@Serializable
data class MissionResponse(
    /**
     * Indicates if the mission was executed successfully.
     */
    val success: Boolean,
    /**
     * The final position of the rover after executing all commands.
     * Format: "x y direction" (e.g., "1 3 N")
     */
    val finalPosition: String,
    /**
     * Human-readable message about the mission execution.
     */
    val message: String,
    /**
     * The original input that was processed for this mission.
     */
    val originalInput: String? = null,
    /**
     * Timestamp when the mission was executed (ISO 8601 format).
     */
    val timestamp: String? = null,
    /**
     * Execution time in milliseconds.
     */
    val executionTimeMs: Long? = null,
    /**
     * Error details if the mission failed.
     */
    val error: ErrorDetails? = null,
)

/**
 * Represents error details when a mission execution fails.
 */
@Serializable
data class ErrorDetails(
    /**
     * Error code for categorizing the type of error.
     */
    val code: String,
    /**
     * Detailed error message.
     */
    val message: String,
    /**
     * Additional context about the error.
     */
    val details: String? = null,
)
