package aoc2022

import aoc.*
import aoc.parser.*

class Day05() {
    sealed interface Crate {
        val id: Char
    }
    object Empty : Crate {
        override val id: Char = '#'
    }
    data class Filled(override val id: Char): Crate

    data class Move(val count: Int, val from: Int, val to: Int)

    val filled = "[" followedBy regex("[A-Z]") followedBy "]" map { c -> Filled(c[0]) }
    val empty = "   " asValue Empty
    val crate = filled or empty
    val crateLine = crate sepBy " " followedBy "\n"
    val crates = zeroOrMore(crateLine)

    val crateNumbers = regex("(?:[ ]*\\d*[ ]*)*") followedBy "\n\n"

    val move = "move " followedBy number()
    val from = " from " followedBy number()
    val to = " to " followedBy number()
    val instructions = zeroOrMore(seq(move, from, to followedBy "\n", ::Move))

    val parser = seq(crates, crateNumbers, instructions) { c, _, i -> Pair(c, i) }

    fun solve(input: String, reorder: (List<List<Crate>>, Move) -> List<List<Crate>>): String {
        val (crates, moves) = parser.parse(input)
        val trans = crates.transpose().map { it.filter { it != Empty } }
        val end = moves.fold(trans, reorder)
        val result = end.map(List<Crate>::first).map(Crate::id)
        return result.joinToString(separator = "")
    }

    fun <T> move1(list: List<List<T>>, m: Move): List<List<T>> =
        move(list, m) { it.reversed() }

    fun <T> move2(list: List<List<T>>, m: Move): List<List<T>> =
        move(list, m) { it }

    fun <T> move(list: List<List<T>>, move: Move, f: (List<T>) -> List<T>): List<List<T>> {
        val movedCrates = f(list[move.from-1].take(move.count))
        return list.mapIndexed { idx, inner ->
            when (idx+1) {
                move.from -> inner.drop(move.count)
                move.to -> movedCrates + inner
                else -> inner
            }
        }
    }

    fun part1(input: String): String {
        return solve(input, ::move1)
    }

    fun part2(input: String): String {
        return solve(input, ::move2)
    }
}

