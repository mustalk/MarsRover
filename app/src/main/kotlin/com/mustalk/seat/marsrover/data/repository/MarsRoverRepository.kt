package com.mustalk.seat.marsrover.data.repository

import com.mustalk.seat.marsrover.core.utils.NetworkResult
import com.mustalk.seat.marsrover.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.data.network.model.MissionResponse

/**
 * Repository interface for Mars rover operations.
 * Abstracts the data source for mission execution.
 */
interface MarsRoverRepository {
    /**
     * Execute a rover mission through the network API.
     *
     * @param missionInput The mission parameters
     * @return NetworkResult containing the mission response
     */
    suspend fun executeMission(missionInput: MarsRoverInput): NetworkResult<MissionResponse>
}
