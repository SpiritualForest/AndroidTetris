package com.androidtetris

import android.view.View
import android.graphics.Point
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import com.androidtetris.game.TetrominoCode

class NextTetrominoCanvas(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    
    // This class just draws all the upcoming tetrominoes on its own canvas.
    // Unlike the GridCanvas, this one is based on dips,
    // rather than converting an internal representation of the whole grid into dps.
    private val tetrominoCoordinates = mapOf(
        TetrominoCode.I to listOf(listOf(1, 1, 1, 1)),
        TetrominoCode.O to listOf(listOf(1, 1), listOf(1, 1)),
        TetrominoCode.J to listOf(listOf(1, 0, 0), listOf(1, 1, 1)),
        TetrominoCode.L to listOf(listOf(0, 0, 1), listOf(1, 1, 1)),
        TetrominoCode.S to listOf(listOf(0, 1, 1), listOf(1, 1, 0)),
        TetrominoCode.Z to listOf(listOf(1, 1, 0), listOf(0, 1, 1)),
        TetrominoCode.T to listOf(listOf(1, 1, 1), listOf(0, 1, 0))
    )

    var upcoming: MutableList<TetrominoCode> = mutableListOf()
    private val paint = Paint()
    private var grid: HashMap<Int, HashMap<Int, TetrominoCode>> = hashMapOf()
    private val squareSize = 15 // In dps
    
    private fun dpToPx(dp: Float): Float {
        val dpi = resources.displayMetrics.densityDpi
        return (dp * (dpi / 160f))
    }

    private fun getWidthDp(): Float {
        return width / resources.displayMetrics.density
    }

    fun getCenter(tetromino: TetrominoCode): Int {
        // Returns the center position in dp
        val tWidth = when(tetromino) {
            TetrominoCode.I -> 4
            TetrominoCode.O -> 2
            else -> 3
        }
        val center = (getWidthDp() / 2) - ((tWidth*squareSize) / 2)
        return center.toInt()
    }

    fun getCoordinates(tetromino: TetrominoCode, spacing: Int): List<Point> {
        // Converts the "coordinates map" of each tetromino to Point(x, y) dp coordinates
        val center = getCenter(tetromino) // Starting position for x
        val coordinates = tetrominoCoordinates[tetromino]!!
        val coordinatesList: MutableList<Point> = mutableListOf()
        var y = spacing // Starting position for y
        for(sublist in coordinates) {
            var x = center
            for(v in sublist) {
                if (v == 1) { 
                    coordinatesList.add(Point(x, y))
                }
                x += squareSize
            }
            y += squareSize
        }
        return coordinatesList.toList()
    }

    override fun onDraw(canvas: Canvas) {
        // First, draw the border
        paint.color = Color.BLACK
        // First left and right borders
        for(y in 0 until height) {
            canvas.drawPoint(0f, y.toFloat(), paint)
            canvas.drawPoint(width.toFloat()-1, y.toFloat(), paint)
        }
        // Now top and bottom
        for(x in 0 until width) {
            canvas.drawPoint(x.toFloat(), 0f, paint)
            canvas.drawPoint(x.toFloat(), height.toFloat()-1, paint)
        }

        // Draw all the tetrominoes
        var spacing = 0 // For vertical spacing between the tetrominoes
        for(t in upcoming) {
            val coordinates = getCoordinates(t, spacing)
            paint.color = Color.RED // FIXME: this should come from settings per tetromino
            for(p in coordinates) {
                canvas.drawRect(dpToPx(p.x.toFloat())+1, dpToPx(p.y.toFloat())+1, 
                dpToPx(p.x+squareSize.toFloat())-1, dpToPx(p.y+squareSize.toFloat())-1, paint)
            }
            spacing += squareSize*3
        }
    }
}
