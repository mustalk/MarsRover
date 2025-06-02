package com.mustalk.seat.marsrover.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DirectionTest {
    @Test
    fun `turnLeft should rotate direction 90 degrees counter-clockwise`() {
        assertEquals(Direction.WEST, Direction.NORTH.turnLeft())
        assertEquals(Direction.NORTH, Direction.EAST.turnLeft())
        assertEquals(Direction.EAST, Direction.SOUTH.turnLeft())
        assertEquals(Direction.SOUTH, Direction.WEST.turnLeft())
    }

    @Test
    fun `turnRight should rotate direction 90 degrees clockwise`() {
        assertEquals(Direction.EAST, Direction.NORTH.turnRight())
        assertEquals(Direction.SOUTH, Direction.EAST.turnRight())
        assertEquals(Direction.WEST, Direction.SOUTH.turnRight())
        assertEquals(Direction.NORTH, Direction.WEST.turnRight())
    }

    @Test
    fun `toChar should return correct character representation`() {
        assertEquals('N', Direction.NORTH.toChar())
        assertEquals('E', Direction.EAST.toChar())
        assertEquals('S', Direction.SOUTH.toChar())
        assertEquals('W', Direction.WEST.toChar())
    }

    @Test
    fun `fromChar should return correct Direction for valid characters`() {
        assertEquals(Direction.NORTH, Direction.fromChar('N'))
        assertEquals(Direction.EAST, Direction.fromChar('E'))
        assertEquals(Direction.SOUTH, Direction.fromChar('S'))
        assertEquals(Direction.WEST, Direction.fromChar('W'))
    }

    @Test
    fun `fromChar should handle lowercase characters`() {
        assertEquals(Direction.NORTH, Direction.fromChar('n'))
        assertEquals(Direction.EAST, Direction.fromChar('e'))
        assertEquals(Direction.SOUTH, Direction.fromChar('s'))
        assertEquals(Direction.WEST, Direction.fromChar('w'))
    }

    @Test
    fun `fromChar should return null for invalid characters`() {
        assertNull(Direction.fromChar('X'))
        assertNull(Direction.fromChar('1'))
        assertNull(Direction.fromChar(' '))
    }
}
