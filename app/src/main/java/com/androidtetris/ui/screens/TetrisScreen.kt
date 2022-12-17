package com.androidtetris.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidtetris.ui.components.TetrisGrid

/* AndroidTetris TetrisScreen: the composable that actually displays the gameplay */

@Composable
fun TetrisScreen() {
    // Top level column
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.5f)
            ) {
                // Left side column, contains upcoming tetrominoes grid, stats, ghost chip
                TetrisGrid(
                    width = 120.dp,
                    height = 200.dp
                )
            }
            Column(modifier = Modifier.weight(0.5f)) {
                // Right side column, contains the tetris game grid
                TetrisGrid(
                    width = 200.dp,
                    height = 440.dp
                )
            }
        }
        Row(modifier = Modifier.fillMaxSize()) {
            // Row that contains the action buttons
            Box(modifier = Modifier.weight(0.5f)) {
                // Restart, pause, endgame buttons
            }
            Box(modifier = Modifier.weight(0.5f)) {
                // Game action buttons: rotate, left, right, down
            }
        }
    }
}