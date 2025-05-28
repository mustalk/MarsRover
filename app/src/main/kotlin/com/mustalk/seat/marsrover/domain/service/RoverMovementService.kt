package com.mustalk.seat.marsrover.domain.service

import com.mustalk.seat.marsrover.domain.model.Plateau
import com.mustalk.seat.marsrover.domain.model.Rover

/**
 * Interface for handling rover movement operations.
 */
interface RoverMovementService {
    /**
     * Executes movement commands on a rover within plateau bounds.
     *
     * @param rover The rover to move.
     * @param plateau The plateau bounds.
     * @param movements The movement command string.
     */
    fun executeMovements(
        rover: Rover,
        plateau: Plateau,
        movements: String,
    )
}
