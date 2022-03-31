package com.androidtetris.game

import android.os.CountDownTimer
import android.util.Log
import com.androidtetris.game.event.*

// Movement directions
enum class Direction { Left, Right, Down }

class Game(var gameLevel: Int = 1, val gridWidth: Int = 10, val gridHeight: Int = 22, val rotationClockwise: Boolean = false, val runInTestMode: Boolean = false) {
    val grid = Grid(gridWidth, gridHeight) // Default is x 10, y 22
    private var tetrominoes = mutableListOf<TetrominoCode>()
    private val tetrominoReferences = hashMapOf(
        TetrominoCode.I to ::I,
        TetrominoCode.O to ::O,
        TetrominoCode.J to ::J,
        TetrominoCode.L to ::L,
        TetrominoCode.S to ::S,
        TetrominoCode.T to ::T,
        TetrominoCode.Z to ::Z,
    )
    lateinit var currentTetromino: Tetromino
        private set
    private var dropSpeed: Long = 1000L // How fast the tetrominoes move downwards automatically. Defaults to 1 sec (1000ms)
    private var downwardsCollisionCount = 0
    val eventDispatcher = EventDispatcher()
    private var gameRunning = false // Game is not running by default
    private var mTimer: CountDownTimer? = null
    var lines = 0
        private set

    init {
        // Create 10 random tetrominoes for spawning
        for (i in 0 until 10) {
            this.tetrominoes.add(getRandomTetromino())
        }
        // Set the game drop speed (how fast the tetrominoes move downwards)
        dropSpeed -= (gameLevel-1)*50 // Reductions of 50ms for each extra level
        startGame()
    }
    
    internal fun startMovementTimer() {
        // Creates a CountDownTimer that automatically moves the tetromino downwards every <dropSpeed> milliseconds
        mTimer?.cancel()
        mTimer = object : CountDownTimer(dropSpeed*gridHeight.toLong(), dropSpeed) {
            override fun onTick(millisInFuture: Long) {
                move(Direction.Down)
            }
            override fun onFinish() {
                dropTetromino()
            }
        }.start()
    }

    internal fun startGame() {
        /* Start the game, spawn the first tetromino,
         * and create our automatic movement thread. */
        gameRunning = true
        spawnNextTetromino()
        startMovementTimer()
        eventDispatcher.dispatch(Event.GameStart)
    }

    internal fun endGame() {
        // Stop the movement timer and clear out the grid.
        gameRunning = false
        mTimer?.cancel()
        grid.clear()
        eventDispatcher.dispatch(Event.GameEnd)
    }

    internal fun pauseGame() {
        // Pauses the game
        gameRunning = false
        mTimer?.cancel()
        eventDispatcher.dispatch(Event.GamePause)
    }

    internal fun unpauseGame() {
        gameRunning = true
        startMovementTimer()
        eventDispatcher.dispatch(Event.GameUnpause)
    }

    private fun getRandomTetromino(): TetrominoCode {
        val codes = TetrominoCode.values()
        val codeIndex = (codes.indices).random()
        return codes[codeIndex]
    }
    
    private fun spawnNextTetromino() {
        // Spawn the upcoming tetromino and then remove it from the list
        val tCode = tetrominoes[0]
        val t = this.tetrominoReferences[tCode]
        this.tetrominoes.removeAt(0)
        // Now add a new one to the list
        this.tetrominoes.add(getRandomTetromino())
        val temp = t?.invoke(grid)!!
        // First we must check if the game can even continue
        if (grid.isCollision(temp.coordinates)) {
            // Top line reached, end the game.
            endGame()
            return
        }
        // Game continues, set the "permanent" currentTetromino
        currentTetromino = temp
        /* Now we have to also dispatch the TetrominoSpawned event,
         * otherwise the UI won't know that it has to draw the tetromino's initial coordinates.
         * It will only draw them after move() has been called once. */
         eventDispatcher.dispatch(Event.TetrominoSpawned, TetrominoSpawnedEventArgs(currentTetromino.coordinates, tCode))
    }

    internal fun getNextTetromino(n: Int = 1): List<TetrominoCode> {
        // Returns a list of the N upcoming tetrominoes to be spawned by the game
        // This is intended to be used by the API, not internally.
        return tetrominoes.slice(0 until n).toList()
    }

    /* Functions that are only used when running the game in test mode */
    internal fun setTetromino(tetrominoCode: TetrominoCode) {
        if (!runInTestMode) {
            println("Can't set tetromino manually when not in test mode")
            return
        }
        val t = tetrominoReferences[tetrominoCode]
        currentTetromino = t?.invoke(grid)!!
    }

