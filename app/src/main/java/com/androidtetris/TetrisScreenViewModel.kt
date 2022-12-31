package com.androidtetris

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.androidtetris.game.API
import com.androidtetris.game.Direction
import com.androidtetris.game.Point
import com.androidtetris.game.TetrominoCode
import com.androidtetris.game.event.Event
import com.androidtetris.game.event.GridChangedEventArgs
import com.androidtetris.game.event.LinesCompletedEventArgs
import com.androidtetris.game.event.TetrominoCoordinatesChangedEventArgs
import com.androidtetris.game.event.TetrominoSpawnedEventArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.runInterruptible

// Interacts with the game engine so that we don't have to implement that stuff
// inside any of the composables

data class TetrisGridState(
    val grid: HashMap<Int, HashMap<Int, TetrominoCode>> = hashMapOf(),
    val tetrominoCoordinates: List<Point> = listOf(),
    val tetromino: TetrominoCode = TetrominoCode.I
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

class TetrisScreenViewModel : ViewModel() {
    var tetrisGridState by mutableStateOf(TetrisGridState())
        private set
    var statsState by mutableStateOf(StatsState())
        private set
    var gameState by mutableStateOf(GameState())
        private set

    private val api = API()

    init {
        Log.d("TetrisScreen", "ViewModel initialized")
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
        tetrisGridState = tetrisGridState.copy(
            tetromino = args.tetromino,
            tetrominoCoordinates = args.coordinates.toList()
        )
    }
    fun coordinatesChanged(args: TetrominoCoordinatesChangedEventArgs) {
        tetrisGridState = tetrisGridState.copy(
            tetromino = args.tetromino,
            tetrominoCoordinates = args.new.toList()
        )
    }
    fun linesCompleted(args: LinesCompletedEventArgs) {
        Log.d("TetrisScreen", "Current lines: ${statsState.lines} and to be added now ${args.lines.size}")
        val lines = statsState.lines + args.lines.size
        statsState = statsState.copy(
            lines = lines,
            // TODO: score, level
        )
        // TODO: line clearing animation
        tetrisGridState = tetrisGridState.copy(
            grid = args.grid
        )
    }
    fun gridChanged(args: GridChangedEventArgs) {
        tetrisGridState = tetrisGridState.copy(
            grid = args.grid
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