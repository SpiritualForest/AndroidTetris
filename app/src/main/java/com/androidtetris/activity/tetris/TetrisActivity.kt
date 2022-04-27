package com.androidtetris.activity.tetris

// Our actual game's UI activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.ActivityInfo
import com.androidtetris.game.API
import com.androidtetris.game.Direction
import com.androidtetris.game.event.*
import android.os.Handler
import android.os.Looper
import android.os.CountDownTimer
import android.app.Activity
import android.widget.Button
import com.androidtetris.R
import com.google.android.material.chip.Chip
import com.androidtetris.game.Point
import com.androidtetris.game.TetrisOptions
import com.androidtetris.settings.* // For SettingsHandler and option name constants
import android.util.Log

// TODO: handle activity OnPause/OnResume

class TetrisActivity : AppCompatActivity() {
    
    private lateinit var mTetris: Tetris
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tetris)
        
        // Disable screen rotation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        
        // Create a new Tetris object and pass the activity.
        // This way we can find the UI elements we want to manipulate from the Tetris object itself,
        // and don't have to find them here and then pass them.
        mTetris = Tetris(this, savedInstanceState)
        if (savedInstanceState != null) {
            // Pause the game
            pauseGame()
        }

        // Movement and rotation buttons
        val down = findViewById<CircleButton>(R.id.btn_down)
        val rotate = findViewById<CircleButton>(R.id.btn_rotate)
        val left = findViewById<CircleButton>(R.id.btn_left)
        val right = findViewById<CircleButton>(R.id.btn_right)
        val ghostChip = findViewById<Chip>(R.id.chip_ghost)
        val btnPause = findViewById<Button>(R.id.btn_pause)
        val btnRestart = findViewById<Button>(R.id.btn_restart)

        btnRestart.setOnClickListener {
            mTetris.restartGame()
        }

        btnPause.setOnClickListener {
            if (!mTetris.isGamePaused) {
                // Pause the game
                pauseGame()
            }
            else {
                // Unpause it
                unpauseGame()
            }
        }
        // Check or uncheck the ghost chip based on if the feature is enabled in settings
        ghostChip.isChecked = SettingsHandler.getBoolean(S_GHOST_ENABLED)

        // Our handler for the touch event runnables
        mHandler = Handler(Looper.getMainLooper())
        
        // Custom OnTouchListeners for each button
        down.setOnTouchListener(getOnTouchListener(TetrisRunnable(mHandler, { mTetris.api.move(Direction.Down) })))
        left.setOnTouchListener(getOnTouchListener(TetrisRunnable(mHandler, { mTetris.api.move(Direction.Left) })))
        right.setOnTouchListener(getOnTouchListener(TetrisRunnable(mHandler, { mTetris.api.move(Direction.Right) })))
        // Rotate gets 70ms delay instead of the default 55ms
        rotate.setOnTouchListener(getOnTouchListener(TetrisRunnable(mHandler, { mTetris.api.rotate() }, 70L)))

        // Now our ghost piece chip
        ghostChip.setOnCheckedChangeListener { _, isChecked ->
            run {
                mTetris.setGhostEnabled(isChecked)
                SettingsHandler.setBoolean(S_GHOST_ENABLED, isChecked)
            }
        }
    }

    fun pauseGame() {
        val btnPause = findViewById<Button>(R.id.btn_pause)
        mTetris.setGamePaused(true)
        mTetris.api.pauseGame()
        btnPause.setText(R.string.btn_unpause)
    }

    fun unpauseGame() {
        val btnPause = findViewById<Button>(R.id.btn_pause)
        mTetris.setGamePaused(false)
        mTetris.api.unpauseGame()
        btnPause.setText(R.string.btn_pause)
    }

    private fun getOnTouchListener(r: TetrisRunnable): View.OnTouchListener {
        // Creates a new OnTouchListener that executes the provided runnable with a delay
        val listener = View.OnTouchListener { v, ev ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Button is down, post the runnable to the handler
                    mHandler.postDelayed(r, r.delay)
                }
                MotionEvent.ACTION_UP -> {
                    // Button released, remove the runnable from the handler's message queue
                    mHandler.removeCallbacks(r)
                }
            }
            v.onTouchEvent(ev)
        }
        return listener
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putAll(mTetris.saveGame(outState))
        }
        super.onSaveInstanceState(outState)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        mTetris.api.endGame()
    }
}

