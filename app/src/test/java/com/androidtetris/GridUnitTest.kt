package com.androidtetris

// Test package
import org.junit.Test
import org.junit.Assert.*

import com.androidtetris.game.*

class GridUnitTest {
    val grid = Grid(10, 22)  // x, y

    @Test
    fun test_getCenter() {
        assertEquals(3, grid.getCenter(4)) // I
        assertEquals(4, grid.getCenter(2)) // O
        assertEquals(4, grid.getCenter(3)) // Rest
    }

    @Test
    fun test_convertCoordinatesMap() {
        // Test the function with I tetromino's coordinates map
        val iCoordinatesMap = arrayOf(0, 1, 2, 3)
        val iRotationsMap = arrayOf(2, 6, 10, 14)
        val coordinates = grid.convertCoordinatesMap(iCoordinatesMap, 4)
        val rotations = grid.convertCoordinatesMap(iRotationsMap, 4)
        // Now test
        assertTrue(coordinates.size == 4)
        assertTrue(rotations.size == 4)
        val correctCoordinates = arrayOf(Point(3, 0), Point(4, 0), Point(5, 0), Point(6, 0))
        val correctRotations = arrayOf(Point(5, 0), Point(5, 1), Point(5, 2), Point(5, 3))
        for(c in coordinates) {
            assertTrue(correctCoordinates.contains(c))
        }
        for(r in rotations) {
            assertTrue(correctRotations.contains(r))
        }
    }

    @Test
    fun test_isCollision() {
        // First, test out of bounds collisions
        assertTrue(grid.isCollision(arrayOf(Point(-1, 0))))
        assertTrue(grid.isCollision(arrayOf(Point(0, grid.height))))
        // Now add some random coordinates to the grid and check that they trigger a collision
        grid.grid[10][9] = TetrominoCode.Z
        assertTrue(grid.isCollision(arrayOf(Point(10, 9))))
        // Now test that no collisions occur when a position is unoccupied
        assertFalse(grid.isCollision(arrayOf(Point(0, 0))))
    }

    @Test
    fun test_pushLines() {
        grid.clear()
        // Fill the last line with I
        grid.grid[grid.height-1] = Array(grid.width) { TetrominoCode.I }
        grid.filledLinePositions[grid.height-1] = grid.width
        grid.grid[0] = Array(grid.width) { TetrominoCode.O }
        grid.filledLinePositions[0] = grid.width
        // Now call pushLines, should push all the O downwards
        grid.clearLine(grid.height-1)
        grid.pushLines(grid.height-1)
        val y = grid.height-1
        for(x in 0 until grid.width) {
            // Assert that the lowest line is all full
            assertNotNull(grid.grid[y][x])
            // And that the highest (0) has been cleared
            assertNull(grid.grid[0][x])
        }
        assertEquals(grid.filledLinePositions[0], 0)
        assertEquals(grid.filledLinePositions[y], grid.width)
    }
}