package aoc2022

import aoc.*
import kotlin.math.absoluteValue
import kotlin.math.sign

sealed interface Dir
object Up: Dir
object Down: Dir
object Left: Dir
object Right: Dir

fun main() {
    data class Move(val dir: Dir, val count: Int)

    val up = Literal("U").to(Up)
    val down = Literal("D").to(Down)
    val left = Literal("L").to(Left)
    val right = Literal("R").to(Right)
    val move = seq(OneOf(up, down, left, right)+" ", number()+"\n") { d, c -> Move(d, c) }
    val parser = zeroOrMore(move)

    fun solve(input: String, n: Int): Int {
        data class State(
            val hits: Set<Coord> = mutableSetOf(),
            val head: Coord = Coord(),
            val tails: List<Coord>
        )

        val moves = parser.parse(input)

        fun Coord.move(dir: Dir): Coord =
            when (dir) {
                Up -> Coord(this.x, this.y + 1)
                Down -> Coord(this.x, this.y - 1)
                Left -> Coord(this.x - 1, this.y)
                Right -> Coord(this.x + 1, this.y)
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
    // test if implementation meets criteria from the description, like:/
    val testInput = readFile(2022, 9, 1).readText()
    val input = readFile(2022, 9).readText()
    check(part1(testInput) == 13)

    println(part1(input))
    check(part1(input) == 6269)

    check(part2(testInput) == 1)
    check(part2(readFile(2022, 9, 2).readText()) == 36)

    println(part2(input))
    check(part2(input) == 2557)

}

