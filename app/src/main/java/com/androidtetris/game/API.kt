package com.androidtetris.game
import kotlin.reflect.* // for KFunction

class API {
    private var gameObj: Game? = null

    fun startGame(gameLevel: Int = 1, gridWidth: Int = 10, gridHeight: Int = 22) {
        // Start a new game
        gameObj = Game(gameLevel = gameLevel, gridWidth = gridWidth, gridHeight = gridHeight)
    }

    fun addCallback(event: Event, func: KFunction<Unit>) {
        gameObj?.eventDispatcher?.addCallback(event, func)
    }

    fun move(direction: Direction) {
        gameObj?.move(direction)
    }

    fun rotate() {
        gameObj?.rotate()
    }

    fun endGame() {
        gameObj?.endGame()
    }
}