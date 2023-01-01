package com.androidtetris

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.androidtetris.ui.screens.HomeScreen
import com.androidtetris.ui.screens.tetris.TetrisScreen
import com.androidtetris.ui.theme.AndroidTetrisTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: handle the savedInstanceState here
        setContent {
            AndroidTetrisTheme {
                TetrisScreen()
            }
        }
    }
}