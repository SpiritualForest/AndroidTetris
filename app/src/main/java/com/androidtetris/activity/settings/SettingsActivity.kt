package com.androidtetris.activity.settings

// Settings, etc.

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.androidtetris.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val tetrominoColorsActivity = findViewById<Button>(R.id.btn_tetrominoColors)
        tetrominoColorsActivity.setOnClickListener { startActivity(Intent(this, TetrominoColorsActivity::class.java)) }
    }
}