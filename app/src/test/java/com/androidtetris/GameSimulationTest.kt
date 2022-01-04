package com.androidtetris

import android.os.CountDownTimer
import org.junit.Test
import org.junit.Assert.*

import com.androidtetris.game.*
import io.mockk.*

class GameSimulationTest {
    // Create a game with the default parameters and set it to run in test mode
    private var gameObj = spyk(Game(runInTestMode = true))
    var linesCompletedCalled = false

    init {
        every { gameObj.startMovementTimer() } returns Unit
    }
    private fun hardMove(direction: Direction, times: Int) {
        // Helper function to repeatedly move the tetromino in the given direction
        // Like a hard drop in the given direction
        for(i in 0 until times) {
            gameObj.move(direction)
        }
    }

    @Test
    fun testDropTetromino() {
        gameObj.setTetromino(TetrominoCode.I)
        // I's initial coordinates are:
        // (3, 0), (4, 0), (5, 0), (6, 0)
        hardMove(Direction.Down, gameObj.gridHeight)
        // Assert that the moves were successful. All y values should be 21
        for(point in gameObj.currentTetromino.coordinates) {
            assertEquals(point.y, 21)
        }
        // Now drop the tetromino and assert that y 21 has x 3-6 set to TetrominoCode.I
        val nextTetrominoCode = gameObj.getNextTetromino()[0]
        gameObj.dropTetromino()
        for(x in 3 until 7) {
            assertEquals(TetrominoCode.I, gameObj.grid.grid[21][x])
        }
        // Now assert that the new tetromino was spawned and set as the current one
        assertEquals(nextTetrominoCode, gameObj.currentTetromino.tetrominoCode)
        // Clear the grid
        gameObj.grid.clear()
    }

    @Test
    fun testLineCompletion() {
        gameObj.setTetromino(TetrominoCode.I)
        // I's initial coordinates are:
        // (3, 0), (4, 0), (5, 0), (6, 0)
        // Now move it all the way to the left and down
        hardMove(Direction.Left, 3)
        hardMove(Direction.Down, gameObj.gridHeight-1)
        gameObj.dropTetromino()
        gameObj.setTetromino(TetrominoCode.I)
        gameObj.move(Direction.Right)
        hardMove(Direction.Down, gameObj.gridHeight-1)
        gameObj.dropTetromino()
        // O's initial x values are 4,5
        gameObj.setTetromino(TetrominoCode.O)
        // Needs to move to x 8,9
        hardMove(Direction.Right, 4)
        hardMove(Direction.Down, gameObj.gridHeight-2) // O takes 2 y positions, so we move it 20 times instead of 21
        gameObj.eventDispatcher.addCallback(Event.LinesCompleted, ::linesCompleted)
        /* Now drop the tetromino. The LinesCompleted event should be dispatched
         and the lineCompleted function that handles it should be called.
         Line y 21 should be the completed one.
         */
        gameObj.dropTetromino()
        assertEquals(0, gameObj.grid.filledLinePositions[gameObj.gridHeight-2])
        assertEquals(2, gameObj.grid.filledLinePositions[gameObj.gridHeight-1])
        // Assert that only the two right-most positions on y 21 are occupied
        for(x in 0 until 2) { assertEquals(TetrominoCode.O, gameObj.grid.grid[gameObj.gridHeight-1][x+gameObj.gridWidth-2]) }
        for(x in 0 until gameObj.gridWidth-2) {
            assertNull(gameObj.grid.grid[gameObj.gridHeight-1][x])
        }
        assertTrue(linesCompletedCalled)
        gameObj.grid.clear()
    }

    // Callback for the API
    fun linesCompleted(args: LinesCompletedEventArgs) {
        // Assert that the last line is the completed one
        linesCompletedCalled = true
        assertEquals(21, args.lines[0])
    }
}