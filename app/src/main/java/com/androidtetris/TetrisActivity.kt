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
import android.os.Looper

class TetrisActivity : AppCompatActivity() {
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
        
        var executeRunnable = false
        down.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View, ev: MotionEvent): Boolean {
                val thread = Thread(Runnable {
                    while(executeRunnable) {
                        tetris.api.move(Direction.Down)
                        Thread.sleep(50)
                    }
                })
                when(ev.action) {
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
        })
    }
}

class Tetris(private val canvas: GridCanvas) {
    val api = API()
    init {
        api.addCallback(Event.CoordinatesChanged, ::coordinatesChanged)
        api.addCallback(Event.GridChanged, ::gridChanged)
        api.addCallback(Event.Collision, ::collision)
        api.addCallback(Event.LinesCompleted, ::linesCompleted)
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
}
