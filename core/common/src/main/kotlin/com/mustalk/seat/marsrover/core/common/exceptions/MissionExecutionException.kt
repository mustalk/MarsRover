package com.mustalk.seat.marsrover.core.common.exceptions

/**
 * Custom exception to represent mission execution errors.
 *
 * @param message The error message
 * @param cause The underlying cause of the exception
 */
class MissionExecutionException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
