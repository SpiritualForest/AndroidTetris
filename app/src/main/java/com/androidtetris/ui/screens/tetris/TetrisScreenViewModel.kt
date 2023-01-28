package com.androidtetris.ui.screens.tetris

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidtetris.game.API
import com.androidtetris.game.Direction
import com.androidtetris.game.Point
import com.androidtetris.game.TetrisOptions
import com.androidtetris.game.TetrominoCode
import com.androidtetris.game.event.Event
import com.androidtetris.game.event.GridChangedEventArgs
import com.androidtetris.game.event.LinesCompletedEventArgs
import com.androidtetris.game.event.TetrominoCoordinatesChangedEventArgs
import com.androidtetris.game.event.TetrominoSpawnedEventArgs
import com.androidtetris.settings.SettingsHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Interacts with the game engine so that we don't have to implement that stuff
// inside any of the composables

data class TetrisGridState(
    val grid: HashMap<Int, HashMap<Int, TetrominoCode>> = hashMapOf(),
    val tetrominoCoordinates: List<Point> = listOf(),
    val tetromino: TetrominoCode = TetrominoCode.I,
    val ghostCoordinates: List<Point> = listOf(),
    val recompositionCount: Int = 0 // This value is only used to trigger recompositions
)

data class UpcomingTetrominoesState(
    val tetrominoes: List<TetrominoCode> = listOf()
)

data class StatsState(
    val lines: Int = 0,
    val score: Int = 0,
    val level: Int = 1,
    val previousLinesCompleted: Int = 1 // For score calculation
)

data class GameState(
    val gameRunning: Boolean = false,
    val gamePaused: Boolean = false
)

class TetrisScreenViewModel : ViewModel() {
    var tetrisGridState by mutableStateOf(TetrisGridState())
        private set
    var statsState by mutableStateOf(StatsState(level = SettingsHandler.getGameLevel()))
        private set
    var gameState by mutableStateOf(GameState())
        private set
    var upcomingTetrominoesState by mutableStateOf(UpcomingTetrominoesState())
        private set

    private val api = API()
    private val gridWidth = SettingsHandler.getGridWidth()
    private val gridHeight = SettingsHandler.getGridHeight()

    // Now game related properties
    var ghostEnabled = SettingsHandler.getGhostEnabled()
    var gameTimeSeconds by mutableStateOf(0)

    init {
        api.createGame(
            TetrisOptions(
                gameLevel = SettingsHandler.getGameLevel(),
                gridSize = Point(x = gridWidth, y = gridHeight),
                invertRotation = SettingsHandler.getInvertRotation(),
                startingHeight = SettingsHandler.getStartingHeight()
            )
        )
        api.addCallback(Event.GameStart, ::gameStart)
        api.addCallback(Event.GameEnd, ::gameEnd)
        api.addCallback(Event.TetrominoSpawned, ::tetrominoSpawned)
        api.addCallback(Event.TetrominoCoordinatesChanged, ::coordinatesChanged)
        api.addCallback(Event.LinesCompleted, ::linesCompleted)
        api.addCallback(Event.GridChanged, ::gridChanged)
        api.addCallback(Event.GamePause, ::gamePaused)
        api.addCallback(Event.GameUnpause, ::gameUnpaused)
        api.startGame()
    }

    fun gameStart() {
        gameState = gameState.copy(gameRunning = true)
    }

    fun gameEnd() {
        gameState = gameState.copy(gameRunning = false)
    }

    fun gamePaused() {
        gameState = gameState.copy(gamePaused = true)
    }

    fun gameUnpaused() {
        gameState = gameState.copy(gamePaused = false)
    }

    fun tetrominoSpawned(args: TetrominoSpawnedEventArgs) {
        val recompositionCount = tetrisGridState.recompositionCount + 1
        tetrisGridState = tetrisGridState.copy(
            tetromino = args.tetromino,
            tetrominoCoordinates = args.coordinates.toList(),
            ghostCoordinates = listOf(),
            recompositionCount = recompositionCount
        )
        upcomingTetrominoesState = upcomingTetrominoesState.copy(
            tetrominoes = api.getNextTetromino(3).reversed()
        )
        if (ghostEnabled) { moveGhostCoordinates() }
    }

    fun coordinatesChanged(args: TetrominoCoordinatesChangedEventArgs) {
        val recompositionCount = tetrisGridState.recompositionCount + 1
        tetrisGridState = tetrisGridState.copy(
            tetromino = args.tetromino,
            tetrominoCoordinates = args.new.toList(),
            ghostCoordinates = listOf(),
            recompositionCount = recompositionCount
        )
        if (ghostEnabled) {
            moveGhostCoordinates()
        }
    }

