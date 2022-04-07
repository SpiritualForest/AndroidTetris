package com.androidtetris

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.view.View
import android.widget.Spinner
import android.widget.CheckBox
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.androidtetris.activity.tetris.TetrisActivity

class MainActivity : AppCompatActivity(), OnItemSelectedListener {
    private lateinit var settingsHandler: SettingsHandler // From com.androidtetris
    private var defaultSpinnerSelection = "Select" // Default text for all the spinners

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settingsHandler = SettingsHandler(this)

        // Start game button
        val startGameBtn = findViewById<Button>(R.id.btn_startgame)
        val startGameIntent = Intent(this, TetrisActivity::class.java)
        startGameBtn.setOnClickListener { startActivity(startGameIntent) }
        
        /* Game options input widgets */
        val invertRotationCheckBox = findViewById<CheckBox>(R.id.checkBox_invertRotation)
        invertRotationCheckBox.isChecked = settingsHandler.getBoolean("invertRotation")
        val gridSizeSpinner = findViewById<Spinner>(R.id.spinner_gridSize)
        val gameLevelSpinner = findViewById<Spinner>(R.id.spinner_gameLevel)
        val startingHeightSpinner = findViewById<Spinner>(R.id.spinner_startingHeight)

        invertRotationCheckBox.setOnCheckedChangeListener {
                _, isChecked -> settingsHandler.setBoolean("invertRotation", isChecked)
        }

        /* FIXME: this is very ugly. Refactor this code to reduce hard coded setting names and such shit. */
        
        // Strings list for the grid size's ArrayAdapter
        val gridSizes: List<String> = listOf(defaultSpinnerSelection, "10x22", "20x44", "25x55", "40x88")
        val gridSizeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gridSizes)
        gridSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gridSizeSpinner.adapter = gridSizeAdapter
        gridSizeSpinner.onItemSelectedListener = this
        val gridSizeSetValue = settingsHandler.getString("gridSize")
        if (gridSizeSetValue != "") {
            gridSizeSpinner.setSelection(getSpinnerIndex(gridSizeSpinner, gridSizeSetValue!!))
        }

        // Game level
        val gameLevels: MutableList<String> = mutableListOf(defaultSpinnerSelection)
        for(i in 1 until 19) { gameLevels.add(i.toString()) }
        val gameLevelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gameLevels)
        gameLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gameLevelSpinner.adapter = gameLevelAdapter
        gameLevelSpinner.onItemSelectedListener = this
        // NOTE: string and int mix-up here, beware!
        val gameLevelSetValue: Int = settingsHandler.getInt("gameLevel")
        if (gameLevelSetValue != -1) {
            // This value exists in our saved settings, so use it
            gameLevelSpinner.setSelection(getSpinnerIndex(gameLevelSpinner, gameLevelSetValue.toString()))
        }

        // Starting height
        val startingHeights: MutableList<String> = mutableListOf(defaultSpinnerSelection)
        for(i in 0 until 11) { startingHeights.add(i.toString()) }
        val startingHeightAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, startingHeights)
        startingHeightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        startingHeightSpinner.adapter = startingHeightAdapter
        startingHeightSpinner.onItemSelectedListener = this
        val startingHeightSetValue = settingsHandler.getInt("startingHeight")
        if (startingHeightSetValue != -1) {
            startingHeightSpinner.setSelection(getSpinnerIndex(startingHeightSpinner, startingHeightSetValue.toString()))
        }
        // Colours stuff here later
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if (parent.getItemAtPosition(position).toString() == defaultSpinnerSelection) {
            // Non-selection
            return
        }
        when (parent.id) {
            R.id.spinner_gridSize -> {
                // Grid size selection
                settingsHandler.setString("gridSize", parent.getItemAtPosition(position).toString())
            }
            R.id.spinner_gameLevel -> {
                // Game level selection
                settingsHandler.setInt("gameLevel", parent.getItemAtPosition(position).toString().toInt())
            }
            R.id.spinner_startingHeight -> {
                // Starting height
                settingsHandler.setInt("startingHeight", parent.getItemAtPosition(position).toString().toInt())
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun getSpinnerIndex(spinner: Spinner, string: String): Int {
        // Find and return the index position of <string> in <spinner>
        for(i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i) == string) {
                return i
            }
        }
        return -1 // If not found
    }
}
