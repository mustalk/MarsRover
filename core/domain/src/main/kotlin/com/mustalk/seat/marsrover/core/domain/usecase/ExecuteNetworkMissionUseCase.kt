package com.mustalk.seat.marsrover.core.domain.usecase

import com.mustalk.seat.marsrover.core.common.exceptions.JsonParsingException
import com.mustalk.seat.marsrover.core.common.exceptions.MissionExecutionException
import com.mustalk.seat.marsrover.core.common.network.NetworkResult
import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.domain.repository.MarsRoverRepository
import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case for executing Mars rover missions through the network API.
 *
 * This use case coordinates network-based mission execution, handling the conversion
 * between UI parameters and domain models, and managing the network result states.
 *
 * Note: This implementation uses plain constructor (no @Inject) as it will be managed by Hilt
 * modules in the app layer when this core:domain module is a pure Kotlin module.
 */
class ExecuteNetworkMissionUseCase(
    private val repository: MarsRoverRepository,
    private val jsonParser: JsonParser,
) {
    /**
     * Execute a rover mission using JSON input through the network API.
     *
     * @param jsonInput The JSON string containing mission parameters
     * @return Flow of NetworkResult states representing the mission execution progress
     */
    suspend fun executeFromJson(jsonInput: String): Flow<NetworkResult<String>> =
        flow {
            emit(NetworkResult.loading())

            try {
                // Parse JSON to domain model
                val instructions = jsonParser.parseInput(jsonInput)

                // Execute mission through repository
                val result = repository.executeMission(instructions)

                // Map result to final position string
                val mappedResult =
                    result.map { missionResult ->
                        if (missionResult.success) {
                            missionResult.finalPosition
                        } else {
                            throw MissionExecutionException(missionResult.message)
                        }
                    }

                emit(mappedResult)
            } catch (e: JsonParsingException) {
                emit(NetworkResult.error(e, "Failed to parse mission input: ${e.message ?: "Invalid JSON format"}"))
            } catch (e: MissionExecutionException) {
                emit(NetworkResult.error(e, "Failed to execute mission: ${e.message}"))
            } catch (e: IllegalArgumentException) {
                emit(NetworkResult.error(JsonParsingException("Invalid JSON structure", e), "Invalid input format"))
            }
        }

    /**
     * Execute a rover mission using builder parameters through the network API.
     *
     * @param plateauWidth Width of the plateau
     * @param plateauHeight Height of the plateau
     * @param roverStartX Initial rover X position
     * @param roverStartY Initial rover Y position
     * @param roverDirection Initial rover direction (N, E, S, W)
     * @param movements Movement commands string (L, R, M)
     * @return Flow of NetworkResult states representing the mission execution progress
     */
    suspend fun executeFromBuilderInputs(
        plateauWidth: Int,
        plateauHeight: Int,
        roverStartX: Int,
        roverStartY: Int,
        roverDirection: String,
        movements: String,
    ): Flow<NetworkResult<String>> =
        flow {
            emit(NetworkResult.loading())

            try {
                // Create domain model from individual parameters
                val instructions =
                    RoverMissionInstructions(
                        plateauTopRightX = plateauWidth,
                        plateauTopRightY = plateauHeight,
                        initialRoverPosition = Position(roverStartX, roverStartY),
                        initialRoverDirection = roverDirection,
                        movementCommands = movements
                    )

                // Execute mission through repository
                val result = repository.executeMission(instructions)

                // Map result to final position string
                val mappedResult =
                    result.map { missionResult ->
                        if (missionResult.success) {
                            missionResult.finalPosition
                        } else {
                            throw MissionExecutionException(missionResult.message)
                        }
                    }

                emit(mappedResult)
            } catch (e: MissionExecutionException) {
                emit(NetworkResult.error(e, "Failed to execute mission: ${e.message}"))
            } catch (e: IllegalArgumentException) {
                emit(NetworkResult.error(MissionExecutionException("Invalid mission parameters", e), "Invalid input parameters"))
            }
        }
}
