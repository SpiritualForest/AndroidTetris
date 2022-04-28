package com.androidtetris.settings

import android.content.SharedPreferences
import android.content.Context
import android.graphics.Color
import com.androidtetris.game.TetrominoCode
import com.androidtetris.R
import com.androidtetris.game.Point

/* Constants for setting names */
    
val S_GRID_SIZE = "gridSize" // String setting
val S_GAME_LEVEL = "gameLevel" // Int
val S_STARTING_HEIGHT = "startingHeight" // Int
val S_INVERT_ROTATION = "invertRotation" // Boolean
val S_GHOST_ENABLED = "ghostEnabled" // Boolean
val S_THEME = "theme" // String. Tetromino colours theme.
val S_TETROMINO_COLOR = "tetrominoColor_%s" // Int. Remember to use String.format() with this.
val S_LOAD_CUSTOM = "loadCustom" // Boolean. Should we load the custom theme on ThemeHandler instantiation?

// Game restoration key name constants (for Bundle object keys) - used in Game.kt and TetrisActivity.kt
// NOTE: these are NOT settings to be saved on persistent storage. Only those starting with S_ are.
val K_LINES = "lines"
val K_DROP_SPEED = "dropSpeed"
val K_GAME_LEVEL = "gameLevel"
val K_TETROMINO = "tetromino"
val K_TETROMINO_ROTATION = "tetrominoRotation"
val K_TETROMINO_COORDINATES = "tetrominoCoordinates"
val K_UPCOMING_TETROMINOES = "upcomingTetrominoes"
val K_GRID = "grid"
val K_SCORE = "score"
val K_GAME_TIME = "gameTime" // Seconds elapsed since starting the game
val K_PREVIOUS_LINE_COUNT = "previousLineCount" // Used to determine score calculation

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

    // These methods are only used for handling
    
    fun setColor(tetrominoCode: TetrominoCode, hexString: String) {
        // Use the inherited setString function for this
        val formattedKey = String.format(S_TETROMINO_COLOR, tetrominoCode.toString())
        setInt(formattedKey, Color.parseColor(hexString))
    }

    fun setColor(tetrominoCode: TetrominoCode, color: Int) {
        val formattedKey = String.format(S_TETROMINO_COLOR, tetrominoCode.toString())
        setInt(formattedKey, color)
    }

    fun getColor(tetrominoCode: TetrominoCode): Int {
        val color = getInt(String.format(S_TETROMINO_COLOR, tetrominoCode.toString()))
        if (color == -1) {
            // No such setting
            return Color.RED // Default
        }
        return color
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

    // Grid size
    fun setGridSize(sizeStr: String) {
        // sizeStr is something like "10x22"
        setString(S_GRID_SIZE, sizeStr)
    }

    fun getGridSize(): Point {
        // Returns Point(10, 22) by default
        val sizeStr = getString(S_GRID_SIZE)
        if (sizeStr == "") { return Point(10, 22) }
        else {
            val (x, y) = sizeStr!!.split("x")
            return Point(x.toInt(), y.toInt())
        }
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

    // Theme settings
    fun setTheme(theme: String) {
        setString(S_THEME, theme)
    }

    fun getTheme(): String {
        return getString(S_THEME)!!
    }

    fun setLoadCustom(load: Boolean) {
        setBoolean(S_LOAD_CUSTOM, load)
    }

    fun getLoadCustom(): Boolean {
        return getBoolean(S_LOAD_CUSTOM)
    }
}
