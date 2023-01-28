package com.androidtetris.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.androidtetris.R
import com.androidtetris.ui.theme.LocalColors

@Composable
fun DropdownMenuSurface(
    title: String,
    selectionText: String,
    modifier: Modifier = Modifier,
    menuExpanded: Boolean = false,
    onMenuClick: () -> Unit = {},
    dropdownMenu: @Composable () -> Unit,
) {
    val colors = LocalColors.current.colors
    Surface(
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TetrisText(
                text = title,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .border(
                            border = BorderStroke(1.dp, colors.BorderColor),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                        .clickable { onMenuClick() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = "Selected option",
                        tint = Color.Green,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    TetrisText(selectionText)
                    Icon(
                        painter = painterResource(
                            id = if (menuExpanded) R.drawable.expand_less else R.drawable.expand_more
                        ),
                        contentDescription = if (menuExpanded) "Expand less" else "Expand more",
                        tint = colors.ForegroundColor
                    )
                }
                dropdownMenu()
            }
        }
    }
}