package com.androidtetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GridCanvas(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    /* Properties */
    var paint = Paint()
    private lateinit var canvas: Canvas
    private var density : Float = resources.displayMetrics.density
    private var dpWidth : Float = 0f
    private var dpHeight : Float = 0f
    var canvasBackgroundColor : Int = Color.LTGRAY
    /* End of properties */

    private fun dpToPx(dp: Float): Int {
        val dpi = resources.displayMetrics.densityDpi
        return (dp * (dpi / 160f)).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        // Set properties
        this.canvas = canvas
        density = resources.displayMetrics.density
        dpHeight = height / density
        dpWidth = width / density

        // Draw border
        paint.strokeWidth = 1f
        paint.color = Color.BLACK
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        // Draw the background
        paint.strokeWidth = 1f
        paint.color = canvasBackgroundColor
        canvas.drawRect(1f, 1f, (width - 1).toFloat(), (height - 1).toFloat(), paint)

        // Experimental. Draw squares
        drawSquaresOnGrid()
    }

    fun drawSquaresOnGrid() {
        // size is how many squares can fit horizontally (x axis)
        val rows = 22
        val columns = 10 // y, x
        val size = dpToPx(dpWidth / columns) // Square size in pixels
        for (y in 0 until rows) {
            println(y)
            for (x in 0 until columns) {
                // x and y *size because convert to pixels
                drawSquare(x * size, y * size, size, Color.RED)
            }
        }
    }

    private fun drawSquare(x: Int, y: Int, size: Int, color: Int) {
        // Draw a square of <size> beginning at <x, y>
        // Size is in pixels
        paint.color = color
        canvas.drawRect(
            (x + 1).toFloat(),
            (y + 1).toFloat(),
            (x + size - 1).toFloat(),
            (y + size - 1).toFloat(),
            paint
        )
    }
}