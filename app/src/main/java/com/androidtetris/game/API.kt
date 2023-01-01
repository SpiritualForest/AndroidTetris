package com.androidtetris.game
import kotlin.reflect.* // for KFunction
import com.androidtetris.game.event.Event
import android.os.Bundle

class API {
    private lateinit var gameObj: Game
    fun createGame(
        options: TetrisOptions = TetrisOptions(),
        savedState: Bundle? = null
    ) {
        gameObj = Game(options, savedState = savedState)
    }
    fun startGame() {
        // Start a new game
        gameObj.startGame()
    }

    fun addCallback(event: Event, func: KFunction<Unit>) {
        gameObj.eventDispatcher.addCallback(event, func)
    }

    fun deleteCallback(event: Event, func: KFunction<Unit>) {
        gameObj.eventDispatcher.deleteCallback(event, func)
    }

    fun move(direction: Direction) {
        gameObj.move(direction)
    }

    fun rotate() {
        gameObj.rotate()
    }

    fun endGame() {
        gameObj.endGame()
    }

    fun lines(): Int {
        return gameObj.lines
    }

    fun level(): Int {
        return gameObj.gameLevel
    }

    fun getNextTetromino(n: Int = 1): List<TetrominoCode> {
        return gameObj.getNextTetromino(n)
    }

    fun pauseGame() {
        gameObj.pauseGame()
    }

    fun unpauseGame() {
        gameObj.unpauseGame()
    }

    fun saveGame(bundleObj: Bundle): Bundle {
        return gameObj.saveGame(bundleObj)
    }
}
