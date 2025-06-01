package com.mustalk.seat.marsrover.data.repository

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.core.utils.NetworkResult
import com.mustalk.seat.marsrover.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.data.model.input.RoverPosition
import com.mustalk.seat.marsrover.data.model.input.TopRightCorner
import com.mustalk.seat.marsrover.data.network.api.MarsRoverApiService
import com.mustalk.seat.marsrover.data.network.model.ErrorDetails
import com.mustalk.seat.marsrover.data.network.model.MissionResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Unit tests for MarsRoverRepositoryImpl.
 * Tests network operations and error handling for both regular and Flow-based mission execution.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MarsRoverRepositoryImplTest {
    private lateinit var repository: MarsRoverRepositoryImpl
    private lateinit var mockApiService: MarsRoverApiService

    private val sampleMissionInput =
        MarsRoverInput(
            topRightCorner = TopRightCorner(x = 5, y = 5),
            roverPosition = RoverPosition(x = 1, y = 2),
            roverDirection = "N",
            movements = "LMLMLMLMM"
        )

    @Before
    fun setup() {
        mockApiService = mockk()
        repository = MarsRoverRepositoryImpl(mockApiService)
    }

    @Test
    fun `executeMission should return success when API call succeeds`() =
        runTest {
            // Given
            val expectedResponse =
                MissionResponse(
                    success = true,
                    finalPosition = "1 3 N",
                    message = "Mission completed successfully",
                    originalInput = "",
                    timestamp = "2024-01-01T00:00:00Z",
                    executionTimeMs = 1000
                )

            coEvery { mockApiService.executeMission(sampleMissionInput) } returns expectedResponse

            // When
            val result = repository.executeMission(sampleMissionInput)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Success::class.java)
            val successResult = result as NetworkResult.Success
            assertThat(successResult.data).isEqualTo(expectedResponse)

            coVerify { mockApiService.executeMission(sampleMissionInput) }
        }

    @Test
    fun `executeMission should return error when API call fails with HttpException`() =
        runTest {
            // Given
            val errorResponse =
                Response.error<MissionResponse>(
                    400,
                    "Bad Request".toResponseBody(null)
                )
            val httpException = HttpException(errorResponse)

            coEvery { mockApiService.executeMission(sampleMissionInput) } throws httpException

            // When
            val result = repository.executeMission(sampleMissionInput)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains("HTTP error: 400")
        }

    @Test
    fun `executeMission should return error when API call fails with ConnectException`() =
        runTest {
            // Given
            coEvery { mockApiService.executeMission(sampleMissionInput) } throws ConnectException("Connection refused")

            // When
            val result = repository.executeMission(sampleMissionInput)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains("Connection refused")
        }

    @Test
    fun `executeMission should return error when API call fails with SocketTimeoutException`() =
        runTest {
            // Given
            coEvery { mockApiService.executeMission(sampleMissionInput) } throws SocketTimeoutException("Timeout")

            // When
            val result = repository.executeMission(sampleMissionInput)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains("Timeout")
        }

    @Test
    fun `executeMission should return error when API call fails with generic exception`() =
        runTest {
            // Given
            coEvery { mockApiService.executeMission(sampleMissionInput) } throws RuntimeException("Unexpected error")

            // When
            val result = repository.executeMission(sampleMissionInput)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains("Unexpected error")
        }

    @Test
    fun `executeMission should handle mission failure response from API`() =
        runTest {
            // Given
            val failureResponse =
                MissionResponse(
                    success = false,
                    finalPosition = "",
                    message = "Mission execution failed",
                    originalInput = "",
                    timestamp = "2024-01-01T00:00:00Z",
                    executionTimeMs = 800,
                    error =
                        ErrorDetails(
                            code = "VALIDATION_ERROR",
                            message = "Invalid rover position",
                            details = "Rover cannot start outside plateau bounds"
                        )
                )

            coEvery { mockApiService.executeMission(sampleMissionInput) } returns failureResponse

            // When
            val result = repository.executeMission(sampleMissionInput)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Success::class.java)
            val successResult = result as NetworkResult.Success
            assertThat(successResult.data).isEqualTo(failureResponse)
        }

    @Test
    fun `executeMission should handle HTTP 500 internal server error`() =
        runTest {
            // Given
            val errorResponse =
                Response.error<MissionResponse>(
                    500,
                    "Internal Server Error".toResponseBody(null)
                )
            val httpException = HttpException(errorResponse)

            coEvery { mockApiService.executeMission(sampleMissionInput) } throws httpException

            // When
            val result = repository.executeMission(sampleMissionInput)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains("HTTP error: 500")
        }

    @Test
    fun `repository should correctly pass all mission input parameters to API service`() =
        runTest {
            // Given
            val complexInput =
                MarsRoverInput(
                    topRightCorner = TopRightCorner(x = 7, y = 9),
                    roverPosition = RoverPosition(x = 3, y = 4),
                    roverDirection = "S",
                    movements = "LMLMLMLMRMRMR"
                )

            val response =
                MissionResponse(
                    success = true,
                    finalPosition = "2 3 E",
                    message = "Complex mission completed",
                    originalInput = "",
                    timestamp = "2024-01-01T00:00:00Z",
                    executionTimeMs = 2500
                )

            coEvery { mockApiService.executeMission(complexInput) } returns response

            // When
            repository.executeMission(complexInput)

            // Then
            coVerify {
                mockApiService.executeMission(
                    match { input ->
                        input.topRightCorner.x == 7 &&
                            input.topRightCorner.y == 9 &&
                            input.roverPosition.x == 3 &&
                            input.roverPosition.y == 4 &&
                            input.roverDirection == "S" &&
                            input.movements == "LMLMLMLMRMRMR"
                    }
                )
            }
        }
}
