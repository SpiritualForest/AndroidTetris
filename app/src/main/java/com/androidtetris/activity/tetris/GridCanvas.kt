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
import android.util.Log

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
    private var sortedGridKeys: List<Int> = listOf()
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
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), paint) // Left
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), paint) // Right
        // Now top and bottom
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, paint) // Top
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paint) // Bottom
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
        // Sort the y-axis keys for the ghost piece function
        // We must always sort the keys because the ghost relies on them
        // for calculating its position. If enabled suddenly mid-game
        // and the sorted keys list is empty, but the grid is not empty,
        // the ghost might be drawn at a position that collides with squares in the grid.
        this.sortedGridKeys = this.grid.keys.toList().sortedBy { it }
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
        var lowestRow = 0 // Lowest y axis
        currentTetrominoCoordinates.forEach { 
            coordinatesCopy.add(Point(it.x, it.y))
            if (it.y > lowestRow) { lowestRow = it.y }
        }
        // Find the starting row for checking collisions
        val closestRow = findClosestLarger(lowestRow, this.sortedGridKeys)
        val diff = (closestRow - lowestRow)
        // Now increase the coordinates y value by diff-1
        // diff-1 because otherwise the bottom-most part of the tetromino will end up on the closestRow.
        coordinatesCopy.forEach { it.y += diff-1 }
        // No collision was detected after the initial hard-drop, so now we continue downwards.
        while(!isGhostCollision(coordinatesCopy)) {
            // Move the copied coordinates downwards until a collision occurs
            for(point in coordinatesCopy) {
                point.y += 1
            }
        }
        ghostCoordinates = coordinatesCopy.toList()
        // If there was a collision on the first iteration, the ghost coordinates will remain empty.
    }
    
    private fun findClosestLarger(n: Int, a: List<Int>): Int {
        /* Modified binary search algorithm that returns the closest
         * element in a to n, which is LARGER than n.
         * It never returns n itself. */
        if (a.isEmpty()) {
            // Empty list, return the lowest row (gridHeight-1)
            return gridHeight-1
        }
        if (n < a[0]) {
            // The first element in the list is already larger than n, so we return that.
            return a[0]
        }
        if (n >= gridHeight-1) {
            return gridHeight-1
        }
        var L = 0
        var H = a.size
        while(true) {
            var m = (L+H) / 2
            if (n >= a[m]) {
                // Examine the higher half next iteration
                L = m
            }
            else {
                if (n >= a[m-1]) {
                    // Because n was smaller than a[m] to get here,
                    // but now it is larger than a[m-1],
                    // this means that a[m] is the closest element in value to n,
                    // that is larger than it. So we found it.
                    return a[m]
                }
                // Examine the lower half next iteration
                H = m
            }
        }
    }

    private fun isGhostCollision(coordinates: List<Point>): Boolean {
        // Checks if there's a collision for every y+1, x of the supplied coordinates, in the grid.
        // y+1 because if we checked y, that's where the tetromino's lowest y point is, and therefore it
        // is guaranteed to not be occupied in the grid. If we did this, the bottom-most line of the ghost
        // would be drawn on the next line.
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
