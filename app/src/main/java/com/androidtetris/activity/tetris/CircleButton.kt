package com.androidtetris.activity.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent
import android.os.Looper
import android.os.Handler
import kotlin.math.min
import com.google.android.material.color.MaterialColors
import com.androidtetris.R

import android.util.Log

/* NOTE:
 * the actual functionality events for this button
 * are handled in TetrisActivity, not here.
 * This view only handles its own drawing.
 * See TetrisActivity to see the various gameplay functions
 * that are hooked to CircleButton views. */

class CircleButton(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private val pressedColor = MaterialColors.getColor(this, R.attr.colorPrimaryVariant) // Colour when pressed
    private var unpressedColor = MaterialColors.getColor(this, R.attr.colorOnPrimary) // Colour when not pressed
    private val mHandler = Handler(Looper.getMainLooper())
    private var drawOutwards = false // If true, draw circles from the center outwards. Otherwise, rollback.
    private var currentCircle = 0f // Which circle we're currently at. 0 to 1 with steps of 0.1
    private var drawingRunnable = DrawingRunnable(this, mHandler)

    override fun onDraw(canvas: Canvas) {
        val center = Point(width / 2, height / 2)
        val completeRadius = min(center.x, center.y)
        
        if (drawOutwards) {
            // Draw outwards until we reach completeRadius
            if (currentCircle < 1f) {
                currentCircle += 0.1f;
            }
            else {
                // No more circles to draw after this
                drawingRunnable.stop()
            }
        }
        else {
            // Draw inwards, circles shrinking, towards the center.
            if (currentCircle > 0f) {
                currentCircle -= 0.1f;
            }
            else {
                // No more circles to draw after this
                drawingRunnable.stop()
            }
        }
        // Draw the whole background circle, first (unpressed colour)
        paint.color = unpressedColor
        canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), completeRadius.toFloat(), paint)
        // Now draw the circle
        val radius = currentCircle * completeRadius.toFloat()
        paint.color = pressedColor
        canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), radius, paint)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                currentCircle = 0f
                drawOutwards = true
                drawingRunnable.restart()
            }
            MotionEvent.ACTION_UP -> {
                drawOutwards = false
                drawingRunnable.restart()
            }
        }
        return true
    }
}

class DrawingRunnable(private val mButton: CircleButton, private val mHandler: Handler) : Runnable {
    /* All this runnable does is call invalidate() repeatedly on
     * the given CircleButton view */

    private var runCode = false // Don't run by default
    
    override fun run() {
        // Run the code
        if (runCode) {
            mHandler.postDelayed(this, 2L)
            mButton.invalidate()
        }
    }

    fun stop() {
        // Stop executing this runnable
        runCode = false
        mHandler.removeCallbacks(this)
    }

    fun start() {
        // Start executing this runnable
        runCode = true
        run()
    }

    fun restart() {
        stop()
        start()
    }
}
