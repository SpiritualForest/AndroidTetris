package com.androidtetris.activity.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent
import android.os.Looper
import android.os.Handler
import kotlin.math.min
import com.google.android.material.color.MaterialColors
import com.androidtetris.R

class CircleButton(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private var currentColor = MaterialColors.getColor(this, R.attr.colorOnPrimary)
    private val mHandler = Handler(Looper.getMainLooper())
    private var radii: MutableList<Float> = mutableListOf() // List of radii to draw

    override fun onDraw(canvas: Canvas) {
        // Draw a circle that spans as close as possible to the canvas's size
        paint.color = currentColor
        val center = Point(width / 2, height / 2)
        val completeRadius = min(center.x, center.y) // The largest radius, a "complete" canvas
        val smallestRadius = 0.2f*completeRadius // 20% of the largest
        canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), completeRadius.toFloat(), paint)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val x: Float = ev.x
        val y: Float = ev.y
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                currentColor = MaterialColors.getColor(this, R.attr.colorPrimaryVariant)
                drawButtonDown()
            }
            MotionEvent.ACTION_UP -> {
                currentColor = MaterialColors.getColor(this, R.attr.colorOnPrimary)
                drawButtonUp()
            }
        }
        invalidate()
        return true
    }

    fun drawButtonDown() {
        // Draw the animation when the button is touched
        radii = mutableListOf()
    }

    fun drawButtonUp() {
        // Draw the animation when the button is released
        radii = mutableListOf()
    }
}
