package com.androidtetris.game.event
import com.androidtetris.game.*

enum class Event {
    TetrominoCoordinatesChanged, Collision, LinesCompleted,
    GameStart, GameEnd, GamePause, GameUnpause,
    GridChanged, TetrominoSpawned
}

data class TetrominoCoordinatesChangedEventArgs(
    val old: Array<Point>,
    val new: Array<Point>,
    val tetromino: TetrominoCode
)
data class CollisionEventArgs(val coordinates: Array<Point>, val direction: Direction)
data class LinesCompletedEventArgs(
    val lines: List<Int>,
    val grid: HashMap<Int, HashMap<Int, TetrominoCode>>
)
data class GridChangedEventArgs(val grid: HashMap<Int, HashMap<Int, TetrominoCode>>)
data class TetrominoSpawnedEventArgs(val coordinates: Array<Point>, val tetromino: TetrominoCode)