    /* Tetromino handling functions */
    private fun moveCoordinates(direction: Direction, coordinates: Array<Point>): Array<Point> {
        // Move our coordinates according to the given direction
        // First, manually copy our array
        val temp = Array(coordinates.size) { Point(0, 0) }
        for((i, point) in coordinates.withIndex()) {
            val pointCopy = point.copyOf()

            when(direction) {
                // Now move the required coordinates in the copied nested list
                Direction.Down -> pointCopy.y++
                Direction.Left -> pointCopy.x--
                Direction.Right -> pointCopy.x++
            }
            temp[i] = pointCopy
        }
        return temp
    }

    internal fun move(direction: Direction) {
        // Move the block on the grid
        if (!gameRunning) { return }
        val moved = moveCoordinates(direction, currentTetromino.coordinates)
        if (grid.isCollision(moved)) {
            eventDispatcher.dispatch(Event.Collision, CollisionEventArgs(currentTetromino.coordinates, direction))
            if (direction == Direction.Down) {
                /* Downwards collision means that the tetromino's
                coordinates must be added to the grid.
                Since the tetromino moves downwards automatically in fixed intervals,
                we want to make sure that the player can move it to the side
                if they want to. Otherwise a single collision, even when the movement
                interval is slow, would lead to the tetromino being added to the grid, rendering it
                immovable afterwards.
                */
                downwardsCollisionCount++
                if (downwardsCollisionCount == 2) {
                    downwardsCollisionCount = 0
                    dropTetromino()
                }
            }
            // Collision occurred, abort operation
            return
        }
        // No collisions occurred, move is successful.
        // Update the rotations too
        for ((i, rotation) in currentTetromino.rotations.withIndex()) {
            currentTetromino.rotations[i] = moveCoordinates(direction, rotation)
        }
        /* Move is successful.
         * Change the coordinates and dispatch the CoordinatesChanged event
         */
        val oldCoordinates = currentTetromino.coordinates.copyOf()
        currentTetromino.coordinates = moved
        eventDispatcher.dispatch(Event.CoordinatesChanged,
            CoordinatesChangedEventArgs(oldCoordinates, moved, currentTetromino.tetrominoCode))
    }

    internal fun rotate() {
        // Rotate the block
        if (!gameRunning) { return }
        val rotations = currentTetromino.rotations
        val rotation = rotations[currentTetromino.currentRotation]
        if (grid.isCollision(rotation)) {
            return
        }
        currentTetromino.currentRotation++
        if (currentTetromino.currentRotation == rotations.size) {
            // All rotations have been cycled through, mark the first rotation as next
            currentTetromino.currentRotation = 0
        }
        // Set the coordinates to the rotated ones
        // And dispatch the CoordinatesChanged event
        val oldCoordinates = currentTetromino.coordinates.copyOf()
        currentTetromino.coordinates = rotation
        eventDispatcher.dispatch(Event.CoordinatesChanged,
            CoordinatesChangedEventArgs(oldCoordinates, rotation, currentTetromino.tetrominoCode))
    }

    /* Grid handling functions */

    internal fun dropTetromino() {
        // This adds the current tetromino's coordinates to the grid after a downwards collision,
        // and then checks if lines were completed
        var lowestLine = 0
        val completedLines: MutableList<Int> = mutableListOf()
        for(coordinatePoint in currentTetromino.coordinates) {
            val x = coordinatePoint.x
            val y = coordinatePoint.y
            grid.fillPosition(x, y, currentTetromino.tetrominoCode)
            if (grid.isLineFull(y)) {
                grid.clearLine(y)
                if (y !in completedLines) { completedLines.add(y) }
                if (y > lowestLine) {
                    lowestLine = y
                }
                lines++
                if (lines % 10 == 0) {
                    gameLevel++
                    dropSpeed -= 50
                }
            }
        }
        if (completedLines.count() > 0) {
            grid.pushLines(lowestLine)
            // Dispatch the LinesCompleted event
            eventDispatcher.dispatch(Event.LinesCompleted,
                LinesCompletedEventArgs(completedLines.toList(), grid.copyOf()))
        }
        else {
            // No lines were completed. Trigger the GridChanged event
            eventDispatcher.dispatch(Event.GridChanged, GridChangedEventArgs(grid.copyOf()))
        }
        // Now spawn the next upcoming tetromino and restart auto-move
        spawnNextTetromino()
        startMovementTimer()
    }
}
