package com.androidtetris.activity.settings

// Sub-settings activity just to set the tetromino colours

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Spinner
import com.androidtetris.R
import com.androidtetris.TetrominoShape
import com.androidtetris.TetrominoShapeConverter
import com.androidtetris.game.TetrominoCode
import android.util.Log

class TetrominoColorsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tetromino_colors)

        // Get all the spinners
        val O = SpinnerObject(findViewById(R.id.o_spinner), TetrominoCode.O) // Width 2
        val I = SpinnerObject(findViewById(R.id.i_spinner), TetrominoCode.I) // Width 4
        val J = SpinnerObject(findViewById(R.id.j_spinner), TetrominoCode.J) // Rest are all width 3
        val L = SpinnerObject(findViewById(R.id.l_spinner), TetrominoCode.L)
        val T = SpinnerObject(findViewById(R.id.t_spinner), TetrominoCode.T)
        val S = SpinnerObject(findViewById(R.id.s_spinner), TetrominoCode.S)
        val Z = SpinnerObject(findViewById(R.id.z_spinner), TetrominoCode.Z)
        val spinnerObjects: List<SpinnerObject> = listOf(O, I, J, L, T, S, Z)
        
        // Red, green, blue, etc 
        val colors: List<Int> = listOf(Color.RED, Color.BLUE, Color.GREEN)
        /*    0xff0000, 0x006400, 0x0000ff, 
            0x00bfff, 0x800080, 0x9400d3,
            0xff1493, 0xf5c71a, 0x560319,
        )*/

        for(spinnerObj in spinnerObjects) {
            val objects: MutableList<TetrominoData> = mutableListOf()
            val shape = TetrominoShape[spinnerObj.tCode]!!
            Log.d("SpinnerObject", spinnerObj.tCode.toString())
            for(color in colors) {
                // Add all the colours and shit
                objects.add(TetrominoData(color, shape))
            }
            // Now set the adapter
            spinnerObj.spinner.adapter = ColorAdapter(this, R.layout.color_dropdown, objects)
        }
        /*
        val os = findViewById<Spinner>(R.id.o_spinner)
        val objects: MutableList<TetrominoData> = mutableListOf()
        val shape = TetrominoShape[TetrominoCode.O]!!
        for(color in colors) {
            objects.add(TetrominoData(color, shape))
        }
        os.adapter = ColorAdapter(this, R.layout.color_dropdown, objects)*/
    }
}

// This object associated a Spinner with its Tetromino code.
// We need this to avoid repeating the same few lines of code 7 times.
data class SpinnerObject(val spinner: Spinner, val tCode: TetrominoCode)

// Contains the data that the spinner itself will use when drawing the dropdown menu.
data class TetrominoData(val color: Int = Color.BLUE, val shape: List<List<Int>> = listOf())

// Our adapter
class ColorAdapter(private val mContext: Context, private val mResource: Int, private val mObjects: List<TetrominoData>) : BaseAdapter() {
    override fun getCount(): Int { return mObjects.size }
    override fun getItem(position: Int): TetrominoData { return mObjects[position] }
    override fun getItemId(position: Int): Long { return position.toLong() } // What is this function?
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var inflatedView: View = convertView ?: LayoutInflater.from(mContext).inflate(mResource, parent, false)
        val colorSelect = inflatedView.findViewById<TetrominoColorSelectView>(R.id.colorSelect)
        colorSelect.color = getItem(position).color
        colorSelect.shape = getItem(position).shape
        return inflatedView
    }
}

class TetrominoColorSelectView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    /* This is the actual view that gets displayed as a dropdown menu item in our respective spinner.
     * It's used in res/layout/color_dropdown.xml, which is what we pass as the resource to ColorAdapter.
     * The layout inflater in ColorAdapter will inflate it and set the coordinates, color, and tWidth. */
    var shape: List<List<Int>> = listOf()
    var color = Color.RED // Default
    private val squareSize = 15 // dp
    private val paint = Paint()

    private fun dpToPx(dp: Float): Float {
        val dpi = resources.displayMetrics.densityDpi
        return (dp * (dpi / 160f))
    }

    override fun onDraw(canvas: Canvas) {
        // Draw the background
        paint.color = Color.LTGRAY
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        // Now draw the tetromino
        val pixelPoints = TetrominoShapeConverter(shape, this, squareSize).getCoordinates()
        paint.color = color
        for(p in pixelPoints) {
            canvas.drawRect(p.x+1, p.y+1, p.x+dpToPx(squareSize.toFloat())-1, p.y+dpToPx(squareSize.toFloat())-1, paint)
        }
    }
 }
