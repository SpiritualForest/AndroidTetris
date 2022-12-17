package com.androidtetris

import com.androidtetris.game.API
import com.androidtetris.game.event.CoordinatesChangedEventArgs
import com.androidtetris.game.event.Event
import com.androidtetris.game.event.GridChangedEventArgs
import com.androidtetris.game.event.LinesCompletedEventArgs
import com.androidtetris.game.event.TetrominoSpawnedEventArgs
import kotlin.reflect.KProperty

// Interacts with the game engine so that we don't have to implement that stuff
// inside any of the composables

class Tetris {
    private val api = API()

    private var gameRunning = false

    private var gamePaused = false
    var lines = 0
        private set
    var level = 1
        private set
    var score = 0
        private set

    init {
        api.addCallback(Event.GameStart, ::gameStart)
        api.addCallback(Event.GameEnd, ::gameEnd)
        api.addCallback(Event.TetrominoSpawned, ::tetrominoSpawned)
        api.addCallback(Event.CoordinatesChanged, ::coordinatesChanged)
        api.addCallback(Event.LinesCompleted, ::linesCompleted)
        api.addCallback(Event.GridChanged, ::gridChanged)
        api.startGame()
    }

    fun gameStart() {
        gameRunning = true
    }

    fun gameEnd() {
        gameRunning = false
    }

    fun tetrominoSpawned(args: TetrominoSpawnedEventArgs) {}

    fun coordinatesChanged(args: CoordinatesChangedEventArgs) {}

    fun linesCompleted(args: LinesCompletedEventArgs) {}

    fun gridChanged(args: GridChangedEventArgs) {}

    operator fun getValue(thisRef: Any, property: KProperty<*>): Int? {
        return when(property.name) {
            "lines" -> lines
            "level" -> level
            "score" -> score
            else -> null
        }
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: Any) {
        when(value) {
            is Int -> when(property.name) {
                "lines" -> lines = value
                "level" -> level = value
                "score" -> score = value
            }
            is Boolean -> when(property.name) {
                "gameRunning" -> gameRunning = value
                "gamePaused" -> gamePaused = value
            }
        }
    }
}