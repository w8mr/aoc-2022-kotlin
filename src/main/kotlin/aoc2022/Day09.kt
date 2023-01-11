package aoc2022

import aoc.*
import aoc.parser.*
import kotlin.math.absoluteValue
import kotlin.math.sign

class Day09 {
    enum class Dir(val text: String) {
        UP("U"),
        DOWN("D"),
        LEFT("L"),
        RIGHT("R")
    }

    data class Move(val dir: Dir, val count: Int)

    val dir = byEnum(Dir::class, Dir::text)
    val move = seq(dir followedBy " ", number() followedBy "\n", ::Move)
    val parser = oneOrMore(move)

    fun solve(input: String, n: Int): Int {
        data class State(
            val hits: Set<Coord> = mutableSetOf(),
            val head: Coord = Coord(),
            val tails: List<Coord>
        )

        val moves = parser.parse(input)

        fun Coord.move(dir: Dir): Coord =
            when (dir) {
                Dir.UP -> Coord(this.x, this.y + 1)
                Dir.DOWN -> Coord(this.x, this.y - 1)
                Dir.LEFT -> Coord(this.x - 1, this.y)
                Dir.RIGHT -> Coord(this.x + 1, this.y)
            }

        fun Coord.follow(head: Coord): Coord =
            if (((this.x - head.x).absoluteValue > 1) || ((this.y - head.y).absoluteValue > 1)) {
                Coord(this.x - (this.x - head.x).sign, this.y - (this.y - head.y).sign)
            } else {
                this
            }

        val r = moves.fold(State(tails = MutableList(n) { Coord() })) { s, m ->
            (0 until m.count).fold(s) { acc, _ ->
                val newHead = acc.head.move(m.dir)
                val newTails = acc.tails.scan(newHead) { previous, tail -> tail.follow(previous) }.drop(1)
                val newHits = acc.hits.plus(newTails.last())
                State(newHits, newHead, newTails)
            }

        }
        return r.hits.size
    }

    fun part1(input: String): Int {
        return solve(input, 1)
    }

    fun part2(input: String): Int {
        return solve(input, 9)
    }
}

