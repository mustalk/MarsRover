package com.mustalk.seat.marsrover.core.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.mustalk.seat.marsrover.core.domain.error.RoverError
import com.mustalk.seat.marsrover.core.domain.parser.JsonParser
import com.mustalk.seat.marsrover.core.domain.service.RoverMovementService
import com.mustalk.seat.marsrover.core.domain.validator.InputValidator
import com.mustalk.seat.marsrover.core.model.Direction
import com.mustalk.seat.marsrover.core.model.Plateau
import com.mustalk.seat.marsrover.core.model.Rover
import com.mustalk.seat.marsrover.core.testing.jvm.data.DomainTestData.TestConstants
import com.mustalk.seat.marsrover.core.testing.jvm.data.DomainTestData.UseCaseTestData
import com.mustalk.seat.marsrover.core.testing.jvm.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ExecuteRoverMissionUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var useCase: ExecuteRoverMissionUseCase
    private lateinit var jsonParser: JsonParser
    private lateinit var inputValidator: InputValidator
    private lateinit var roverMovementService: RoverMovementService

    @Before
    fun setUp() {
        // Create mocks for domain dependencies since core:domain is a pure Kotlin module
        jsonParser = mockk()
        inputValidator = mockk()
        roverMovementService = mockk()

        // Create use case with mocked dependencies
        useCase = ExecuteRoverMissionUseCase(jsonParser, inputValidator, roverMovementService)
    }

    @Test
    fun `should return correct final position for provided example`() {
        // Given
        val testData = UseCaseTestData.SuccessfulExecution.STANDARD_MISSION
        val instructions = testData.EXPECTED_RESULT
        val plateau = TestConstants.STANDARD_PLATEAU
        val direction = Direction.NORTH
        val position = TestConstants.POSITION_1_2

        // Mock the flow through the use case
        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("N") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } returns Unit
        every { roverMovementService.executeMovements(any(), plateau, TestConstants.STANDARD_MOVEMENTS) } answers {
            // Mock the rover movement to end up at (1, 3) facing North
            val rover = firstArg<Rover>()
            rover.position = TestConstants.POSITION_1_3
            rover.direction = Direction.NORTH
        }

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(UseCaseTestData.SuccessfulExecution.EXPECTED_FINAL_POSITION)
    }

    @Test
    fun `should handle rover at origin moving north`() {
        // Given
        val testData = UseCaseTestData.SuccessfulExecution.SimpleMove
        val instructions = testData.INSTRUCTIONS
        val plateau = TestConstants.STANDARD_PLATEAU
        val direction = Direction.NORTH
        val position = TestConstants.POSITION_0_0

        // Mock the flow through the use case
        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("N") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } returns Unit
        every { roverMovementService.executeMovements(any(), plateau, TestConstants.SINGLE_MOVE) } answers {
            // Mock the rover movement to end up at (0, 1) facing North
            val rover = firstArg<Rover>()
            rover.position = TestConstants.POSITION_0_1
            rover.direction = Direction.NORTH
        }

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testData.EXPECTED_POSITION)
    }

    @Test
    fun `should handle rover moving to plateau boundary`() {
        // Given
        val testData = UseCaseTestData.SuccessfulExecution.BoundaryMovement
        val instructions = testData.INSTRUCTIONS
        val plateau = TestConstants.SMALL_PLATEAU
        val direction = Direction.EAST
        val position = TestConstants.POSITION_1_1

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("E") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } returns Unit
        every { roverMovementService.executeMovements(any(), plateau, TestConstants.SINGLE_MOVE) } answers {
            val rover = firstArg<Rover>()
            rover.position = TestConstants.POSITION_2_1
            rover.direction = Direction.EAST
        }

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testData.EXPECTED_POSITION)
    }

    @Test
    fun `should not move when trying to go beyond plateau boundary`() {
        // Given
        val testData = UseCaseTestData.SuccessfulExecution.BoundaryBlocked
        val instructions = testData.INSTRUCTIONS
        val plateau = TestConstants.SMALL_PLATEAU
        val direction = Direction.NORTH
        val position = TestConstants.POSITION_2_2

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("N") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } returns Unit
        every { roverMovementService.executeMovements(any(), plateau, TestConstants.SINGLE_MOVE) } answers {
            val rover = firstArg<Rover>()
            rover.position = TestConstants.POSITION_2_2 // Stays at same position (boundary)
            rover.direction = Direction.NORTH
        }

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testData.EXPECTED_POSITION)
    }

    @Test
    fun `should handle multiple boundary crossing attempts`() {
        // Given
        val testData = UseCaseTestData.SuccessfulExecution.MultipleBoundaryAttempts
        val instructions = testData.INSTRUCTIONS
        val plateau = Plateau(1, 1)
        val direction = Direction.WEST
        val position = TestConstants.POSITION_0_0

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("W") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } returns Unit
        every { roverMovementService.executeMovements(any(), plateau, TestConstants.BOUNDARY_TEST_COMMANDS) } answers {
            val rover = firstArg<Rover>()
            // Starting at (0,0) facing W:
            // M - can't move west (out of bounds), stay at (0,0) facing W
            // M - can't move west (out of bounds), stay at (0,0) facing W
            // M - can't move west (out of bounds), stay at (0,0) facing W
            // M - can't move west (out of bounds), stay at (0,0) facing W
            // S - invalid command, ignore (stays facing W)
            // M - can't move west (out of bounds), stay at (0,0) facing W
            // M - can't move west (out of bounds), stay at (0,0) facing W
            // M - can't move west (out of bounds), stay at (0,0) facing W
            rover.position = TestConstants.POSITION_0_0
            rover.direction = Direction.WEST
        }

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testData.EXPECTED_POSITION)
    }

    @Test
    fun `should ignore invalid movement characters`() {
        // Given
        val testData = UseCaseTestData.SuccessfulExecution.InvalidCharacterIgnore
        val instructions = testData.INSTRUCTIONS
        val plateau = TestConstants.STANDARD_PLATEAU
        val direction = Direction.NORTH
        val position = TestConstants.POSITION_1_1

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("N") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } returns Unit
        every { roverMovementService.executeMovements(any(), plateau, TestConstants.INVALID_COMMANDS) } answers {
            val rover = firstArg<Rover>()
            // Starting at (1,1) facing N:
            // M - move north to (1,2) facing N
            // X - invalid, ignore
            // L - turn left to face W
            // 1 - invalid, ignore
            // R - turn right to face N
            // @ - invalid, ignore
            // M - move north to (1,3) facing N
            rover.position = TestConstants.POSITION_1_3
            rover.direction = Direction.NORTH
        }

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testData.EXPECTED_POSITION)
    }

    @Test
    fun `should handle empty movement string`() {
        // Given
        val testData = UseCaseTestData.SuccessfulExecution.EmptyMovements
        val instructions = testData.INSTRUCTIONS
        val plateau = TestConstants.STANDARD_PLATEAU
        val direction = Direction.EAST
        val position = TestConstants.POSITION_2_3

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("E") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } returns Unit
        every { roverMovementService.executeMovements(any(), plateau, TestConstants.EMPTY_COMMANDS) } answers {
            val rover = firstArg<Rover>()
            rover.position = TestConstants.POSITION_2_3
            rover.direction = Direction.EAST
        }

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testData.EXPECTED_POSITION)
    }

    @Test
    fun `should handle only turning movements`() {
        // Given
        val testData = UseCaseTestData.SuccessfulExecution.OnlyRotations
        val instructions = testData.INSTRUCTIONS
        val plateau = TestConstants.STANDARD_PLATEAU
        val direction = Direction.NORTH
        val position = TestConstants.POSITION_2_3

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("N") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } returns Unit
        every { roverMovementService.executeMovements(any(), plateau, TestConstants.FOUR_LEFT_TURNS) } answers {
            val rover = firstArg<Rover>()
            rover.position = TestConstants.POSITION_2_3
            rover.direction = Direction.NORTH // Full rotation back to North
        }

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testData.EXPECTED_POSITION)
    }

    @Test
    fun `should return error for malformed JSON`() {
        // Given
        val invalidJson = UseCaseTestData.ErrorScenarios.INVALID_JSON
        every { jsonParser.parseInput(invalidJson) } throws RoverError.InvalidInputFormat(TestConstants.INVALID_JSON_MESSAGE)

        // When
        val result = useCase(invalidJson)

        // Then
        assertThat(result.isFailure).isTrue()
        val error = result.exceptionOrNull()
        assertThat(error).isInstanceOf(RoverError.InvalidInputFormat::class.java)
    }

    @Test
    fun `should return error for missing required fields`() {
        // Given
        val testData = UseCaseTestData.ErrorScenarios.MissingRequiredFields
        every { jsonParser.parseInput(testData.JSON) } throws RoverError.InvalidInputFormat("Missing required fields")

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isFailure).isTrue()
        val error = result.exceptionOrNull()
        assertThat(error).isInstanceOf(RoverError.InvalidInputFormat::class.java)
    }

    @Test
    fun `should return error for negative plateau dimensions`() {
        // Given
        val testData = UseCaseTestData.ErrorScenarios.NegativePlateauDimensions
        val instructions = testData.INSTRUCTIONS

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } throws RoverError.InvalidPlateauDimensions(-1, 5)

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isFailure).isTrue()
        val error = result.exceptionOrNull()
        assertThat(error).isInstanceOf(RoverError.InvalidPlateauDimensions::class.java)
    }

    @Test
    fun `should return error for invalid direction character`() {
        // Given
        val testData = UseCaseTestData.ErrorScenarios.InvalidDirection
        val instructions = testData.INSTRUCTIONS
        val plateau = TestConstants.STANDARD_PLATEAU

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection(TestConstants.INVALID_DIRECTION_X) } throws
            RoverError.InvalidDirectionChar(TestConstants.INVALID_DIRECTION_X)

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isFailure).isTrue()
        val error = result.exceptionOrNull()
        assertThat(error).isInstanceOf(RoverError.InvalidDirectionChar::class.java)
    }

    @Test
    fun `should return error for multi-character direction string`() {
        // Given
        val testData = UseCaseTestData.ErrorScenarios.MultiCharacterDirection
        val instructions = testData.INSTRUCTIONS
        val plateau = TestConstants.STANDARD_PLATEAU

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection(TestConstants.INVALID_DIRECTION_NE) } throws
            RoverError.InvalidDirectionChar(TestConstants.INVALID_DIRECTION_NE)

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isFailure).isTrue()
        val error = result.exceptionOrNull()
        assertThat(error).isInstanceOf(RoverError.InvalidDirectionChar::class.java)
    }

    @Test
    fun `should return error for rover initial position outside plateau bounds`() {
        // Given
        val testData = UseCaseTestData.ErrorScenarios.OutOfBoundsPosition
        val instructions = testData.INSTRUCTIONS
        val plateau = TestConstants.STANDARD_PLATEAU
        val direction = Direction.NORTH
        val position = TestConstants.POSITION_6_2

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("N") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } throws
            RoverError.InvalidInitialPosition(6, 2, TestConstants.STANDARD_PLATEAU_X, TestConstants.STANDARD_PLATEAU_Y)

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isFailure).isTrue()
        val error = result.exceptionOrNull()
        assertThat(error).isInstanceOf(RoverError.InvalidInitialPosition::class.java)
    }

    @Test
    fun `should handle lowercase direction character`() {
        // Given
        val testData = UseCaseTestData.SuccessfulExecution.LowercaseDirection
        val instructions = testData.INSTRUCTIONS
        val plateau = TestConstants.STANDARD_PLATEAU
        val direction = Direction.NORTH
        val position = TestConstants.POSITION_1_2

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("n") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } returns Unit
        every { roverMovementService.executeMovements(any(), plateau, TestConstants.SINGLE_MOVE) } answers {
            val rover = firstArg<Rover>()
            rover.position = TestConstants.POSITION_1_3
            rover.direction = Direction.NORTH
        }

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testData.EXPECTED_POSITION)
    }

    @Test
    fun `should handle single cell plateau`() {
        // Given
        val testData = UseCaseTestData.SuccessfulExecution.SingleCellPlateau
        val instructions = testData.INSTRUCTIONS
        val plateau = TestConstants.TINY_PLATEAU
        val direction = Direction.NORTH
        val position = TestConstants.POSITION_0_0

        every { jsonParser.parseInput(testData.JSON) } returns instructions
        every { inputValidator.validateAndCreatePlateau(instructions) } returns plateau
        every { inputValidator.validateAndParseDirection("N") } returns direction
        every { inputValidator.validateInitialPosition(position, plateau) } returns Unit
        every { roverMovementService.executeMovements(any(), plateau, TestConstants.TINY_PLATEAU_COMMANDS) } answers {
            val rover = firstArg<Rover>()
            // Starting at (0,0) facing N on a 1x1 plateau:
            // M - can't move north (out of bounds), stay at (0,0) facing N
            // R - turn right to face E
            // L - turn left to face N
            // M - can't move north (out of bounds), stay at (0,0) facing N
            rover.position = TestConstants.POSITION_0_0
            rover.direction = Direction.NORTH
        }

        // When
        val result = useCase(testData.JSON)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(testData.EXPECTED_POSITION)
    }
}
