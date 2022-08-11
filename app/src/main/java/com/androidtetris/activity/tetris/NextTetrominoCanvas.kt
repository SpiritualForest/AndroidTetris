package com.androidtetris.activity.tetris

import android.view.View
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.androidtetris.game.TetrominoCode
import com.androidtetris.TetrominoShapeConverter
import com.androidtetris.TetrominoShape // Default tetromino coordinates
import com.androidtetris.settings.theme.ThemeHandler // For tetromino colours
import com.androidtetris.R
import com.google.android.material.color.MaterialColors

class NextTetrominoCanvas(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    
    // This class just draws all the upcoming tetrominoes on its own canvas.
    // Unlike the GridCanvas, this one is based on converting the shape of the tetrominoes
    // into PointF(x, y) pixel positions on which to draw squares,
    // rather than converting an internal representation of an entire grid into dps and then into pixels.

    var upcoming: MutableList<TetrominoCode> = mutableListOf()
    private val paint = Paint()
    private val squareSizeDp = 15 // dp
    private val squareSizePx = dpToPx(squareSizeDp.toFloat()) // pixels
    private val colors = ThemeHandler.getThemeColors()
    private val tetrominoShapeConverter = TetrominoShapeConverter(listOf(listOf(1, 1, 1)), this, squareSizeDp)

    private fun dpToPx(dp: Float): Float {
        val dpi = resources.displayMetrics.densityDpi
        return (dp * (dpi / 160f))
    }

    override fun onDraw(canvas: Canvas) {
        // First, draw the border
        paint.color = MaterialColors.getColor(this, R.attr.colorOnPrimary)
        // First left and right borders
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), paint) // Left
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), paint) // Right
        // Now top and bottom
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, paint) // Top
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paint) // Bottom
        
        // Draw all the tetrominoes
        var spacing = 0 // For vertical spacing between the tetrominoes
        for(tetromino in upcoming) {
            // NOTE: the coordinates are in pixels, not dp
            val shape = TetrominoShape[tetromino]!!
            paint.color = colors[tetromino]!!
            tetrominoShapeConverter.shape = shape
            val coordinates = tetrominoShapeConverter.getCoordinates(spacing)
            for(p in coordinates) {
                canvas.drawRect(p.x+1, p.y+1, p.x+squareSizePx-1, p.y+squareSizePx-1, paint)
            }
            spacing += squareSizeDp*3
        }
    }
}
