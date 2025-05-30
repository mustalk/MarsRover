package com.mustalk.seat.marsrover.domain.usecase

import com.mustalk.seat.marsrover.core.utils.NetworkResult
import com.mustalk.seat.marsrover.core.utils.exceptions.JsonParsingException
import com.mustalk.seat.marsrover.core.utils.exceptions.MissionExecutionException
import com.mustalk.seat.marsrover.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.data.model.input.RoverPosition
import com.mustalk.seat.marsrover.data.model.input.TopRightCorner
import com.mustalk.seat.marsrover.data.repository.MarsRoverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for executing Mars rover missions through the network API.
 *
 * This use case coordinates network-based mission execution, handling the conversion
 * between UI state and network DTOs, and managing the network result states.
 */
@Singleton
class ExecuteNetworkMissionUseCase
    @Inject
    constructor(
        private val repository: MarsRoverRepository,
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
                    // Parse JSON to domain input model
                    val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                    val missionInput = json.decodeFromString<MarsRoverInput>(jsonInput)

                    // Execute mission through repository
                    val result = repository.executeMission(missionInput)

                    // Map result to final position string
                    val mappedResult =
                        result.map { response ->
                            if (response.success) {
                                response.finalPosition
                            } else {
                                throw MissionExecutionException(response.message)
                            }
                        }

                    emit(mappedResult)
                } catch (e: kotlinx.serialization.SerializationException) {
                    emit(NetworkResult.error(JsonParsingException("Failed to parse JSON input", e), "Invalid JSON format"))
                } catch (e: JsonParsingException) {
                    emit(NetworkResult.error(e, "Failed to parse mission input: ${e.message}"))
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
                    // Create mission input from individual parameters
                    val missionInput =
                        MarsRoverInput(
                            topRightCorner = TopRightCorner(plateauWidth, plateauHeight),
                            roverPosition = RoverPosition(roverStartX, roverStartY),
                            roverDirection = roverDirection,
                            movements = movements
                        )

                    // Execute mission through repository
                    val result = repository.executeMission(missionInput)

                    // Map result to final position string
                    val mappedResult =
                        result.map { response ->
                            if (response.success) {
                                response.finalPosition
                            } else {
                                throw MissionExecutionException(response.message)
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
