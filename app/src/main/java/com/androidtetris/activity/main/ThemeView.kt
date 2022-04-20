package com.androidtetris.activity.main

/* AndroidTetris ThemeView */

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.Paint
import com.androidtetris.TetrominoShapeConverter
import com.androidtetris.TetrominoShape // HashMap of shapes
import com.androidtetris.game.TetrominoCode
import com.androidtetris.settings.*
import com.androidtetris.settings.theme.ThemeHandler
import com.androidtetris.R
import com.google.android.material.color.MaterialColors

class ThemeView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    // This view is used to provide a preview for the user's selected colour theme.
    // It is used in MainActivity.
    private val topRow = listOf(TetrominoCode.I, TetrominoCode.T, TetrominoCode.L)
    private val bottomRow = listOf(TetrominoCode.J, TetrominoCode.O, TetrominoCode.S, TetrominoCode.Z)
    private val squareSize = 20
    private val shapeConverter = TetrominoShapeConverter(listOf(listOf(1)), this, squareSize)
    private val paint = Paint()
    
    private fun dpToPx(dp: Float): Float {
        val dpi = resources.displayMetrics.densityDpi
        return (dp * (dpi / 160f))
    }

    override fun onDraw(canvas: Canvas) {
        // Fill in the background colour
        paint.color = MaterialColors.getColor(this, R.attr.colorSurface)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        // Draw the top row first
        var x = dpToPx(5f).toInt()
        var y = dpToPx(10f).toInt()
        drawRow(topRow, x, y, dpToPx(squareSize.toFloat()*4).toInt(), canvas)
        // Now bottom
        x = dpToPx(5f).toInt()
        y = dpToPx(squareSize*2.toFloat()).toInt()
        drawRow(bottomRow, x, y, -1, canvas)
    }
    
    private fun drawRow(row: List<TetrominoCode>, horizontalStartingPosition: Int, verticalStartingPosition: Int, incX: Int, canvas: Canvas) {
        var x = horizontalStartingPosition
        var y = verticalStartingPosition
        val colorValues = ThemeHandler.getThemeColors() // Map<TetrominoCode, Int>
        for(tetromino in row) {
            shapeConverter.shape = TetrominoShape[tetromino]!!
            // The coordinates are in pixels, not dp
            val coordinates = shapeConverter.getCoordinates(verticalStartingPosition = y, horizontalStartingPosition = x)
            paint.color = colorValues[tetromino]!!
            for(point in coordinates) {
                canvas.drawRect(point.x+1, point.y+1, point.x+dpToPx(squareSize.toFloat())-1, point.y+dpToPx(squareSize.toFloat())-1, paint)
            }
            if (incX == -1) { 
                // Special case for bottom row 
                x += dpToPx(squareSize.toFloat()*shapeConverter.shape[0].size).toInt()
            }
            else { x += incX }
        }
    }
}
