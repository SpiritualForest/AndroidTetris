package com.androidtetris.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androidtetris.NavDestination
import com.androidtetris.R
import com.androidtetris.game.Point
import com.androidtetris.settings.SettingsHandler
import com.androidtetris.ui.components.TetrisDropdownMenuItem
import com.androidtetris.ui.components.TetrisDropdownMenuItemData
import com.androidtetris.ui.components.TetrisText
import com.androidtetris.ui.theme.LocalColors

/* AndroidTetris application entry point screen */

// Maybe use Surface here for each option?

@Composable
fun HomeScreen(navController: NavController) {
    val colors = LocalColors.current.colors
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(
                    route = NavDestination.Tetris.route,
                ) {
                    launchSingleTop = true
                }
            }
        ) {
            Text("Start Game")
        }
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        Surface(
            shape = RoundedCornerShape(10.dp),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var invertRotation by remember { mutableStateOf(SettingsHandler.getInvertRotation()) }
                Checkbox(
                    checked = invertRotation,
                    onCheckedChange = { checked ->
                        SettingsHandler.setInvertRotation(checked)
                        invertRotation = checked
                    }
                )
                TetrisText(text = stringResource(id = R.string.invert_rotation))
            }
        }
        Surface(
            shape = RoundedCornerShape(10.dp),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            // Grid size menu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var gridSizeMenuExpanded by remember { mutableStateOf(false) }
                TetrisText(
                    stringResource(id = R.string.txt_gridSize),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                val gridWidth = SettingsHandler.getGridWidth()
                val gridHeight = SettingsHandler.getGridHeight()
                IconButton(
                    onClick = { gridSizeMenuExpanded = !gridSizeMenuExpanded },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(modifier = Modifier
                        .wrapContentWidth()
                        .border(
                        BorderStroke(1.dp, colors.BorderColor),
                        shape = RoundedCornerShape(8.dp)
                    )
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.check),
                            contentDescription = "Selected option",
                            tint = Color.Green,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        TetrisText("${gridWidth}x${gridHeight}")
                        Icon(
                          painter = painterResource(
                              id = if (gridSizeMenuExpanded) R.drawable.expand_less else R.drawable.expand_more
                          ),
                          contentDescription = if (gridSizeMenuExpanded) "Expand less" else "Expand more",
                          tint = colors.ForegroundColor
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    DropdownMenu(
                        expanded = gridSizeMenuExpanded,
                        onDismissRequest = { gridSizeMenuExpanded = false }
                    ) {
                        // 10 x 22
                        val gridSizes = listOf(
                            Point(10, 22), // Default
                            Point(15, 33),
                            Point(20, 44),
                            Point(30, 66)
                        )
                        gridSizes.forEach {
                            val width = it.x
                            val height = it.y
                            val selected = width == gridWidth && height == gridHeight
                            TetrisDropdownMenuItem(
                                item = TetrisDropdownMenuItemData(
                                    title = "${width}x${height}",
                                    selected = selected,
                                    onClick = {
                                        SettingsHandler.setGridWidth(width)
                                        SettingsHandler.setGridHeight(height)
                                        gridSizeMenuExpanded = false
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}