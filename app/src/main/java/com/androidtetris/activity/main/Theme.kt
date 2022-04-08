package com.androidtetris.activity.main

/* AndroidTetris themes handler */

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.androidtetris.TetrominoShapeConverter
import com.androidtetris.TetrominoShape // HashMap of shapes
import com.androidtetris.game.TetrominoCode

val coldTheme = listOf("#af64f5", "#d7b9f2", "#4e3bdc", "#73708b", "#bfbbdb", "#aaa1ea")
val hotTheme = listOf("#e25d00", "#e50135", "#f90b96", "#f9e50b", "#f20000", "#ff9150")

val themes = hashMapOf(
    "Cold" to coldTheme,
    "hot" to hotTheme,
)

class ThemeView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    // This view is used to provide a preview for the user's selected colour theme.
    // It is used in MainActivity.
}
