package com.androidtetris.settings

import android.content.SharedPreferences
import android.content.Context
import android.graphics.Color
import com.androidtetris.game.TetrominoCode
import com.androidtetris.R

/* Constants for setting names */
    
val S_GRID_SIZE = "gridSize"
val S_GAME_LEVEL = "gameLevel"
val S_STARTING_HEIGHT = "startingHeight"
val S_INVERT_ROTATION = "invertRotation"
val S_GHOST_ENABLED = "ghostEnabled"
val S_THEME = "theme" // Tetromino colours theme
val S_TETROMINO_COLOR = "tetrominoColor_%s" // Remember to use String.format() with this
val S_LOAD_CUSTOM = "loadCustom" // Should we load the custom theme on ThemeHandler instantiation?

/* Our settings handling singleton object. Intended to be used globally throughout the application.
 * This why we don't have to constantly instantiate this object and open the preferences file in each activity. */

object SettingsHandler {
    private lateinit var sharedPref: SharedPreferences

    fun openSharedPreferences(mContext: Context) {
        sharedPref = mContext.getSharedPreferences(mContext.resources.getString(R.string.preference_file), Context.MODE_PRIVATE)
    }

    fun setString(key: String, value: String) {
        // saveString("key", "value")
        with (sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun setInt(key: String, value: Int) {
        with (sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }
    
    fun setBoolean(key: String, value: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun getString(key: String): String? {
        // Returns empty string as default value
        return sharedPref.getString(key, "")
    }

    fun getInt(key: String): Int {
        return sharedPref.getInt(key, -1)
    }

    fun getBoolean(key: String): Boolean {
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
}
