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

    fun part1(input: String): Int {
        data class State(val hits: Set<Coord> = mutableSetOf(), val head: Coord = Coord(), val tail: Coord = Coord())
        val moves = parser.parse(input)
        println(moves)

        fun Coord.move(dir: Dir) : Coord =
            when (dir) {
                Up -> Coord(this.x, this.y + 1)
                Down -> Coord(this.x, this.y - 1)
                Left -> Coord(this.x - 1, this.y)
                Right -> Coord(this.x + 1, this.y)
            }

        fun Coord.follow(head: Coord): Coord =
            if (((this.x-head.x).absoluteValue>1) || ((this.y-head.y).absoluteValue>1)) {
                Coord(this.x-(this.x-head.x).sign, this.y-(this.y-head.y).sign)
            } else {
                this
            }

        val r =  moves.fold(State()) { s, m ->
            (0 until m.count).fold(s) { acc, _ ->
                val newHead = acc.head.move(m.dir)
                val newTail = acc.tail.follow(newHead)
               // println("head: $newHead, tail: $newTail")
                val newHits = acc.hits.plus(newTail)
                State(newHits, newHead, newTail)
            }

        }
        return r.hits.size
    }

    fun part2(input: String): Int {
        return TODO()
    }
    // test if implementation meets criteria from the description, like:/
    val testInput = readFile(2022, 9, 1).readText()
    val input = readFile(2022, 9).readText()
    check(part1(testInput) == 13)

    println(part1(input))
    check(part1(input) == 6269)

    check(part2(testInput) == TODO())

    println(part2(input))
    check(part2(input) == TODO())

}

