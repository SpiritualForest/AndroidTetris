package com.androidtetris.game

import android.os.CountDownTimer
import android.os.Bundle // to save game state in case of sudden activity stoppage
import com.androidtetris.game.event.*
import com.androidtetris.settings.* // For Bundle keys
import kotlin.math.floor // For the grid randomizer
import android.util.Log

// Movement directions
enum class Direction { Left, Right, Down }

// Game options class
data class TetrisOptions(
    val gameLevel: Int = 1, // Game level, affects the tetromino's dropping speed
    val gridSize: Point = Point(10, 22), // Point(x, y) size of the grid. x is width, y is height
    val invertRotation: Boolean = false, // Rotation direction. Counter clockwise is normal
    val startingHeight: Int = 0 // Starting height. If more than 0, will populate the grid with random squares across <startingHeight> lines
)

class Game(private val options: TetrisOptions = TetrisOptions(), val runInTestMode: Boolean = false, val savedState: Bundle? = null) {
    private val gridHeight = options.gridSize.y
    private val gridWidth = options.gridSize.x
    var gameLevel = options.gameLevel
        private set
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
    private var gameRestored = false

    init {
        // First, check if a game state was saved. If yes, we load it.
        if (savedState != null) {
            loadGame(savedState)
            gameRestored = true
        }
        else {
            // No previous game state was saved, so this is a new game.
            // Create 4 new random tetrominoes.
            // Rest of the stuff is done in startGame(), which the API calls manually.
            for (i in 0 until 4) {
                this.tetrominoes.add(getRandomTetromino())
            }
        }
    }

    private fun randomizeGrid(height: Int) {
        // Populate the grid with random squares, spanning across <height> percentage of lines
        val lines = floor((height / 10f) * gridHeight).toInt()
        for(y in gridHeight downTo gridHeight - lines) {
            /* We create a list of indices from 0 to gridWidth.
            * Those are our x values for the grid. Each x represents one square on that y line.
            * We remove a random amount of x values from the xPositions list
            * and then proceed to add the remaining x values to the grid, with a random
            * tetromino code created for each one.
            * The tetromino code determines the colour of the square. */
            val xPositions: MutableList<Int> = mutableListOf()
            for(x in 0 until gridWidth) { xPositions.add(x) }
            val xValuesToRemove = (1..gridWidth-1).random() // So that at least one square remains, but never a full line.
            // Now remove <squaresToRemove> x values from xPositions
            for(i in 0 until xValuesToRemove) {
                xPositions.removeAt(xPositions.indices.random())
            }
            // Populate the nested hashmap with random x positions
            grid.grid[y] = HashMap()
            for(x in xPositions) {
                grid.grid[y]?.set(x, getRandomTetromino())
            }
        }
    }

    fun saveGame(bundle: Bundle): Bundle {
        /* Saves the current game state in a Bundle object.
         * We need it when we want to restore the game into the state it was
         * in case the Android activity that handles the gameplay UI was suddenly stopped
         * and then restarted.
         * Returns a Bundle object that will be used in onSaveInstanceState() in TetrisActivity */
        bundle.putInt(K_GAME_LEVEL, gameLevel)
        bundle.putInt(K_LINES, lines)
        bundle.putLong(K_DROP_SPEED, dropSpeed)
        bundle.putSerializable(K_TETROMINO, currentTetromino.tetrominoCode)
        bundle.putInt(K_TETROMINO_ROTATION, currentTetromino.currentRotation)
        
        /* Pack the grid */
        val gridValuesList: ArrayList<Int> = arrayListOf()
        val tetrominoCodes = TetrominoCode.values()
        for(y in grid.grid.keys) {
            for(x in grid.grid[y]!!.keys) {
                // We use the index of the tetromino in the TetrominoCode enum to determine which one it is
                val tetrominoInteger = tetrominoCodes.indexOf(grid.grid[y]!![x]!!)
                // Now pack
                val packed = ((x and 255) shl 16) or ((y and 255) shl 8) or (tetrominoInteger and 255)
                gridValuesList.add(packed)
            }
        }
        bundle.putIntegerArrayList(K_GRID, gridValuesList)
        
        // Now also pack the current tetromino's coordinates
        val coordinatesList: ArrayList<Int> = arrayListOf()
        for(point in currentTetromino.coordinates) {
            val packed = (point.x and 255) shl 8 or (point.y and 255)
            coordinatesList.add(packed)
        }
        bundle.putIntegerArrayList(K_TETROMINO_COORDINATES, coordinatesList)

        // Now put the the upcoming tetrominoes list too
        val upcomingTetrominoes: ArrayList<Int> = arrayListOf()
        for(t in this.tetrominoes) {
            upcomingTetrominoes.add(tetrominoCodes.indexOf(t))
        }
        bundle.putIntegerArrayList(K_UPCOMING_TETROMINOES, upcomingTetrominoes)

        return bundle
    }

