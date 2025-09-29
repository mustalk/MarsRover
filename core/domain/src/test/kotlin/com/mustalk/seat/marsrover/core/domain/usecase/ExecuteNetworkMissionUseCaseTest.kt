package com.mustalk.seat.marsrover.core.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.core.common.exceptions.JsonParsingException
import com.mustalk.seat.marsrover.core.common.network.NetworkResult
import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.domain.repository.MarsRoverRepository
import com.mustalk.seat.marsrover.core.model.MissionResult
import com.mustalk.seat.marsrover.core.testing.jvm.data.DomainTestData.UseCaseTestData
import com.mustalk.seat.marsrover.core.testing.jvm.data.MarsRoverTestData.RepositoryTestData
import com.mustalk.seat.marsrover.core.testing.jvm.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for ExecuteNetworkMissionUseCase.
 * Tests both JSON and individual input execution methods with Flow operations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExecuteNetworkMissionUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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
            val testData = UseCaseTestData.SuccessfulExecution.STANDARD_MISSION
            val instructions = testData.EXPECTED_RESULT

            val expectedMissionResult =
                MissionResult(
                    success = true,
                    finalPosition = RepositoryTestData.StandardMission.FINAL_POSITION,
                    message = RepositoryTestData.StandardMission.SUCCESS_MESSAGE
                )

            coEvery { jsonParser.parseInput(testData.JSON) } returns instructions
            coEvery { mockRepository.executeMission(instructions) } returns NetworkResult.success(expectedMissionResult)

            // When
            val results = useCase.executeFromJson(testData.JSON).toList()

            // Then
            assertThat(results).hasSize(2)
            assertThat(results[0]).isInstanceOf(NetworkResult.Loading::class.java)
            assertThat(results[1]).isInstanceOf(NetworkResult.Success::class.java)

            val successResult = results[1] as NetworkResult.Success
            assertThat(successResult.data).isEqualTo(RepositoryTestData.StandardMission.FINAL_POSITION)

            coVerify { mockRepository.executeMission(instructions) }
        }

    @Test
    fun `executeFromJson should handle JSON parsing error`() =
        runTest {
            // Given
            val invalidJson = UseCaseTestData.ErrorScenarios.INVALID_JSON
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
            val testData = RepositoryTestData.StandardMission
            val instructions = testData.INPUT

            val successMissionResult =
                MissionResult(
                    success = true,
                    finalPosition = testData.FINAL_POSITION,
                    message = testData.SUCCESS_MESSAGE
                )

            coEvery { mockRepository.executeMission(instructions) } returns NetworkResult.success(successMissionResult)

            // When
            val results = mutableListOf<NetworkResult<String>>()
            useCase
                .executeFromBuilderInputs(
                    plateauWidth = testData.TOP_RIGHT_X,
                    plateauHeight = testData.TOP_RIGHT_Y,
                    roverStartX = testData.ROVER_START_X,
                    roverStartY = testData.ROVER_START_Y,
                    roverDirection = testData.ROVER_START_DIRECTION,
                    movements = testData.ROVER_MOVEMENTS
                ).collect { results.add(it) }

            // Then
            assertThat(results).hasSize(2)
            assertThat(results[0]).isInstanceOf(NetworkResult.Loading::class.java)
            assertThat(results[1]).isInstanceOf(NetworkResult.Success::class.java)
            assertThat((results[1] as NetworkResult.Success).data).isEqualTo(testData.FINAL_POSITION)
        }

    @Test
    fun `executeFromBuilderInputs should handle repository error`() =
        runTest {
            // Given
            coEvery { mockRepository.executeMission(any()) } returns
                NetworkResult.error(
                    RuntimeException(RepositoryTestData.NetworkErrors.CONNECTION_REFUSED),
                    RepositoryTestData.NetworkErrors.CONNECTION_REFUSED
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
            assertThat(errorResult.message).isEqualTo(RepositoryTestData.NetworkErrors.CONNECTION_REFUSED)
        }

    @Test
    fun `executeFromJson should handle mission failure response`() =
        runTest {
            // Given
            val testData = UseCaseTestData.ErrorScenarios.OutOfBoundsPosition
            val instructions = testData.INSTRUCTIONS

            val failureMissionResult =
                MissionResult(
                    success = false,
                    finalPosition = "",
                    message = RepositoryTestData.ErrorCases.OUT_OF_BOUNDS_DETAILS
                )

            coEvery { jsonParser.parseInput(testData.JSON) } returns instructions
            coEvery { mockRepository.executeMission(instructions) } returns NetworkResult.success(failureMissionResult)

            // When
            val results = useCase.executeFromJson(testData.JSON).toList()

            // Then
            assertThat(results).hasSize(2)
            assertThat(results[1]).isInstanceOf(NetworkResult.Error::class.java)

            val errorResult = results[1] as NetworkResult.Error
            assertThat(errorResult.message).contains(RepositoryTestData.ErrorCases.OUT_OF_BOUNDS_DETAILS)
        }

    @Test
    fun `executeFromJson should emit loading then success result for simple movement`() =
        runTest {
            // Given
            val testData = UseCaseTestData.SuccessfulExecution.SimpleMove
            val instructions = testData.INSTRUCTIONS

            val expectedMissionResult =
                MissionResult(
                    success = true,
                    finalPosition = testData.EXPECTED_POSITION,
                    message = RepositoryTestData.StandardMission.SUCCESS_MESSAGE
                )

            coEvery { jsonParser.parseInput(testData.JSON) } returns instructions
            coEvery { mockRepository.executeMission(instructions) } returns NetworkResult.success(expectedMissionResult)

            // When
            val results = useCase.executeFromJson(testData.JSON).toList()

            // Then
            assertThat(results).hasSize(2)
            assertThat(results[0]).isInstanceOf(NetworkResult.Loading::class.java)
            assertThat(results[1]).isInstanceOf(NetworkResult.Success::class.java)

            val successResult = results[1] as NetworkResult.Success
            assertThat(successResult.data).isEqualTo(testData.EXPECTED_POSITION)

            coVerify { mockRepository.executeMission(instructions) }
        }
}
