package com.androidtetris.game

/*
Our Tetris grid.
10x22 (x, y) default measurements.
Supply to the constructor.
*/

/* Coordinates matrix for tetrominoes:
[0, 1, 2, 3,
4, 5, 6, 7,
8, 9, 10, 11,
12, 13, 14, 15]
 */
class Grid(val width: Int, val height: Int) {
    // columns, rows (x, y)
    // Create 2D array of rows,columns
    // All elements are set to null by default.
    // A TetrominoCode value instead of null means the position is occupied.
    var grid = Array(height) { Array<TetrominoCode?>(width) { null } }
    var filledLinePositions = Array(height) { 0 } // To track line completions

    fun getCenter(tWidth: Int): Int {
        /* Get the center x position in the grid based on
        the tetromino's width
         */
        return (width / 2) - (tWidth / 2).toInt()
    }

    private fun findArrayPosition(n: Int, length: Int): Point {
        /* This function calculates the x and y indices of a number <n>
        in an array of arrays based on the length of the nested arrays.
        a 2D array of 10 arrays of 4 elements each, has length 4.
        [[0, 1, 2, 3], -> 0: [0, 1, 2, 3]
         [4, 5, 6, 7], -> 1: [0, 1, 2, 3]
         [8, 9, 10, 11], -> 2: [0, 1, 2, 3]
         [12, 13, 14, 15]] -> 3: [0, 1, 2, 3]
        Example with n=15, length=4
        x = 15 % 4 (== 3)
        y = (15-x) / 4 (12 / 4 == 3)
        Result is (3, 3) */
        val x = n % length
        val y = (n-x) / length
        return Point(x, y)
    }

    fun convertCoordinatesMap(map: Array<Int>, tWidth: Int): Array<Point> {
        /* Converts the given coordinates map to (x, y) positions */
        // I block: arrayOf(0, 1, 2, 3)
        val coordinates = Array(map.size) { Point(0, 0) }
        val gridCenter = getCenter(tWidth)
        for((i, n) in map.withIndex()) {
            /* We calculate where i is located in our array of arrays
             and then increase its x coordinate by <gridCenter> position.
             y retains its value.
             For example if x resulted in 2 and gridCenter is 3, then x is 2+3 == 5.
             */
            val point = findArrayPosition(n, 4)
            point.x += gridCenter
            coordinates[i] = point
        }
        return coordinates
    }

    fun isCollision(coordinates: Array<Point>): Boolean {
        for(point in coordinates) {
            val x = point.x
            val y = point.y
            if ((x >= width) || (x < 0)) {
                // x goes out of bounds
                return true
            }
            else if (y >= height) {
                // y out of bounds
                return true
            }
            else if (grid[y][x] != null) {
                // Position occupied in grid
                return true
            }
        }
        // No collisions
        return false
    }

    fun isLineFull(y: Int): Boolean {
        return filledLinePositions[y] == width
    }

    fun clear() {
        // Clear the whole grid
        grid = Array(height) { Array<TetrominoCode?>(width) { null } }
    }

    fun fillPosition(x: Int, y: Int, tetrominoCode: TetrominoCode) {
        // Fill the position at grid[y][x].
        // Tetromino code is a number from 1-7 which represents the tetromino.
        // We're doing this in case the UI wants to use a different colour for each tetromino,
        // so it could distinguish between them.
        // 0 means the position is empty.
        grid[y][x] = tetrominoCode
        filledLinePositions[y]++
    }

    fun clearLine(y: Int) {
        // Reset all the positions on this line to null
        grid[y] = Array<TetrominoCode?>(width) { null }
        filledLinePositions[y] = 0
    }

    fun pushLines(lowestLine: Int) {
        // Push all the lines downwards after a line (or more) were completed.
        var highest = 0
        for((i, x) in filledLinePositions.withIndex()) {
            if (x > 0) {
                highest = i
                break
            }
        }
        var step = 0
        for(y in lowestLine downTo highest) {
            // Loop backwards from the lowest y to the highest.
            // If the line's filled positions count is 0, we skip it
            // Otherwise we copy the array and place it <step> steps down,
            // and then clear the line.
            if (filledLinePositions[y] == 0) { step++ }
            else {
                grid[y+step] = grid[y].copyOf()
                filledLinePositions[y+step] = filledLinePositions[y]
                clearLine(y)
            }
        }
    }
}