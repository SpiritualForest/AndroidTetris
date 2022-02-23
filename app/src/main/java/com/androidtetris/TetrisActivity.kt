package com.androidtetris

// Our actual game's UI activity

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.androidtetris.game.API
import com.androidtetris.game.Direction
import com.androidtetris.game.event.*
import java.lang.Thread
import kotlin.reflect.*

class TetrisActivity : AppCompatActivity() {
    
    private var executeRunnable = false // For the threads that repeatedly call tetris.api.move()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tetris)
        val canvas = findViewById<GridCanvas>(R.id.gridCanvas)
        val tetris = Tetris(canvas)

        // Movement and rotation buttons
        val down = findViewById<CircleButton>(R.id.btn_down)
        val rotate = findViewById<CircleButton>(R.id.btn_rotate)
        val left = findViewById<CircleButton>(R.id.btn_left)
        val right = findViewById<CircleButton>(R.id.btn_right)
        
        down.setOnTouchListener(getOnTouchListener(tetris, Direction.Down))
        left.setOnTouchListener(getOnTouchListener(tetris, Direction.Left))
        right.setOnTouchListener(getOnTouchListener(tetris, Direction.Right))
        rotate.setOnTouchListener(getOnTouchListener(tetris, null))
    }

    private fun getOnTouchListener(tetrisObj: Tetris, direction: Direction?): View.OnTouchListener {
        // Creates a new OnTouchListener and gives it a runnable that executes tetris.api.move(direction)
        // If the direction is null, it executes the rotate() function instead.
        val listener = object : View.OnTouchListener {
            override fun onTouch(v: View, ev: MotionEvent): Boolean {
                val thread = Thread(Runnable {
                    while(executeRunnable) {
                        if (direction != null) {
                            // move()
                            tetrisObj.api.move(direction)
                        }
                        else { 
                            // Rotate
                            tetrisObj.api.rotate()
                        }
                        Thread.sleep(100)
                    }
                })
                when (ev.action) {
                    MotionEvent.ACTION_DOWN -> {
                        executeRunnable = true
                        thread.start()
                    }
                    MotionEvent.ACTION_UP -> {
                        executeRunnable = false
                        thread.join()
                    }
                }
                return v.onTouchEvent(ev)
            }
        }
        return listener
    }

}

class Tetris(private val canvas: GridCanvas) {
    val api = API()
    init {
        api.addCallback(Event.CoordinatesChanged, ::coordinatesChanged)
        api.addCallback(Event.GridChanged, ::gridChanged)
        api.addCallback(Event.Collision, ::collision)
        api.addCallback(Event.LinesCompleted, ::linesCompleted)
        api.addCallback(Event.GameEnd, ::gameEnd)
        api.startGame()
    }

    fun coordinatesChanged(args: CoordinatesChangedEventArgs) {
        canvas.drawTetromino(args.old.toList(), args.new.toList(), api.getCurrentTetromino())
    }

    fun gridChanged(args: GridChangedEventArgs) {
        canvas.drawGrid(args.grid)
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
        canvas.linesCompleted(args)
    }

    fun gameEnd() {
        println("Game ends")
    }
}
