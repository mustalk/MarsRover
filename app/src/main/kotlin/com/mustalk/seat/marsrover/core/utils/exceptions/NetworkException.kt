package com.mustalk.seat.marsrover.core.utils.exceptions

/**
 * Custom exception to represent errors during network operations.
 *
 * @param message The error message
 * @param cause The underlying cause of the exception
 */
class NetworkException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
