package aoc2022

import aoc.*
import java.util.*

class Day24() {
    enum class GridType(val text: String) {
        WALL("#"),
        EMPTY("."),
        UP("^"),
        RIGHT(">"),
        DOWN("v"),
        LEFT("<")
    }

    data class State(val time: Int, val row: Int, val col: Int) {
        var previousState: State? = null
    }

    val gridtype = byEnum(GridType::class, GridType::text)
    val line = oneOrMore(gridtype).asArray() followedBy "\n"
    val parser = oneOrMore(line).asArray()

    fun part1(input: String): Int {
        val parsed = parser.parse(input)

        val withoutWalls = parsed.drop(1).dropLast(1).map { it.drop(1).dropLast(1) }
        val width = withoutWalls[0].size
        val height = withoutWalls.size

        val grids = listOf(GridType.UP, GridType.DOWN, GridType.LEFT, GridType.RIGHT).map {gt ->
            gt to withoutWalls.map { it.map { if (it == gt) gt else GridType.EMPTY}}
        }.toMap()
/*        grids.values.forEach {
            println(it.map { it.map(GridType::text).joinToString("") }.joinToString("\n")+"\n")
        }
*/
        val blocked = mutableMapOf<Int, Array<BooleanArray>>()

        grids.values.forEach { rows ->
            rows.forEachIndexed { rowIndex, cols ->
                cols.forEachIndexed { colIndex, cell ->
                    (0 until width.lcm(height)).forEach { time ->
                        val grid = blocked.getOrPut(time) { Array(height) { BooleanArray(width) } }
                        when (cell) {
                            GridType.EMPTY -> {}
                            GridType.RIGHT -> grid[rowIndex][(colIndex + time).mod(width)] = true
                            GridType.LEFT -> grid[rowIndex][(colIndex - time).mod(width)] = true
                            GridType.UP -> grid[(rowIndex - time).mod(height)][colIndex] = true
                            GridType.DOWN -> grid[(rowIndex + time).mod(height)][colIndex] = true
                            else -> {}
                        }
                    }
                }
            }
        }
/*        (0..10).forEach { time ->
            println("\nRound $time\n")
            blocked.getValue(time).joinToString("\n") { row -> row.map { if (it) '*' else '.' }.joinToString("") }.println()

        }
*/

        val initialState = State(0, -1, 0)

        val queue: Queue<State> = LinkedList()
        val seen = mutableSetOf<State>()

        queue.add(initialState)

        var bestState = initialState


        while (queue.isNotEmpty()) {
            val state = queue.remove()
            if (state in seen) continue
            seen.add(state)

            val (time, row, col) = state

            val newTime = time + 1
            val blockNow = blocked.getValue(newTime % blocked.size)

            val validRows = 0 until height
            val validCols = 0 until width
            fun isValidAndNotBlocked(row: Int, col: Int) =
                (row == -1 && col == 0) || (row == height && col == width - 1) || (row in validRows && col in validCols) && !blockNow.get(row).get(col)

            fun add(
                row: Int,
                col: Int
            ) {
                val newState = State(newTime, row, col)
                newState.previousState = state
                queue.add(newState)
            }


            if (row == height && col == width - 1) {
                bestState = state
                break
            } else {
                if (isValidAndNotBlocked(row, col + 1)) add(row, col + 1)
                if (isValidAndNotBlocked(row + 1, col)) add(row + 1, col)
                if (isValidAndNotBlocked(row, col)) add(row, col)
                if (isValidAndNotBlocked(row - 1, col)) add(row - 1, col)
                if (isValidAndNotBlocked(row, col - 1)) add(row, col - 1)
            }
        }

        return bestState.time
    }


    fun part2(input: String): Int {
        return TODO()
    }
}

