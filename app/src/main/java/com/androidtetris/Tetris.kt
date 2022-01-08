package com.androidtetris

import android.graphics.Color
import com.androidtetris.game.API
import com.androidtetris.game.event.*

class Tetris(private val canvas: GridCanvas) {
    val api = API()
    val gridSize = api.getGridSize()
    init {
        api.addCallback(Event.CoordinatesChanged, ::coordinatesChanged)
        api.startGame()
    }

    fun coordinatesChanged(args: CoordinatesChangedEventArgs) {
        val tetrominoCode = api.getCurrentTetromino()
        val squares: MutableList<Square> = mutableListOf()
        for(point in args.new) {
            squares.add(Square(point, tetrominoCode))
        }
        canvas.squaresToDraw = squares.toList()
        //canvas.gridToDraw = api.getGrid()
        canvas.invalidate()
    }
}