class TetrisRunnable(handler: Handler, private val lambda: () -> Unit, val delay: Long = 55L) : Runnable {
    private val mHandler = handler

    override fun run() {
        /* We want to execute this runnable repeatedly as long as the button
         * it is attached to is pressed.
         * The runnable will be removed from the handler's message queue
         * when the button is released.
         * See the getOnTouchListener() function in the activity class. */
        lambda()
        mHandler.postDelayed(this, delay)
    }
}

class Tetris(private var activity: Activity, private val savedState: Bundle?) {
    // This classes uses the API to interact with the tetris game engine.
    private val mSettings = SettingsHandler
    var isGamePaused = false
        private set

    // The UI elements we want to manipulate based on events
    private val gameCanvas: GridCanvas = activity.findViewById(R.id.gridCanvas)
    private val nextTetrominoCanvas: NextTetrominoCanvas = activity.findViewById(R.id.nextTetrominoCanvas)
    private val linesText: TextView = activity.findViewById(R.id.txt_lines)
    private val levelText: TextView = activity.findViewById(R.id.txt_level)
    private val scoreText: TextView = activity.findViewById(R.id.txt_score)
    private val timeText: TextView = activity.findViewById(R.id.txt_time)

    // Required for scoring calculation when lines are completed
    private var score = savedState?.getInt(K_SCORE) ?: 0
    private var previousLineCount = 1
    private val scoreMultiplication = listOf(40, 100, 300, 1200) // 1 line, 2 line, 3 lines, 4 lines

    val api = API()
    private var invertRotation = false
    private var gameLevel = savedState?.getInt(K_GAME_LEVEL) ?: 1
    private var lines = savedState?.getInt(K_LINES) ?: 0
    private var gridSize = Point(10, 22)
    private var startingHeight = 0
    private var gameTime = savedState?.getInt(K_GAME_TIME) ?: 0 // Time elapsed in seconds since the game started
    private var gameTimer: CountDownTimer? = null
    private var gameRestored = false
    
    init {
        loadSettings()
        api.createGame(TetrisOptions(gameLevel, gridSize, invertRotation, startingHeight), savedState)
        gameCanvas.setGridSize(gridSize.x, gridSize.y)
        api.addCallback(Event.CoordinatesChanged, ::coordinatesChanged)
        api.addCallback(Event.GridChanged, ::gridChanged)
        api.addCallback(Event.Collision, ::collisionOccurred)
        api.addCallback(Event.LinesCompleted, ::linesCompleted)
        api.addCallback(Event.TetrominoSpawned, ::tetrominoSpawned)
        api.addCallback(Event.GameEnd, ::gameEnded)
        api.addCallback(Event.GameStart, ::gameStarted)
        api.startGame()
        levelText.text = "Level: $gameLevel"
        scoreText.text = "Score: $score"
        linesText.text = "Lines: $lines"
        // FIXME: game time should be updated here too
        if (savedState != null) {
            // A restored game. We need to tell the nextTetrominoCanvas to draw the upcoming ones.
            drawUpcomingTetrominoes()
            gameRestored = true
        }
    }

    private fun loadSettings() {
        // Loads up the game options saved settings
        invertRotation = mSettings.getBoolean(S_INVERT_ROTATION)
        gameLevel = mSettings.getInt(S_GAME_LEVEL)
        if (gameLevel == -1) {
            // Setting doesn't exist. Set to default of 1
            gameLevel = 1
        }

        val gridSizeSetting = mSettings.getString(S_GRID_SIZE)
        if (gridSizeSetting == "") {
            // Not found. Set to default of 10x22
            gridSize = Point(10, 22)
        }
        else {
            // It's a string of "NxN", for example "10x22"
            val (x, y) = gridSizeSetting!!.split("x")
            gridSize = Point(x.toInt(), y.toInt())
        }

        startingHeight = mSettings.getInt(S_STARTING_HEIGHT)
        if (startingHeight == -1) {
            // Not found, set to default of 0
            startingHeight = 0
        }
    }

