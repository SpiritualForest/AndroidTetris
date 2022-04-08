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
import com.androidtetris.settings.ColorHandler // Defined in SettingsHandler.kt

class NextTetrominoCanvas(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    
    // This class just draws all the upcoming tetrominoes on its own canvas.
    // Unlike the GridCanvas, this one is based on converting the shape of the tetrominoes
    // into PointF(x, y) pixel positions on which to draw squares,
    // rather than converting an internal representation of an entire grid into dps and then into pixels.

    var upcoming: MutableList<TetrominoCode> = mutableListOf()
    private val paint = Paint()
    private val squareSizeDp = 15 // dp
    private val squareSizePx = dpToPx(squareSizeDp.toFloat()) // pixels
    private val colorHandler = ColorHandler(context)
    private val tetrominoShapeConverter = TetrominoShapeConverter(listOf(listOf(1, 1, 1)), this, squareSizeDp)

    private fun dpToPx(dp: Float): Float {
        val dpi = resources.displayMetrics.densityDpi
        return (dp * (dpi / 160f))
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
        for(tetromino in upcoming) {
            // NOTE: the coordinates are in pixels, not dp
            val shape = TetrominoShape[tetromino]!!
            paint.color = colorHandler.getColor(tetromino) // Red if setting not found
            tetrominoShapeConverter.shape = shape
            val coordinates = tetrominoShapeConverter.getCoordinates(spacing)
            for(p in coordinates) {
                canvas.drawRect(p.x+1, p.y+1, p.x+squareSizePx-1, p.y+squareSizePx-1, paint)
            }
            spacing += squareSizeDp*3
        }
    }
}
