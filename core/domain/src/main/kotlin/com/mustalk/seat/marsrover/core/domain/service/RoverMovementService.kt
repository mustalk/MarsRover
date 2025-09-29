package com.mustalk.seat.marsrover.core.domain.service

import com.mustalk.seat.marsrover.core.model.Plateau
import com.mustalk.seat.marsrover.core.model.Rover

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
