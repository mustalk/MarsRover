package com.mustalk.seat.marsrover.core.domain.service

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.mustalk.seat.marsrover.core.model.Direction
import com.mustalk.seat.marsrover.core.model.Rover
import com.mustalk.seat.marsrover.core.testing.jvm.data.DomainTestData.MovementServiceTestData
import com.mustalk.seat.marsrover.core.testing.jvm.data.DomainTestData.TestConstants
import com.mustalk.seat.marsrover.core.testing.jvm.util.MainDispatcherRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RoverMovementServiceImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var movementService: RoverMovementServiceImpl

    @Before
    fun setUp() {
        movementService = RoverMovementServiceImpl()
    }

    @Test
    fun `should turn rover left correctly`() {
        val testData = MovementServiceTestData.Rotations.TurnLeft
        val rover = testData.INITIAL_ROVER.copy()
        val plateau = TestConstants.STANDARD_PLATEAU

        movementService.executeMovements(rover, plateau, testData.COMMAND)

        assertThat(rover.position).isEqualTo(testData.EXPECTED_POSITION)
        assertThat(rover.direction).isEqualTo(testData.EXPECTED_DIRECTION)
    }

    @Test
    fun `should turn rover right correctly`() {
        val testData = MovementServiceTestData.Rotations.TurnRight
        val rover = testData.INITIAL_ROVER.copy()
        val plateau = TestConstants.STANDARD_PLATEAU

        movementService.executeMovements(rover, plateau, testData.COMMAND)

        assertThat(rover.position).isEqualTo(testData.EXPECTED_POSITION)
        assertThat(rover.direction).isEqualTo(testData.EXPECTED_DIRECTION)
    }

    @Test
    fun `should move rover forward within bounds`() {
        val testData = MovementServiceTestData.BasicMovements.MoveNorth
        val rover = testData.INITIAL_ROVER.copy()
        val plateau = TestConstants.STANDARD_PLATEAU

        movementService.executeMovements(rover, plateau, testData.COMMAND)

        assertThat(rover.position).isEqualTo(testData.EXPECTED_POSITION)
        assertThat(rover.direction).isEqualTo(testData.EXPECTED_DIRECTION)
    }

    @Test
    fun `should not move rover beyond plateau boundary`() {
        val testData = MovementServiceTestData.BoundaryTests.HitNorthBoundary
        val rover = testData.INITIAL_ROVER.copy()
        val plateau = MovementServiceTestData.BoundaryTests.PLATEAU

        movementService.executeMovements(rover, plateau, testData.COMMAND)

        // Should stay at same position
        assertThat(rover.position).isEqualTo(testData.EXPECTED_POSITION)
        assertThat(rover.direction).isEqualTo(testData.EXPECTED_DIRECTION)
    }

    @Test
    fun `should handle complex movement sequence`() {
        val testData = MovementServiceTestData.ComplexMovements.StandardMission
        val rover = testData.INITIAL_ROVER.copy()

        movementService.executeMovements(rover, testData.PLATEAU, testData.COMMANDS)

        assertThat(rover.position).isEqualTo(testData.EXPECTED_POSITION)
        assertThat(rover.direction).isEqualTo(testData.EXPECTED_DIRECTION)
    }

    @Test
    fun `should ignore invalid movement characters`() {
        val plateau = TestConstants.STANDARD_PLATEAU
        val rover = Rover(TestConstants.POSITION_2_2, Direction.NORTH)

        movementService.executeMovements(rover, plateau, TestConstants.INVALID_COMMANDS)

        // Starting at (2,2) facing N:
        // M - move north to (2,3) facing N
        // X - invalid, ignore
        // L - turn left to face W
        // 1 - invalid, ignore
        // R - turn right to face N
        // @ - invalid, ignore
        // M - move north to (2,4) facing N
        assertThat(rover.position).isEqualTo(TestConstants.POSITION_2_4)
        assertThat(rover.direction).isEqualTo(Direction.NORTH)
    }

    @Test
    fun `should handle empty movement string`() {
        val plateau = TestConstants.STANDARD_PLATEAU
        val rover = Rover(TestConstants.POSITION_2_2, Direction.EAST)

        movementService.executeMovements(rover, plateau, TestConstants.EMPTY_COMMANDS)

        // Should not change anything
        assertThat(rover.position).isEqualTo(TestConstants.POSITION_2_2)
        assertThat(rover.direction).isEqualTo(Direction.EAST)
    }

    @Test
    fun `should handle movement in all directions`() {
        val plateau = TestConstants.STANDARD_PLATEAU

        // Test North movement
        val roverNorth = Rover(TestConstants.POSITION_2_2, Direction.NORTH)
        movementService.executeMovements(roverNorth, plateau, TestConstants.SINGLE_MOVE)
        assertThat(roverNorth.position).isEqualTo(TestConstants.POSITION_2_3)

        // Test East movement
        val roverEast = Rover(TestConstants.POSITION_2_2, Direction.EAST)
        movementService.executeMovements(roverEast, plateau, TestConstants.SINGLE_MOVE)
        assertThat(roverEast.position).isEqualTo(TestConstants.POSITION_3_2)

        // Test South movement
        val roverSouth = Rover(TestConstants.POSITION_2_2, Direction.SOUTH)
        movementService.executeMovements(roverSouth, plateau, TestConstants.SINGLE_MOVE)
        assertThat(roverSouth.position).isEqualTo(TestConstants.POSITION_2_1)

        // Test West movement
        val roverWest = Rover(TestConstants.POSITION_2_2, Direction.WEST)
        movementService.executeMovements(roverWest, plateau, TestConstants.SINGLE_MOVE)
        assertThat(roverWest.position).isEqualTo(TestConstants.POSITION_1_2)
    }

    @Test
    fun `should handle boundary conditions for all edges`() {
        val plateau = TestConstants.SMALL_PLATEAU

        val testCases =
            listOf(
                // West edge
                // Starting at (0,0) facing W on a 3x3 plateau:
                // M - can't move west (out of bounds), stay at (0,0) facing W
                Triple(TestConstants.POSITION_0_0, Direction.WEST, TestConstants.POSITION_0_0),
                // South edge
                // Starting at (0,0) facing S on a 3x3 plateau:
                // M - can't move south (out of bounds), stay at (0,0) facing S
                Triple(TestConstants.POSITION_0_0, Direction.SOUTH, TestConstants.POSITION_0_0),
                // East edge
                // Starting at (2,2) facing E on a 2x2 plateau:
                // M - can't move east (out of bounds), stay at (2,2) facing E
                Triple(TestConstants.POSITION_2_2, Direction.EAST, TestConstants.POSITION_2_2),
                // North edge
                // Starting at (2,2) facing N on a 2x2 plateau:
                // M - can't move north (out of bounds), stay at (2,2) facing N
                Triple(TestConstants.POSITION_2_2, Direction.NORTH, TestConstants.POSITION_2_2)
            )

        testCases.forEach { (startPos, direction, expectedPos) ->
            val rover = Rover(startPos, direction)
            movementService.executeMovements(rover, plateau, TestConstants.SINGLE_MOVE)
            assertWithMessage("Failed for $direction movement from $startPos")
                .that(rover.position)
                .isEqualTo(expectedPos)
        }
    }
}
