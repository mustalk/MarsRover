package com.mustalk.seat.marsrover.core.domain.repository

import com.mustalk.seat.marsrover.core.model.RoverMissionInstructions

/**
 * Repository interface for Mars rover operations.
 * Abstracts the data source for mission execution using domain models.
 */
interface MarsRoverRepository {
    /**
     * Execute a rover mission through the network API.
     *
     * @param instructions The mission instructions as domain model
     * @return Result containing the mission response as a String or exception
     */
    suspend fun executeMission(instructions: RoverMissionInstructions): Result<String>
}
