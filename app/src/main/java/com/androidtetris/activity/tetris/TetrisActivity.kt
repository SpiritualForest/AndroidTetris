package com.androidtetris.activity.tetris

// Our actual game's UI activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.androidtetris.game.API
import com.androidtetris.game.Direction
import com.androidtetris.game.event.*
import android.os.Handler
import android.os.Looper
import android.app.Activity
import com.androidtetris.activity.tetris.NextTetrominoCanvas
import com.androidtetris.R

// TODO: handle activity OnPause/OnResume

class TetrisActivity : AppCompatActivity() {
    
    private lateinit var mTetris: Tetris
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tetris)
        
        // Create a new Tetris object and pass the activity.
        // This way we can find the UI elements we want to manipulate from the Tetris object itself,
        // and don't have to find them here and then pass them.
        mTetris = Tetris(this)

        // Movement and rotation buttons
        val down = findViewById<CircleButton>(R.id.btn_down)
        val rotate = findViewById<CircleButton>(R.id.btn_rotate)
        val left = findViewById<CircleButton>(R.id.btn_left)
        val right = findViewById<CircleButton>(R.id.btn_right)

        // Our handler for the touch event runnables
        mHandler = Handler(Looper.getMainLooper())
        
        // Custom OnTouchListeners for each button
        down.setOnTouchListener(getOnTouchListener(TetrisRunnable(mHandler, { mTetris.api.move(Direction.Down) })))
        left.setOnTouchListener(getOnTouchListener(TetrisRunnable(mHandler, { mTetris.api.move(Direction.Left) })))
        right.setOnTouchListener(getOnTouchListener(TetrisRunnable(mHandler, { mTetris.api.move(Direction.Right) })))
        // Rotate gets 60ms delay instead of the default 50ms
        rotate.setOnTouchListener(getOnTouchListener(TetrisRunnable(mHandler, { mTetris.api.rotate() }, 60L)))
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
    
    override fun onDestroy() {
        super.onDestroy()
        mTetris.api.endGame()
    }
}

class TetrisRunnable(handler: Handler, lambda: () -> Unit, val delay: Long = 50L) : Runnable {
    private val mHandler = handler
    private val lambda = lambda // The lambda that the runnable will run

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

class Tetris(activity: Activity) {
    // This classes uses the API to interact with the tetris game engine.

    // The UI elements we want to manipulate based on events
    private val gameCanvas: GridCanvas = activity.findViewById<GridCanvas>(R.id.gridCanvas)
    private val nextTetrominoCanvas: NextTetrominoCanvas = activity.findViewById<NextTetrominoCanvas>(R.id.nextTetrominoCanvas)
    private val linesText: TextView = activity.findViewById<TextView>(R.id.txt_lines)
    private val levelText: TextView = activity.findViewById<TextView>(R.id.txt_level)
    private val scoreText: TextView = activity.findViewById<TextView>(R.id.txt_score)
    
    // Required for scoring calculation when lines are completed
    private var score = 0
    private var previousLineCount = 1
    private val scoreMultiplication = listOf(40, 100, 300, 1200) // 1 line, 2 line, 3 lines, 4 lines

    val api = API()
    init {
        api.addCallback(Event.CoordinatesChanged, ::coordinatesChanged)
        api.addCallback(Event.GridChanged, ::gridChanged)
        api.addCallback(Event.Collision, ::collision)
        api.addCallback(Event.LinesCompleted, ::linesCompleted)
        api.addCallback(Event.TetrominoSpawned, ::tetrominoSpawned)
        api.addCallback(Event.GameEnd, ::gameEnd)
        api.addCallback(Event.GameStart, ::gameStart)
        api.startGame()
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

    fun collision(args: CollisionEventArgs) {
        // Called when a collision occurs
        // Args: Tetromino coordinates, direction of movement
        println("Collision")
    }

    fun linesCompleted(args: LinesCompletedEventArgs) {
        // Called when lines are completed and the grid changes as a result.
        gameCanvas.linesCompleted(args)
        val lines = api.lines()
        val level = api.level()
        linesText.text = "Lines: $lines"
        levelText.text = "Level: $level"
        
        score += scoreMultiplication[args.lines.size-1] * level * previousLineCount
        previousLineCount = args.lines.size
        scoreText.text = "Score: $score"
    }

    fun tetrominoSpawned(args: TetrominoSpawnedEventArgs) {
        // Get the first 3 upcoming tetrominoes
        // Args: the tetromino's coordinates, and the tetromino
        val upcoming = api.getNextTetromino(3).toMutableList()
        upcoming.reverse()
        nextTetrominoCanvas.upcoming = upcoming
        nextTetrominoCanvas.invalidate()
        // Now draw this one
        val coordinates = args.coordinates.toList()
        gameCanvas.drawTetromino(coordinates, coordinates, args.tetromino)
    }        

    fun gameEnd() {
        println("Game ends")
    }

    fun gameStart() {
        println("Game starts")
    }
}
