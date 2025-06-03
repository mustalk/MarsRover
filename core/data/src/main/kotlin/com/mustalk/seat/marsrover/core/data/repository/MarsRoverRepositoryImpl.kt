package com.mustalk.seat.marsrover.core.data.repository

import com.mustalk.seat.marsrover.core.common.network.NetworkResult
import com.mustalk.seat.marsrover.core.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.core.data.model.input.RoverPosition
import com.mustalk.seat.marsrover.core.data.model.input.TopRightCorner
import com.mustalk.seat.marsrover.core.data.network.api.MarsRoverApiService
import com.mustalk.seat.marsrover.core.domain.repository.MarsRoverRepository
import com.mustalk.seat.marsrover.core.model.MissionResult
import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MarsRoverRepository that uses the network API.
 * This implementation bridges the domain layer (RoverMissionInstructions) with the data layer (DTOs).
 */
@Singleton
class MarsRoverRepositoryImpl
    @Inject
    constructor(
        private val apiService: MarsRoverApiService,
    ) : MarsRoverRepository {
        /**
         * Execute a rover mission through the network API.
         * Maps domain model to DTO, makes network call, and maps response to domain model.
         *
         * @param instructions The mission instructions as domain model
         * @return NetworkResult containing the mission result as domain model
         */
        override suspend fun executeMission(instructions: RoverMissionInstructions): NetworkResult<MissionResult> =
            NetworkResult.safeCall {
                // Map domain model to DTO for network call
                val dto = mapToMarsRoverInput(instructions)

                // Make network call with DTO
                val response = apiService.executeMission(dto)

                // Return final position from response
                // Map DTO response to domain model
                MissionResult(
                    success = response.success,
                    finalPosition = response.finalPosition,
                    message = response.message
                )
            }

        /**
         * Maps RoverMissionInstructions (domain model) to MarsRoverInput (DTO).
         */
        private fun mapToMarsRoverInput(instructions: RoverMissionInstructions): MarsRoverInput =
            MarsRoverInput(
                topRightCorner =
                    TopRightCorner(
                        x = instructions.plateauTopRightX,
                        y = instructions.plateauTopRightY
                    ),
                roverPosition =
                    RoverPosition(
                        x = instructions.initialRoverPosition.x,
                        y = instructions.initialRoverPosition.y
                    ),
                roverDirection = instructions.initialRoverDirection,
                movements = instructions.movementCommands
            )
    }
