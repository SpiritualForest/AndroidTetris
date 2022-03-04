package com.androidtetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.os.Handler
import android.os.Looper
import com.androidtetris.game.Point
import com.androidtetris.game.TetrominoCode
import com.androidtetris.game.event.LinesCompletedEventArgs
import com.androidtetris.game.event.CollisionEventArgs

// TODO: "explosions" animation with "flying pixels" on collision events

class GridCanvas(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    /* This View displays the actual gameplay. I should probably change its name. */

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
    private var currentTetrominoCoordinates: List<Point> = listOf()
    /* In practice, drawGrid() will never be called before drawTetromino().
     * This is why it's safe to have a default TetrominoCode, making the object non-nullable. */
    private var currentTetromino = TetrominoCode.I
    private var collisionPixels: MutableList<Point> = mutableListOf()
    private var collisionOccurred = false // If set to true, the next call to onDraw() will draw the collisionPixels on the canvas.
    private val mHandler = Handler(Looper.getMainLooper())

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
        // In case of a collision event, draw the collision pixels.
        if (!collisionOccurred) { return }
        paint.color = tetrominoColors[currentTetromino]!!
        for(point in collisionPixels) {
            canvas.drawPoint(point.x.toFloat(), point.y.toFloat(), paint)
        }
    }

    private fun drawSquare(x: Float, y: Float, size: Float, color: Int, canvas: Canvas) {
        // x, y, and size are in dp
        paint.color = color
        canvas.drawRect(dpToPx(x)+1, dpToPx(y)+1, dpToPx(x+size)-1, dpToPx(y+size)-1, paint)
    }

    fun drawTetromino(old: List<Point>, new: List<Point>, tetrominoCode: TetrominoCode) {
        currentTetrominoCoordinates = new
        currentTetromino = tetrominoCode
        // Remove the old ones and add the new ones
        removeCoordinates(old)
        addCoordinates(new, tetrominoCode)
        this.invalidate()
    }

    fun removeCoordinates(coordinates: List<Point>) {
        // Remove the coordinates from the grid
        for(point in coordinates) {
            var y = point.y
            var x = point.x
            if (this.grid.containsKey(y)) {
                var subMap = this.grid[y]
                if (subMap!!.containsKey(x)) {
                    subMap.remove(x)
                }
            }
        }
    }
    
    fun addCoordinates(coordinates: List<Point>, tetrominoCode: TetrominoCode) {
        // Add the given coordinates to the grid
        for(point in coordinates) {
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
    }

    fun drawGrid(newGrid: HashMap<Int, HashMap<Int, TetrominoCode>>) {
        // Set the newGrid as the grid
        this.grid = newGrid
        
        // Now we can add the currentTetromino's coordinates to it
        val currentTetromino = this.currentTetromino
        addCoordinates(currentTetrominoCoordinates, currentTetromino)
        this.invalidate()
    }

    fun linesCompleted(args: LinesCompletedEventArgs) {
        // Call the line clearing animation function for all the completed lines,
        // and then redraw the grid.
        val delay = ((gridWidth / 2) * 50) + 5L // By default, (5*50)+5, resulting in 255ms
        for(y in args.lines) {
            clearLine(y)
        }
        mHandler.postDelayed({ drawGrid(args.grid) }, delay)
    }

    fun clearLine(y: Int) {
        /* Line clearing animation function.
         * Remove two squares at a time, starting at the center of the line
         * and continues "outwards" towards the edges. */
        var decreasingCenterx = (gridWidth / 2) - 1
        var delay = 0L
        // Using increasingCenterx as our counter removes the need for a separate counter variable.
        for(increasingCenterx in (gridWidth / 2) until gridWidth) {
            // Create a runnable which removes the current squares
            mHandler.postDelayed(object : Runnable { 
                val dec = decreasingCenterx
                val inc = increasingCenterx
                val y = y
                override fun run() { 
                    // We have to call invalidate() here because removeCoordinates() doesn't.
                    // If we don't do it, the animation won't happen.
                    // Only the grid will be fully redrawn after 255ms.
                    removeCoordinates(listOf(Point(dec, y), Point(inc, y)))
                    invalidate()
                }
            }, delay)
            // Since increasingCenterx is increased as the loop counter, we only need to decrease decreasingCenterx here.
            decreasingCenterx--
            delay += 50
        }
    }

    fun drawCollision(args: CollisionEventArgs) {
        // Draw "explosions" of "flying pixels" when a collision occurs.
        // Basically, calculate the trajectory of the dispersement based on
        // the direction of the collision and the tetromino's coordinates.
        // Add these as Point(x, y) to collisionPixels, and then trigger the redrawing.
        collisionOccurred = true
    }
}
