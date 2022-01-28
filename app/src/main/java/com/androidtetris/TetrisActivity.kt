package com.androidtetris

// Our actual game's UI activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.androidtetris.game.API
import com.androidtetris.game.Direction
import com.androidtetris.game.event.*

class TetrisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tetris)
        val canvas = findViewById<GridCanvas>(R.id.gridCanvas)
        val tetris = Tetris(canvas)

        val left = findViewById<Button>(R.id.btn_left)
        left.setOnClickListener { tetris.api.move(Direction.Left) }
        val right = findViewById<Button>(R.id.btn_right)
        right.setOnClickListener { tetris.api.move(Direction.Right) }
        val down = findViewById<Button>(R.id.btn_down)
        down.setOnClickListener { tetris.api.move(Direction.Down) }
        val rotate = findViewById<Button>(R.id.btn_rotate)
        rotate.setOnClickListener { tetris.api.rotate() }
    }
}

class Tetris(private val canvas: GridCanvas) {
    val api = API()
    val gridSize = api.getGridSize()
    init {
        api.addCallback(Event.CoordinatesChanged, ::coordinatesChanged)
        api.addCallback(Event.GridChanged, ::gridChanged)
        api.startGame()
    }

    fun coordinatesChanged(args: CoordinatesChangedEventArgs) {
        val tetrominoCode = api.getCurrentTetromino()
        val squares: MutableList<Square> = mutableListOf()
        for(point in args.new) {
            squares.add(Square(point, tetrominoCode))
        }
        canvas.drawTetromino(squares.toList())
    }

    fun gridChanged(args: GridChangedEventArgs) {
        canvas.drawGrid(api.getGrid())
    }
}