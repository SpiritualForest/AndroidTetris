package com.androidtetris.game

// Tetromino codes - so that the UI can differentiate between them for colours if desired
enum class TetrominoCode { I, O, J, L, S, T, Z }

/*
Coordinates matrix:
[0, 1, 2, 3,
4, 5, 6, 7,
8, 9, 10, 11,
12, 13, 14, 15]
We're going to represent the tetrominoes' coordinates
based on this system. We "draw" the tetromino using these values
and then convert them to their respective (x, y) position within
this grid. So value 9 is at (x, y) (1, 2), second index of the third sub-array.
15 is at (3, 3). 10 is at (2, 2).
*/

data class Point(var x: Int, var y: Int) {
    fun copyOf(): Point {
        // Return a copy of this point
        return Point(x, y)
    }

    override fun toString(): String {
        return String.format("%dx%d", x, y)
    }
}

abstract class Tetromino(private val grid: Grid) {
    open val coordinatesMap = arrayOf<Int>()
    open val rotationsMap = arrayOf(arrayOf<Int>())
    open val tetrominoCode: TetrominoCode = TetrominoCode.I // Individual tetromino ID
    open var rotations: MutableList<Array<Point>> = mutableListOf()
        protected set
    open var coordinates = arrayOf<Point>()
        internal set
    var currentRotation = 0 // Set to first rotation

    /* All tetrominoes except I and O will have width 3 */
    open val width  = 3
    protected fun initCoordinates() {
        // Gotta implement this because of Kotlin's initialization rules
        coordinates = grid.convertCoordinatesMap(coordinatesMap, width)
        for (rotationMap in rotationsMap) {
            rotations.add(grid.convertCoordinatesMap(rotationMap, width))
        }
    }
}

data class I(val grid: Grid) : Tetromino(grid) {
    override val width = 4
    override val tetrominoCode = TetrominoCode.I
    override var coordinatesMap = arrayOf(0, 1, 2, 3)
    override val rotationsMap = arrayOf(
        arrayOf(2, 6, 10, 14),
        arrayOf(0, 1, 2, 3),
    )
    init {
        initCoordinates()
    }
}

data class O(val grid: Grid) : Tetromino(grid) {
    override val width = 2
    override val tetrominoCode = TetrominoCode.O
    override var coordinatesMap = arrayOf(0, 1, 4, 5)
    override val rotationsMap = arrayOf(arrayOf(0, 1, 4, 5))
    init {
        initCoordinates()
    }
}

data class J(val grid: Grid) : Tetromino(grid) {
    override val tetrominoCode = TetrominoCode.J
    override var coordinatesMap = arrayOf(0, 4, 5, 6)
    override val rotationsMap = arrayOf(
        arrayOf(1, 5, 9, 8),
        arrayOf(0, 1, 2, 6),
        arrayOf(0, 1, 4, 8),
        arrayOf(0, 4, 5, 6),
    )
    init {
        initCoordinates()
    }
}

data class L(val grid: Grid) : Tetromino(grid) {
    override val tetrominoCode = TetrominoCode.L
    override var coordinatesMap = arrayOf(2, 4, 5, 6)
    override val rotationsMap = arrayOf(
        arrayOf(0, 1, 5, 9),
        arrayOf(0, 1, 2, 4),
        arrayOf(0, 4, 8, 9),
        arrayOf(2, 4, 5, 6),
    )
    init {
        initCoordinates()
    }
}

data class S(val grid: Grid) : Tetromino(grid) {
    override val tetrominoCode = TetrominoCode.S
    override var coordinatesMap = arrayOf(1, 2, 4, 5)
    override val rotationsMap = arrayOf(
        arrayOf(0, 4, 5, 9),
        arrayOf(1, 2, 4, 5),
    )
    init {
        initCoordinates()
    }
}

data class T(val grid: Grid) : Tetromino(grid) {
    override val tetrominoCode = TetrominoCode.T
    override var coordinatesMap = arrayOf(0, 1, 2, 5)
    override val rotationsMap = arrayOf(
        arrayOf(0, 4, 8, 5),
        arrayOf(1, 4, 5, 6),
        arrayOf(1, 4, 5, 9),
        arrayOf(0, 1, 2, 5)
    )
    init {
        initCoordinates()
    }
}

data class Z(val grid: Grid) : Tetromino(grid) {
    override val tetrominoCode = TetrominoCode.Z
    override var coordinatesMap = arrayOf(0, 1, 5, 6)
    override val rotationsMap = arrayOf(
        arrayOf(1, 4, 5, 8),
        arrayOf(0, 1, 5, 6),
    )
    init {
        initCoordinates()
    }
}
