package com.mustalk.seat.marsrover.core.common.network

import com.mustalk.seat.marsrover.core.common.exceptions.NetworkException

/**
 * A sealed class representing the result of a network operation.
 * Provides a functional programming approach to handling network results.
 *
 * @param T The type of data on success
 */
@Suppress("TooGenericExceptionCaught")
sealed class NetworkResult<out T> {
    /**
     * Represents a successful network operation.
     */
    data class Success<T>(
        val data: T,
    ) : NetworkResult<T>()

    /**
     * Represents a network error.
     */
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "Unknown error occurred",
    ) : NetworkResult<Nothing>()

    /**
     * Represents a loading state for network operations.
     */
    data object Loading : NetworkResult<Nothing>()

    /**
     * Returns true if this is a Success result.
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Returns true if this is an Error result.
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Returns true if this is a Loading result.
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Returns the data if this is a Success, otherwise null.
     */
    fun getOrNull(): T? =
        when (this) {
            is Success -> data
            is Error, Loading -> null
        }

    /**
     * Returns the data if this is a Success, otherwise throws the exception.
     */
    fun getOrThrow(): T =
        when (this) {
            is Success -> data
            is Error -> throw exception
            Loading -> error("Cannot get data from Loading state")
        }

    /**
     * Transforms the success data using the provided function.
     *
     * @param transform Function to transform the success data
     * @return A new NetworkResult with the transformed data
     */
    inline fun <R> map(transform: (T) -> R): NetworkResult<R> =
        when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(exception, message)
            Loading -> Loading
        }

    /**
     * Transforms the success data using a function that returns another NetworkResult.
     *
     * @param transform Function that transforms the success data to another NetworkResult
     * @return The result of the transformation
     */
    inline fun <R> flatMap(transform: (T) -> NetworkResult<R>): NetworkResult<R> =
        when (this) {
            is Success -> transform(data)
            is Error -> Error(exception, message)
            Loading -> Loading
        }

    /**
     * Executes different actions based on the result type.
     *
     * @param onSuccess Action to execute on success
     * @param onError Action to execute on error
     * @param onLoading Action to execute on loading
     */
    inline fun fold(
        onSuccess: (T) -> Unit = {},
        onError: (Throwable, String) -> Unit = { _, _ -> },
        onLoading: () -> Unit = {},
    ) {
        when (this) {
            is Success -> onSuccess(data)
            is Error -> onError(exception, message)
            Loading -> onLoading()
        }
    }

    companion object {
        /**
         * Creates a Success result.
         */
        fun <T> success(data: T): NetworkResult<T> = Success(data)

        /**
         * Creates an Error result.
         */
        fun error(
            exception: Throwable,
            message: String? = null,
        ): NetworkResult<Nothing> = Error(exception, message ?: exception.message ?: "Unknown error occurred")

        /**
         * Creates a Loading result.
         */
        fun loading(): NetworkResult<Nothing> = Loading

        /**
         * Safely executes a network operation and wraps the result.
         *
         * @param operation The network operation to execute
         * @return NetworkResult wrapping the operation result
         */
        suspend inline fun <T> safeCall(crossinline operation: suspend () -> T): NetworkResult<T> =
            try {
                Success(operation())
            } catch (e: java.io.IOException) {
                // Covers network IO issues (connectivity, timeout)
                Error(NetworkException("Network operation failed: ${e.message}", e), e.message ?: "Network error")
            } catch (exception: Throwable) {
                // Fallback for other unexpected errors
                Error(exception)
            }
    }
}
