package com.androidtetris.activity.main

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
import com.androidtetris.R
import com.androidtetris.settings.* // For SettingsHandler and game options name constants like S_INVERT_ROTATION
import com.androidtetris.activity.main.ThemeView
import com.androidtetris.settings.theme.ThemeHandler

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

        val customizeThemeBtn = findViewById<Button>(R.id.btn_customizeTheme)
        val customizeThemeIntent = Intent(this, TetrominoColorsActivity::class.java)
        /* TODO: pass the selected theme colours as parameters to this activity */
        customizeThemeBtn.setOnClickListener { startActivity(customizeThemeIntent) }
        
        /* Game options input widgets */
        val invertRotationCheckBox = findViewById<CheckBox>(R.id.checkBox_invertRotation)
        invertRotationCheckBox.isChecked = settingsHandler.getBoolean(S_INVERT_ROTATION)
        val gridSizeSpinner = findViewById<Spinner>(R.id.spinner_gridSize)
        val gameLevelSpinner = findViewById<Spinner>(R.id.spinner_gameLevel)
        val startingHeightSpinner = findViewById<Spinner>(R.id.spinner_startingHeight)
        val colorThemeSpinner = findViewById<Spinner>(R.id.spinner_colorTheme)

        invertRotationCheckBox.setOnCheckedChangeListener {
                _, isChecked -> settingsHandler.setBoolean(S_INVERT_ROTATION, isChecked)
        }

        /* FIXME: this is very ugly. Refactor this code to reduce hard coded setting names and such shit. */
        
        // Strings list for the grid size's ArrayAdapter
        val gridSizes: List<String> = listOf(defaultSpinnerSelection, "10x22", "20x44", "25x55", "40x88")
        // Set the adapter
        setAdapter(gridSizeSpinner, gridSizes)
        val gridSizeSetValue = settingsHandler.getString(S_GRID_SIZE)
        if (gridSizeSetValue != "") {
            gridSizeSpinner.setSelection(getSpinnerIndex(gridSizeSpinner, gridSizeSetValue!!))
        }

        // Game level
        val gameLevels: MutableList<String> = mutableListOf(defaultSpinnerSelection)
        for(i in 1 until 20) { gameLevels.add(i.toString()) }
        // Set the adapter
        setAdapter(gameLevelSpinner, gameLevels)
        // NOTE: string and int mix-up here, beware!
        val gameLevelSetValue: Int = settingsHandler.getInt(S_GAME_LEVEL)
        if (gameLevelSetValue != -1) {
            // This value exists in our saved settings, so use it
            gameLevelSpinner.setSelection(getSpinnerIndex(gameLevelSpinner, gameLevelSetValue.toString()))
        }

        // Starting height
        val startingHeights: MutableList<String> = mutableListOf(defaultSpinnerSelection)
        for(i in 0 until 8) { startingHeights.add(i.toString()) }
        // Set the adapter for this spinner
        setAdapter(startingHeightSpinner, startingHeights)
        val startingHeightSetValue = settingsHandler.getInt(S_STARTING_HEIGHT)
        if (startingHeightSetValue != -1) {
            startingHeightSpinner.setSelection(getSpinnerIndex(startingHeightSpinner, startingHeightSetValue.toString()))
        }
        // Colour theme selection
        setAdapter(colorThemeSpinner, ThemeHandler.getThemes())
        // Set selection to current theme
        colorThemeSpinner.setSelection(getSpinnerIndex(colorThemeSpinner, ThemeHandler.theme))
    }

    private fun setAdapter(spinner: Spinner, objects: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, objects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if (view == null) { return }
        if (parent.getItemAtPosition(position).toString() == defaultSpinnerSelection) {
            // Non-selection
            return
        }
        when (parent.id) {
            R.id.spinner_gridSize -> {
                // Grid size selection
                settingsHandler.setString(S_GRID_SIZE, parent.getItemAtPosition(position).toString())
            }
            R.id.spinner_gameLevel -> {
                // Game level selection
                settingsHandler.setInt(S_GAME_LEVEL, parent.getItemAtPosition(position).toString().toInt())
            }
            R.id.spinner_startingHeight -> {
                // Starting height
                settingsHandler.setInt(S_STARTING_HEIGHT, parent.getItemAtPosition(position).toString().toInt())
            }
            R.id.spinner_colorTheme -> {
                // Colour
                val themeView = findViewById<ThemeView>(R.id.themeView)
                val themeName = parent.getItemAtPosition(position).toString()
                ThemeHandler.setTheme(themeName)
                // Update the ThemeView
                themeView.invalidate()
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
