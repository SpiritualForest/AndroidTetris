package com.androidtetris.ui.screens.tetris

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidtetris.game.API
import com.androidtetris.game.Direction
import com.androidtetris.game.Point
import com.androidtetris.game.TetrominoCode
import com.androidtetris.game.event.Event
import com.androidtetris.game.event.GridChangedEventArgs
import com.androidtetris.game.event.LinesCompletedEventArgs
import com.androidtetris.game.event.TetrominoCoordinatesChangedEventArgs
import com.androidtetris.game.event.TetrominoSpawnedEventArgs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Interacts with the game engine so that we don't have to implement that stuff
// inside any of the composables

data class TetrisGridState(
    val grid: HashMap<Int, HashMap<Int, TetrominoCode>> = hashMapOf(),
    val tetrominoCoordinates: List<Point> = listOf(),
    val tetromino: TetrominoCode = TetrominoCode.I,
    val recompositionCount: Int = 0 // This value is only used to trigger recompositions
)

data class UpcomingTetrominoesState(
    val tetrominoes: List<TetrominoCode> = listOf()
)

data class StatsState(
    val lines: Int = 0,
    val score: Int = 0,
    val level: Int = 0
)

data class GameState(
    val gameRunning: Boolean = false,
    val gamePaused: Boolean = false
)

class TetrisScreenViewModel(
    private val gridWidth: Int,
    private val gridHeight: Int
    ) : ViewModel() {
    var tetrisGridState by mutableStateOf(TetrisGridState())
        private set
    var statsState by mutableStateOf(StatsState())
        private set
    var gameState by mutableStateOf(GameState())
        private set
    var upcomingTetrominoesState by mutableStateOf(UpcomingTetrominoesState())
        private set

    private val api = API()

    // Now game related properties
    // TODO: read from settings, do not hard code here
    var ghostEnabled by mutableStateOf(false)

    init {
        api.createGame()
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
            recompositionCount = recompositionCount
        )
        upcomingTetrominoesState = upcomingTetrominoesState.copy(
            tetrominoes = api.getNextTetromino(3).reversed()
        )
    }
    fun coordinatesChanged(args: TetrominoCoordinatesChangedEventArgs) {
        val recompositionCount = tetrisGridState.recompositionCount + 1
        tetrisGridState = tetrisGridState.copy(
            tetromino = args.tetromino,
            tetrominoCoordinates = args.new.toList(),
            recompositionCount = recompositionCount
        )
    }
    fun linesCompleted(args: LinesCompletedEventArgs) {
        statsState = statsState.copy(
            lines = api.lines(),
            level = api.level()
        )
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
}