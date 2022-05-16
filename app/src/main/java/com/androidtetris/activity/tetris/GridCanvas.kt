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
import com.google.android.material.color.MaterialColors
import com.androidtetris.settings.* // For S_GHOST_ENABLED
import com.androidtetris.game.*
import com.androidtetris.game.event.*
import com.androidtetris.R
import com.androidtetris.settings.theme.ThemeHandler // For colours

class GridCanvas(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    /* This View displays the actual gameplay. I should probably change its name. */

    /* Properties */
    private val paint = Paint()
    // Grid defaults to 10x22 squares
    private var gridWidth = 10
    private var gridHeight = 22
    private var grid: HashMap<Int, HashMap<Int, TetrominoCode>> = hashMapOf()
    private var currentTetrominoCoordinates: List<Point> = listOf()
    /* In practice, drawGrid() will never be called before drawTetromino().
     * This is why it's safe to have a default TetrominoCode, making the object non-nullable. */
    private var currentTetromino = TetrominoCode.I
    private val mHandler = Handler(Looper.getMainLooper())
    private val tetrominoColors: Map<TetrominoCode, Int> = ThemeHandler.getThemeColors()
    var ghostEnabled: Boolean = SettingsHandler.getGhostEnabled() // Ghost piece feature enabled?
    private var ghostCoordinates: List<Point> = listOf()
    private var gamePaused = false // If true, will draw "PAUSE" on the canvas when onDraw() is called

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
        paint.color = MaterialColors.getColor(this, R.attr.colorOnPrimary)
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
        if (gamePaused) {
            // onDraw() called when the game is paused.
            // Draw "PAUSE" at the center of the canvas.
            val text = "PAUSE"
            val textSize = 50f
            val x = (width / 2) - ((text.length / 2) * textSize) + (text.length / 2)
            val y = (height / 2) - (textSize / 2)
            paint.color = MaterialColors.getColor(this, R.attr.colorOnPrimary)
            paint.textSize = textSize
            canvas.drawText("PAUSE", x, y, paint)
            return
        }
        val dpWidth = getSizeDp().x
        val squareSizeDp = dpWidth / gridWidth
        
        // Draw the ghost first, because if we draw the tetromino and then the ghost,
        // it creates this weird coloured squares instad of the tetromino's real colour,
        // once it drops into the position that the ghost occupies. I don't know why this happens, yet.
        // The colour comes from the the theme's colorOnSurface attribute, with alpha (transparency) set to 50.
        if (ghostEnabled) {
            /* First, compare the ghost's coordinates with the tetromino's coordinates.
             * If they are equal, it means we no longer draw the ghost, even
             * if the tetromino moves side to side again. */
            var equal = true
            for((i, ghostPoint) in ghostCoordinates.withIndex()) {
                val tetPoint = currentTetrominoCoordinates[i]
                if ((ghostPoint.y != tetPoint.y) or (ghostPoint.x != tetPoint.x)) {
                    equal = false
                    break
                }
            }
            if (equal) {
                ghostCoordinates = listOf()
            }
            val colorInt = MaterialColors.getColor(this, R.attr.colorOnSurface)
            val red = colorInt and 0xff
            val green = colorInt and 0x00ff
            val blue = colorInt and 0x0000ff
            val argb = Color.argb(50, red, green, blue)
            for(point in ghostCoordinates) {
                val x = point.x // dps
                val y = point.y // dps
                drawSquare(x.toFloat()*squareSizeDp, y.toFloat()*squareSizeDp, squareSizeDp, argb, canvas)
            }
        }

        // Draw the tetromino
        for(tetPoint in currentTetrominoCoordinates) {
            val x = tetPoint.x
            val y = tetPoint.y
            val color = tetrominoColors[currentTetromino]!!
            drawSquare(x.toFloat()*squareSizeDp, y.toFloat()*squareSizeDp, squareSizeDp, color, canvas)
        }

        // Draw the grid
        for(y in this.grid.keys) {
            for(x in this.grid[y]?.keys!!) {
                val color: Int = tetrominoColors[this.grid[y]!![x]]!!
                drawSquare(x.toFloat()*squareSizeDp, y.toFloat()*squareSizeDp, squareSizeDp, color, canvas)
            }
        }
    }

    private fun drawSquare(x: Float, y: Float, size: Float, color: Int, canvas: Canvas) {
        // x, y, and size are in dp
        paint.color = color
        canvas.drawRect(dpToPx(x)+1, dpToPx(y)+1, dpToPx(x+size)-1, dpToPx(y+size)-1, paint)
    }

    fun drawTetromino(new: List<Point>, tetrominoCode: TetrominoCode) {
        // Set the coordinates and tetromino code
        currentTetrominoCoordinates = new
        currentTetromino = tetrominoCode
        // Draw the ghost if enabled
        if (ghostEnabled) {
            drawGhost()
        }
        // The actual drawing of the tetromino takes place in onDraw()
        // We only set its coordinates and instruct the View to be updated here.
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
        this.invalidate()
    }

    fun linesCompleted(args: LinesCompletedEventArgs) {
        // Call the line clearing animation function for all the completed lines,
        // and then redraw the grid.
        val delay = ((gridWidth / 2) * 50) + 5L // By default, (5*50)+5, resulting in 255ms
        // Add the current tetromino's coordinates to the grid, otherwise it will disappear briefly
        // while the line clearing takes place.
        addCoordinates(currentTetrominoCoordinates, currentTetromino)
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
        while(!isGhostCollision(coordinatesCopy)) {
            // Move the copied coordinates downwards until a collision occurs
            ghostCoordinates = coordinatesCopy.toList()
            for(point in coordinatesCopy) {
                point.y += 1
            }
        }
    }

    private fun isGhostCollision(coordinates: List<Point>): Boolean {
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

    fun setGamePaused(paused: Boolean) {
        // The call to invalidate() will either clear the canvas
        // or draw the grid onto it, depending on whether running is true or false
        // true == draw, false == clear
        gamePaused = paused
        invalidate()
    }

    fun clearGrid() { 
        this.grid.clear()
    }
}
