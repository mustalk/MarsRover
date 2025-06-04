package com.mustalk.seat.marsrover.core.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.core.common.exceptions.JsonParsingException
import com.mustalk.seat.marsrover.core.common.network.NetworkResult
import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.domain.repository.MarsRoverRepository
import com.mustalk.seat.marsrover.core.model.MissionResult
import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ExecuteNetworkMissionUseCase.
 * Tests both JSON and individual input execution methods with Flow operations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExecuteNetworkMissionUseCaseTest {
    private lateinit var useCase: ExecuteNetworkMissionUseCase
    private lateinit var jsonParser: JsonParser
    private lateinit var mockRepository: MarsRoverRepository

    @Before
    fun setup() {
        mockRepository = mockk()
        jsonParser = mockk()
        useCase = ExecuteNetworkMissionUseCase(mockRepository, jsonParser)
    }

    @Test
    fun `executeFromJson should emit loading then success result`() =
        runTest {
            // Given
            val jsonInput =
                """
                {
                    "topRightCorner": {"x": 5, "y": 5},
                    "roverPosition": {"x": 1, "y": 2},
                    "roverDirection": "N",
                    "movements": "LMLMLMLMM"
                }
                """.trimIndent()

            val instructions =
                RoverMissionInstructions(
                    plateauTopRightX = 5,
                    plateauTopRightY = 5,
                    initialRoverPosition = Position(1, 2),
                    initialRoverDirection = "N",
                    movementCommands = "LMLMLMLMM"
                )

            val expectedMissionResult =
                MissionResult(
                    success = true,
                    finalPosition = "1 3 N",
                    message = "Mission completed successfully"
                )

            coEvery { jsonParser.parseInput(jsonInput) } returns instructions
            coEvery { mockRepository.executeMission(instructions) } returns NetworkResult.success(expectedMissionResult)

            // When
            val results = useCase.executeFromJson(jsonInput).toList()

            // Then
            assertThat(results).hasSize(2)
            assertThat(results[0]).isInstanceOf(NetworkResult.Loading::class.java)
            assertThat(results[1]).isInstanceOf(NetworkResult.Success::class.java)

            val successResult = results[1] as NetworkResult.Success
            assertThat(successResult.data).isEqualTo("1 3 N")

            coVerify { mockRepository.executeMission(instructions) }
        }

    @Test
    fun `executeFromJson should handle JSON parsing error`() =
        runTest {
            // Given
            val invalidJson = """{"invalid": json}"""
            coEvery { jsonParser.parseInput(invalidJson) } throws JsonParsingException("Invalid JSON")

            // When
            val results = useCase.executeFromJson(invalidJson).toList()

            // Then
            assertThat(results).hasSize(2)
            assertThat(results[0]).isInstanceOf(NetworkResult.Loading::class.java)
            assertThat(results[1]).isInstanceOf(NetworkResult.Error::class.java)

            val errorResult = results[1] as NetworkResult.Error
            assertThat(errorResult.message).contains("Failed to parse mission input: Invalid JSON")
        }

    @Test
    fun `executeFromBuilderInputs should emit loading then success result`() =
        runTest {
            // Given
            val instructions =
                RoverMissionInstructions(
                    plateauTopRightX = 5,
                    plateauTopRightY = 5,
                    initialRoverPosition = Position(1, 2),
                    initialRoverDirection = "N",
                    movementCommands = "LMLMLMLMM"
                )

            val successMissionResult =
                MissionResult(
                    success = true,
                    finalPosition = "1 3 N",
                    message = "Mission completed successfully"
                )

            coEvery { mockRepository.executeMission(instructions) } returns NetworkResult.success(successMissionResult)

            // When
            val results = mutableListOf<NetworkResult<String>>()
            useCase
                .executeFromBuilderInputs(
                    plateauWidth = 5,
                    plateauHeight = 5,
                    roverStartX = 1,
                    roverStartY = 2,
                    roverDirection = "N",
                    movements = "LMLMLMLMM"
                ).collect { results.add(it) }

            // Then
            assertThat(results).hasSize(2)
            assertThat(results[0]).isInstanceOf(NetworkResult.Loading::class.java)
            assertThat(results[1]).isInstanceOf(NetworkResult.Success::class.java)
            assertThat((results[1] as NetworkResult.Success).data).isEqualTo("1 3 N")
        }

    @Test
    fun `executeFromBuilderInputs should handle repository error`() =
        runTest {
            // Given
            coEvery { mockRepository.executeMission(any()) } returns
                NetworkResult.error(
                    RuntimeException("Network error"),
                    "Network connection failed"
                )

            // When
            val results = mutableListOf<NetworkResult<String>>()
            useCase
                .executeFromBuilderInputs(
                    plateauWidth = 3,
                    plateauHeight = 3,
                    roverStartX = 0,
                    roverStartY = 0,
                    roverDirection = "N",
                    movements = "M"
                ).collect { results.add(it) }

            // Then
            assertThat(results).hasSize(2)
            assertThat(results[0]).isInstanceOf(NetworkResult.Loading::class.java)
            assertThat(results[1]).isInstanceOf(NetworkResult.Error::class.java)

            val errorResult = results[1] as NetworkResult.Error
            assertThat(errorResult.message).isEqualTo("Network connection failed")
        }

    @Test
    fun `executeFromJson should handle mission failure response`() =
        runTest {
            // Given
            val jsonInput =
                """
                {
                    "topRightCorner": {"x": 2, "y": 2},
                    "roverPosition": {"x": 5, "y": 5},
                    "roverDirection": "N",
                    "movements": "M"
                }
                """.trimIndent()

            val instructions =
                RoverMissionInstructions(
                    plateauTopRightX = 2,
                    plateauTopRightY = 2,
                    initialRoverPosition = Position(5, 5),
                    initialRoverDirection = "N",
                    movementCommands = "M"
                )

            val failureMissionResult =
                MissionResult(
                    success = false,
                    finalPosition = "",
                    message = "Rover position out of bounds"
                )

            coEvery { jsonParser.parseInput(jsonInput) } returns instructions
            coEvery { mockRepository.executeMission(instructions) } returns NetworkResult.success(failureMissionResult)

            // When
            val results = useCase.executeFromJson(jsonInput).toList()

            // Then
            assertThat(results).hasSize(2)
            assertThat(results[1]).isInstanceOf(NetworkResult.Error::class.java)

            val errorResult = results[1] as NetworkResult.Error
            assertThat(errorResult.message).contains("Rover position out of bounds")
        }
}
