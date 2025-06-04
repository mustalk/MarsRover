package com.mustalk.seat.marsrover.core.data.repository

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.core.common.network.NetworkResult
import com.mustalk.seat.marsrover.core.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.core.data.model.input.RoverPosition
import com.mustalk.seat.marsrover.core.data.model.input.TopRightCorner
import com.mustalk.seat.marsrover.core.data.network.api.MarsRoverApiService
import com.mustalk.seat.marsrover.core.data.network.model.ErrorDetails
import com.mustalk.seat.marsrover.core.data.network.model.MissionResponse
import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions
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
 * Tests network operations and error handling for mission execution with domain models.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MarsRoverRepositoryImplTest {
    private lateinit var repository: MarsRoverRepositoryImpl
    private lateinit var mockApiService: MarsRoverApiService

    private val sampleMissionInstructions =
        RoverMissionInstructions(
            plateauTopRightX = 5,
            plateauTopRightY = 5,
            initialRoverPosition = Position(1, 2),
            initialRoverDirection = "N",
            movementCommands = "LMLMLMLMM"
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

            val expectedDto =
                MarsRoverInput(
                    topRightCorner = TopRightCorner(5, 5),
                    roverPosition = RoverPosition(1, 2),
                    roverDirection = "N",
                    movements = "LMLMLMLMM"
                )

            coEvery { mockApiService.executeMission(expectedDto) } returns expectedResponse

            // When
            val result = repository.executeMission(sampleMissionInstructions)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Success::class.java)
            val successResult = result as NetworkResult.Success
            assertThat(successResult.data.success).isTrue()
            assertThat(successResult.data.finalPosition).isEqualTo("1 3 N")
            assertThat(successResult.data.message).isEqualTo("Mission completed successfully")

            coVerify { mockApiService.executeMission(expectedDto) }
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

            coEvery { mockApiService.executeMission(any()) } throws httpException

            // When
            val result = repository.executeMission(sampleMissionInstructions)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains("HTTP 400 Response.error()")
        }

    @Test
    fun `executeMission should return error when API call fails with ConnectException`() =
        runTest {
            // Given
            coEvery { mockApiService.executeMission(any()) } throws ConnectException("Connection refused")

            // When
            val result = repository.executeMission(sampleMissionInstructions)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains("Connection refused")
        }

    @Test
    fun `executeMission should return error when API call fails with SocketTimeoutException`() =
        runTest {
            // Given
            coEvery { mockApiService.executeMission(any()) } throws SocketTimeoutException("Timeout")

            // When
            val result = repository.executeMission(sampleMissionInstructions)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains("Timeout")
        }

    @Test
    fun `executeMission should return error when API call fails with generic exception`() =
        runTest {
            // Given
            coEvery { mockApiService.executeMission(any()) } throws RuntimeException("Unexpected error")

            // When
            val result = repository.executeMission(sampleMissionInstructions)

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

            coEvery { mockApiService.executeMission(any()) } returns failureResponse

            // When
            val result = repository.executeMission(sampleMissionInstructions)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Success::class.java)
            val successResult = result as NetworkResult.Success
            assertThat(successResult.data.success).isFalse()
            assertThat(successResult.data.message).isEqualTo("Mission execution failed")
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

            coEvery { mockApiService.executeMission(any()) } throws httpException

            // When
            val result = repository.executeMission(sampleMissionInstructions)

            // Then
            assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
            val errorResult = result as NetworkResult.Error
            assertThat(errorResult.message).contains("HTTP 500 Response.error()")
        }

    @Test
    fun `repository should correctly pass all mission input parameters to API service`() =
        runTest {
            // Given
            val complexInstructions =
                RoverMissionInstructions(
                    plateauTopRightX = 7,
                    plateauTopRightY = 9,
                    initialRoverPosition = Position(3, 4),
                    initialRoverDirection = "S",
                    movementCommands = "LMLMLMLMRMRMR"
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

            coEvery { mockApiService.executeMission(any()) } returns response

            // When
            repository.executeMission(complexInstructions)

            // Then - Verify the correct DTO was passed to the API
            coVerify {
                mockApiService.executeMission(
                    match<MarsRoverInput> { dto ->
                        dto.topRightCorner.x == 7 &&
                            dto.topRightCorner.y == 9 &&
                            dto.roverPosition.x == 3 &&
                            dto.roverPosition.y == 4 &&
                            dto.roverDirection == "S" &&
                            dto.movements == "LMLMLMLMRMRMR"
                    }
                )
            }
        }
}
