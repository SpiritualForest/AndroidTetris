package com.androidtetris

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startGame = findViewById<Button>(R.id.btn_startgame)
        val tetrisIntent = Intent(this, TetrisActivity::class.java)

        startGame.setOnClickListener { startActivity(tetrisIntent) }
    }
}