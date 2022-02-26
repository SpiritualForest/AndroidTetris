package com.androidtetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent
import kotlin.math.min

class CircleButton(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private val upColor = Color.BLACK // Default when up
    private val downColor = Color.GRAY // When pressed
    private var currentColor = upColor
    //private val center = Point(width / 2, height / 2)

    override fun onDraw(canvas: Canvas) {
        // Draw a circle that spans as close as possible to the canvas's size
        paint.color = currentColor
        val center = Point(width / 2, height / 2)
        val radius = min(center.x, center.y)
        canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), radius.toFloat(), paint)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val x: Float = ev.x
        val y: Float = ev.y
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                currentColor = downColor
            }
            MotionEvent.ACTION_UP -> {
                currentColor = upColor
            }
        }
        invalidate()
        return true
    }

    fun drawButtonDown(canvas: Canvas) {
        // Draw the animation when the button is touched
    }

    fun drawButtonUp(canvas: Canvas) {
        // Draw the animation when the button is released
    }
}
