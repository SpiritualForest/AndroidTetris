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
    //var grid = Array(height) { Array<TetrominoCode?>(width) { null } }
    var grid: HashMap<Int, HashMap<Int, TetrominoCode>> = hashMapOf()

    internal fun getCenter(tWidth: Int): Int {
        /* Get the center x position in the grid based on
        the tetromino's width
         */
        return (width / 2) - (tWidth / 2)
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

    internal fun convertCoordinatesMap(map: Array<Int>, tWidth: Int): Array<Point> {
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

    internal fun isCollision(coordinates: Array<Point>): Boolean {
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
            else if ((y in grid) && (x in grid[y]!!)) {
                // Position occupied in grid
                return true
            }
        }
        // No collisions
        return false
    }

    fun clear() {
        // Clear the whole grid
        grid.clear()
    }

    fun clearLine(y: Int) {
        grid.remove(y)
    }

    fun fillPosition(x: Int, y: Int, tetrominoCode: TetrominoCode) {
        // Add a coordinate point to the grid
        if (y !in grid) {
            // new y
            grid[y] = hashMapOf(x to tetrominoCode)
        }
        else {
            // this y exists, extend it
            val subMap = grid[y]
            subMap?.put(x, tetrominoCode)
        }
    }

    fun pushLines(lowestLine: Int) {
        // Push all the lines downwards after a line (or more) were completed.
        // "Lowest" and "highest" refer to their visual position on the grid,
        // not their actual numerical values. The higher a line is visually in the grid,
        // the lower its value is, because it's closer to 0. The "highest" line is at 0.
        val highest = grid.keys.toList().minOrNull()
        if (highest == null) {
            // Some error? Why was this function called?
            println("pushLines($lowestLine) called erroneously when the grid is empty.")
            return
        }
        var step = 0
        for(y in lowestLine downTo highest) {
            // Loop backwards from the lowest y to the highest.
            // If the line's filled positions count is 0, we skip it
            // Otherwise we copy the array and place it <step> steps down,
            // and then clear the line.
            if (y !in grid) { step++ }
            else {
                grid[y+step] = grid[y]!!
                grid.remove(y)
            }
        }
    }

    fun isLineFull(y: Int): Boolean {
        if (y !in grid) { return false }
        return grid[y]!!.count() == width
    }
}