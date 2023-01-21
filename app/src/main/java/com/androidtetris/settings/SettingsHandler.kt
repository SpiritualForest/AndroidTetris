package com.androidtetris.settings

import android.content.SharedPreferences
import android.content.Context
import com.androidtetris.R
import com.androidtetris.game.Point

/* Constants for setting names */
    
const val S_GRID_WIDTH = "gridWidth" // String setting
const val S_GRID_HEIGHT = "gridHeight"
const val S_GAME_LEVEL = "gameLevel" // Int
const val S_STARTING_HEIGHT = "startingHeight" // Int
const val S_INVERT_ROTATION = "invertRotation" // Boolean
const val S_GHOST_ENABLED = "ghostEnabled" // Boolean

// Game restoration key name constants (for Bundle object keys) - used in Game.kt and TetrisActivity.kt
// NOTE: these are NOT settings to be saved on persistent storage. Only those starting with S_ are.
const val K_LINES = "lines"
const val K_DROP_SPEED = "dropSpeed"
const val K_GAME_LEVEL = "gameLevel"
const val K_TETROMINO = "tetromino"
const val K_TETROMINO_ROTATION = "tetrominoRotation"
const val K_TETROMINO_COORDINATES = "tetrominoCoordinates"
const val K_UPCOMING_TETROMINOES = "upcomingTetrominoes"
const val K_GRID = "grid"
const val K_SCORE = "score"
const val K_GAME_TIME = "gameTime" // Seconds elapsed since starting the game
const val K_PREVIOUS_LINE_COUNT = "previousLineCount" // Used to determine score calculation

/* Our settings handling singleton object. Intended to be used globally throughout the application.
 * This is so we don't have to constantly instantiate this object and open the preferences file in each activity. */

object SettingsHandler {
    private lateinit var sharedPref: SharedPreferences

    fun openSharedPreferences(mContext: Context) {
        /* NOTE: this function MUST be called before doing anything else. */
        sharedPref = mContext.getSharedPreferences(mContext.resources.getString(R.string.preference_file), Context.MODE_PRIVATE)
    }

    private fun setString(key: String, value: String) {
        // saveString("key", "value")
        with (sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    private fun setInt(key: String, value: Int) {
        with (sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }
    
    private fun setBoolean(key: String, value: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    private fun getString(key: String): String? {
        // Returns empty string as default value
        return sharedPref.getString(key, "")
    }

    private fun getInt(key: String): Int {
        return sharedPref.getInt(key, -1)
    }

    private fun getBoolean(key: String): Boolean {
        // False by default
        return sharedPref.getBoolean(key, false)
    }

    /* Wrapper functions for handling specific settings. */

    // Invert rotation
    fun setInvertRotation(enabled: Boolean) {
        setBoolean(S_INVERT_ROTATION, enabled)
    }

    fun getInvertRotation(): Boolean {
        // Returns false if not found
        return getBoolean(S_INVERT_ROTATION)
    }
    
    // Game level
    fun setGameLevel(level: Int) {
        setInt(S_GAME_LEVEL, level)
    }

    fun getGameLevel(): Int {
        // Returns 1 by default
        val level = getInt(S_GAME_LEVEL)
        if (level == -1) { return 1 }
        return level
    }

    fun setGridWidth(width: Int) = setInt(S_GRID_WIDTH, width)
    fun setGridHeight(height: Int) = setInt(S_GRID_HEIGHT, height)

    fun getGridWidth(): Int {
        val width = getInt(S_GRID_WIDTH)
        return if (width == -1) 10 else width
    }

    fun getGridHeight(): Int {
        val height = getInt(S_GRID_HEIGHT)
        return if (height == -1) 22 else height
    }

    // Starting height
    fun setStartingHeight(height: Int) {
        setInt(S_STARTING_HEIGHT, height)
    }

    fun getStartingHeight(): Int {
        // Returns 0 by default
        val height = getInt(S_STARTING_HEIGHT)
        if (height == -1) { return 0 }
        return height
    }

    // Ghost piece
    fun setGhostEnabled(enabled: Boolean) {
        setBoolean(S_GHOST_ENABLED, enabled)
    }

    fun getGhostEnabled(): Boolean {
        // False by default
        return getBoolean(S_GHOST_ENABLED)
    }
}
