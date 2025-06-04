package com.mustalk.seat.marsrover.core.common.network

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Unit tests for NetworkResult monad functionality.
 */
class NetworkResultTest {
    @Test
    fun `success creates success result`() {
        val data = "test data"
        val result = NetworkResult.success(data)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.isError).isFalse()
        assertThat(result.isLoading).isFalse()
        assertThat(result.getOrNull()).isEqualTo(data)
        assertThat(result.getOrThrow()).isEqualTo(data)
    }

    @Test
    fun `error creates error result`() {
        val exception = RuntimeException("test error")
        val result = NetworkResult.error(exception)

        assertThat(result.isSuccess).isFalse()
        assertThat(result.isError).isTrue()
        assertThat(result.isLoading).isFalse()
        assertThat(result.getOrNull() as String?).isNull()
    }

    @Test
    fun `error getOrThrow throws exception`() {
        val exception = RuntimeException("test error")
        val result = NetworkResult.error(exception)

        val thrown = runCatching { result.getOrThrow() }
        assertThat(thrown.isFailure).isTrue()
        assertThat(thrown.exceptionOrNull()).isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun `loading creates loading result`() {
        val result = NetworkResult.loading()

        assertThat(result.isSuccess).isFalse()
        assertThat(result.isError).isFalse()
        assertThat(result.isLoading).isTrue()
        assertThat(result.getOrNull() as String?).isNull()
    }

    @Test
    fun `loading getOrThrow throws exception`() {
        val result = NetworkResult.loading()

        val thrown = runCatching { result.getOrThrow() }
        assertThat(thrown.isFailure).isTrue()
        assertThat(thrown.exceptionOrNull()).isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `map transforms success data`() {
        val original = NetworkResult.success(5)
        val transformed = original.map { it * 2 }

        assertThat(transformed.isSuccess).isTrue()
        assertThat(transformed.getOrNull()).isEqualTo(10)
    }

    @Test
    fun `map preserves error`() {
        val exception = RuntimeException("test error")
        val original = NetworkResult.error(exception)
        val transformed = original.map { "should not be called" }

        assertThat(transformed.isError).isTrue()
        assertThat(transformed.getOrNull() as String?).isNull()
    }

    @Test
    fun `map preserves loading`() {
        val original = NetworkResult.loading()
        val transformed = original.map { "should not be called" }

        assertThat(transformed.isLoading).isTrue()
        assertThat(transformed.getOrNull() as String?).isNull()
    }

    @Test
    fun `flatMap transforms success with another result`() {
        val original = NetworkResult.success(5)
        val transformed = original.flatMap { NetworkResult.success(it * 2) }

        assertThat(transformed.isSuccess).isTrue()
        assertThat(transformed.getOrNull()).isEqualTo(10)
    }

    @Test
    fun `flatMap can transform success to error`() {
        val original = NetworkResult.success(5)
        val transformed = original.flatMap { NetworkResult.error(RuntimeException("transformed error")) }

        assertThat(transformed.isError).isTrue()
        assertThat(transformed.getOrNull() as String?).isNull()
    }

    @Test
    fun `flatMap preserves error`() {
        val exception = RuntimeException("test error")
        val original = NetworkResult.error(exception)
        val transformed = original.flatMap { NetworkResult.success("should not be called") }

        assertThat(transformed.isError).isTrue()
        assertThat(transformed.getOrNull() as String?).isNull()
    }

    @Test
    fun `fold executes success action`() {
        var successCalled = false
        var errorCalled = false
        var loadingCalled = false

        val result = NetworkResult.success("test")
        result.fold(
            onSuccess = { successCalled = true },
            onError = { _, _ -> errorCalled = true },
            onLoading = { loadingCalled = true }
        )

        assertThat(successCalled).isTrue()
        assertThat(errorCalled).isFalse()
        assertThat(loadingCalled).isFalse()
    }

    @Test
    fun `fold executes error action`() {
        var successCalled = false
        var errorCalled = false
        var loadingCalled = false

        val result = NetworkResult.error(RuntimeException("test"))
        result.fold(
            onSuccess = { successCalled = true },
            onError = { _, _ -> errorCalled = true },
            onLoading = { loadingCalled = true }
        )

        assertThat(successCalled).isFalse()
        assertThat(errorCalled).isTrue()
        assertThat(loadingCalled).isFalse()
    }

    @Test
    fun `fold executes loading action`() {
        var successCalled = false
        var errorCalled = false
        var loadingCalled = false

        val result = NetworkResult.loading()
        result.fold(
            onSuccess = { successCalled = true },
            onError = { _, _ -> errorCalled = true },
            onLoading = { loadingCalled = true }
        )

        assertThat(successCalled).isFalse()
        assertThat(errorCalled).isFalse()
        assertThat(loadingCalled).isTrue()
    }

    @Test
    fun `safeCall wraps successful operation`() =
        runTest {
            val result = NetworkResult.safeCall { "success" }

            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo("success")
        }

    @Test
    fun `safeCall wraps exception in error`() =
        runTest {
            val exception = RuntimeException("test error")
            val result = NetworkResult.safeCall { throw exception }

            assertThat(result.isError).isTrue()
            assertThat(result.getOrNull() as String?).isNull()
        }
}
