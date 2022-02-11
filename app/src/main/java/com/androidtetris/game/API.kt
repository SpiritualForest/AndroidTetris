package com.androidtetris.game
import kotlin.reflect.* // for KFunction
import com.androidtetris.game.event.Event

class API {
    private val gameObj = Game()

    fun startGame(gameLevel: Int = 1, gridWidth: Int = 10, gridHeight: Int = 22) {
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

    fun getGrid(): Map<Int, HashMap<Int, TetrominoCode>> {
        return gameObj.grid.grid.toMap()
    }

    fun getCurrentTetromino(): TetrominoCode {
        return gameObj.currentTetromino.tetrominoCode
    }

    fun lines(): Int {
        return gameObj.lines
    }

    fun level(): Int {
        return gameObj.gameLevel
    }
}
