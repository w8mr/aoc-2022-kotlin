package aoc2022

import aoc.*
import java.util.*

class Day14 {
    data class Line(val start: Coord, val end: Coord)

    class State(val ls: List<Line>) {
        fun build(): MutableMap<Int, TreeSet<Int>> {
            val map = mutableMapOf<Int, TreeSet<Int>>()
            ls.forEach { (start, end) ->
                (minOf(start.x, end.x)..maxOf(start.x, end.x)).forEach { x ->
                    (minOf(start.y, end.y)..maxOf(start.y, end.y)).forEach { y ->
                        insert(map, x, y)
                    }
                }
            }
            return map
        }
        val blocked: MutableMap<Int, TreeSet<Int>> = build()

        private fun insert(map: MutableMap<Int, TreeSet<Int>>, x: Int, y: Int) {
            map.compute(x) {
                _, set -> when (set) {
                    null -> {
                        val s = TreeSet<Int>()
                        s.add(y)
                        s
                    }

                    else -> {
                        set.add(y)
                        set
                    }
                }
            }
        }

        private fun insert(x: Int, y: Int) = insert(blocked, x,y )

        private fun findIntersect(x: Int, y: Int): Int? =
            blocked[x]?.asSequence()?.dropWhile { i -> i < y }?.firstOrNull()

        fun simulateSandDrop(): Boolean {
            var x = 500
            var y: Int? = 0
            while (true) {
                y = findIntersect(x, y!!)
                if ((y == null) || (y == 0 && x ==500)){
                    return false
                } else {
                    if (y == findIntersect(x - 1, y)) {
                        if (y == findIntersect(x + 1, y)) {
                            insert(x, y - 1)
                            return true
                        } else {
                            x++
                        }
                    } else {
                        x--
                    }
                }
            }
        }
    }

    fun createLine(coords: List<Coord>): List<Line> =
        coords.zipWithNext(::Line)

    val coord = seq(number() followedBy ",", number(), ::Coord)
    val line = coord sepBy " -> " followedBy "\n" map ::createLine
    val lines = zeroOrMore(line) map { it.flatten() }

    private fun solve(lines: List<Line>): Int {
        val state = State(lines)
        var count = -1
        do {
            count++
            val r = state.simulateSandDrop()
        } while (r)
        return count
    }

    fun part1(input: String): Int {
        val lines = lines.parse(input)
        return solve(lines)
    }

    fun part2(input: String): Int {
        val lines = lines.parse(input)
        val lowestY = lines.fold(0) { acc, (start, end) -> maxOf(acc, start.y, end.y) }
        val bottomY = lowestY + 2
        val linesWithBottom = lines + listOf(Line(Coord(499-bottomY, bottomY), Coord(501+bottomY, bottomY)))
        return solve(linesWithBottom)
    }
}