    private fun loadGame(savedState: Bundle) {
        // Load a game
        // Stats first
        gameLevel = savedState.getInt(K_GAME_LEVEL)
        lines = savedState.getInt(K_LINES)
        dropSpeed = savedState.getLong(K_DROP_SPEED)
        
        // For tetromino, we must create a new object and then set its coordinates and current rotation
        val t = savedState.getSerializable(K_TETROMINO)
        // Populate the coordinates array of points
        val coordinatesList = savedState.getIntegerArrayList(K_TETROMINO_COORDINATES)
        val coordinates = Array<Point>(coordinatesList!!.size) { Point(0, 0) }
        for((i, xy) in coordinatesList.withIndex()) {
            val x = (xy shr 8) and 255
            val y = xy and 255
            coordinates[i] = Point(x, y)
        }
        val rotation = savedState.getInt(K_TETROMINO_ROTATION)
        // Create the tetromino object, set its coordinates and rotation, and then set it as our current tetromino in the game
        val tetrominoObj = tetrominoReferences[t]!!
        currentTetromino = tetrominoObj.invoke(grid)
        currentTetromino.currentRotation = rotation
        currentTetromino.coordinates = coordinates

        // Now we unpack the grid
        val grid: HashMap<Int, HashMap<Int, TetrominoCode>> = hashMapOf()
        val tetrominoCodes = TetrominoCode.values()
        val gridValuesList = savedState.getIntegerArrayList(K_GRID)
        for(xyc in gridValuesList!!) {
            val x = (xyc shr 16) and 255
            val y = (xyc shr 8) and 255
            val c = xyc and 255
            if (y !in grid.keys) {
                grid[y] = HashMap()
            }
            grid[y]!![x] = tetrominoCodes[c]
        }
        this.grid.grid = grid

        // Upcoming tetrominoes
        val upcoming = savedState.getIntegerArrayList(K_UPCOMING_TETROMINOES)
        for(upcomingTetromino in upcoming!!) {
            this.tetrominoes.add(tetrominoCodes[upcomingTetromino])
        }
        eventDispatcher.dispatch(Event.GridChanged, GridChangedEventArgs(this.grid.copyOf()))
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
        if (!gameRestored) {
            // This is a new game. Spawn a new tetromino
            spawnNextTetromino()
            if (options.startingHeight > 0) {
                // Randomize the grid
                randomizeGrid(options.startingHeight)
            }
        }
        else {
            // A game was restored. Indicate that when startGame() is called again,
            // it will start a new game.
            gameRestored = false
        }
        // Set the game drop speed (how fast the tetrominoes move downwards)
        gameLevel = options.gameLevel
        dropSpeed -= (gameLevel-1)*50 // Reductions of 50ms for each extra level

        startMovementTimer()
        eventDispatcher.dispatch(Event.GameStart) 
        // We dispatch the GridChanged here in case any changes were to the grid,
        // such as having it be populated by a random starting height.
        eventDispatcher.dispatch(Event.GridChanged, GridChangedEventArgs(this.grid.copyOf()))
    }

    internal fun endGame() {
        // Stop the movement timer and clear out the grid.
        gameRunning = false
        grid.clear()
        mTimer?.cancel()
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
        if (options.invertRotation) {
            // Reverse the rotation order if the user wants it the other way around
            currentTetromino.rotations.reverse()
        }
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
                if ((lines % 10 == 0) && (dropSpeed > 100)) {
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