    fun coordinatesChanged(args: CoordinatesChangedEventArgs) {
        // Called when the tetromino's coordinates changed due to movement
        // Args: the tetromino's previous coordinates, the new coordinates, and the tetromino
        gameCanvas.drawTetromino(args.old.toList(), args.new.toList(), api.getCurrentTetromino())
    }

    fun gridChanged(args: GridChangedEventArgs) {
        // Called when the game's internal grid changes without lines being completed
        // Args: the grid
        gameCanvas.drawGrid(args.grid)
    }

    fun collisionOccurred(args: CollisionEventArgs) {
        // Called when a collision occurs
        // Args: Tetromino coordinates, direction of movement
        println("Collision")
    }

    fun linesCompleted(args: LinesCompletedEventArgs) {
        // Called when lines are completed and the grid changes as a result.
        gameCanvas.linesCompleted(args)
        lines = api.lines()
        gameLevel = api.level()
        linesText.text = "Lines: $lines"
        levelText.text = "Level: $gameLevel"
        
        score += scoreMultiplication[args.lines.size-1] * gameLevel * (startingHeight+1) * previousLineCount
        previousLineCount = args.lines.size
        scoreText.text = "Score: $score"
    }

    fun tetrominoSpawned(args: TetrominoSpawnedEventArgs) {
        // This function is called when a new tetromino is spawned
        // First, draw the updated upcoming tetrominoes
        drawUpcomingTetrominoes()
        // Now draw the newly spawned tetromino
        val coordinates = args.coordinates.toList()
        gameCanvas.drawTetromino(coordinates, coordinates, args.tetromino)
    }

    private fun drawUpcomingTetrominoes() {
        // Get the first 3 upcoming tetrominoes
        // Args: the tetromino's coordinates, and the tetromino
        val upcoming = api.getNextTetromino(3).toMutableList()
        upcoming.reverse()
        nextTetrominoCanvas.upcoming = upcoming
        nextTetrominoCanvas.invalidate()
    }

    fun gameEnded() {
        gameTimer?.cancel()
    }
    
    fun gameStarted() {
        if (gameRestored) {
            gameTime = 0
            gameRestored = false
        }
        startGameTimer()
    }

    fun setGhostEnabled(enabled: Boolean) {
        gameCanvas.ghostEnabled = enabled
    }

    fun setGamePaused(paused: Boolean) {
        // Tell the GridCanvas that the game isn't running, so it should not draw anything.
        isGamePaused = paused
        gameCanvas.setGamePaused(paused)
    }

    fun restartGame() {
        gameCanvas.clearGrid()
        /* If the game is restarted while paused, the GridCanvas's gamePaused property
         * will remain set to "true", which will cause it to draw "PAUSED" on the canvas
         * instead of the new game's grid and moving tetromino. This is why we must
         * explicitly set it to false here, just in case. */
        gameCanvas.setGamePaused(false)
        // Also set the pause button's text back to "Pause"
        val btnPause = activity.findViewById<Button>(R.id.btn_pause)
        btnPause.setText(R.string.btn_pause)
        isGamePaused = false // And this class's property too
        /* Now reset the lines count, level, and score */
        linesText.text = "Lines: 0"
        scoreText.text = "Score: 0"
        levelText.text = "Level: 1"
        api.endGame()
        api.startGame()
    }

    fun saveGame(bundleObj: Bundle): Bundle {
        bundleObj.putInt(K_SCORE, this.score)
        bundleObj.putInt(K_GAME_TIME, this.gameTime)
        return api.saveGame(bundleObj)
    }

    private fun increaseGameTimer() {
        if (isGamePaused) { return }
        gameTime++
        val s = gameTime % 60
        val m = (gameTime-s) / 60
        var mStr = m.toString()
        var sStr = s.toString()
        if (m < 10) { mStr = "0" + mStr }
        if (s < 10) { sStr = "0" + sStr }
        timeText.text = String.format("Time: %s:%s", mStr, sStr)
    }

    private fun startGameTimer() {
        // Creates a CountDownTimer that automatically moves the tetromino downwards every <dropSpeed> milliseconds
        gameTimer?.cancel()
        gameTimer = object : CountDownTimer(600*1000L, 1000L) {
            override fun onTick(millisInFuture: Long) {
                increaseGameTimer()
            }
            override fun onFinish() {
                startGameTimer()
            }
        }.start()
    }

}