    fun linesCompleted(args: LinesCompletedEventArgs) {
        // Calculate score
        val scoreMultiplication = listOf(40, 100, 300, 1200)
        val score = statsState.score + (scoreMultiplication[args.lines.size-1] * (statsState.level + 1) * statsState.previousLinesCompleted)
        statsState = statsState.copy(
            lines = api.lines(),
            level = api.level(),
            score = score,
            previousLinesCompleted = args.lines.size
        )

        // Now we do the line clearing animation shit
        val grid = tetrisGridState.copy().grid
        // We have to add the tetromino's coordinates to the grid to complete the lines
        // If we don't do this, the animations will be incomplete.
        tetrisGridState.tetrominoCoordinates.forEach {
            grid[it.y]?.put(it.x, tetrisGridState.tetromino)
        }
        viewModelScope.launch {
            // Line clearing animation of removing two squares at a time starting at the center and moving outwards
            var recompositionCount = 0
            var delayMs = 0L
            var decreasingHorizontalPosition = (gridWidth / 2) - 1
            for (increasingHorizontalPosition in (gridWidth / 2) until gridWidth) {
                args.lines.forEach { y ->
                    grid[y]?.remove(decreasingHorizontalPosition)
                    grid[y]?.remove(increasingHorizontalPosition)
                }
                delay(delayMs)
                decreasingHorizontalPosition--
                recompositionCount++
                tetrisGridState = tetrisGridState.copy(
                    grid = grid,
                    recompositionCount = recompositionCount
                )
                delayMs += 25
            }
            // All animations done, we can now put the new grid in place
            delay(20)
            recompositionCount++
            tetrisGridState = tetrisGridState.copy(
                grid = args.grid,
                recompositionCount = recompositionCount
            )
        }
    }

    fun gridChanged(args: GridChangedEventArgs) {
        tetrisGridState = tetrisGridState.copy(
            grid = args.grid,
            recompositionCount = 0
        )
    }

    fun move(direction: Direction) {
        if (!gameState.gameRunning || gameState.gamePaused) {
            return
        }
        api.move(direction)
    }

    fun rotate() {
        if (!gameState.gameRunning || gameState.gamePaused) {
            return
        }
        api.rotate()
    }

    fun pauseGame() {
        api.pauseGame()
    }

    fun unpauseGame() {
        api.unpauseGame()
    }

    fun restartGame() {
        api.endGame()
        // Clear the grid state and stats state
        tetrisGridState = TetrisGridState()
        statsState = StatsState(level = SettingsHandler.getGameLevel())
        gameTimeSeconds = 0
        api.startGame()
    }

    fun setTheGhostEnabled(enabled: Boolean) {
        // "TheGhost" because we get this stupid signature clash otherwise
        SettingsHandler.setGhostEnabled(enabled)
        ghostEnabled = enabled
    }

    fun isGhostEnabled(): Boolean {
        return ghostEnabled
    }

    private fun moveGhostCoordinates() {
        val coordinates = tetrisGridState.copy().tetrominoCoordinates.toList()
        val grid = tetrisGridState.grid
        val coordinatesCopy: MutableList<Point> = mutableListOf()
        var lowestRow = 0 // Lowest y axis
        coordinates.forEach {
            coordinatesCopy.add(Point(it.x, it.y))
            if (it.y > lowestRow) { lowestRow = it.y }
        }
        // Find the starting row for checking collisions
        val closestRow = findClosestLarger(lowestRow, grid.keys.sorted())
        val diff = (closestRow - lowestRow)
        // Now increase the coordinates y value by diff-1
        // diff-1 because otherwise the bottom-most part of the tetromino will end up on the closestRow.
        coordinatesCopy.forEach { it.y += diff-1 }
        // No collision was detected after the initial hard-drop, so now we continue downwards.
        while (!isGhostCollision(coordinatesCopy)) {
            // Move the copied coordinates downwards until a collision occurs
            coordinatesCopy.forEach {
                it.y++
            }
        }
        tetrisGridState = tetrisGridState.copy(
            ghostCoordinates = coordinatesCopy.toList(),
        )
    }

    private fun isGhostCollision(coordinates: MutableList<Point>): Boolean {
        val grid = tetrisGridState.grid
        coordinates.forEach { point ->
            val y = point.y + 1
            if (y > gridHeight-1) { return true }
            if (grid.containsKey(y) && grid[y]!!.containsKey(point.x)) {
                return true
            }
        }
        // If we reached here, no collisions occurred
        return false
    }

    private fun findClosestLarger(n: Int, a: List<Int>): Int {
        /* Modified binary search algorithm that returns the closest
         * element in a to n, which is LARGER than n.
         * It never returns n itself. */
        if (a.isEmpty()) {
            // Empty list, return the lowest row (gridHeight-1)
            return gridHeight - 1
        }
        if (n < a[0]) {
            // The first element in the list is already larger than n, so we return that.
            return a[0]
        }
        if (n >= gridHeight - 1) {
            return gridHeight - 1
        }
        var low = 0
        var high = a.size
        while (true) {
            val m = (low + high) / 2
            if (n >= a[m]) {
                // Examine the higher half next iteration
                low = m
            } else {
                if (n >= a[m - 1]) {
                    // Because n was smaller than a[m] to get here,
                    // but now it is larger than a[m-1],
                    // this means that a[m] is the closest element in value to n,
                    // that is larger than it. So we found it.
                    return a[m]
                }
                // Examine the lower half next iteration
                high = m
            }
        }
    }

    fun increaseGameTimer(): Int {
        gameTimeSeconds++
        return gameTimeSeconds
    }
}