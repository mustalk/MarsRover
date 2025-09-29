package com.mustalk.seat.marsrover.core.domain.validator

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.core.model.Direction
import com.mustalk.seat.marsrover.core.model.Position
import com.mustalk.seat.marsrover.core.testing.jvm.data.DomainTestData.TestConstants
import com.mustalk.seat.marsrover.core.testing.jvm.data.DomainTestData.ValidatorTestData
import com.mustalk.seat.marsrover.core.testing.jvm.util.MainDispatcherRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class InputValidatorImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var inputValidator: InputValidatorImpl

    @Before
    fun setUp() {
        inputValidator = InputValidatorImpl()
    }

    @Test
    fun `should create valid plateau from standard instructions`() {
        val instructions = ValidatorTestData.ValidPlateaus.STANDARD_PLATEAU

        val plateau = inputValidator.validateAndCreatePlateau(instructions)

        assertThat(plateau.maxX).isEqualTo(TestConstants.STANDARD_PLATEAU_X)
        assertThat(plateau.maxY).isEqualTo(TestConstants.STANDARD_PLATEAU_Y)
    }

    @Test(expected = RoverError.InvalidPlateauDimensions::class)
    fun `should throw InvalidPlateauDimensions for negative X`() {
        val instructions = ValidatorTestData.InvalidPlateaus.NEGATIVE_X
        inputValidator.validateAndCreatePlateau(instructions)
    }

    @Test(expected = RoverError.InvalidPlateauDimensions::class)
    fun `should throw InvalidPlateauDimensions for negative Y`() {
        val instructions = ValidatorTestData.InvalidPlateaus.NEGATIVE_Y
        inputValidator.validateAndCreatePlateau(instructions)
    }

    @Test
    fun `should parse valid direction characters`() {
        ValidatorTestData.DirectionValidation.VALID_DIRECTIONS.forEach { (input, expected) ->
            val result = inputValidator.validateAndParseDirection(input)
            assertThat(result).isEqualTo(expected)
        }
    }

    @Test
    fun `should handle lowercase direction characters`() {
        val lowercaseDirections =
            mapOf(
                "n" to Direction.NORTH,
                "e" to Direction.EAST,
                "s" to Direction.SOUTH,
                "w" to Direction.WEST
            )

        lowercaseDirections.forEach { (input, expected) ->
            val result = inputValidator.validateAndParseDirection(input)
            assertThat(result).isEqualTo(expected)
        }
    }

    @Test(expected = RoverError.InvalidDirectionChar::class)
    fun `should throw InvalidDirectionChar for invalid character`() {
        inputValidator.validateAndParseDirection(TestConstants.INVALID_DIRECTION_X)
    }

    @Test(expected = RoverError.InvalidDirectionChar::class)
    fun `should throw InvalidDirectionChar for multi-character string`() {
        inputValidator.validateAndParseDirection(TestConstants.INVALID_DIRECTION_NE)
    }

    @Test
    fun `should throw InvalidDirectionChar for invalid characters`() {
        ValidatorTestData.DirectionValidation.INVALID_DIRECTIONS.forEach { invalidDirection ->
            try {
                inputValidator.validateAndParseDirection(invalidDirection)
                assertThat(false).isTrue() // Should not reach here
            } catch (e: RoverError.InvalidDirectionChar) {
                // Expected exception
                assertThat(e).isInstanceOf(RoverError.InvalidDirectionChar::class.java)
            }
        }
    }

    @Test
    fun `should validate position within plateau bounds`() {
        ValidatorTestData.PositionValidation.VALID_POSITIONS.forEach { position ->
            // Should not throw exception
            inputValidator.validateInitialPosition(position, ValidatorTestData.PositionValidation.STANDARD_PLATEAU)
        }
    }

    @Test
    fun `should validate position at plateau boundaries`() {
        val plateau = ValidatorTestData.PositionValidation.STANDARD_PLATEAU
        val boundaryPositions =
            listOf(
                TestConstants.POSITION_0_0,
                TestConstants.POSITION_5_5,
                Position(0, 5),
                Position(5, 0)
            )

        // Test corner positions - should not throw exception
        boundaryPositions.forEach { position ->
            inputValidator.validateInitialPosition(position, plateau)
        }
    }

    @Test
    fun `should throw InvalidInitialPosition for positions outside bounds`() {
        val plateau = ValidatorTestData.PositionValidation.STANDARD_PLATEAU

        ValidatorTestData.PositionValidation.INVALID_POSITIONS.forEach { position ->
            try {
                inputValidator.validateInitialPosition(position, plateau)
                assertThat(false).isTrue() // Should not reach here
            } catch (e: RoverError.InvalidInitialPosition) {
                // Expected exception
                assertThat(e).isInstanceOf(RoverError.InvalidInitialPosition::class.java)
            }
        }
    }
}
