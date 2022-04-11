/* A class that calculates the positioning for a single tetromino's coordinates, in pixels,
 * based on the width of the View and the tetromino, and the coordinates list supplied.
 * The coordinates list is a list of lists such as:
 * TetrominoCode.I to listOf(listOf(1, 1, 1, 1)), where 1 represents a drawable square, and 0 means skip this position
 * TetrominoCode.J to listOf(listOf(1, 0, 0), listOf(1, 1, 1)) will be converted to x,y points that represent the shape:
 * #
 * ###
 */

package com.androidtetris
import android.graphics.PointF
import android.view.View
import com.androidtetris.game.TetrominoCode

// Global constant. A simple map that maps each tetromino code to its shape declaration
val TetrominoShape = mapOf(
    TetrominoCode.I to listOf(listOf(1, 1, 1, 1)),
    TetrominoCode.O to listOf(listOf(1, 1), listOf(1, 1)),
    TetrominoCode.J to listOf(listOf(1, 0, 0), listOf(1, 1, 1)),
    TetrominoCode.L to listOf(listOf(0, 0, 1), listOf(1, 1, 1)),
    TetrominoCode.S to listOf(listOf(0, 1, 1), listOf(1, 1, 0)),
    TetrominoCode.Z to listOf(listOf(1, 1, 0), listOf(0, 1, 1)),
    TetrominoCode.T to listOf(listOf(1, 1, 1), listOf(0, 1, 0))
)

class TetrominoShapeConverter(var shape: List<List<Int>>, private var view: View, private var squareSize: Int = 15) {
    /* Parameters:
     * shape: a shape to draw, like the ones declared in TetrominoShape
     * view: the View object that wants to convert the shape into pixels.
     * We need the view because we have to know its size to perform the required calculations.
     * squareSize: the size of each square, in dp. Defaults to 15
     */
    private var tetrominoWidth = 0

    private fun dpToPx(dp: Float): Float {
        val dpi = view.resources.displayMetrics.densityDpi
        return (dp * (dpi / 160f))
    }

    private fun getWidthDp(): Float {
        // Returns the view width in dp.
        return view.width / view.resources.displayMetrics.density
    }

    private fun getCenter(): Float {
        // Returns the position in the canvas on which to start drawing the first square
        return (getWidthDp() / 2) - ((tetrominoWidth*squareSize) / 2)
    }

    fun getCoordinates(verticalStartingPosition: Int = 0, horizontalStartingPosition: Int = -1): List<PointF> {
        // Converts the "coordinates map" of each tetromino to PointF(x, y) pixel coordinates
        // First, set the width of the tetromino based on the given shape
        this.tetrominoWidth = shape[0].size
        val center = getCenter() // Starting position for x
        val coordinatesList: MutableList<PointF> = mutableListOf()
        var y = verticalStartingPosition
        var x = 0f
        for(sublist in shape) {
            // If the horizontal starting position is -1, we restart at the center.
            // Otherwise, we start wherever that position is set to.
            x = if (horizontalStartingPosition == -1) {
                center
            } else {
                horizontalStartingPosition.toFloat()
            }
            for(v in sublist) {
                if (v == 1) {
                    // Draw a square at this x,y - so add this point to the list.
                    coordinatesList.add(PointF(dpToPx(x.toFloat()), dpToPx(y.toFloat())))
                }
                x += squareSize
            }
            // Move downwards to the next row
            y += squareSize
        }
        return coordinatesList.toList()
    }
}

