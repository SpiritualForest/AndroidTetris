package com.androidtetris

// Sub-settings activity just to set the tetromino colours

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TetrominoColorsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tetromino_colors)
    }
}