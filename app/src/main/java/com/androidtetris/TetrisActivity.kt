package com.androidtetris

// Our actual game's UI activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.androidtetris.game.API
import com.androidtetris.game.Direction
import com.androidtetris.game.event.*
import android.util.Log

class TetrisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tetris)
        val canvas = findViewById<GridCanvas>(R.id.gridCanvas)
        val tetris = Tetris(canvas)
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
