package com.androidtetris.activity.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.os.Handler
import android.os.Looper
import com.androidtetris.ColorHandler
import com.androidtetris.game.*
import com.androidtetris.game.event.*


// TODO: "explosions" animation with "flying pixels" on collision events

class GridCanvas(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    /* This View displays the actual gameplay. I should probably change its name. */

    /* Properties */
    private val paint = Paint()
    private var canvasBackgroundColor : Int = Color.LTGRAY
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
    private val colorHandler = ColorHandler(context)
    private val tetrominoColors: Map<TetrominoCode, Int> = colorHandler.getAllColors()
    var ghostEnabled = false // Is the ghost piece feature enabled?
    private var ghostCoordinates: List<Point> = listOf()

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

    private fun getSizeDp(): PointF {
        val density = resources.displayMetrics.density
        val dpHeight = height / density
        val dpWidth = width / density
        return PointF(dpWidth, dpHeight)
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
        val dpWidth = getSizeDp().x
        val squareSizeDp = dpWidth / gridWidth
        for(y in this.grid.keys) {
            for(x in this.grid[y]?.keys!!) {
                val color: Int = tetrominoColors[this.grid[y]!![x]]!!
                drawSquare(x.toFloat()*squareSizeDp, y.toFloat()*squareSizeDp, squareSizeDp, color, canvas)
            }
        }
        // Draw the ghost
        // First, set the colour
        if (!ghostEnabled) { return }
        val colorInt = colorHandler.getColor(currentTetromino)
        val red = (colorInt and 0xff) shl 16
        val green = (colorInt and 0x00ff) shl 8
        val blue = colorInt and 0x0000ff
        val argb = Color.argb(50, red, green, blue)
        for(point in ghostCoordinates) {
            val x = point.x // dps
            val y = point.y // dps
            drawSquare(x.toFloat()*squareSizeDp, y.toFloat()*squareSizeDp, squareSizeDp, argb, canvas)
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
        // Remove the old coordinates and add the new ones
        removeCoordinates(old)
        if (ghostEnabled) {
            drawGhost()
        }
        // If we add the new coordinates to the grid before drawing the ghost piece,
        // its collision detection mechanism will detect the new coordinates as a colliding object,
        // and not draw the ghost piece. This is why we must call addCoordinates()
        // AFTER drawing the ghost.
        addCoordinates(new, tetrominoCode)
        this.invalidate()
    }

    fun removeCoordinates(coordinates: List<Point>) {
        // Remove the coordinates from the grid
        for(point in coordinates) {
            val y = point.y
            val x = point.x
            if (this.grid.containsKey(y)) {
                val subMap = this.grid[y]
                if (subMap!!.containsKey(x)) {
                    subMap.remove(x)
                }
            }
        }
    }
    
    private fun addCoordinates(coordinates: List<Point>, tetrominoCode: TetrominoCode) {
        // Add the given coordinates to the grid
        for(point in coordinates) {
            val y = point.y
            val x = point.x
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

    private fun clearLine(y: Int) {
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

    private fun drawGhost() {
        /* Draws the ghost piece.
         * Note that we do NOT call invalidate() here, because this function
         * is called by the drawTetromino() function, only if the ghost feature is enabled.
         * drawTetromino() already calls invalidate(), so we don't need to call it here. */

        // In the collision detection function, we check if all the points y+1
        // collides with something. If there are no collisions, we increase the y axis
        // in the actual Point object itself and assign the coordinates list to our
        // ghostCoordinates property.
        // This way we don't need to repeatedly make copies of everything.
        // We only copy the tetromino coordinates' point objects once, and operate on those.
        val coordinatesCopy: MutableList<Point> = mutableListOf()
        currentTetrominoCoordinates.forEach { coordinatesCopy.add(Point(it.x, it.y)) }
        while(!isDownwardsCollision(coordinatesCopy)) {
            // Move the copied coordinates downwards until a collision occurs
            ghostCoordinates = coordinatesCopy.toList()
            for(point in coordinatesCopy) {
                point.y += 1
            }
        }
    }

    private fun isDownwardsCollision(coordinates: List<Point>): Boolean {
        // Checks if there's a collision for every y+1, x of the supplied coordinates, in the grid.
        // This function is used solely for the ghost piece's hard-dropping.
        for(point in coordinates) {
            val y = point.y+1
            if (y > gridHeight-1) { return true }
            if (this.grid.containsKey(y) && this.grid[y]!!.containsKey(point.x)) {
                return true
            }
        }
        return false
    }
}
