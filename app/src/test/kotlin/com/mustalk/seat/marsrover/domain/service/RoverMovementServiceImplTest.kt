package com.mustalk.seat.marsrover.domain.service

import com.mustalk.seat.marsrover.core.model.Direction
import com.mustalk.seat.marsrover.core.model.Plateau
import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.model.Rover
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RoverMovementServiceImplTest {
    private lateinit var movementService: RoverMovementServiceImpl

    @Before
    fun setUp() {
        movementService = RoverMovementServiceImpl()
    }

    @Test
    fun `should turn rover left correctly`() {
        val plateau = Plateau(5, 5)
        val rover = Rover(Position(2, 2), Direction.NORTH)

        movementService.executeMovements(rover, plateau, "L")

        assertEquals(Position(2, 2), rover.position)
        assertEquals(Direction.WEST, rover.direction)
    }

    @Test
    fun `should turn rover right correctly`() {
        val plateau = Plateau(5, 5)
        val rover = Rover(Position(2, 2), Direction.NORTH)

        movementService.executeMovements(rover, plateau, "R")

        assertEquals(Position(2, 2), rover.position)
        assertEquals(Direction.EAST, rover.direction)
    }

    @Test
    fun `should move rover forward within bounds`() {
        val plateau = Plateau(5, 5)
        val rover = Rover(Position(2, 2), Direction.NORTH)

        movementService.executeMovements(rover, plateau, "M")

        assertEquals(Position(2, 3), rover.position)
        assertEquals(Direction.NORTH, rover.direction)
    }

    @Test
    fun `should not move rover beyond plateau boundary`() {
        val plateau = Plateau(5, 5)
        val rover = Rover(Position(5, 5), Direction.NORTH)

        movementService.executeMovements(rover, plateau, "M")

        // Should stay at same position
        assertEquals(Position(5, 5), rover.position)
        assertEquals(Direction.NORTH, rover.direction)
    }

    @Test
    fun `should handle complex movement sequence`() {
        val plateau = Plateau(5, 5)
        val rover = Rover(Position(1, 2), Direction.NORTH)

        movementService.executeMovements(rover, plateau, "LMLMLMLMM")

        assertEquals(Position(1, 3), rover.position)
        assertEquals(Direction.NORTH, rover.direction)
    }

    @Test
    fun `should ignore invalid movement characters`() {
        val plateau = Plateau(5, 5)
        val rover = Rover(Position(2, 2), Direction.NORTH)

        movementService.executeMovements(rover, plateau, "MXL1R@M")

        // Should execute: M (move to 2,3), L (turn to W), R (turn to N), M (move to 2,4)
        assertEquals(Position(2, 4), rover.position)
        assertEquals(Direction.NORTH, rover.direction)
    }

    @Test
    fun `should handle empty movement string`() {
        val plateau = Plateau(5, 5)
        val rover = Rover(Position(2, 2), Direction.EAST)

        movementService.executeMovements(rover, plateau, "")

        // Should not change anything
        assertEquals(Position(2, 2), rover.position)
        assertEquals(Direction.EAST, rover.direction)
    }

    @Test
    fun `should handle movement in all directions`() {
        val plateau = Plateau(5, 5)

        // Test North movement
        val roverNorth = Rover(Position(2, 2), Direction.NORTH)
        movementService.executeMovements(roverNorth, plateau, "M")
        assertEquals(Position(2, 3), roverNorth.position)

        // Test East movement
        val roverEast = Rover(Position(2, 2), Direction.EAST)
        movementService.executeMovements(roverEast, plateau, "M")
        assertEquals(Position(3, 2), roverEast.position)

        // Test South movement
        val roverSouth = Rover(Position(2, 2), Direction.SOUTH)
        movementService.executeMovements(roverSouth, plateau, "M")
        assertEquals(Position(2, 1), roverSouth.position)

        // Test West movement
        val roverWest = Rover(Position(2, 2), Direction.WEST)
        movementService.executeMovements(roverWest, plateau, "M")
        assertEquals(Position(1, 2), roverWest.position)
    }

    @Test
    fun `should handle boundary conditions for all edges`() {
        val plateau = Plateau(2, 2)

        val testCases =
            listOf(
                // West edge
                Triple(Position(0, 0), Direction.WEST, Position(0, 0)),
                // South edge
                Triple(Position(0, 0), Direction.SOUTH, Position(0, 0)),
                // East edge
                Triple(Position(2, 2), Direction.EAST, Position(2, 2)),
                // North edge
                Triple(Position(2, 2), Direction.NORTH, Position(2, 2))
            )

        testCases.forEach { (startPos, direction, expectedPos) ->
            val rover = Rover(startPos, direction)
            movementService.executeMovements(rover, plateau, "M")
            assertEquals("Failed for $direction movement from $startPos", expectedPos, rover.position)
        }
    }
}
