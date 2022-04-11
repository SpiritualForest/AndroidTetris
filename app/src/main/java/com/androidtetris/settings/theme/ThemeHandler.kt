package com.androidtetris.settings.theme

/* AndroidTetris theme handler */

import com.androidtetris.settings.ColorHandler
import com.androidtetris.game.TetrominoCode
import android.graphics.Color
import android.content.Context

// Theme names
val T_CUSTOM = "Custom"
val T_DARK = "Dark" // Mostly blue, purple, dark colours
val T_BRIGHT = "Bright" // Mostly red, orange, yellow, warm colours
val T_RAINBOW = "Rainbow"

/* This is a singleton object which is intended to be imported by other packages for use.
 * We need to do it this way so that the ThemeHandler object can be instantiated only once,
 * and thus not have all kinds of conflicts when changing settings from different activities. */

object ThemeHandler {

    // Hash map that keeps all the themes loaded in memory,
    // so that we won't have to read from storage each time we want to change themes.
    private var themes: HashMap<String, HashMap<TetrominoCode, Int>> = hashMapOf()
    
    // The currently set theme
    var theme: String = T_RAINBOW // Default value
        private set

    // Theme colour collections
    private val darkColors = listOf("#1A4780", "#0A0A0A", "#8A8A8A", "#330033", "#000B61", "#003D31", "#2C002E")
    private val brightColors = listOf("#FF14A3", "#FF141E", "#FF8D14", "#C400F0", "#00F0E8", "#00F039", "#B4F202")
    private val rainbowColors = listOf("#ff0000", "#ff8c00", "#ffff00", "#008000", "#0000ff", "#4b0082", "#ee82ee")

    init {
        // Add the theme names as keys in the themes map
        listOf(T_DARK, T_BRIGHT, T_RAINBOW).forEach { themes[it] = hashMapOf() }
        
        // Now populate the nested map with TetrominoCode -> ColorString key-value pairs.
        for((i, tetromino) in TetrominoCode.values().withIndex()) {
            // Dark
            themes[T_DARK]!![tetromino] = Color.parseColor(darkColors[i])
            
            // Bright
            themes[T_BRIGHT]!![tetromino] = Color.parseColor(brightColors[i])

            // Rainbow
            themes[T_RAINBOW]!![tetromino] = Color.parseColor(rainbowColors[i])
        }
    }

    fun getThemes(): List<String> {
        return themes.keys.toList()
    }

    fun loadCustomTheme(mContext: Context) {
        /* Load the custom theme from storage.
        * This function should only be called if the custom theme
        * is set as the chosen theme when the application starts. */
        val colorHandler = ColorHandler(mContext)
        themes[T_CUSTOM] = hashMapOf()
        for(tetromino in TetrominoCode.values()) {
            themes[T_CUSTOM]!![tetromino] = colorHandler.getColor(tetromino)
        }
    }

    fun setTheme(name: String) {
        if (name !in themes.keys) { return }
        this.theme = name
    }

    fun getThemeColors(): Map<TetrominoCode, Int> {
        return themes[this.theme]!!.toMap()
    }
}
