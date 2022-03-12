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

class NextTetrominoCanvas(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    
    // This class just draws all the upcoming tetrominoes on its own canvas.
    // Unlike the GridCanvas, this one is based on dips,
    // rather than converting an internal representation of the whole grid into dps.

    var upcoming: MutableList<TetrominoCode> = mutableListOf()
    private val paint = Paint()
    private val squareSize = 15 // In dps

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
        val x = resources
        // Now top and bottom
        for(x in 0 until width) {
            canvas.drawPoint(x.toFloat(), 0f, paint)
            canvas.drawPoint(x.toFloat(), height.toFloat()-1, paint)
        }

        // Draw all the tetrominoes
        var spacing = 0 // For vertical spacing between the tetrominoes
        for(t in upcoming) {
            // NOTE: the coordinates are in pixels, not dp
            val shape = TetrominoShape[t]!!
            val coordinates = TetrominoShapeConverter(shape, this, squareSize).getCoordinates(spacing)
            paint.color = Color.RED // FIXME: this should come from settings per tetromino
            for(p in coordinates) {
                canvas.drawRect(p.x+1, p.y+1, p.x+dpToPx(squareSize.toFloat())-1, p.y+dpToPx(squareSize.toFloat())-1, paint)
            }
            spacing += squareSize*3
        }
    }
}
