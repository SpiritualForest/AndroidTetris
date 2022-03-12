package com.androidtetris

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.androidtetris.activity.settings.SettingsActivity
import com.androidtetris.activity.tetris.TetrisActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingsBtn = findViewById<Button>(R.id.btn_startSettingsActivity)
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        settingsBtn.setOnClickListener { startActivity(settingsIntent) }

        val startGameBtn = findViewById<Button>(R.id.btn_startgame)
        val startGameIntent = Intent(this, TetrisActivity::class.java)
        startGameBtn.setOnClickListener { startActivity(startGameIntent) }
    }
}
