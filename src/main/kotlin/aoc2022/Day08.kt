package aoc2022

import aoc.*

class Day08 {
    val row = oneOrMore(digit()) followedBy "\n"
    val parser = oneOrMore(row)

    fun part1(input: String): Int {
        val grid = parser.parse(input)
        val height = grid.size
        val width = grid[0].size

        val visible = mutableSetOf<Coord>()

        fun checkTrees(
            main: IntProgression,
            sub: IntProgression,
            toCoord: (a: Int, b: Int) -> Coord
        ) {
            for (a in main) {
                var highest = -1
                for (b in sub) {
                    val c = toCoord(a, b)
                    val v = grid[c.y][c.x]
                    if (v > highest) {
                        visible.add(c)
                        highest = v
                    }
                }
            }
        }

        checkTrees(0..height-1, 0..width-1) { y, x -> Coord(x,y) }
        checkTrees(0..height-1, width-1 downTo 0) { y, x -> Coord(x,y) }
        checkTrees(0..width-1, 0..height-1) { x, y -> Coord(x,y) }
        checkTrees(0..width-1, height-1 downTo 0) { x, y -> Coord(x,y) }
        return visible.size
    }

    fun part2(input: String): Int {
        val grid = parser.parse(input)
        val height = grid.size
        val width = grid[0].size

        var high = -1

        fun check(c: Coord, range: IntProgression, toCoord: (c: Coord, i: Int) -> Coord): Int {
            val v = grid[c.y][c.x]
            var count = 0
            for (i in range) {
                count++
                val c2 = toCoord(c, i)
                if (grid[c2.y][c2.x] >= v) break
            }
            return count
        }

        for (y in 1..height-2) {
            for (x in 1..width-2) {
                val up = check(Coord(x,y), 1.. y) { c, i -> Coord(c.x, c.y - i) }
                val down = check(Coord(x,y), 1.. height-1-y) { c, i -> Coord(c.x, c.y + i)}
                val left = check(Coord(x,y), 1.. x) { c, i -> Coord(c.x - i, c.y) }
                val right = check(Coord(x,y), 1.. width-1-x) { c, i -> Coord(c.x + i, c.y) }
                val score = up * down * left * right
                if (score>high) {
                    high = score
                }
            }
        }

        return high
    }
}

