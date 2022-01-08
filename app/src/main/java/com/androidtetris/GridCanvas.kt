package com.androidtetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.androidtetris.Tetris
import com.androidtetris.game.Point
import com.androidtetris.game.Tetromino
import com.androidtetris.game.TetrominoCode

// https://stackoverflow.com/questions/17596053/cannot-call-custom-draw-method-from-another-class-in-android

data class Square(val coordinates: Point, val tetrominoCode: TetrominoCode)

class GridCanvas(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    /* Properties */
    private val tetrominoColors: HashMap<TetrominoCode, Int> = hashMapOf(
        TetrominoCode.I to Color.CYAN,
        TetrominoCode.O to Color.YELLOW,
        TetrominoCode.T to Color.BLACK,
        TetrominoCode.J to Color.BLUE,
        TetrominoCode.L to Color.MAGENTA,
        TetrominoCode.S to Color.GREEN,
        TetrominoCode.Z to Color.RED,
    )
    var paint = Paint()
    // FIXME: This is very ugly. Do not do it like this for real!
    var squaresToDraw: List<Square> = listOf()
    //var gridToDraw: List<List<TetrominoCode?>> = listOf()
    var canvasBackgroundColor : Int = Color.LTGRAY
    //private lateinit var tetrisObj: Tetris // Our tetris game object
    /* End of properties */

    private fun dpToPx(dp: Float): Float {
        val dpi = resources.displayMetrics.densityDpi
        return (dp * (dpi / 160f))
    }

    override fun onDraw(canvas: Canvas) {
        /* All drawing must be done through this function.
         * Call canvas.invalidate() to trigger it.
         */
        super.onDraw(canvas)
        val density = resources.displayMetrics.density
        val dpHeight = height / density
        val dpWidth = width / density

        // Draw border
        paint.strokeWidth = 1f
        paint.color = Color.BLACK
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        // Draw the background
        paint.strokeWidth = 1f
        paint.color = canvasBackgroundColor
        canvas.drawRect(1f, 1f, (width - 1).toFloat(), (height - 1).toFloat(), paint)

        val gridWidth = 10 //tetrisObj.gridSize?.x
        val squareSizeDp = dpWidth / gridWidth
        // Redraw the existing grid first
        // FIXME: This is very ugly and will be a nightmare to maintain! Refactor this after experimentation!
        /*for((y, sublist) in gridToDraw.withIndex()) {
            for((x, tetrominoCode) in sublist.withIndex()) {
                if (tetrominoCode == null) { continue }
                val color = tetrominoColors[tetrominoCode]!!
                val yDp = y*squareSizeDp
                val xDp = x*squareSizeDp
                drawSquare(xDp, yDp, squareSizeDp, color, canvas)
            }
        }*/
        if (squaresToDraw.count() > 0) {
            // Draw these squares and then clear the list
            for(sq in squaresToDraw) {
                val color = tetrominoColors[sq.tetrominoCode]!!
                val y = sq.coordinates.y*squareSizeDp
                val x = sq.coordinates.x*squareSizeDp
                drawSquare(x, y, squareSizeDp, color, canvas)
            }
            squaresToDraw = listOf()
        }
    }

    private fun drawSquare(x: Float, y: Float, size: Float, color: Int, canvas: Canvas) {
        paint.color = color
        canvas.drawRect(dpToPx(x), dpToPx(y), dpToPx(x+size-1), dpToPx(y+size-1), paint)
    }
}