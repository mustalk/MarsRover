package com.mustalk.seat.marsrover.domain.validator

import com.mustalk.seat.marsrover.data.model.input.MarsRoverInput
import com.mustalk.seat.marsrover.data.model.input.RoverPosition
import com.mustalk.seat.marsrover.data.model.input.TopRightCorner
import com.mustalk.seat.marsrover.domain.error.RoverError
import com.mustalk.seat.marsrover.domain.model.Direction
import com.mustalk.seat.marsrover.domain.model.Plateau
import com.mustalk.seat.marsrover.domain.model.Position
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InputValidatorImplTest {
    private lateinit var inputValidator: InputValidatorImpl

    @Before
    fun setUp() {
        inputValidator = InputValidatorImpl()
    }

    @Test
    fun `should create valid plateau from input`() {
        val input =
            MarsRoverInput(
                topRightCorner = TopRightCorner(5, 5),
                roverPosition = RoverPosition(1, 2),
                roverDirection = "N",
                movements = "LM"
            )

        val plateau = inputValidator.validateAndCreatePlateau(input)

        assertEquals(5, plateau.maxX)
        assertEquals(5, plateau.maxY)
    }

    @Test(expected = RoverError.InvalidPlateauDimensions::class)
    fun `should throw InvalidPlateauDimensions for negative X`() {
        val input =
            MarsRoverInput(
                topRightCorner = TopRightCorner(-1, 5),
                roverPosition = RoverPosition(1, 2),
                roverDirection = "N",
                movements = "LM"
            )

        inputValidator.validateAndCreatePlateau(input)
    }

    @Test(expected = RoverError.InvalidPlateauDimensions::class)
    fun `should throw InvalidPlateauDimensions for negative Y`() {
        val input =
            MarsRoverInput(
                topRightCorner = TopRightCorner(5, -1),
                roverPosition = RoverPosition(1, 2),
                roverDirection = "N",
                movements = "LM"
            )

        inputValidator.validateAndCreatePlateau(input)
    }

    @Test
    fun `should parse valid direction characters`() {
        assertEquals(Direction.NORTH, inputValidator.validateAndParseDirection("N"))
        assertEquals(Direction.EAST, inputValidator.validateAndParseDirection("E"))
        assertEquals(Direction.SOUTH, inputValidator.validateAndParseDirection("S"))
        assertEquals(Direction.WEST, inputValidator.validateAndParseDirection("W"))
    }

    @Test
    fun `should handle lowercase direction characters`() {
        assertEquals(Direction.NORTH, inputValidator.validateAndParseDirection("n"))
        assertEquals(Direction.EAST, inputValidator.validateAndParseDirection("e"))
        assertEquals(Direction.SOUTH, inputValidator.validateAndParseDirection("s"))
        assertEquals(Direction.WEST, inputValidator.validateAndParseDirection("w"))
    }

    @Test(expected = RoverError.InvalidDirectionChar::class)
    fun `should throw InvalidDirectionChar for invalid character`() {
        inputValidator.validateAndParseDirection("X")
    }

    @Test(expected = RoverError.InvalidDirectionChar::class)
    fun `should throw InvalidDirectionChar for multi-character string`() {
        inputValidator.validateAndParseDirection("NE")
    }

    @Test(expected = RoverError.InvalidDirectionChar::class)
    fun `should throw InvalidDirectionChar for empty string`() {
        inputValidator.validateAndParseDirection("")
    }

    @Test
    fun `should validate position within plateau bounds`() {
        val plateau = Plateau(5, 5)
        val position = Position(2, 3)

        // Should not throw exception
        inputValidator.validateInitialPosition(position, plateau)
    }

    @Test
    fun `should validate position at plateau boundaries`() {
        val plateau = Plateau(5, 5)

        // Test corner positions
        inputValidator.validateInitialPosition(Position(0, 0), plateau)
        inputValidator.validateInitialPosition(Position(5, 5), plateau)
        inputValidator.validateInitialPosition(Position(0, 5), plateau)
        inputValidator.validateInitialPosition(Position(5, 0), plateau)
    }

    @Test(expected = RoverError.InvalidInitialPosition::class)
    fun `should throw InvalidInitialPosition for X outside bounds`() {
        val plateau = Plateau(5, 5)
        val position = Position(6, 3)

        inputValidator.validateInitialPosition(position, plateau)
    }

    @Test(expected = RoverError.InvalidInitialPosition::class)
    fun `should throw InvalidInitialPosition for Y outside bounds`() {
        val plateau = Plateau(5, 5)
        val position = Position(3, 6)

        inputValidator.validateInitialPosition(position, plateau)
    }

    @Test(expected = RoverError.InvalidInitialPosition::class)
    fun `should throw InvalidInitialPosition for negative coordinates`() {
        val plateau = Plateau(5, 5)
        val position = Position(-1, 3)

        inputValidator.validateInitialPosition(position, plateau)
    }
}
