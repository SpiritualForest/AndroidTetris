package com.androidtetris.ui.screens.tetris

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.androidtetris.R
import com.androidtetris.game.Direction
import com.androidtetris.settings.SettingsHandler
import com.androidtetris.ui.components.GameActionButton
import com.androidtetris.ui.components.TetrisGrid
import com.androidtetris.ui.components.TetrisText
import com.androidtetris.ui.components.UpcomingTetrominoesBox
import com.androidtetris.ui.theme.LocalColors
import kotlinx.coroutines.delay

/* AndroidTetris TetrisScreen: the composable that actually displays the gameplay */

@Composable
fun TetrisScreen() {
    val viewModel by remember { mutableStateOf(TetrisScreenViewModel()) }
    var isGhostEnabled by remember { mutableStateOf(viewModel.isGhostEnabled()) }
    Log.d("TetrisScreen", "Recomposed")
    val colors = LocalColors.current.colors
    Log.d("TetrisScreen", "${LocalColors.current.isDark}")
    Column(
        modifier = Modifier
            .background(colors.BackgroundColor)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(0.35f)
            ) {
                // Left side column, contains upcoming tetrominoes grid, stats, ghost chip
                UpcomingTetrominoesBox(
                    width = 120.dp,
                    height = 200.dp,
                    viewModel = viewModel,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                Stats(viewModel)
                TimeText(viewModel)
                val ghostIconTint = if (isGhostEnabled) Color.Green else Color.Red
                IconButton(
                    onClick = {
                        isGhostEnabled = !isGhostEnabled
                        viewModel.setTheGhostEnabled(isGhostEnabled)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                        .border(
                            BorderStroke(1.dp, ghostIconTint),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val icon = if (isGhostEnabled) R.drawable.check else R.drawable.close
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "Enable or disable ghost",
                            tint = ghostIconTint
                        )
                        TetrisText(
                            text = "Ghost",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
                IconButton(
                    onClick = { viewModel.restartGame() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                        .border(
                            BorderStroke(1.dp, colors.ForegroundColor),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.restart),
                            contentDescription = "Restart the game",
                            tint = colors.ForegroundColor
                        )
                        TetrisText(
                            text = "Restart",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
                // Pause stuff
                IconButton(
                    onClick = {
                        if (viewModel.gameState.gamePaused) {
                            viewModel.unpauseGame()
                        } else {
                            viewModel.pauseGame()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                        .border(
                            BorderStroke(1.dp, colors.ForegroundColor),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val icon =
                            if (viewModel.gameState.gamePaused) R.drawable.play else R.drawable.pause
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "Pause or unpause the game",
                            tint = colors.ForegroundColor
                        )
                        TetrisText(
                            text = if (viewModel.gameState.gamePaused) "Unpause" else "Pause",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.weight(0.65f),
                horizontalAlignment = Alignment.End
            ) {
                // Right side column, contains the tetris game grid
                val fraction = 0.8f
                TetrisGrid(
                    width = 180.dp,
                    height = 396.dp,
                    viewModel = viewModel,
                    gridWidth = SettingsHandler.getGridWidth(),
                    gridHeight = SettingsHandler.getGridHeight()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = fraction)
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GameActionButton(
                        drawable = R.drawable.arrow_up,
                        actionDelay = 100L,
                        onActionDown = { viewModel.rotate() }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(fraction = fraction),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GameActionButton(
                        drawable = R.drawable.arrow_left,
                        onActionDown = { viewModel.move(Direction.Left) }
                    )
                    GameActionButton(
                        drawable = R.drawable.arrow_right,
                        onActionDown = { viewModel.move(Direction.Right) }
                    )
                }
                Box(
                    modifier = Modifier.fillMaxWidth(fraction = fraction),
                    contentAlignment = Alignment.Center
                ) {
                    GameActionButton(
                        drawable = R.drawable.arrow_down,
                        onActionDown = { viewModel.move(Direction.Down) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Stats(
    viewModel: TetrisScreenViewModel,
    modifier: Modifier = Modifier
) {
    val gameStats by remember { derivedStateOf { viewModel.statsState } }
    Column(modifier = modifier) {
        TetrisText("Lines: ${gameStats.lines}")
        TetrisText("Score: ${gameStats.score}")
        TetrisText("Level: ${gameStats.level}")
    }
}

@Composable
fun TimeText(viewModel: TetrisScreenViewModel) {
    val keepCounting = viewModel.gameState.gameRunning && !viewModel.gameState.gamePaused
    var count by remember { mutableStateOf(viewModel.gameTimeSeconds) }
    val convertedCount = when (count) {
        in 0..9 -> "00:0$count"
        in 10..60 -> "00:$count"
        else -> {
            val seconds = count % 60
            val minutes = (count - seconds) / 60
            val minutesString = if (minutes < 10) "0$minutes" else "$minutes"
            val secondsString = if (seconds < 10) "0$seconds" else "$seconds"
            "$minutesString:$secondsString"
        }
    }
    LaunchedEffect(keepCounting) {
        while(keepCounting) {
            delay(1000)
            count = viewModel.increaseGameTimer()
        }
    }
    TetrisText(text = "Time: $convertedCount")
}