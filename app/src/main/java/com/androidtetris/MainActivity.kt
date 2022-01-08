package com.androidtetris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var tetris: Tetris
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val canvasView = findViewById<GridCanvas>(R.id.gridCanvas)
        tetris = Tetris(canvasView)
    }
}