package aoc2022

import aoc.*

sealed interface Crate {
    val id: Char
}
object Empty : Crate {
    override val id: Char = '#'
}
data class Filled(override val id: Char): Crate

data class Move(val count: Int, val from: Int, val to: Int)

fun main() {
    val filled = Seq3(Literal("["), Regex("[A-Z]"), Literal("]")) { _, c, _-> Filled(c[0]) }
    val empty = Literal("   ").to(Empty)
    val crate = Seq(OneOf(filled, empty), optional(Literal(" "))) { c, _ -> c }
    val crateLine = Seq(ZeroOrMore(crate), Literal("\n")) { cs, _ -> cs}
    val crates = ZeroOrMore(crateLine)

    val crateNumbers = Seq(Regex("(?:[ ]*\\d*[ ]*)*"), Literal("\n\n")) { n, _ -> n }

    val move = Seq(Literal("move "), number()) { _, c -> c }
    val from = Seq(Literal(" from "), number()) { _, c -> c }
    val to = Seq(Literal(" to "), number()) { _, c -> c }
    val instructions = ZeroOrMore(Seq4(move, from, to, Literal("\n") ) { c, f, t, _ -> Move(c,f,t) })

    val parser = Seq3(crates, crateNumbers, instructions) { c, _, i -> Pair(c, i) }

    fun <T> List<List<T>>.move(move: Move, f: (List<T>) -> List<T>): List<List<T>> {
        val movedCrates = f(this[move.from-1].take(move.count))
        return this.mapIndexed { idx, list ->
            when (idx+1) {
                move.from -> list.drop(move.count)
                move.to -> movedCrates + list
                else -> list
            }
        }
    }

    fun <T> List<List<T>>.move1(m: Move): List<List<T>> =
        this.move(m) { it.reversed() }

    fun <T> List<List<T>>.move2(m: Move): List<List<T>> =
        this.move(m) { it }


    fun solve(input: String, reorder: (List<List<Crate>>, Move) -> List<List<Crate>>): String {
        val (crates, moves) = parser.parse(input)
        val trans = crates.transpose().map { it.filter { it != Empty } }
        val end = moves.fold(trans, reorder)
        val result = end.map(List<Crate>::first).map(Crate::id)
        return result.joinToString(separator = "")
    }

    fun part1(input: String): String {
        return solve(input, List<List<Crate>>::move1)
    }

    fun part2(input: String): String {
        return solve(input, List<List<Crate>>::move2)
    }
    // test if implementation meets criteria from the description, like:/
    val testInput = readFile(2022, 5, 1).readText()
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readFile(2022, 5).readText()

    check(part1(input) == "PSNRGBTFT")
    check(part2(input) == "BNTZFPMMW")
    println(part1(input))
    println(part2(input))

}

