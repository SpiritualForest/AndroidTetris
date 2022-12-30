package com.androidtetris.ui.components

import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameActionButton(
    @DrawableRes drawable: Int,
    modifier: Modifier = Modifier,
    actionDelay: Long = 60,
    onActionDown: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var isDown by remember { mutableStateOf(false) }
    val iconTint = if (isDown) Color.Magenta else Color.Black

    Box(modifier = modifier
        .border(
            BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(32.dp)
        )
        .pointerInteropFilter {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDown = true
                    scope.launch {
                        while (isDown) {
                            yield()
                            onActionDown()
                            delay(actionDelay)
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    isDown = false
                    true
                }
                else -> false // Do not handle anything else
            }
        }
    ) {
        Icon(
            painter = painterResource(id = drawable),
            contentDescription = null,
            tint = iconTint
        )
    }
}