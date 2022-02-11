package com.androidtetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CircleButton(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()

    override fun onDraw(canvas: Canvas) {
        // Draw a circle that spans as close as possible to the canvas's size
        val center = Point(width / 2, height / 2)
        paint.color = Color.RED
        val radius = min(center.x, center.y)
        canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), radius.toFloat(), paint)
    }

    fun drawButtonPress() {
        // Draw the button press animation
    }
}
