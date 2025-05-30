package com.mustalk.seat.marsrover.core.utils.exceptions

/**
 * Custom exception to represent JSON parsing errors.
 *
 * @param message The error message
 * @param cause The underlying cause of the exception
 */
class JsonParsingException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
