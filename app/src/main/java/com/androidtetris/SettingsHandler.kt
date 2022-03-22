package com.androidtetris

import android.content.SharedPreferences
import android.content.Context
import android.graphics.Color
import com.androidtetris.game.TetrominoCode

open class SettingsHandler(mContext: Context) {
    private val sharedPref: SharedPreferences = mContext.getSharedPreferences(mContext.resources.getString(R.string.preference_file), Context.MODE_PRIVATE)
    //private val sharedPref = getDefaultSharedPreferences(mContext)

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
}

class ColorHandler(mContext: Context) : SettingsHandler(mContext) {
    // Specialized class to set and get only colour values
    
    fun setColor(tetrominoCode: TetrominoCode, hexString: String) {
        // Use the inherited setString function for this
        val formattedKey = String.format("tetrominoColor_%s", tetrominoCode.toString())
        setInt(formattedKey, Color.parseColor(hexString))
    }

    fun setColor(tetrominoCode: TetrominoCode, color: Int) {
        val formattedKey = String.format("tetrominoColor_%s", tetrominoCode.toString())
        setInt(formattedKey, color)
    }

    fun getColor(tetrominoCode: TetrominoCode): Int {
        val color = getInt(String.format("tetrominoColor_%s", tetrominoCode.toString()))
        if (color == -1) {
            // No such setting
            return Color.RED // Default
        }
        return color
    }

    fun getAllColors(): Map<TetrominoCode, Int> {
        // Maps all the colours to their tetromino code
        val colorsMap: HashMap<TetrominoCode, Int> = hashMapOf()
        for(v in TetrominoCode.values()) {
            val color = getColor(v) // -1 if not found
            colorsMap[v] = color
        }
        return colorsMap.toMap()
    }
}
