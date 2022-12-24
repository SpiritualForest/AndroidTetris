package com.androidtetris

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.androidtetris.game.API
import com.androidtetris.game.Point
import com.androidtetris.game.TetrominoCode
import com.androidtetris.game.event.Event
import com.androidtetris.game.event.GridChangedEventArgs
import com.androidtetris.game.event.LinesCompletedEventArgs
import com.androidtetris.game.event.TetrominoCoordinatesChangedEventArgs
import com.androidtetris.game.event.TetrominoSpawnedEventArgs

// Interacts with the game engine so that we don't have to implement that stuff
// inside any of the composables

data class TetrisScreenUiState(
    val grid: HashMap<Int, HashMap<Int, TetrominoCode>> = hashMapOf(),
    val tetromino: TetrominoCode = TetrominoCode.I,
    val coordinates: Array<Point> = arrayOf(),
    val lines: Int = 0,
    val score: Int = 0,
    val level: Int = 0,
    val gameRunning: Boolean = false,
    val gamePaused: Boolean = false,
    val completedLines: List<Int> = listOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TetrisScreenUiState

        if (grid != other.grid) return false
        if (tetromino != other.tetromino) return false
        if (!coordinates.contentEquals(other.coordinates)) return false
        if (lines != other.lines) return false
        if (score != other.score) return false
        if (level != other.level) return false
        if (gameRunning != other.gameRunning) return false
        if (gamePaused != other.gamePaused) return false

        return true
    }

    override fun hashCode(): Int {
        var result = grid.hashCode()
        result = 31 * result + tetromino.hashCode()
        result = 31 * result + coordinates.contentHashCode()
        result = 31 * result + lines
        result = 31 * result + score
        result = 31 * result + level
        result = 31 * result + gameRunning.hashCode()
        result = 31 * result + gamePaused.hashCode()
        return result
    }
}

class TetrisScreenViewModel : ViewModel() {
    var uiState by mutableStateOf(TetrisScreenUiState())
        private set
    private val api = API()

    init {
        Log.d("TetrisViewModel", "init called")
        api.createGame()
        api.addCallback(Event.GameStart, ::gameStart)
        api.addCallback(Event.GameEnd, ::gameEnd)
        api.addCallback(Event.TetrominoSpawned, ::tetrominoSpawned)
        api.addCallback(Event.TetrominoCoordinatesChanged, ::coordinatesChanged)
        api.addCallback(Event.LinesCompleted, ::linesCompleted)
        api.addCallback(Event.GridChanged, ::gridChanged)
        api.startGame()
    }

    fun gameStart() {
        uiState = uiState.copy(gameRunning = true)
    }

    fun gameEnd() {
        uiState = uiState.copy(gameRunning = false)
    }

    fun tetrominoSpawned(args: TetrominoSpawnedEventArgs) {
        uiState = uiState.copy(
            tetromino = args.tetromino,
            coordinates = args.coordinates
        )
    }

    fun coordinatesChanged(args: TetrominoCoordinatesChangedEventArgs) {
        //Log.d("TetrisViewModel", "CoordinatesChanged called")
        uiState = uiState.copy(coordinates = args.new)
    }

    fun linesCompleted(args: LinesCompletedEventArgs) {
        uiState = uiState.copy(
            grid = args.grid,
            completedLines = args.lines
        )
    }

    fun gridChanged(args: GridChangedEventArgs) {
        uiState = uiState.copy(grid = args.grid)
    }
}