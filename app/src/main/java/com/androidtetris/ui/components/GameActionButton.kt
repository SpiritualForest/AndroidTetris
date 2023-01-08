package com.androidtetris.ui.components

import android.util.Log
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.androidtetris.ui.theme.LocalColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameActionButton(
    @DrawableRes drawable: Int,
    modifier: Modifier = Modifier,
    actionDelay: Long = 60,
    onActionDown: () -> Unit = {}
) {
    var isDown by remember { mutableStateOf(false) }

    LaunchedEffect(isDown) {
        while (isDown) {
            yield()
            onActionDown()
            delay(actionDelay)
        }
    }
    val colors = LocalColors.current.colors
    Box(modifier = modifier
        .border(
            BorderStroke(1.dp, colors.BorderColor),
            shape = RoundedCornerShape(16.dp)
        )
        .pointerInteropFilter { motionEvent ->
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    isDown = true
                    true
                }

                MotionEvent.ACTION_UP -> {
                    isDown = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    isDown = true
                    true
                }

                MotionEvent.ACTION_POINTER_DOWN -> {
                    isDown = true
                    true
                }

                MotionEvent.ACTION_POINTER_UP -> {
                    isDown = false
                    true
                }

                else -> {
                    Log.d("MotionEventLog", "Event: $motionEvent")
                    false
                } // Do not handle anything else
            }
        }
    ) {
        Icon(
            painter = painterResource(id = drawable),
            contentDescription = null,
            tint = if (isDown) Color.Magenta else colors.ForegroundColor
        )
    }
}