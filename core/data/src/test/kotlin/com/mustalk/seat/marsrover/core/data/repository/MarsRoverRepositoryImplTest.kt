package com.mustalk.seat.marsrover.core.data.repository

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.core.common.network.NetworkResult
import com.mustalk.seat.marsrover.core.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.core.data.model.input.RoverPosition
import com.mustalk.seat.marsrover.core.data.model.input.TopRightCorner
import com.mustalk.seat.marsrover.core.data.network.api.MarsRoverApiService
import com.mustalk.seat.marsrover.core.data.network.model.ErrorDetails
import com.mustalk.seat.marsrover.core.data.network.model.MissionResponse
import com.mustalk.seat.marsrover.core.testing.jvm.data.MarsRoverTestData
import com.mustalk.seat.marsrover.core.testing.jvm.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Unit tests for MarsRoverRepositoryImpl.
 * Tests network operations and error handling for mission execution with domain models.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MarsRoverRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: MarsRoverRepositoryImpl
    private lateinit var mockApiService: MarsRoverApiService

    @Before
    fun setup() {
        mockApiService = mockk()
        repository = MarsRoverRepositoryImpl(mockApiService)
    }

    @Test
    fun `executeMission should return success when API call succeeds`() =
        runTest {
            // Given
            val testData = MarsRoverTestData.RepositoryTestData.StandardMission

            val expectedResponse =
                MissionResponse(
                    success = true,
                    finalPosition = testData.FINAL_POSITION,
                    message = testData.SUCCESS_MESSAGE,
                    originalInput = "",
                    timestamp = "2024-01-01T00:00:00Z",
                    executionTimeMs = testData.EXECUTION_TIME_MS
                )

            val inputDto =
                MarsRoverInput(
                    topRightCorner = TopRightCorner(testData.TOP_RIGHT_X, testData.TOP_RIGHT_Y),
                    roverPosition = RoverPosition(testData.ROVER_START_X, testData.ROVER_START_Y),
                    roverDirection = testData.ROVER_START_DIRECTION,
                    movements = testData.ROVER_MOVEMENTS
                )

            coEvery { mockApiService.executeMission(inputDto) } returns expectedResponse

            // When
            val result = repository.executeMission(testData.INPUT)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Success::class.java)
            val successResult = result as NetworkResult.Success
            assertThat(successResult.data.success).isTrue()
            assertThat(successResult.data.finalPosition).isEqualTo(testData.FINAL_POSITION)
            assertThat(successResult.data.message).isEqualTo(testData.SUCCESS_MESSAGE)

            coVerify { mockApiService.executeMission(inputDto) }
        }

    @Test
    fun `executeMission should return error when API call fails with HttpException`() =
        runTest {
            // Given
            val testData = MarsRoverTestData.RepositoryTestData.HttpErrors
            val errorResponse =
                Response.error<MissionResponse>(
                    testData.BAD_REQUEST_CODE,
                    "Bad Request".toResponseBody(null)
                )
            val httpException = HttpException(errorResponse)

            coEvery { mockApiService.executeMission(any()) } throws httpException

            // When
            val result = repository.executeMission(MarsRoverTestData.RepositoryTestData.StandardMission.INPUT)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains("HTTP ${testData.BAD_REQUEST_CODE} Response.error()")
        }

    @Test
    fun `executeMission should return error when API call fails with ConnectException`() =
        runTest {
            // Given
            val testData = MarsRoverTestData.RepositoryTestData.NetworkErrors
            coEvery { mockApiService.executeMission(any()) } throws ConnectException(testData.CONNECTION_REFUSED)

            // When
            val result = repository.executeMission(MarsRoverTestData.RepositoryTestData.StandardMission.INPUT)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains(testData.CONNECTION_REFUSED)
        }

    @Test
    fun `executeMission should return error when API call fails with SocketTimeoutException`() =
        runTest {
            // Given
            val testData = MarsRoverTestData.RepositoryTestData.NetworkErrors
            coEvery { mockApiService.executeMission(any()) } throws SocketTimeoutException(testData.TIMEOUT)

            // When
            val result = repository.executeMission(MarsRoverTestData.RepositoryTestData.StandardMission.INPUT)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains(testData.TIMEOUT)
        }

    @Test
    fun `executeMission should return error when API call fails with generic exception`() =
        runTest {
            // Given
            val testData = MarsRoverTestData.RepositoryTestData.NetworkErrors
            coEvery { mockApiService.executeMission(any()) } throws RuntimeException(testData.UNEXPECTED_ERROR)

            // When
            val result = repository.executeMission(MarsRoverTestData.RepositoryTestData.StandardMission.INPUT)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains(testData.UNEXPECTED_ERROR)
        }

    @Test
    fun `executeMission should handle mission failure response from API`() =
        runTest {
            // Given
            val errorTestData = MarsRoverTestData.RepositoryTestData.ErrorCases
            val failureResponse =
                MissionResponse(
                    success = false,
                    finalPosition = "",
                    message = errorTestData.FAILURE_MESSAGE,
                    originalInput = "",
                    timestamp = "2024-01-01T00:00:00Z",
                    executionTimeMs = errorTestData.EXECUTION_TIME_MS,
                    error =
                        ErrorDetails(
                            code = errorTestData.VALIDATION_ERROR_CODE,
                            message = errorTestData.INVALID_POSITION_MESSAGE,
                            details = errorTestData.OUT_OF_BOUNDS_DETAILS
                        )
                )

            coEvery { mockApiService.executeMission(any()) } returns failureResponse

            // When
            val result = repository.executeMission(MarsRoverTestData.RepositoryTestData.StandardMission.INPUT)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Success::class.java)
            val successResult = result as NetworkResult.Success
            assertThat(successResult.data.success).isFalse()
            assertThat(successResult.data.message).isEqualTo(errorTestData.FAILURE_MESSAGE)
        }

    @Test
    fun `executeMission should handle HTTP 500 internal server error`() =
        runTest {
            // Given
            val testData = MarsRoverTestData.RepositoryTestData.HttpErrors
            val errorResponse =
                Response.error<MissionResponse>(
                    testData.INTERNAL_SERVER_ERROR_CODE,
                    "Internal Server Error".toResponseBody(null)
                )
            val httpException = HttpException(errorResponse)

            coEvery { mockApiService.executeMission(any()) } throws httpException

            // When
            val result = repository.executeMission(MarsRoverTestData.RepositoryTestData.StandardMission.INPUT)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains("HTTP ${testData.INTERNAL_SERVER_ERROR_CODE} Response.error()")
        }

    @Test
    fun `repository should correctly pass all mission input parameters to API service`() =
        runTest {
            // Given
            val testData = MarsRoverTestData.RepositoryTestData.ComplexMission

            val expectedDto =
                MarsRoverInput(
                    topRightCorner = TopRightCorner(testData.TOP_RIGHT_X, testData.TOP_RIGHT_Y),
                    roverPosition = RoverPosition(testData.ROVER_START_X, testData.ROVER_START_Y),
                    roverDirection = testData.ROVER_START_DIRECTION,
                    movements = testData.ROVER_MOVEMENTS
                )

            val response =
                MissionResponse(
                    success = true,
                    finalPosition = testData.FINAL_POSITION,
                    message = testData.SUCCESS_MESSAGE,
                    originalInput = "",
                    timestamp = "2024-01-01T00:00:00Z",
                    executionTimeMs = testData.EXECUTION_TIME_MS
                )

            coEvery { mockApiService.executeMission(any()) } returns response

            // When
            repository.executeMission(testData.INPUT)

            // Then - Verify the correct DTO was passed to the API
            coVerify {
                mockApiService.executeMission(
                    match<MarsRoverInput> { dto ->
                        dto.topRightCorner.x == testData.TOP_RIGHT_X &&
                            dto.topRightCorner.y == testData.TOP_RIGHT_Y &&
                            dto.roverPosition.x == testData.ROVER_START_X &&
                            dto.roverPosition.y == testData.ROVER_START_Y &&
                            dto.roverDirection == testData.ROVER_START_DIRECTION &&
                            dto.movements == testData.ROVER_MOVEMENTS
                    }
                )
            }
        }
}
