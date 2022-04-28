package com.androidtetris.activity.main

// Sub-settings activity just to set the tetromino colours

import android.content.Context
import android.content.Intent
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
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import com.androidtetris.settings.theme.ThemeHandler
import com.androidtetris.R
import com.androidtetris.TetrominoShape
import com.androidtetris.TetrominoShapeConverter
import com.androidtetris.game.TetrominoCode

class TetrominoColorsActivity : AppCompatActivity(), OnItemSelectedListener {
    
    private var spinnerCalls = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tetromino_colors)

        val currentThemeColors = ThemeHandler.getThemeColors() // Required for spinner.setSelection()
        
        val btnApply = findViewById<Button>(R.id.btn_apply)

        // Get all the spinners
        val O = SpinnerObject(findViewById(R.id.o_spinner), TetrominoCode.O) // Width 2
        val I = SpinnerObject(findViewById(R.id.i_spinner), TetrominoCode.I) // Width 4
        val J = SpinnerObject(findViewById(R.id.j_spinner), TetrominoCode.J) // Rest are all width 3
        val L = SpinnerObject(findViewById(R.id.l_spinner), TetrominoCode.L)
        val T = SpinnerObject(findViewById(R.id.t_spinner), TetrominoCode.T)
        val S = SpinnerObject(findViewById(R.id.s_spinner), TetrominoCode.S)
        val Z = SpinnerObject(findViewById(R.id.z_spinner), TetrominoCode.Z)
        val spinnerObjects: List<SpinnerObject> = listOf(O, I, J, L, T, S, Z)

        val colorHexStrings = ThemeHandler.getAllColors() // All available colours regardless of theme association

        // Now create an adapter for each spinner
        for(spinnerObj in spinnerObjects) {
            // Mark the current activity as responding for the spinner's selection events
            spinnerObj.spinner.onItemSelectedListener = this
            val colors: MutableList<Int> = mutableListOf()
            colorHexStrings.forEach { colors.add(Color.parseColor(it)) }
            
            val tetrominoDataObjects: MutableList<TetrominoData> = mutableListOf()
            val shape = TetrominoShape[spinnerObj.tetrominoCode]!!
            for(col in colors) {
                tetrominoDataObjects.add(TetrominoData(col, shape, spinnerObj.tetrominoCode))
            }
            // Set the adapter
            spinnerObj.spinner.adapter = ColorAdapter(this, R.layout.color_dropdown, tetrominoDataObjects)
            
            // Set the selected colour on each spinner item respectively.
            val color = currentThemeColors[spinnerObj.tetrominoCode]!!
            spinnerObj.spinner.setSelection(getSpinnerIndex(spinnerObj.spinner, color))
        }

        // Applying the changes
        btnApply.setOnClickListener {
            val colors: HashMap<TetrominoCode, Int> = hashMapOf()
            for(spinnerObj in spinnerObjects) {
                val tetrominoDataObj = spinnerObj.spinner.getSelectedItem() as TetrominoData
                colors[tetrominoDataObj.tetrominoCode] = tetrominoDataObj.color
            }
            // Save custom theme
            ThemeHandler.saveCustomTheme(colors)
            Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun getSpinnerIndex(spinner: Spinner, color: Int): Int {
        /* Returns the index of the TetrominoData object within the spinner
         * based on the given color */
        for(i in 0 until spinner.count) {
            val tetrominoDataObj: TetrominoData = spinner.getItemAtPosition(i) as TetrominoData
            if (tetrominoDataObj.color == color) { return i }
        }
        return -1
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        /* Spinner dropdown menu selection callback.
         * This function doesn't actually do anything, because we're not setting
         * each menu's selection individually, but we must provide it to conform
         * to the OnItemSelectedListener interface.
         * The settings are saved all at once by pressing the Apply button. */
    }            

    override fun onNothingSelected(parent: AdapterView<*>) {
        println("onNothingSelected() called.")
    }
}

// This object associates a Spinner with the tetromino we want it to draw as its menu items.
// We need this to avoid repeating the same few lines of code 7 times.
data class SpinnerObject(val spinner: Spinner, val tetrominoCode: TetrominoCode)

// Contains the data that the spinner itself will use when drawing the dropdown menu.
data class TetrominoData(val color: Int = Color.RED, val shape: List<List<Int>> = listOf(), val tetrominoCode: TetrominoCode)

// Our adapter
class ColorAdapter(private val mContext: Context, private val mResource: Int, private val mObjects: List<TetrominoData>) : BaseAdapter() {
    override fun getCount(): Int { return mObjects.size }
    override fun getItem(position: Int): TetrominoData { return mObjects[position] }
    override fun getItemId(position: Int): Long { return position.toLong() } // What is this function?
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflatedView: View = convertView ?: LayoutInflater.from(mContext).inflate(mResource, parent, false)
        val colorSelect = inflatedView.findViewById<TetrominoColorSelectView>(R.id.colorSelect)
        colorSelect.color = getItem(position).color
        colorSelect.shape = getItem(position).shape
        colorSelect.tetrominoCode = getItem(position).tetrominoCode
        return inflatedView
    }
}

class TetrominoColorSelectView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    /* This is the actual view that gets displayed as a dropdown menu item in our respective spinner.
     * It's used in res/layout/color_dropdown.xml, which is what we pass as the resource to ColorAdapter.
     * The layout inflater in ColorAdapter will inflate it and set the shape and color. */
    var shape: List<List<Int>> = listOf(listOf(1))
    var color = Color.RED // Default
    var tetrominoCode = TetrominoCode.I // Default
    private val squareSize = 15 // dp
    private val paint = Paint()
    private val tetrominoShapeConverter = TetrominoShapeConverter(shape, this, squareSize)

    private fun getCenterVertical(): Int {
        val center = ((height / resources.displayMetrics.density) / 2) - squareSize
        return center.toInt()
    }
    private fun dpToPx(dp: Float): Float {
        val dpi = resources.displayMetrics.densityDpi
        return (dp * (dpi / 160f))
    }

    override fun onDraw(canvas: Canvas) {
        // Draw the background of the canvas
        paint.color = Color.LTGRAY
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        // Set the shape and color to draw
        tetrominoShapeConverter.shape = shape
        paint.color = color
        val pixelPoints = tetrominoShapeConverter.getCoordinates(getCenterVertical())
        for(p in pixelPoints) {
            canvas.drawRect(p.x+1, p.y+1, p.x+dpToPx(squareSize.toFloat())-1, p.y+dpToPx(squareSize.toFloat())-1, paint)
        }
    }
 }
