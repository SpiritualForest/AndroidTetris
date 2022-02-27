package com.androidtetris

// Our actual game's UI activity

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.androidtetris.game.API
import com.androidtetris.game.Direction
import com.androidtetris.game.event.*
import android.os.Handler
import android.os.Looper

class TetrisActivity : AppCompatActivity() {
    
    private lateinit var mTetris: Tetris
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tetris)
        val gameCanvas = findViewById<GridCanvas>(R.id.gridCanvas)
        val nextTetrominoCanvas = findViewById<NextTetrominoCanvas>(R.id.nextTetrominoCanvas)
        mTetris = Tetris(gameCanvas, nextTetrominoCanvas)

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

class TetrisRunnable(handler: Handler, lambda: () -> Unit, delay: Long = 50L) : Runnable {
    private val mHandler = handler
    private val lambda = lambda // The lambda that the runnable will run
    val delay = delay

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

class Tetris(private val gameCanvas: GridCanvas, private val nextTetrominoCanvas: NextTetrominoCanvas) {
    val api = API()
    init {
        api.addCallback(Event.CoordinatesChanged, ::coordinatesChanged)
        api.addCallback(Event.GridChanged, ::gridChanged)
        api.addCallback(Event.Collision, ::collision)
        api.addCallback(Event.LinesCompleted, ::linesCompleted)
        api.addCallback(Event.GameEnd, ::gameEnd)
        api.addCallback(Event.GameStart, ::gameStart)
        api.startGame()
    }

    fun coordinatesChanged(args: CoordinatesChangedEventArgs) {
        gameCanvas.drawTetromino(args.old.toList(), args.new.toList(), api.getCurrentTetromino())
        
        // Get the first 3 upcoming tetrominoes
        val upcoming = api.getNextTetromino(3).toMutableList()
        upcoming.reverse()
        nextTetrominoCanvas.upcoming = upcoming
        nextTetrominoCanvas.invalidate()
    }

    fun gridChanged(args: GridChangedEventArgs) {
        gameCanvas.drawGrid(args.grid)
    }

    fun collision(args: CollisionEventArgs) {
        val direction = args.direction
        Log.d("CollisionEvent", "Collision event occurred: $direction")
        val tetromino = api.getCurrentTetromino()
        Log.d("CollisionEvent", "Colliding tetromino: $tetromino")
        val grid = api.getGrid()
        Log.d("CollisionEvent", "Grid at time of collision: $grid")
    }

    fun linesCompleted(args: LinesCompletedEventArgs) {
        gameCanvas.linesCompleted(args)
    }

    fun gameEnd() {
        println("Game ends")
    }

    fun gameStart() {
        println("Game starts")
    }
}
