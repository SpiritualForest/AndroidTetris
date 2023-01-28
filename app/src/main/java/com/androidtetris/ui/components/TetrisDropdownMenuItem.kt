package com.androidtetris.ui.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.androidtetris.R

@Composable
fun TetrisDropdownMenuItem(
    item: TetrisDropdownMenuItemData
) {
    DropdownMenuItem(
        text = { TetrisText(item.title) },
        onClick = item.onClick,
        leadingIcon = {
            if (item.selected) {
                Icon(
                    painter = painterResource(id = R.drawable.check),
                    contentDescription = "Option is selected",
                    tint = Color.Green
                )
            }
        }
    )
}

data class TetrisDropdownMenuItemData(
    val title: String,
    val selected: Boolean,
    val onClick: () -> Unit
)