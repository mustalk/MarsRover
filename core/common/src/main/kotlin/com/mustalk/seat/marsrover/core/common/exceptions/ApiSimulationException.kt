package com.mustalk.seat.marsrover.core.common.exceptions

/**
 * Custom exception to represent API simulation errors.
 *
 * @param message The error message
 * @param cause The underlying cause of the exception
 */
class ApiSimulationException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
