package com.mustalk.seat.marsrover.data.network.api

import com.mustalk.seat.marsrover.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.data.network.model.MissionResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API service interface for Mars Rover mission operations.
 *
 * Defines the endpoints for executing rover missions through a simulated network API.
 * In a real application, this would connect to a Mars mission control server.
 */
interface MarsRoverApiService {
    /**
     * Execute a Mars rover mission by sending mission parameters to the server.
     *
     * @param missionInput The mission parameters including plateau size, rover position,
     *                     direction, and movement commands
     * @return MissionResponse containing the final rover position and execution details
     */
    @POST("api/v1/mars-rover/execute")
    suspend fun executeMission(
        @Body missionInput: MarsRoverInput,
    ): MissionResponse
}
