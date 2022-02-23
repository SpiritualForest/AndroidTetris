package com.androidtetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.androidtetris.game.Point
import com.androidtetris.game.TetrominoCode
import com.androidtetris.game.event.LinesCompletedEventArgs

// https://stackoverflow.com/questions/17596053/cannot-call-custom-draw-method-from-another-class-in-android

class GridCanvas(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    /* Properties */
    // TODO: this needs to be customizable
    private val tetrominoColors: HashMap<TetrominoCode, Int> = hashMapOf(
        TetrominoCode.I to Color.CYAN,
        TetrominoCode.O to Color.YELLOW,
        TetrominoCode.T to Color.BLACK,
        TetrominoCode.J to Color.BLUE,
        TetrominoCode.L to Color.MAGENTA,
        TetrominoCode.S to Color.GREEN,
        TetrominoCode.Z to Color.RED,
    )
    private val paint = Paint()
    var canvasBackgroundColor : Int = Color.LTGRAY
    // Grid defaults to 10x22 squares
    private var gridWidth = 10
    private var gridHeight = 22
    private var grid: HashMap<Int, HashMap<Int, TetrominoCode>> = hashMapOf()

    private fun dpToPx(dp: Float): Float {
        val dpi = resources.displayMetrics.densityDpi
        return (dp * (dpi / 160f))
    }

    fun setGridSize(width: Int, height: Int) {
        // Set the grid size in squares
        if ((this.width / width) != (this.height / height)) {
            /* Let's say our canvas size in pixels is 200x440.
             * If we want to set the grid to be composed of 10x22 squares, that works,
             * because each square will have a width and height of 20 pixels (200 / 10 == 20, 440 / 22 == 20).
             * But if the grid width and height do not equally align with the canvas's size,
             * an unequal size of pixels is obtained. This is a problem, because then the square is no longer a square,
             * but rather some other rectangle. This means that, since we want to draw a square, 
             * some axis (x or y) on the display will not be completely full. There will be a small empty space left
             * due to the missing pixels.
             * Say if we wanted to set the grid size to 8x22 squares, and the canvas is still 200x440 pixels.
             * This results in an unequal amount of pixels. 200 / 8 == 25, but 440 / 22 is 20.
             * This is why we must perform this check. */
            return
        }
        // Pixels are equal, size is possible.
        gridWidth = width
        gridHeight = height
    }

    private fun getSizeDp(): Array<Float> {
        val density = resources.displayMetrics.density
        val dpHeight = height / density
        val dpWidth = width / density
        return arrayOf(dpWidth, dpHeight)
    }

    override fun onDraw(canvas: Canvas) {
        /* All drawing must be done through this function.
         * Call canvas.invalidate() to trigger it.
         */
        // Draw border and background
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
        // Now fill the rest with the background colour
        paint.color = canvasBackgroundColor
        canvas.drawRect(1f, 1f, width.toFloat()-1, height.toFloat()-1, paint)

        // Draw the grid
        val (dpWidth, dpHeight) = getSizeDp()
        val squareSizeDp = dpWidth / gridWidth
        for(y in this.grid.keys) {
            for(x in this.grid[y]?.keys!!) {
                val color = tetrominoColors[this.grid[y]!![x]]
                drawSquare(x.toFloat()*squareSizeDp, y.toFloat()*squareSizeDp, squareSizeDp, color!!, canvas)
            }
        }
    }

    private fun drawSquare(x: Float, y: Float, size: Float, color: Int, canvas: Canvas) {
        // x, y, and size are in dp
        paint.color = color
        canvas.drawRect(dpToPx(x)+1, dpToPx(y)+1, dpToPx(x+size)-1, dpToPx(y+size)-1, paint)
    }

    fun drawTetromino(old: List<Point>, new: List<Point>, tetrominoCode: TetrominoCode) {
        // First, remove the tetromino's old coordinates
        for(point in old) {
            var y = point.y
            var x = point.x
            if (this.grid.containsKey(y)) {
                var subMap = this.grid[y]
                if (subMap!!.containsKey(x)) {
                    subMap.remove(x)
                }
            }
        }
        // Now add the new ones
        for(point in new) {
            var y = point.y
            var x = point.x
            if (!this.grid.containsKey(y)) {
                // New row, create new hashmap
                this.grid[y] = hashMapOf(x to tetrominoCode)
            }
            else {
                // Row exists, only need to add value
                this.grid[y]!![x] = tetrominoCode
            }
        }
        this.invalidate()
    }

    fun drawGrid(newGrid: Map<Int, HashMap<Int, TetrominoCode>>) {
        // Copy the grid into our own grid representation object
        this.grid.clear()
        for(y in newGrid.keys) {
            // Create a new copy of the submap
            var subMap = HashMap(newGrid[y])
            this.grid[y] = subMap
        }
        this.invalidate()
    }

    fun linesCompleted(args: LinesCompletedEventArgs) {
        // Line clear animation, etc. TODO.
        // For now, just redraw the grid when this function is called.
        drawGrid(args.grid)
    }
}
