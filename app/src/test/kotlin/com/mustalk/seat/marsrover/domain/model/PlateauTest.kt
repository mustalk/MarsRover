package com.mustalk.seat.marsrover.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlateauTest {
    @Test
    fun `isWithinBounds should return true for valid positions`() {
        val plateau = Plateau(5, 5)

        assertTrue(plateau.isWithinBounds(Position(0, 0)))
        assertTrue(plateau.isWithinBounds(Position(2, 3)))
        assertTrue(plateau.isWithinBounds(Position(5, 5)))
        assertTrue(plateau.isWithinBounds(Position(0, 5)))
        assertTrue(plateau.isWithinBounds(Position(5, 0)))
    }

    @Test
    fun `isWithinBounds should return false for positions outside bounds`() {
        val plateau = Plateau(5, 5)

        assertFalse(plateau.isWithinBounds(Position(-1, 0)))
        assertFalse(plateau.isWithinBounds(Position(0, -1)))
        assertFalse(plateau.isWithinBounds(Position(6, 5)))
        assertFalse(plateau.isWithinBounds(Position(5, 6)))
        assertFalse(plateau.isWithinBounds(Position(-1, -1)))
        assertFalse(plateau.isWithinBounds(Position(6, 6)))
    }

    @Test
    fun `isWithinBounds should work with single cell plateau`() {
        val plateau = Plateau(0, 0)

        assertTrue(plateau.isWithinBounds(Position(0, 0)))
        assertFalse(plateau.isWithinBounds(Position(1, 0)))
        assertFalse(plateau.isWithinBounds(Position(0, 1)))
        assertFalse(plateau.isWithinBounds(Position(-1, 0)))
        assertFalse(plateau.isWithinBounds(Position(0, -1)))
    }

    @Test
    fun `isWithinBounds should work with rectangular plateau`() {
        val plateau = Plateau(3, 7)

        assertTrue(plateau.isWithinBounds(Position(0, 0)))
        assertTrue(plateau.isWithinBounds(Position(3, 7)))
        assertTrue(plateau.isWithinBounds(Position(1, 5)))
        assertFalse(plateau.isWithinBounds(Position(4, 7)))
        assertFalse(plateau.isWithinBounds(Position(3, 8)))
    }
}
