package com.androidtetris

import org.junit.Test
import org.junit.Assert.*
import com.androidtetris.game.*

class TetrominoFunctionsUnitTest {
    private val gameObj = Game(TetrisOptions(isTestMode = true))

    @Test
    fun test_moveLeft() {
        // Test the movement to the left (x-1)
        gameObj.setTetromino(TetrominoCode.I)
        // I's initial coordinates are:
        // (3, 0), (4, 0), (5, 0), (6, 0)
        val tetromino = gameObj.currentTetromino
        val original = tetromino.coordinates
        val correctResult = listOf(Point(2, 0), Point(3, 0), Point(4, 0), Point(5, 0))
        gameObj.move(Direction.Left)
        assertFalse(original == tetromino.coordinates)
        for(result in correctResult) {
            assertTrue(tetromino.coordinates.contains(result))
        }
    }

    @Test
    fun test_moveRight() {
        // Movement to the right (x+1)
        gameObj.setTetromino(TetrominoCode.I)
        val i = gameObj.currentTetromino
        val original = i.coordinates
        gameObj.move(Direction.Right)
        val correctResult = listOf(Point(4, 0), Point(5, 0), Point(6, 0), Point(7, 0))
        assertFalse(original == i.coordinates)
        for(result in correctResult) {
            assertTrue(i.coordinates.contains(result))
        }
    }

    @Test
    fun test_moveDown() {
        // y+1
        gameObj.setTetromino(TetrominoCode.I)
        val i = gameObj.currentTetromino
        val original = i.coordinates
        gameObj.move(Direction.Down)
        val correctResult = listOf(Point(3, 1), Point(4, 1), Point(5, 1), Point(6, 1))
        assertFalse(original == i.coordinates)
        for(result in correctResult) {
            assertTrue(i.coordinates.contains(result))
            assertFalse(original.contains(result))
        }
    }

    @Test
    fun test_rotate() {
        // Test it with the most basic functionality
        gameObj.setTetromino(TetrominoCode.I)
        val i = gameObj.currentTetromino
        // Original rotation: // (3, 0), (4, 0), (5, 0), (6, 0)
        // Second (and only other) rotation: (5, 0), (5, 1), (5, 2), (5, 3)
        val original = arrayOf(Point(3, 0), Point(4, 0), Point(5, 0), Point(6, 0))
        val second = arrayOf(Point(5, 0), Point(5, 1), Point(5, 2), Point(5, 3))
        gameObj.rotate()
        for(xy in second) {
            assertTrue(i.coordinates.contains(xy))
        }
        // Now rotate again, back to the original form
        gameObj.rotate()
        for(xy in original) {
            assertTrue(i.coordinates.contains(xy))
        }
    }

    @Test
    fun test_rotate_after_move() {
        // Original rotation: // (3, 0), (4, 0), (5, 0), (6, 0)
        // Second (and only other) rotation: (5, 0), (5, 1), (5, 2), (5, 3)
        gameObj.setTetromino(TetrominoCode.I)
        val i = gameObj.currentTetromino
        val originalArrayObj = i.coordinates
        gameObj.move(Direction.Down)
        gameObj.rotate() // Now we are the vertical shape of I
        // After moving downwards, all the y values should increase by one, and be at the range of 1-4
        val correctResultDown = arrayOf(Point(5, 1), Point(5, 2), Point(5, 3), Point(5, 4))
        assertFalse(i.coordinates == originalArrayObj)
        for(xy in correctResultDown) {
            assertTrue(i.coordinates.contains(xy))
        }
        // Now try to the right, x+1. All y should be 1, and x should range from 4-7
        gameObj.move(Direction.Right)
        gameObj.rotate() // Now back to the original horizontal shape of I
        val correctResultRight = arrayOf(Point(4, 1), Point(5, 1), Point(6, 1), Point(7, 1))
        for(xy in correctResultRight) {
            assertTrue(i.coordinates.contains(xy))
        }
        // Now left twice. x-2. y should be 1-4, x is all 4
        gameObj.move(Direction.Left)
        gameObj.move(Direction.Left)
        gameObj.rotate() // Now back to vertical
        val correctResultLeft = arrayOf(Point(4, 1), Point(4, 2), Point(4, 3), Point(4, 4))
        assertArrayEquals(correctResultLeft, i.coordinates)
        for(xy in correctResultLeft) {
            assertTrue(i.coordinates.contains(xy))
        }
    }
}
