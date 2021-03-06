package com.androidtetris

import org.junit.Test
import org.junit.Assert.*

import com.androidtetris.game.*
import com.androidtetris.game.event.*
import io.mockk.*

class GameSimulationTest {
    // Create a game with the default parameters and set it to run in test mode
    private val tetrisOptions = TetrisOptions(isTestMode = true)
    private var gameObj = spyk(Game(tetrisOptions))
    private val gridHeight = tetrisOptions.gridSize.y
    private val gridWidth = tetrisOptions.gridSize.x
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
        hardMove(Direction.Down, gridHeight)
        // Assert that the moves were successful. All y values should be 21
        for(point in gameObj.currentTetromino.coordinates) {
            assertEquals(21, point.y)
        }
        // Now drop the tetromino and assert that y 21 has x 3-6 set to TetrominoCode.I
        val nextTetrominoCode = gameObj.getNextTetromino()[0]
        gameObj.dropTetromino()
        for(x in 3 until 7) {
            assertEquals(TetrominoCode.I, gameObj.grid.grid[21]?.get(x))
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
        hardMove(Direction.Down, gridHeight-1)
        gameObj.dropTetromino()
        gameObj.setTetromino(TetrominoCode.I)
        gameObj.move(Direction.Right)
        hardMove(Direction.Down, gridHeight-1)
        gameObj.dropTetromino()
        // O's initial x values are 4,5
        gameObj.setTetromino(TetrominoCode.O)
        // Needs to move to x 8,9
        hardMove(Direction.Right, 4)
        hardMove(Direction.Down, gridHeight-2) // O takes 2 y positions, so we move it 20 times instead of 21
        gameObj.eventDispatcher.addCallback(Event.LinesCompleted, ::linesCompleted)
        /* Now drop the tetromino. The LinesCompleted event should be dispatched
         and the lineCompleted function that handles it should be called.
         Line y 21 should be the completed one.
         */
        gameObj.dropTetromino()
        assertNull(gameObj.grid.grid[gridHeight-2])
        assertEquals(2, gameObj.grid.grid[gridHeight-1]?.count())
        // Assert that only the two right-most positions on y 21 are occupied
        for(x in 0 until 2) { assertEquals(TetrominoCode.O,
            gameObj.grid.grid[gridHeight-1]?.get(x+gridWidth-2)
        ) }
        for(x in 0 until gridWidth-2) {
            assertNull(gameObj.grid.grid[gridHeight-1]?.get(x))
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
