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
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import com.androidtetris.activity.tetris.TetrisActivity
import com.androidtetris.R
import com.androidtetris.settings.* // For SettingsHandler and game options name constants like S_INVERT_ROTATION
import com.androidtetris.activity.main.ThemeView
import com.androidtetris.settings.theme.ThemeHandler

class MainActivity : AppCompatActivity(), OnItemSelectedListener {
    private var defaultSpinnerSelection = "Select" // Default text for all the spinners

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Disable screen rotation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        
        // Pass our activity context to the SettingsHandler singleton and open the preferences file
        // NOTE: if this call is omitted, reading and writing of settings with SettingsHandler becomes impossible.
        SettingsHandler.openSharedPreferences(this)

        // Start game button
        val startGameBtn = findViewById<Button>(R.id.btn_startgame)
        val startGameIntent = Intent(this, TetrisActivity::class.java)
        startGameBtn.setOnClickListener { startActivity(startGameIntent) }

        val customizeThemeBtn = findViewById<Button>(R.id.btn_customizeTheme)
        val customizeThemeIntent = Intent(this, TetrominoColorsActivity::class.java)
        customizeThemeBtn.setOnClickListener { startActivity(customizeThemeIntent) }
        
        /* Game options input widgets */
        val invertRotationCheckBox = findViewById<CheckBox>(R.id.checkBox_invertRotation)
        invertRotationCheckBox.isChecked = SettingsHandler.getInvertRotation()
        val gridSizeSpinner = findViewById<Spinner>(R.id.spinner_gridSize)
        val gameLevelSpinner = findViewById<Spinner>(R.id.spinner_gameLevel)
        val startingHeightSpinner = findViewById<Spinner>(R.id.spinner_startingHeight)
        val colorThemeSpinner = findViewById<Spinner>(R.id.spinner_colorTheme)

        invertRotationCheckBox.setOnCheckedChangeListener {
                _, isChecked -> SettingsHandler.setInvertRotation(isChecked)
        }
        
        // Strings list for the grid size's ArrayAdapter
        val gridSizes: List<String> = listOf(defaultSpinnerSelection, "10x22", "20x44", "25x55", "40x88")
        // Set the adapter
        setAdapter(gridSizeSpinner, gridSizes)
        // getGridSize() returns Point(x, y). Using .toString() will return "XxY", such as "10x22"
        val gridSizeSetValue = SettingsHandler.getGridSize().toString() // Will be "10x22" by default
        gridSizeSpinner.setSelection(getSpinnerIndex(gridSizeSpinner, gridSizeSetValue))

        // Game level
        val gameLevels: MutableList<String> = mutableListOf(defaultSpinnerSelection)
        for(i in 1 until 20) { gameLevels.add(i.toString()) }
        // Set the adapter
        setAdapter(gameLevelSpinner, gameLevels)
        val gameLevelSetValue: Int = SettingsHandler.getGameLevel() // Returns 1 by default
        gameLevelSpinner.setSelection(getSpinnerIndex(gameLevelSpinner, gameLevelSetValue.toString()))

        // Starting height
        val startingHeights: MutableList<String> = mutableListOf(defaultSpinnerSelection)
        for(i in 0 until 8) { startingHeights.add(i.toString()) }
        // Set the adapter for this spinner
        setAdapter(startingHeightSpinner, startingHeights)
        val startingHeightSetValue = SettingsHandler.getStartingHeight() // 0 by default
        startingHeightSpinner.setSelection(getSpinnerIndex(startingHeightSpinner, startingHeightSetValue.toString()))

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
                SettingsHandler.setGridSize(parent.getItemAtPosition(position).toString())
            }
            R.id.spinner_gameLevel -> {
                // Game level selection
                SettingsHandler.setGameLevel(parent.getItemAtPosition(position).toString().toInt())
            }
            R.id.spinner_startingHeight -> {
                // Starting height
                SettingsHandler.setStartingHeight(parent.getItemAtPosition(position).toString().toInt())
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
