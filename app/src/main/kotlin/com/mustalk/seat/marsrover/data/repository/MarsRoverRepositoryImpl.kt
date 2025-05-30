package com.mustalk.seat.marsrover.data.repository

import com.mustalk.seat.marsrover.core.utils.NetworkResult
import com.mustalk.seat.marsrover.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.data.network.api.MarsRoverApiService
import com.mustalk.seat.marsrover.data.network.model.MissionResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MarsRoverRepository that uses the network API.
 */
@Singleton
class MarsRoverRepositoryImpl
    @Inject
    constructor(
        private val apiService: MarsRoverApiService,
    ) : MarsRoverRepository {
        /**
         * Execute a rover mission through the network API.
         *
         * @param missionInput The mission parameters
         * @return NetworkResult containing the mission response
         */
        override suspend fun executeMission(missionInput: MarsRoverInput): NetworkResult<MissionResponse> =
            NetworkResult.safeCall {
                apiService.executeMission(missionInput)
            }
    }
